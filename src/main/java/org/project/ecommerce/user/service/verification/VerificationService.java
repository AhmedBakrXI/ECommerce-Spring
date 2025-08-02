package org.project.ecommerce.user.service.verification;

import org.project.ecommerce.email.service.EmailService;
import org.project.ecommerce.user.model.UserModel;
import org.project.ecommerce.user.model.VerificationToken;
import org.project.ecommerce.user.repository.UserModelRepository;
import org.project.ecommerce.user.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static java.time.LocalDateTime.now;

@Service
public class VerificationService {
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private UserModelRepository userModelRepository;

    @Autowired
    private EmailService emailService;

    public boolean verify(String token) {
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
        if (verificationToken.isPresent()) {
            verificationTokenRepository.delete(verificationToken.get());
            UserModel user = verificationToken.get().getUser();
            user.setIsEnabled(true);
            userModelRepository.save(user);
            return true;
        }
        return false;
    }

    public String createVerificationToken(String username) {
        Optional<UserModel> existingUser = userModelRepository.findByUsername(username);
        if (existingUser.isEmpty()) {
            return null;
        }
        VerificationToken verificationToken = VerificationToken.builder()
                .user(existingUser.get())
                .token(UUID.randomUUID().toString())
                .createdAt(now())
                .expiryDate(now().plusHours(1))
                .build();
        return verificationTokenRepository.save(verificationToken).getToken();
    }

    public void removeVerificationToken(String token) {
        verificationTokenRepository.deleteByToken(token);
    }

    public String requestNewToken(String userEmail) {
        Optional<UserModel> existingUser = userModelRepository.findByEmail(userEmail);
        // There exists no user by this email
        if (existingUser.isEmpty() || existingUser.get().isEnabled()) {
            return null;
        }

        Optional<VerificationToken> existingToken = verificationTokenRepository.findByUser_Email(userEmail);
        if (existingToken.isEmpty()) {
            // There exists a user with no token
            return existingUser.map(userModel -> createVerificationToken(userModel.getUsername()))
                    .orElse(null);
        }

        // There exists User and Token
        VerificationToken verificationToken = existingToken.get();
        // if token is expired then generate new one else return same one
        if (isTokenExpired(verificationToken)) {
            verificationToken.setToken(UUID.randomUUID().toString());
            return verificationTokenRepository.save(verificationToken).getToken();
        }
        return verificationToken.getToken();
    }

    public void sendTokenToUser(String email, String token) {
        if (email == null || token == null) {
            throw new IllegalArgumentException("Email and token are required");
        }
        String link = String.format("http://localhost:8080/verify?token=%s", token);
        emailService.sendVerificationEmail(email, link);
    }

    private boolean isTokenExpired(VerificationToken token) {
        return token.getExpiryDate().isBefore(now());
    }
}
