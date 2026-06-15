package com.courtmaster.api.service;

import com.courtmaster.api.exception.BadRequestException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${courtmaster.jwt.secret}")
    private String secretKey;

    @Value("${courtmaster.jwt.expiration}")
    private long expirationTime;

    private Key getSigningKey(){
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String generarToken(String username){
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    public String extraerUsername(String token){
        return extraerClaim(token, Claims::getSubject);
    }

    public boolean esTokenValido(String token, String username){
        final String tokenUsername = extraerUsername(token);
        return (tokenUsername.equals(username) && !esTokenExpirado(token));
    }

    private boolean esTokenExpirado(String token){
        return extraerClaim(token, Claims::getExpiration).before(new Date());
    }

    public <T> T extraerClaim(String token, Function<Claims, T> claimsResolver){
        try {
            final Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
            return claimsResolver.apply(claims);
            
        } catch (ExpiredJwtException ex) {
            throw new BadRequestException("El token JWT ha expirado.");
        } catch (SignatureException ex) {
            throw new BadRequestException("Firma del token inválida o manipulada.");
        } catch (Exception ex) {
            throw new BadRequestException("Token JWT inválido o mal estructurado.");
        }
    }
}