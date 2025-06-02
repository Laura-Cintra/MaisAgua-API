package br.com.fiap.mais_agua.service;

import br.com.fiap.mais_agua.model.Token;
import br.com.fiap.mais_agua.model.Usuario;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {
    private Instant expiresAt = LocalDateTime.now().plusMinutes(10).toInstant(ZoneOffset.ofHours(-3));

    private Algorithm algorithm = Algorithm.HMAC256("secret");
    public Token createToken(Usuario user
    ){
        var jwt = JWT.create()
                .withSubject(user.getId_usuario().toString())
                .withClaim("email", user.getEmail())
                .withExpiresAt(expiresAt)
                .sign(algorithm);

        return new Token(jwt, user.getEmail());
    }

    public Usuario getUserFromToken(String token){
        var verifiedToken = JWT.require(algorithm).build().verify(token);

        return Usuario.builder()
                .id_usuario(Integer.valueOf(verifiedToken.getSubject()))
                .email(verifiedToken.getClaim("email").toString()).build();
    }
}
