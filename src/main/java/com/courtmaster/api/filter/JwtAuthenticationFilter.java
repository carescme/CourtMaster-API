package com.courtmaster.api.filter;

import com.courtmaster.api.model.Usuario;
import com.courtmaster.api.repository.UsuarioRepository;
import com.courtmaster.api.service.JwtService;
import com.courtmaster.api.exception.BadRequestException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        
        try {
            userEmail = jwtService.extraerUsername(jwt);
            
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (jwtService.esTokenValido(jwt, userEmail)) {
                    Usuario usuario = usuarioRepository.findByEmail(userEmail).orElse(null);
                    
                    if (usuario == null) {
                        lanzarErrorJson(response, "El usuario asociado al token ya no existe.", HttpStatus.UNAUTHORIZED);
                        return;
                    }

                    List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name())
                    );

                    UserDetails userDetails = new User(userEmail, "", authorities);

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            
            filterChain.doFilter(request, response);

        } catch (BadRequestException e) {
            logger.error("Error de autenticación JWT: " + e.getMessage());
            lanzarErrorJson(response, e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            logger.error("Fallo imprevisto en el filtro de seguridad: " + e.getMessage());
            lanzarErrorJson(response, "No se pudo procesar la autenticación de seguridad.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void lanzarErrorJson(HttpServletResponse response, String mensaje, HttpStatus status) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> cuerpoError = new HashMap<>();
        cuerpoError.put("timestamp", LocalDateTime.now().toString());
        cuerpoError.put("status", status.value());
        cuerpoError.put("error", status.getReasonPhrase());
        cuerpoError.put("mensaje", mensaje);

        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(cuerpoError));
    }
}