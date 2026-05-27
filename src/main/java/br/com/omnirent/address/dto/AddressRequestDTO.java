package br.com.omnirent.address.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddressRequestDTO(
        String id,

        @NotBlank(message = "blank")
        @Size(min = 3, max = 120, message = "size")
        String street,

        @NotBlank(message = "blank")
        @Size(max = 20, message = "max_size")
        String number,

        @Size(max = 80, message = "max_size")
        String complement,

        @NotBlank(message = "blank")
        @Size(max = 80, message = "max_size")
        String district,

        @NotBlank(message = "blank")
        @Size(max = 80, message = "max_size")
        String city,

        @NotBlank(message = "blank")
        @Size(max = 40, message = "max_size")
        String state,

        @NotBlank(message = "blank")
        @Size(max = 40, message = "max_size")
        String country,

        @NotBlank(message = "blank")
        @Size(max = 20, message = "max_size")
        String zipCode
) {}
