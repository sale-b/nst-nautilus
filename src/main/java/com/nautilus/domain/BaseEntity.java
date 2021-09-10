package com.nautilus.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@SuperBuilder
public abstract class BaseEntity {
    protected Long id;
    protected LocalDateTime createdOn;
    protected LocalDateTime modifiedOn;
}
