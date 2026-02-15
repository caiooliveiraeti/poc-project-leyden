package br.eti.caiooliveira.dogs;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record DogRequest(
		@NotBlank @Size(max = 100) String name,
		@NotBlank @Size(max = 100) String breed,
		@NotNull @Min(0) Integer age,
		@NotNull @Positive Double weight) {
}
