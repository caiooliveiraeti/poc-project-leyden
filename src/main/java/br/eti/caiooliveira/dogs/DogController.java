package br.eti.caiooliveira.dogs;

import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/dogs")
public class DogController {

	private final DogRepository repository;

	public DogController(DogRepository repository) {
		this.repository = repository;
	}

	@PostMapping
	public ResponseEntity<DogResponse> create(@Valid @RequestBody DogRequest request) {
		var dog = new Dog(request.name(), request.breed(), request.age(), request.weight());
		var saved = repository.save(dog);
		return ResponseEntity.status(HttpStatus.CREATED).body(DogResponse.from(saved));
	}

	@GetMapping
	public List<DogResponse> list() {
		return StreamSupport.stream(repository.findAll().spliterator(), false)
				.map(DogResponse::from)
				.toList();
	}

	@GetMapping("/{id}")
	public DogResponse getById(@PathVariable Long id) {
		return repository.findById(id)
				.map(DogResponse::from)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}

	@PutMapping("/{id}")
	public DogResponse update(@PathVariable Long id, @Valid @RequestBody DogRequest request) {
		var dog = repository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		dog.setName(request.name());
		dog.setBreed(request.breed());
		dog.setAge(request.age());
		dog.setWeight(request.weight());
		var saved = repository.save(dog);
		return DogResponse.from(saved);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		if (!repository.existsById(id)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		repository.deleteById(id);
		return ResponseEntity.noContent().build();
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Map<String, Object> handleValidationErrors(MethodArgumentNotValidException ex) {
		var errors = ex.getBindingResult().getFieldErrors().stream()
				.collect(java.util.stream.Collectors.toMap(
						FieldError::getField,
						e -> e.getDefaultMessage() != null ? e.getDefaultMessage() : "invalid",
						(a, b) -> a));
		return Map.of("errors", errors);
	}
}
