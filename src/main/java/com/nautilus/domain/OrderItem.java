package com.nautilus.domain;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OrderItem extends BaseEntity {
    private String articleName;
    private Double articlePrice;
    private Double articleTax;
    private Integer quantity;
    @ToString.Exclude
    private Order order;

    public OrderItem(long id, String articleName, double articlePrice, double articleTax, int quantity, Order order, LocalDateTime createdOn, LocalDateTime modifiedOn) {
        this(articleName, articlePrice, articleTax, quantity, order);
        this.id = id;
        this.createdOn = createdOn;
        this.modifiedOn = modifiedOn;
    }
}
