package com.instagram.User;
import com.instagram.User.dto.LoginRequest;
import com.instagram.User.dto.UserDto;
import com.instagram.User.exception.EmailAlreadyExistsException;
import com.instagram.User.exception.InvalidAccessException;
import com.instagram.User.exception.ResourceNotFoundException;
import com.instagram.User.exception.UsernameAlreadyExistsException;
import com.instagram.User.mapper.UserMapper;
import com.instagram.User.model.User;
import com.instagram.User.repository.UserRepository;
import com.instagram.User.service.JwtTokenProvider;
import com.instagram.User.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserMapper userMapper;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void testRegisterUser() {
        User user = User.builder()
                .id(5)
                .email("test@gmail.com")
                .name("test")
                .profileUrl("/profile.png")
                .isPrivate(true)
                .username("test123")
                .password("hello123")
                .build();


        Mockito.when(userRepository.existsByUsername("test123")).thenReturn(false);
        Mockito.when(userRepository.existsByEmail("test@gmail.com")).thenReturn(false);
        Mockito.when(passwordEncoder.encode("hello123")).thenReturn("encodedPassword");


        Mockito.when(userRepository.save(user)).thenReturn(user);
        User result = userService.registerUser(user);

        assertEquals(user, result);
        assertEquals("encodedPassword", user.getPassword());

        Mockito.when(userRepository.existsByUsername("test123")).thenReturn(true);
        Mockito.when(userRepository.existsByEmail("test@gmail.com")).thenReturn(false);
        assertThrows(UsernameAlreadyExistsException.class, () -> userService.registerUser(user));

        Mockito.when(userRepository.existsByUsername("test123")).thenReturn(false);
        Mockito.when(userRepository.existsByEmail("test@gmail.com")).thenReturn(true);
        assertThrows(EmailAlreadyExistsException.class, () -> userService.registerUser(user));

    }
    @Test
    void testFindAll() {
        User user1 = User.builder().id(5).email("test@gmail.com").name("test").profileUrl("/profile.png").isPrivate(true).username("test123").password("hello123").build();
        User user2 = User.builder().id(6).email("test2@gmail.com").name("test2").profileUrl("/profile.png").isPrivate(true).username("test1234").password("hello1234").build();

        List<User> userList = new ArrayList<>();
        userList.add(user1);
        userList.add(user2);

        Mockito.when(userRepository.findAll()).thenReturn(userList);

        UserDto userDto1 = UserDto.builder().id(5).name("test").profileUrl("/profile.png").isPrivate(true).username("test123").build();
        UserDto userDto2 = UserDto.builder().id(6).name("test2").profileUrl("/profile.png").isPrivate(true).username("test1234").build();

        Mockito.when(userMapper.userToUserDto(user1)).thenReturn(userDto1);
        Mockito.when(userMapper.userToUserDto(user2)).thenReturn(userDto2);

        List<UserDto> result = userService.findAll();

        assertEquals(2, result.size());
        assertEquals(userDto1, result.get(0));
        assertEquals(userDto2, result.get(1));

    }

    @Test
    void testFindByUsername_UserExists() {

        User user1 = User.builder().id(5).email("test@gmail.com").name("test").profileUrl("/profile.png").isPrivate(true).username("test123").password("hello123").build();

        Mockito.when(userRepository.findByUsername("test123")).thenReturn(Optional.of(user1));

        UserDto userDto = UserDto.builder().id(5).name("test").profileUrl("/profile.png").isPrivate(true).username("test123").build();
        Mockito.when(userMapper.userToUserDto(user1)).thenReturn(userDto);

        UserDto result = userService.findByUsername("test123");
        assertEquals(userDto, result);

    }
    @Test
    void testFindByUsername_UserDoesNotExist() {

        Mockito.when(userRepository.findByUsername("username")).thenReturn(Optional.empty());

        UserDto result = userService.findByUsername("username");


        assertNull(result);
    }
    @Test
    void testFindByUserId_UserExists() {

        User user1 = User.builder().id(5).email("test@gmail.com").name("test").profileUrl("/profile.png").isPrivate(true).username("test123").password("hello123").build();


        Mockito.when(userRepository.findById(5L)).thenReturn(Optional.of(user1));


        UserDto userDto = UserDto.builder().id(5L).name("test").profileUrl("/profile.png").isPrivate(true).username("test123").build();
        Mockito.when(userMapper.userToUserDto(user1)).thenReturn(userDto);


        UserDto result = userService.findByUserId(5L);


        assertEquals(userDto, result);

    }

    @Test
    void testFindByUserId_UserDoesNotExist() {

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.findByUserId(1L));
    }

    @Test
    void testAuthenticateAndGetToken_Authenticated() {

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.isAuthenticated()).thenReturn(true);
        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);


        Mockito.when(jwtTokenProvider.generateToken("username")).thenReturn("token");


        LoginRequest loginRequest = new LoginRequest("username", "password");


        String result = userService.authenticateAndGetToken(loginRequest);


        assertEquals("token", result);


    }

    @Test
    void testAuthenticateAndGetToken_NotAuthenticated() {

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.isAuthenticated()).thenReturn(false);
        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);


        LoginRequest loginRequest = new LoginRequest("username", "password");

        assertThrows(InvalidAccessException.class, () -> userService.authenticateAndGetToken(loginRequest));

    }


}
