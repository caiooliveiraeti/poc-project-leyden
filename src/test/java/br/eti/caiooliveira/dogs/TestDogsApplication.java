package br.eti.caiooliveira.dogs;

import org.springframework.boot.SpringApplication;

public class TestDogsApplication {

	public static void main(String[] args) {
		SpringApplication.from(DogsApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
