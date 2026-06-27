package com.fiap.tech_challenge_backend.acesso.application.ports;

import java.util.Map;

public interface TokenGenerator {

    String generateToken(String subject, Map<String, Object> claims);
}
