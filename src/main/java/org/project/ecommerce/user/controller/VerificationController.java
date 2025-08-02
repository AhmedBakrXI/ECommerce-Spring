package org.project.ecommerce.user.controller;

import org.project.ecommerce.user.service.verification.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
@RequestMapping("/api/v1/verify")
public class VerificationController {
    private final Logger logger = Logger.getLogger(VerificationController.class.getName());

    @Autowired
    private VerificationService verificationService;

    @GetMapping("/by-token")
    public ResponseEntity<Boolean> requestVerification(@RequestParam("token") String token) {
        boolean isVerified = verificationService.verify(token);
        if (isVerified) {
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/request-new-token")
    public ResponseEntity<Boolean> sendVerificationMail(@RequestParam("email") String email) {
        try {
            String token = verificationService.requestNewToken(email);
            if (token != null) {
                verificationService.sendTokenToUser(email, token);
                return ResponseEntity.ok(true);
            }
        } catch (Exception e) {
            logger.warning(e.getMessage());
        }
        return ResponseEntity.notFound().build();
    }
}
