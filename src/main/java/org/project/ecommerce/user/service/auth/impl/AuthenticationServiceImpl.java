package org.project.ecommerce.user.service.auth.impl;

import org.project.ecommerce.user.dto.auth.request.AuthenticationRequest;
import org.project.ecommerce.user.dto.auth.response.AuthenticationResponse;
import org.project.ecommerce.user.security.jwt.JwtService;
import org.project.ecommerce.user.service.auth.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Override
    public AuthenticationResponse login(AuthenticationRequest request) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        final String username = userDetails.getUsername();
        final String accessToken = jwtService.generateAccessToken(username);
        final String refreshToken = jwtService.generateRefreshToken(username);
        final String tokenType = "Bearer";

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType(tokenType)
                .build();
    }

    @Override
    public AuthenticationResponse refresh(AuthenticationRequest request) {
        return null;
    }
}
