package com.instagram.User.controller;

import com.instagram.User.dto.ApiResponse;
import com.instagram.User.dto.LoginRequest;
import com.instagram.User.dto.SignupRequest;
import com.instagram.User.dto.UserDto;
import com.instagram.User.exception.EmailAlreadyExistsException;
import com.instagram.User.exception.UsernameAlreadyExistsException;
import com.instagram.User.mapper.UserMapper;
import com.instagram.User.repository.UserRepository;
import com.instagram.User.service.JwtTokenProvider;
import com.instagram.User.service.UserService;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.instagram.User.model.User;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


import java.net.URI;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private  final UserMapper userMapper;

    /**
     * This method verifies username and password and on success returns a Jwt Authorization token
     * @param loginRequest
     * @return
     */
    @PostMapping("/token")
    public String authenticateAndGetToken(@RequestBody LoginRequest loginRequest) {
        return userService.authenticateAndGetToken(loginRequest);
    }

    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserDto>> findAll() {
        return ResponseEntity
                .ok(userService.findAll());
    }

    /**
     * Adding new User, if Username or Email already exists an exception is thrown otherwis persisted in the database
     * @param signupRequest
     * @return
     */
    @PostMapping("/register")
    public String addUser(@RequestBody SignupRequest signupRequest){
        User user = userMapper.signupRequestToUser(signupRequest);
        try {
            userService.registerUser(user);
        } catch (UsernameAlreadyExistsException e) {
            throw new UsernameAlreadyExistsException(e.getMessage());
        } catch (EmailAlreadyExistsException e){
            throw new EmailAlreadyExistsException(e.getMessage());
        }

        return "User created successfully";
    }

    @PutMapping("/updateUser")
    public User updateUser(@RequestBody User user){return userRepository.saveAndFlush(user);
    }

    @GetMapping(value = "/users/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDto findUser(@PathVariable String username) {
        return userService.findByUsername(username);
    }
    /**
     * Get user id using username
     * @param username
     * @return
     */
    @GetMapping("/getid/{username}")
    public long getUserId(@PathVariable String username){
        Optional<User> user = userRepository.findByUsername(username);
        if(user.isEmpty()){
            throw new NotFoundException();
        }
        return user.get().getId();
    }

    @GetMapping("/validate/{token}")
    public String validateToken(@PathVariable String token){
        jwtTokenProvider.validateToken(token);
        return "Token is valid";
    }

    @GetMapping(value = "/user/{userid}")
    public ResponseEntity<UserDto>  findByUserId(@PathVariable long userid) {
        UserDto userDto = userService.findByUserId(userid);
            return ResponseEntity.ok(userDto);

    }

}
