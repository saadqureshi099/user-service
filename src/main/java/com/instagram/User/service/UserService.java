package com.instagram.User.service;

import com.instagram.User.dto.LoginRequest;
import com.instagram.User.dto.UserDto;
import com.instagram.User.exception.EmailAlreadyExistsException;
import com.instagram.User.exception.InvalidAccessException;
import com.instagram.User.exception.ResourceNotFoundException;
import com.instagram.User.exception.UsernameAlreadyExistsException;
import com.instagram.User.mapper.UserMapper;
import com.instagram.User.model.User;
import com.instagram.User.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserMapper userMapper;

    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserMapper userMapper) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userMapper = userMapper;
    }


    public List<UserDto> findAll(){
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(userMapper::userToUserDto)
                .toList();
    }
    public UserDto findByUsername(String username){
        Optional<User> userOptional = userRepository.findByUsername(username);
        return userOptional.map(userMapper::userToUserDto).orElse(null);
    }

    /**
     * Registers a new user, throws exception if email or username already exists
     * @param user
     * @return
     * @throws UsernameAlreadyExistsException
     * @throws EmailAlreadyExistsException
     */
    public User registerUser(User user) throws UsernameAlreadyExistsException, EmailAlreadyExistsException{
        if(Boolean.TRUE.equals(userRepository.existsByUsername(user.getUsername()))){
           throw new UsernameAlreadyExistsException("username Already Exists");
        }
        if(Boolean.TRUE.equals(userRepository.existsByEmail(user.getEmail()))){
            throw new EmailAlreadyExistsException("Email already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public UserDto findByUserId(long userid){
        User user = userRepository.findById(userid)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userid));
        return userMapper.userToUserDto(user);
    }

    /**
     * Validates username and password and on success returns a JWT authentication token.
     * @param loginRequest
     * @return
     */
    public String authenticateAndGetToken(LoginRequest loginRequest) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        if (authenticate.isAuthenticated()) {
            return jwtTokenProvider.generateToken(loginRequest.getUsername());
        } else {
            throw new InvalidAccessException("invalid access");
        }
    }


}
