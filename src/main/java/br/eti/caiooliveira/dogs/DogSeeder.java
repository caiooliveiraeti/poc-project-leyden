package br.eti.caiooliveira.dogs;

import java.util.List;
import java.util.Random;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

@Component
public class DogSeeder implements ApplicationRunner {

	private static final int SEED_COUNT = 1000;

	private record BreedInfo(String breed, double minWeight, double maxWeight) {}

	private static final List<BreedInfo> BREEDS = List.of(
		new BreedInfo("Labrador Retriever", 25.0, 36.0),
		new BreedInfo("Golden Retriever", 25.0, 34.0),
		new BreedInfo("German Shepherd", 22.0, 40.0),
		new BreedInfo("Bulldog", 18.0, 25.0),
		new BreedInfo("Poodle", 18.0, 32.0),
		new BreedInfo("Beagle", 9.0, 11.0),
		new BreedInfo("Rottweiler", 35.0, 60.0),
		new BreedInfo("Dachshund", 7.0, 14.5),
		new BreedInfo("Yorkshire Terrier", 2.0, 3.2),
		new BreedInfo("Boxer", 25.0, 32.0),
		new BreedInfo("Siberian Husky", 16.0, 27.0),
		new BreedInfo("Pomeranian", 1.4, 3.2),
		new BreedInfo("Shih Tzu", 4.0, 7.3),
		new BreedInfo("Border Collie", 12.0, 20.0),
		new BreedInfo("Doberman Pinscher", 27.0, 45.0),
		new BreedInfo("Australian Shepherd", 18.0, 29.0),
		new BreedInfo("Cavalier King Charles Spaniel", 5.4, 8.2),
		new BreedInfo("Miniature Schnauzer", 5.0, 9.0),
		new BreedInfo("Cocker Spaniel", 12.0, 16.0),
		new BreedInfo("Pug", 6.0, 8.0),
		new BreedInfo("Great Dane", 45.0, 90.0),
		new BreedInfo("Chihuahua", 1.5, 3.0),
		new BreedInfo("Maltese", 1.4, 3.6),
		new BreedInfo("Bernese Mountain Dog", 32.0, 52.0),
		new BreedInfo("French Bulldog", 8.0, 13.0),
		new BreedInfo("Weimaraner", 25.0, 40.0),
		new BreedInfo("Dalmatian", 20.0, 32.0),
		new BreedInfo("Akita", 32.0, 59.0),
		new BreedInfo("Whippet", 6.8, 14.0),
		new BreedInfo("Basset Hound", 20.0, 29.0)
	);

	private static final List<String> NAMES = List.of(
		"Rex", "Buddy", "Max", "Charlie", "Cooper", "Rocky", "Bear", "Duke",
		"Tucker", "Jack", "Toby", "Milo", "Oliver", "Leo", "Zeus", "Loki",
		"Benny", "Oscar", "Finn", "Hank", "Gus", "Bruno", "Archie", "Thor",
		"Louie", "Murphy", "Baxter", "Moose", "Jasper", "Scout",
		"Luna", "Bella", "Daisy", "Lucy", "Sadie", "Molly", "Maggie", "Chloe",
		"Sophie", "Stella", "Penny", "Nala", "Rosie", "Ruby", "Lola", "Coco",
		"Willow", "Hazel", "Ellie", "Pepper", "Ginger", "Roxy", "Sasha", "Mia",
		"Piper", "Winnie", "Abby", "Zoey", "Gracie", "Honey"
	);

	private final JdbcClient jdbcClient;

	public DogSeeder(JdbcClient jdbcClient) {
		this.jdbcClient = jdbcClient;
	}

	@Override
	public void run(ApplicationArguments args) {
		Long count = jdbcClient.sql("SELECT COUNT(*) FROM dog")
				.query(Long.class)
				.single();

		if (count > 0) {
			return;
		}

		var random = new Random(42);

		for (int i = 0; i < SEED_COUNT; i++) {
			var breed = BREEDS.get(random.nextInt(BREEDS.size()));
			var name = NAMES.get(random.nextInt(NAMES.size()));
			int age = random.nextInt(1, 16);
			double weight = Math.round((breed.minWeight + random.nextDouble() * (breed.maxWeight - breed.minWeight)) * 10.0) / 10.0;

			jdbcClient.sql("INSERT INTO dog (name, breed, age, weight) VALUES (:name, :breed, :age, :weight)")
					.param("name", name)
					.param("breed", breed.breed)
					.param("age", age)
					.param("weight", weight)
					.update();
		}
	}
}