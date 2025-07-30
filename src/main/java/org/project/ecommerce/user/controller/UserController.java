package org.project.ecommerce.user.controller;

import org.project.ecommerce.user.dto.CreateAccountRequest;
import org.project.ecommerce.user.dto.auth.request.AuthenticationRequest;
import org.project.ecommerce.user.dto.UpgradeRoleRequest;
import org.project.ecommerce.user.dto.UserResponseDto;
import org.project.ecommerce.user.dto.auth.request.RefreshRequest;
import org.project.ecommerce.user.dto.auth.response.AuthenticationResponse;
import org.project.ecommerce.user.enums.UserResponseCode;
import org.project.ecommerce.user.exceptions.UsernameExistsException;
import org.project.ecommerce.user.model.UserModel;
import org.project.ecommerce.user.service.UserAccountManager;
import org.project.ecommerce.user.service.auth.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    @Autowired
    private UserAccountManager userAccountManager;

    @Autowired
    private AuthenticationService authenticationService;

    @PutMapping("/upgrade-role")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<UserResponseDto> upgradeUserRole(@RequestBody UpgradeRoleRequest request) {
        Optional<UserModel> userModel = userAccountManager
                .upgradeUserRole(request.getUsername(), request.getUserRole());

        if (userModel.isPresent()) {
            UserResponseDto userResponseDto = UserResponseDto.builder()
                    .username(userModel.get().getUsername())
                    .userResponseCode(UserResponseCode.SUCCESS)
                    .build();
            return ResponseEntity.ok(userResponseDto);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                UserResponseDto.builder()
                        .username(request.getUsername())
                        .userResponseCode(UserResponseCode.USER_NOT_FOUND)
                        .build()
        );
    }


    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> createUser(@RequestBody CreateAccountRequest request) {
        try {
            userAccountManager.createUser(request);
            return ResponseEntity.ok(
                    UserResponseDto.builder()
                            .username(request.getUsername())
                            .userResponseCode(UserResponseCode.SUCCESS)
                            .build()
            );
        } catch (UsernameExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    UserResponseDto.builder()
                            .username(request.getUsername())
                            .userResponseCode(UserResponseCode.USER_ALREADY_EXISTS)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    UserResponseDto.builder()
                            .username(request.getUsername())
                            .build()
            );
        }
    }

    @PostMapping("/auth")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        try {
            return ResponseEntity.ok(authenticationService.login(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refresh(@RequestBody RefreshRequest request) {
        try {
            return ResponseEntity.ok(authenticationService.refresh(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
