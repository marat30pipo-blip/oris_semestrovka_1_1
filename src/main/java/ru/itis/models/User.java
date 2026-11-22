package ru.itis.models;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
public class User {
    private Long userId;
    private String email;
    private String passwordHash;
    private String role;
}
