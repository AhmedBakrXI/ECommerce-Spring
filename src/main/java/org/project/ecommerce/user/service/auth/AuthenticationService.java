package org.project.ecommerce.user.service.auth;

import org.project.ecommerce.user.dto.auth.request.AuthenticationRequest;
import org.project.ecommerce.user.dto.auth.request.RefreshRequest;
import org.project.ecommerce.user.dto.auth.response.AuthenticationResponse;

public interface AuthenticationService {
    AuthenticationResponse login(AuthenticationRequest request);
    AuthenticationResponse refresh(RefreshRequest request);
}
