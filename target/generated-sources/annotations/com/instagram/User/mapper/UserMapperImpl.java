package com.instagram.User.mapper;

import com.instagram.User.dto.SignupRequest;
import com.instagram.User.dto.UserDto;
import com.instagram.User.model.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-07-13T17:57:37+0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.7 (Private Build)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDto userToUserDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserDto.UserDtoBuilder userDto = UserDto.builder();

        userDto.id( user.getId() );
        userDto.name( user.getName() );
        userDto.username( user.getUsername() );
        userDto.profileUrl( user.getProfileUrl() );
        userDto.isPrivate( user.getIsPrivate() );

        return userDto.build();
    }

    @Override
    public User signupRequestToUser(SignupRequest signupRequest) {
        if ( signupRequest == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.name( signupRequest.getName() );
        user.email( signupRequest.getEmail() );
        user.username( signupRequest.getUsername() );
        user.profileUrl( signupRequest.getProfileUrl() );
        user.password( signupRequest.getPassword() );
        user.isPrivate( signupRequest.getIsPrivate() );

        return user.build();
    }
}
