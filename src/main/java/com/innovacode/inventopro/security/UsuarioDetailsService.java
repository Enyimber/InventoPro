package com.innovacode.inventopro.security;

import com.innovacode.inventopro.model.Usuario;
import com.innovacode.inventopro.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioDetailsService implements UserDetailsService {
    private final UsuarioRepository repo;

    @Override
    public UserDetails loadUserByUsername(String username) {
        Usuario u = repo.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
        return new User(u.getUsername(), u.getPassword(),
            u.getRoles().stream()
             .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
             .collect(Collectors.toSet()));
    }
}
