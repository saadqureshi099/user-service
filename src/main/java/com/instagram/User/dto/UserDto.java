package com.instagram.User.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private long id;
    private String name;
    private String username;
    private String profileUrl;
    private Boolean isPrivate;
}
