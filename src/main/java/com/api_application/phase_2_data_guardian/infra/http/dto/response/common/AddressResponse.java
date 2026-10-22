package com.api_application.phase_2_data_guardian.infra.http.dto.response.common;

public record AddressResponse(
        String street,
        String number,
        String complement,
        String neighborhood,
        String city,
        String state,
        String zipCode
) {
}
