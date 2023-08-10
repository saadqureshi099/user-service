package com.instagram.User.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="users_tbl")
@Getter
@Setter
@Builder
//@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String email;
    private String username;
    private String profileUrl;
    private String password;
    private Boolean isPrivate;

}
