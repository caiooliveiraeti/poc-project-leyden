package br.eti.caiooliveira.dogs;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.simple.JdbcClient;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class DogSeederTest {

	@Autowired
	private JdbcClient jdbcClient;

	@Autowired
	private DogSeeder dogSeeder;

	@Autowired
	private DogRepository dogRepository;

	@BeforeEach
	void setUp() {
		dogRepository.deleteAll();
	}

	@Test
	void shouldSeedDogsOnEmptyDatabase() throws Exception {
		dogSeeder.run(null);

		long count = jdbcClient.sql("SELECT COUNT(*) FROM dog")
				.query(Long.class)
				.single();

		assertThat(count).isEqualTo(1000);
	}

	@Test
	void shouldNotSeedWhenDogsAlreadyExist() throws Exception {
		dogRepository.save(new Dog("Existing", "Labrador", 5, 30.0));

		dogSeeder.run(null);

		long count = jdbcClient.sql("SELECT COUNT(*) FROM dog")
				.query(Long.class)
				.single();

		assertThat(count).isEqualTo(1);
	}

	@Test
	void shouldSeedAtLeast20DistinctBreeds() throws Exception {
		dogSeeder.run(null);

		long distinctBreeds = jdbcClient.sql("SELECT COUNT(DISTINCT breed) FROM dog")
				.query(Long.class)
				.single();

		assertThat(distinctBreeds).isGreaterThanOrEqualTo(20);
	}
}