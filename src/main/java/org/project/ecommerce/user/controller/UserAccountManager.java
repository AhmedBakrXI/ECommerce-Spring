package org.project.ecommerce.user.controller;

import org.project.ecommerce.user.enums.UserRole;
import org.project.ecommerce.user.model.UserModel;
import org.project.ecommerce.user.repository.UserModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

@Service
public class UserAccountManager implements UserDetailsManager {
    @Autowired
    private UserModelRepository userModelRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public void createUser(UserDetails user) {
        if (userModelRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("User already exists with username: " + user.getUsername());
        }
        userModelRepository.save(convertToUserModel(user));
    }

    @Override
    public void updateUser(UserDetails user) {
        if (!userModelRepository.existsByUsername(user.getUsername())) {
            throw new UsernameNotFoundException("User not found with username: " + user.getUsername());
        }
        UserModel userModel = convertToUserModel(user);
        userModel.setPassword(passwordEncoder.encode(user.getPassword()));
        userModel.setRole(getUserRole(user));
        userModelRepository.save(userModel);
    }

    @Override
    public void deleteUser(String username) {
        userModelRepository.findByUsername(username).ifPresent(userModelRepository::delete);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            UserModel userModel = userModelRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
            userModel.setPassword(passwordEncoder.encode(newPassword));
            userModelRepository.save(userModel);
        } else {
            throw new IllegalStateException("No authenticated user found to change password.");
        }
    }

    @Override
    public boolean userExists(String username) {
        return userModelRepository.existsByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserModel userModel = userModelRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return User.withUsername(userModel.getUsername())
                .password(passwordEncoder.encode(userModel.getPassword()))
                .roles(userModel.getRole().getRoleAsString())
                .build();
    }

    private UserModel convertToUserModel(UserDetails userDetails) {
        return UserModel.builder()
                .username(userDetails.getUsername())
                .password(passwordEncoder.encode(userDetails.getPassword()))
                .role(getUserRole(userDetails))
                .build();
    }

    private UserRole getUserRole(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .findFirst()
                .map(authority -> UserRole.valueOf(authority.getAuthority()))
                .orElse(UserRole.CUSTOMER);
    }
}
