package com.nautilus.domain;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class OrderItem {

    private Long id;
    private String articleName;
    private Double articlePrice;
    private Integer quantity;
    @ToString.Exclude
    private Order order;
    private LocalDateTime createdOn;
    private LocalDateTime modifiedOn;

}
