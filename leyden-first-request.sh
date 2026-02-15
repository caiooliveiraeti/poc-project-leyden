


#!/bin/bash

#unzip -p dogs-0.0.1-SNAPSHOT.jar META-INF/MANIFEST.MF | more

# ==============================================================================
# CONFIGURATION & VARIABLES
# ==============================================================================
JAR_FILE="target/dogs-0.0.1-SNAPSHOT.jar"
APP_DIR="target/application"
API_URL="http://localhost:8080/api/dogs"
CURL_FMT="Code: %{http_code} | Latency: %{time_total}s\n"

# Function to clean up background processes in case of exit
cleanup() {
    pkill -f "dogs-0.0.1-SNAPSHOT.jar"
}
trap cleanup EXIT

echo "======================================================================"
echo "PHASE 1: PREPARATION (JAR EXTRACTION)"
echo "Rationale: Extracting the JAR removes Spring Boot's custom ClassLoader"
echo "overhead, making it fully compatible with Leyden/CDS optimizations."
echo "======================================================================"

# Clean up previous runs
rm -rf *.aot *.aotconf *.log $APP_DIR

# Extract the JAR (Best practice for Spring Boot + AOT)
java -Djarmode=tools -jar $JAR_FILE extract --destination $APP_DIR

echo "-> JAR successfully extracted to $APP_DIR"
echo ""

# ==============================================================================
# PHASE 2: TRAINING RUNS (GENERATING THE CACHES)
# ==============================================================================

echo "======================================================================"
echo "SCENARIO A: 'Spring Context Only' (onRefresh)"
echo "Strategy: Loads framework classes but captures NO JIT profile data."
echo "Use Case: Fast CI/CD pipelines where you cannot spin up dependencies."
echo "======================================================================"

# Uses Spring's flag to kill the app immediately after the context refreshes
java -XX:AOTCacheOutput=app-on-refresh.aot \
     -Dspring.context.exit=onRefresh \
     -jar $APP_DIR/dogs-0.0.1-SNAPSHOT.jar

echo "-> Cache 'app-on-refresh.aot' generated."
echo ""

echo "======================================================================"
echo "SCENARIO B: 'Startup Only' (No Traffic)"
echo "Strategy: Full startup, but JIT is 'cold' (no Method Profiling)."
echo "Result: Fast startup, but the first request will still trigger compilation."
echo "======================================================================"

# Start in background
java -XX:AOTCacheOutput=app-without-request.aot \
     -jar $APP_DIR/dogs-0.0.1-SNAPSHOT.jar &
PID_B=$!

# Wait for startup (adjust sleep as needed)
sleep 5
echo "-> App started. Stopping gracefully to write cache..."
kill $PID_B
wait $PID_B 2>/dev/null

echo "-> Cache 'app-without-request.aot' generated."
echo ""

echo "======================================================================"
echo "SCENARIO C: 'Peak Performance' (Synthetic Traffic)"
echo "Strategy: We bombard the API during training."
echo "Captures: Loaded Classes + JEP 515 JIT Method Profiles"
echo "======================================================================"

# Start in background
java -XX:AOTCacheOutput=app-with-request.aot \
     -jar $APP_DIR/dogs-0.0.1-SNAPSHOT.jar &
PID_C=$!

echo "-> Waiting for startup to begin load generation..."
sleep 5

echo "-> Generating synthetic traffic to train the JIT (Method Profiling)..."
for i in {1..50}; do
  curl -o /dev/null -s $API_URL
done

echo "-> Load complete. Stopping gracefully to write optimized cache..."
kill $PID_C
wait $PID_C 2>/dev/null

echo "-> Cache 'app-with-request.aot' generated."
echo ""

# ==============================================================================
# PHASE 3: VALIDATION (COMPARING TIME-TO-FIRST-RESPONSE)
# ==============================================================================

run_validation() {
    CACHE_FILE=$1
    DESCRIPTION=$2

    echo "----------------------------------------------------------------------"
    echo "Scenario: $DESCRIPTION"
    echo "----------------------------------------------------------------------"
    rm -f aot-cache-app.log

    if [ "$CACHE_FILE" == "none" ]; then
        java -jar $APP_DIR/dogs-0.0.1-SNAPSHOT.jar > aot-cache-app.log 2>&1 &
    else
        java -XX:AOTCache=$CACHE_FILE -jar $APP_DIR/dogs-0.0.1-SNAPSHOT.jar > aot-cache-app.log 2>&1 &
    fi
    APP_PID=$!
    sleep 5

    echo "-> [METRIC 1] Startup Time (JVM + Spring Boot Init):"
    grep "Started .* in" aot-cache-app.log | sed 's/^.*Started/Started/'
    echo "-> [METRIC 2] Time-to-First-Response (Business Logic Execution):"
    curl -o /dev/null -s -w "$CURL_FMT" $API_URL

    echo "-> Stopping application..."
    kill $APP_PID
    wait $APP_PID 2>/dev/null
    echo ""
}

echo "======================================================================"
echo "FINAL RESULTS: VALIDATING 'TIME-TO-FIRST-RESPONSE'"
echo "======================================================================"

# 1. Baseline (No Cache)
run_validation "none" "BASELINE (Standard JIT / No Cache)"

# 2. Validate onRefresh (Fast start, but first request might lag)
run_validation "app-on-refresh.aot" "Scenario A (Spring Context Only)"

# 3. Validate Startup Only (Full start, but cold JIT)
run_validation "app-without-request.aot" "Scenario B (Startup Only / No Traffic)"

# 4. Validate Traffic Trained (Optimized with JEP 515 - Should be fastest)
run_validation "app-with-request.aot" "Scenario C (Full Profiled / Peak Performance)"