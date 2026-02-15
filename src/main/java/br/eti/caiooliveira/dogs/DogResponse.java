package br.eti.caiooliveira.dogs;

public record DogResponse(Long id, String name, String breed, Integer age, Double weight) {

	public static DogResponse from(Dog dog) {
		return new DogResponse(dog.getId(), dog.getName(), dog.getBreed(), dog.getAge(), dog.getWeight());
	}
}
