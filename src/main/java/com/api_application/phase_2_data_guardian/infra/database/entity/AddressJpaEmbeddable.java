package com.api_application.phase_2_data_guardian.infra.database.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressJpaEmbeddable {

    @Column(name = "street", nullable = false, length = 150)
    private String street;

    @Column(name = "address_number", nullable = false, length = 20)
    private String number;

    @Column(name = "address_complement", length = 100)
    private String complement;

    @Column(name = "neighborhood", nullable = false, length = 100)
    private String neighborhood;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "state", nullable = false, length = 2)
    private String state;

    @Column(name = "zip_code", nullable = false, length = 9)
    private String zipCode;
}
