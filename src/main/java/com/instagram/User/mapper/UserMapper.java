package com.instagram.User.mapper;

import com.instagram.User.dto.SignupRequest;
import com.instagram.User.dto.UserDto;
import com.instagram.User.model.User;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring"
)
public interface UserMapper {
    UserDto userToUserDto(User user);
    User signupRequestToUser(SignupRequest signupRequest);

}
