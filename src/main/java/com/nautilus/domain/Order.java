package com.nautilus.domain;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
public class Order {

    private Long id;
    @ToString.Exclude
    private Customer customer;
    private LocalDate date;
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private List<OrderItem> items = new ArrayList<>();
    private DeliveredBy deliveredBy;
    private Boolean payed;
    private Integer packagingDebtSmall;
    private Integer packagingDebtLarge;
    private String note;
    private LocalDateTime createdOn;
    private LocalDateTime modifiedOn;

    public void addItem(OrderItem orderItem) {
        this.items.add(orderItem);
        orderItem.setOrder(this);
    }

    public void removeItem(OrderItem orderItem) {
        this.items.remove(orderItem);
        orderItem.setOrder(null);
    }

    //immutable list
    public List<OrderItem> getItems() {
        return Arrays.asList(this.items.toArray(new OrderItem[]{}));
    }

    public void clearItems(){
       this.items = new ArrayList<>();
    }

    public Optional<OrderItem> findOrderItemByArticleName(String article) {
        return this.items.stream()
                .filter(orderItem -> orderItem.getArticleName().equals(article))
                .findFirst();
    }

    public enum DeliveredBy {
        NONE {
            @Override
            public String getStringValue() {
                return "NONE";
            }

            @Override
            public String toString() {
                return "Nije isporučeno";
            }
        },
        FIRST {
            @Override
            public String getStringValue() {
                return "FIRST";
            }

            @Override
            public String toString() {
                return "1";
            }
        },

        SECOND {
            @Override
            public String getStringValue() {
                return "SECOND";
            }

            @Override
            public String toString() {
                return "2";
            }
        };

        public abstract String getStringValue();
    }
}
