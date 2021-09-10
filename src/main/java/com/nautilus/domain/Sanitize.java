package com.nautilus.domain;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Sanitize {

    private Long id;
    private Customer customer;
    private LocalDate date;
    private LocalDateTime createdOn;
    private LocalDateTime modifiedOn;

    public String getCustomerName() {
        return this.customer.getName();
    }

    public String getCustomerAddress() {
        return this.customer.getAddress();
    }

    public String getCustomerPhone() {
        return this.customer.getPhone();
    }
}
