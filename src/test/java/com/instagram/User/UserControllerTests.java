package com.instagram.User;

import com.instagram.User.controller.UserController;
import com.instagram.User.dto.LoginRequest;
import com.instagram.User.dto.SignupRequest;
import com.instagram.User.dto.UserDto;
import com.instagram.User.exception.EmailAlreadyExistsException;
import com.instagram.User.exception.UsernameAlreadyExistsException;
import com.instagram.User.mapper.UserMapper;
import com.instagram.User.model.User;
import com.instagram.User.repository.UserRepository;
import com.instagram.User.service.JwtTokenProvider;
import com.instagram.User.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTests {

    @Mock
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @InjectMocks
    private UserController userController;
    private List<UserDto> userList;

    @BeforeEach
    void setUp() {
        this.userList = new ArrayList<>();
        this.userList.add(new UserDto(1L,"moiz","moiz1","profile.png",false));
        this.userList.add(new UserDto(2L,"moiz","moiz2","profile.png",false));
        this.userList.add(new UserDto(3L,"moiz","moiz3","profile.png",false));

    }

    @Test
    void TestFindAll() throws Exception {
        given(userService.findAll()).willReturn(userList);

        when(userService.findAll()).thenReturn(userList);

        // When
        ResponseEntity<List<UserDto>> responseEntity = userController.findAll();

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(userList, responseEntity.getBody());
        verify(userService).findAll();
    }
    @Test
    void testAddUser_Success() {
        // Given
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setName("demo");
        signupRequest.setUsername("demo1234");
        signupRequest.setPassword("demo123");
        signupRequest.setEmail("demo@gmail.com");
        signupRequest.setIsPrivate(true);
        signupRequest.setProfileUrl("/profile.png");

        User user = User.builder().id(1).name("demo").profileUrl("/profile.png").isPrivate(true).username("demo1234").email("demo@gmail.com").build();

        when(userMapper.signupRequestToUser(signupRequest)).thenReturn(user);
        when(userService.registerUser(user)).thenReturn(user);
        // When
        String result = userController.addUser(signupRequest);
        // Then
        assertEquals("User created successfully", result);
    }
    @Test
    void testAddUser_UsernameAlreadyExistsException() {
        // Given
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setName("demo");
        signupRequest.setUsername("demo1234");
        signupRequest.setPassword("demo123");
        signupRequest.setEmail("demo@gmail.com");
        signupRequest.setIsPrivate(true);
        signupRequest.setProfileUrl("/profile.png");

        User user = User.builder().id(1).name("demo").profileUrl("/profile.png").isPrivate(true).username("demo1234").email("demo@gmail.com").build();

        when(userMapper.signupRequestToUser(signupRequest)).thenReturn(user);
        doThrow(new UsernameAlreadyExistsException("Username already exists")).when(userService).registerUser(any(User.class));

        // When and Then
        assertThrows(UsernameAlreadyExistsException.class, () -> userController.addUser(signupRequest));
        verify(userMapper).signupRequestToUser(signupRequest);
        verify(userService).registerUser(any(User.class));
        verifyNoMoreInteractions(userMapper, userService);
    }

    @Test
    void testAddUser_EmailAlreadyExistsException() {
        // Given
        SignupRequest signupRequest = new SignupRequest();
        when(userMapper.signupRequestToUser(signupRequest)).thenReturn(new User());
        doThrow(new EmailAlreadyExistsException("Email already exists")).when(userService).registerUser(any(User.class));

        // When and Then
        assertThrows(RuntimeException.class, () -> userController.addUser(signupRequest));
        verify(userMapper).signupRequestToUser(signupRequest);
        verify(userService).registerUser(any(User.class));
        verifyNoMoreInteractions(userMapper, userService);
    }

    @Test
    void testAddUser_RuntimeException() {
        // Given
        SignupRequest signupRequest = new SignupRequest();
        when(userMapper.signupRequestToUser(signupRequest)).thenReturn(new User());
        doThrow(new RuntimeException("Unexpected exception")).when(userService).registerUser(any(User.class));

        // When and Then
        assertThrows(RuntimeException.class, () -> userController.addUser(signupRequest));
        verify(userMapper).signupRequestToUser(signupRequest);
        verify(userService).registerUser(any(User.class));
        verifyNoMoreInteractions(userMapper, userService);
    }
    @Test
    void testUpdateUser_Success() {
        // Given
        User user = User.builder().id(5).email("test@gmail.com").name("test").profileUrl("/profile.png").isPrivate(true).username("test123").password("hello123").build();

        when(userRepository.saveAndFlush(user)).thenReturn(user);

        // When
        User result = userController.updateUser(user);

        // Then
        assertEquals(user, result);
    }
    @Test
    void testFindUser_Success() {
        // Given
        String username = "testuser";
        UserDto userDto = UserDto.builder().id(5).name("test").profileUrl("/profile.png").isPrivate(true).username("test123").build();


        when(userService.findByUsername(username)).thenReturn(userDto);

        // When
        UserDto result = userController.findUser(username);

        // Then
        assertEquals(userDto, result);
    }
    @Test
    void testGetUserId_Success() {
        // Given
        String username = "testuser";
        User user = User.builder().id(5).email("test@gmail.com").name("test").profileUrl("/profile.png").isPrivate(true).username("test123").password("hello123").build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // When
        long result = userController.getUserId(username);

        // Then
        assertEquals(user.getId(), result);
        verify(userRepository).findByUsername(username);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testGetUserId_UserNotFound() {
        String username = "nonexistentuser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userController.getUserId(username));

    }
    @Test
    void testValidateToken_ValidToken() {
        // Given
        String token = "validToken";
        String result = userController.validateToken(token);
        assertEquals("Token is valid", result);

    }
    @Test
    void testFindByUserId_Success() {
        // Given
        long userId = 1L;
        UserDto userDto = UserDto.builder().id(1).name("test").profileUrl("/profile.png").isPrivate(true).username("test123").build();

        when(userService.findByUserId(userId)).thenReturn(userDto);

        // When
        ResponseEntity<UserDto> result = userController.findByUserId(userId);

        // Then
        assertEquals(userDto, result.getBody());
        assertEquals(HttpStatus.OK, result.getStatusCode());

    }
    @Test
     void testAuthenticateAndGetToken_Success() {
        // Given
        LoginRequest loginRequest = new LoginRequest("username", "password");
        String token = "testToken";

        when(userService.authenticateAndGetToken(loginRequest)).thenReturn(token);

        // When
        String result = userController.authenticateAndGetToken(loginRequest);

        // Then
        assertEquals(token, result);
        verify(userService).authenticateAndGetToken(loginRequest);
        verifyNoMoreInteractions(userService);
    }
}
