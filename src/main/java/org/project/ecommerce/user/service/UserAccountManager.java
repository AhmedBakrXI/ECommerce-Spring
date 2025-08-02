package org.project.ecommerce.user.service;

import org.project.ecommerce.user.dto.ChangePasswordRequest;
import org.project.ecommerce.user.dto.CreateAccountRequest;
import org.project.ecommerce.user.enums.UserRole;
import org.project.ecommerce.user.exceptions.UsernameExistsException;
import org.project.ecommerce.user.model.UserModel;
import org.project.ecommerce.user.repository.UserModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserAccountManager implements UserDetailsManager {
    @Autowired
    private UserModelRepository userModelRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public UserModel createUser(CreateAccountRequest request) {
        UserModel userModel = UserModel.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .isEnabled(false)
                .role(UserRole.CUSTOMER)
                .build();
        createUser(userModel);
        return userModel;
    }

    @Override
    public void createUser(UserDetails user) {
        if (userModelRepository.existsByUsername(user.getUsername())) {
            throw new UsernameExistsException("User already exists with username: " + user.getUsername());
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

    public void changePassword(ChangePasswordRequest changePasswordRequest) {
        if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match.");
        }
        final UserModel userModel = userModelRepository.findByUsername(changePasswordRequest.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + changePasswordRequest.getUsername()));

        // Current Password Validation
        final String currentPasswordEncoded = passwordEncoder.encode(changePasswordRequest.getCurrentPassword());
        if (!passwordEncoder.matches(currentPasswordEncoded, userModel.getPassword())) {
            throw new IllegalArgumentException("Passwords do not match.");
        }
        userModel.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userModelRepository.save(userModel);
    }

    @Override
    public boolean userExists(String username) {
        return userModelRepository.existsByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userModelRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    public Optional<UserModel> upgradeUserRole(String username, UserRole userRole) {
        Optional<UserModel> optionalUser = userModelRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            UserModel userModel = optionalUser.get();
            userModel.setRole(userRole);
            return Optional.of(userModelRepository.save(userModel));
        }
        return Optional.empty();
    }

    private UserModel convertToUserModel(UserDetails userDetails) {
        return (UserModel) userDetails;
    }

    private UserRole getUserRole(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .findFirst()
                .map(authority -> UserRole.valueOf(authority.getAuthority()))
                .orElse(UserRole.CUSTOMER);
    }
}
