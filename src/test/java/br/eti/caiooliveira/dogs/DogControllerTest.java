package br.eti.caiooliveira.dogs;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DogControllerTest {

	@Autowired
	private DogRepository repository;

	private RestClient restClient;

	@BeforeEach
	void setUp(@LocalServerPort int port) {
		repository.deleteAll();
		restClient = RestClient.builder()
				.baseUrl("http://localhost:" + port)
				.build();
	}

	@Test
	void shouldCreateDog() {
		var request = new DogRequest("Rex", "Labrador", 3, 25.5);

		var response = restClient.post()
				.uri("/api/dogs")
				.contentType(MediaType.APPLICATION_JSON)
				.body(request)
				.retrieve()
				.body(DogResponse.class);

		assertThat(response).isNotNull();
		assertThat(response.id()).isNotNull();
		assertThat(response.name()).isEqualTo("Rex");
		assertThat(response.breed()).isEqualTo("Labrador");
		assertThat(response.age()).isEqualTo(3);
		assertThat(response.weight()).isEqualTo(25.5);
	}

	@Test
	void shouldReturn400WhenCreatingDogWithMissingFields() {
		var request = Map.of("name", "Rex", "breed", "Labrador");

		var ex = org.junit.jupiter.api.Assertions.assertThrows(HttpClientErrorException.BadRequest.class, () ->
				restClient.post()
						.uri("/api/dogs")
						.contentType(MediaType.APPLICATION_JSON)
						.body(request)
						.retrieve()
						.body(String.class));

		assertThat(ex.getStatusCode().value()).isEqualTo(400);
	}

	@Test
	void shouldReturn400WhenCreatingDogWithInvalidValues() {
		var request = Map.of("name", "", "breed", "Labrador", "age", -1, "weight", 0);

		var ex = org.junit.jupiter.api.Assertions.assertThrows(HttpClientErrorException.BadRequest.class, () ->
				restClient.post()
						.uri("/api/dogs")
						.contentType(MediaType.APPLICATION_JSON)
						.body(request)
						.retrieve()
						.body(String.class));

		assertThat(ex.getStatusCode().value()).isEqualTo(400);
	}

	@Test
	void shouldReturn400WhenNameExceedsMaxLength() {
		var longName = "a".repeat(101);
		var request = new DogRequest(longName, "Labrador", 3, 25.5);

		var ex = org.junit.jupiter.api.Assertions.assertThrows(HttpClientErrorException.BadRequest.class, () ->
				restClient.post()
						.uri("/api/dogs")
						.contentType(MediaType.APPLICATION_JSON)
						.body(request)
						.retrieve()
						.body(String.class));

		assertThat(ex.getStatusCode().value()).isEqualTo(400);
	}

	@Test
	void shouldListAllDogs() {
		repository.save(new Dog("Rex", "Labrador", 3, 25.5));
		repository.save(new Dog("Buddy", "Golden Retriever", 5, 30.0));

		var response = restClient.get()
				.uri("/api/dogs")
				.retrieve()
				.body(new ParameterizedTypeReference<List<DogResponse>>() {});

		assertThat(response).hasSize(2);
	}

	@Test
	void shouldReturnEmptyListWhenNoDogs() {
		var response = restClient.get()
				.uri("/api/dogs")
				.retrieve()
				.body(new ParameterizedTypeReference<List<DogResponse>>() {});

		assertThat(response).isEmpty();
	}

	@Test
	void shouldGetDogById() {
		var saved = repository.save(new Dog("Rex", "Labrador", 3, 25.5));

		var response = restClient.get()
				.uri("/api/dogs/{id}", saved.getId())
				.retrieve()
				.body(DogResponse.class);

		assertThat(response).isNotNull();
		assertThat(response.name()).isEqualTo("Rex");
	}

	@Test
	void shouldReturn404WhenDogNotFound() {
		var ex = org.junit.jupiter.api.Assertions.assertThrows(HttpClientErrorException.NotFound.class, () ->
				restClient.get()
						.uri("/api/dogs/{id}", 999)
						.retrieve()
						.body(DogResponse.class));

		assertThat(ex.getStatusCode().value()).isEqualTo(404);
	}

	@Test
	void shouldUpdateDog() {
		var saved = repository.save(new Dog("Rex", "Labrador", 3, 25.5));
		var updateRequest = new DogRequest("Rex Updated", "Labrador", 4, 26.0);

		var response = restClient.put()
				.uri("/api/dogs/{id}", saved.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.body(updateRequest)
				.retrieve()
				.body(DogResponse.class);

		assertThat(response).isNotNull();
		assertThat(response.name()).isEqualTo("Rex Updated");
		assertThat(response.age()).isEqualTo(4);
		assertThat(response.weight()).isEqualTo(26.0);
	}

	@Test
	void shouldReturn404WhenUpdatingNonExistentDog() {
		var updateRequest = new DogRequest("Rex", "Labrador", 3, 25.5);

		var ex = org.junit.jupiter.api.Assertions.assertThrows(HttpClientErrorException.NotFound.class, () ->
				restClient.put()
						.uri("/api/dogs/{id}", 999)
						.contentType(MediaType.APPLICATION_JSON)
						.body(updateRequest)
						.retrieve()
						.body(DogResponse.class));

		assertThat(ex.getStatusCode().value()).isEqualTo(404);
	}

	@Test
	void shouldReturn400WhenUpdatingWithInvalidData() {
		var saved = repository.save(new Dog("Rex", "Labrador", 3, 25.5));
		var updateRequest = Map.of("name", "", "breed", "", "age", -1, "weight", 0);

		var ex = org.junit.jupiter.api.Assertions.assertThrows(HttpClientErrorException.BadRequest.class, () ->
				restClient.put()
						.uri("/api/dogs/{id}", saved.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.body(updateRequest)
						.retrieve()
						.body(String.class));

		assertThat(ex.getStatusCode().value()).isEqualTo(400);
	}

	@Test
	void shouldDeleteDog() {
		var saved = repository.save(new Dog("Rex", "Labrador", 3, 25.5));

		restClient.delete()
				.uri("/api/dogs/{id}", saved.getId())
				.retrieve()
				.toBodilessEntity();

		assertThat(repository.findById(saved.getId())).isEmpty();
	}

	@Test
	void shouldReturn404WhenDeletingNonExistentDog() {
		var ex = org.junit.jupiter.api.Assertions.assertThrows(HttpClientErrorException.NotFound.class, () ->
				restClient.delete()
						.uri("/api/dogs/{id}", 999)
						.retrieve()
						.toBodilessEntity());

		assertThat(ex.getStatusCode().value()).isEqualTo(404);
	}
}
