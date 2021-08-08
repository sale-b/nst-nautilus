package com.nautilus.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class Customer extends BaseEntity {
    private String name;
    private String city;
    private String address;
    private String phone;
    private LocalDate date;
    private LegalForm legalForm;
    private Integer requiredSanitisePeriodInMonths;
    private Double debt;
    private Integer packagingSmall;
    private Integer packagingLarge;

    public Customer(long id, String name, String city, String address, String phone, LocalDate date, LegalForm legalForm, int requiredSanitisePeriod, double debt, int packagingSmall, int packagingLarge, LocalDateTime createdOn, LocalDateTime modifiedOn) {
        this(name, city, address, phone, date, legalForm, requiredSanitisePeriod, debt, packagingSmall, packagingLarge);
        this.id = id;
        this.createdOn = createdOn;
        this.modifiedOn = modifiedOn;
    }

    @Override
    public String toString() {
        return String.format("%s - %s - %s - %s - %s", name, city, address, phone, legalForm);
    }

    public enum LegalForm {
        LEGAL_ENTITY {
            @Override
            public String toString() {
                return "Pravno lice";
            }

            @Override
            public String serialize() {
                return "LEGAL_ENTITY";
            }
        },

        INDIVIDUAL {
            @Override
            public String toString() {
                return "Fizičko lice";
            }

            @Override
            public String serialize() {
                return "INDIVIDUAL";
            }
        };

        public abstract String serialize();
    }

}
