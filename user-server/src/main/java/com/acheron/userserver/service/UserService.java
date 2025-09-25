package com.acheron.userserver.service;

import com.acheron.userserver.dto.UserCreateDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import com.acheron.userserver.entity.User;
import com.acheron.userserver.repo.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService   {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    public Optional<User> findByName(String name) {
        return userRepository.findByUsername(name);
    }



    @Transactional
    public void save(UserCreateDto user) {
        if (user.getPassword() != null) {
            user.setPassword(user.getPassword());
        }
        userRepository.save(new User(null, user.getEmail(), user.getUsername(), user.getDisplayName(), null,user.getImage(), user.isEmailVerified(), User.AuthMethod.valueOf(user.getAuthMethod()), User.Role.valueOf(user.getRole()), user.getPassword()));
    }

    @Transactional
    public void update(User user) {
        userRepository.save(user);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

}
