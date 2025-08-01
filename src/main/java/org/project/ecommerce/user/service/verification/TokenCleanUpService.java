package org.project.ecommerce.user.service.verification;

import org.project.ecommerce.user.model.VerificationToken;
import org.project.ecommerce.user.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.time.LocalDateTime.now;

@Service
public class TokenCleanUpService {
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void cleanUpVerificationTokens() {
        List<VerificationToken> expiredTokens = verificationTokenRepository.findAll().stream()
                .filter((token) -> token.getExpiryDate().isBefore(now()))
                .toList();
        verificationTokenRepository.deleteAll(expiredTokens);
    }
}
