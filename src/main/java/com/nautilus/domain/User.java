/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nautilus.domain;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 *
 * @author laptop-02
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class User {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private LocalDate dob;
    private String gender;
    private String role;
}
