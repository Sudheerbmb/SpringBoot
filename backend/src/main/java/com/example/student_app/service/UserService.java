package com.example.student_app.service;

import com.example.student_app.model.User;
import com.example.student_app.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    public User createUser(String username, String email, String password, User.UserRole role) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists: " + username);
        }

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists: " + email);
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);

        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(Long userId, String email, String firstName, String lastName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        if (email != null && !email.equals(user.getEmail())) {
            if (userRepository.existsByEmail(email)) {
                throw new RuntimeException("Email already exists: " + email);
            }
            user.setEmail(email);
        }

        if (firstName != null) {
            user.setFirstName(firstName);
        }

        if (lastName != null) {
            user.setLastName(lastName);
        }

        return userRepository.save(user);
    }

    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Verify old password (you would need to implement password verification)
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        userRepository.deleteById(userId);
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }
}
