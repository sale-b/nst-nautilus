package com.nautilus.domain.dto;

import com.nautilus.domain.Customer;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
public class OrderDto {

    private Long id;
    private String name;
    private String address;
    private String phone;
    private String city;
    private Customer.LegalForm legalForm;
    private String waterSmall;
    private String waterLarge;
    private String glasses;
    private String deliveredBy;
    private String payed;
    private String note;
    private String totalPrice;

}
