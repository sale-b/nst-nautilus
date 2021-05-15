package com.nautilus.domain;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Article {
    private Long id;
    private String name;
    private Double price;
    private Boolean mandatory;
    private LocalDateTime createdOn;
    private LocalDateTime modifiedOn;

    @Override
    public String toString() {
        return String.format("%s - %.02f din.", this.name, this.price);
    }

}
