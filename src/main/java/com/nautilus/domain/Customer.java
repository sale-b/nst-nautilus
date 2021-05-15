package com.nautilus.domain;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Customer {
    private Long id;
    private String name;
    private String city;
    private String address;
    private String phone;
    private LocalDate date;
    private CustomerType type;
    private Integer sanitisePeriodInMonths;
    private Double debt;
    private Integer packagingSmall;
    private Integer packagingLarge;
    private Boolean backlogPackagingSmall;
    private Boolean backlogPackagingLarge;
    private LocalDateTime createdOn;
    private LocalDateTime modifiedOn;

    @Override
    public String toString() {
        return String.format("%s - %s - %s - %s - %s", name, city, address, phone, type);
    }

    public enum CustomerType {
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
