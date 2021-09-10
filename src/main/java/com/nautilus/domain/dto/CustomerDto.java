package com.nautilus.domain.dto;

import com.nautilus.domain.Customer;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
public class CustomerDto {

    private Long id;
    private String name;
    private String city;
    private String address;
    private String phone;
    private LocalDate date;
    private Customer.LegalForm legalForm;
    private Integer monthsWithoutFulfilledMonthlyObligation;
    private Integer requiredSanitisePeriodInMonths;
    private LocalDate lastSanitiseDate;
    private Integer monthsUntilSanitize;
    private Double debt;
    private Integer packagingSmall;
    private Integer packagingLarge;

}
