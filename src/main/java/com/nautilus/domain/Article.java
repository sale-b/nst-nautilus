package com.nautilus.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

import static com.nautilus.util.Formatter.formatPrice;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class Article extends BaseEntity{
    private String name;
    private Double price;
    private Double tax;
    private Boolean mandatory;

    public Article(long id, String name, double price, Double tax, boolean mandatory, LocalDateTime createdOn, LocalDateTime modifiedOn) {
        this(name, price, tax, mandatory);
        this.id = id;
        this.createdOn = createdOn;
        this.modifiedOn=modifiedOn;
    }

    @Override
    public String toString() {
        return String.format("%s - %s", this.name, formatPrice(this.price));
    }

}
