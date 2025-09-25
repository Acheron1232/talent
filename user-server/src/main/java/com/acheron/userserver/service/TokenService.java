package com.acheron.userserver.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.acheron.userserver.entity.Token;
import com.acheron.userserver.entity.User;
import com.acheron.userserver.repo.TokenRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepository tokenRepository;

    public Token generateConfirmationToken(User user) {
        Token token = new Token(null, UUID.randomUUID().toString(), user, LocalDateTime.now().plusHours(24), Token.TokenStatus.ACTIVE, Token.TokenType.CONFIRM);
        log.info("Generating confirmation token for {}", user.getUsername());
        return tokenRepository.save(token);
    }

    public Optional<Token> getToken(String token) {
        return tokenRepository.findByToken(token);
    }

    public Token generateResetToken(User user) {
        Token token = new Token(null, UUID.randomUUID().toString(), user, LocalDateTime.now().plusHours(24), Token.TokenStatus.ACTIVE, Token.TokenType.RESET);
        log.info("Generating reset token for {}", user.getUsername());
        return tokenRepository.save(token);
    }

    public void delete(Token token) {
        tokenRepository.delete(token);
    }
}
