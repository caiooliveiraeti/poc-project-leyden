package br.eti.caiooliveira.dogs;

import org.springframework.data.annotation.Id;

public class Dog {

	@Id
	private Long id;
	private String name;
	private String breed;
	private Integer age;
	private Double weight;

	public Dog() {
	}

	public Dog(String name, String breed, Integer age, Double weight) {
		this.name = name;
		this.breed = breed;
		this.age = age;
		this.weight = weight;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBreed() {
		return breed;
	}

	public void setBreed(String breed) {
		this.breed = breed;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}
}
