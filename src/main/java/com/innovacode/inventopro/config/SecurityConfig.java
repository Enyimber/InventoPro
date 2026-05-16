package com.innovacode.inventopro.config;

import com.innovacode.inventopro.security.UsuarioDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Jerarquía: ADMIN hereda TODOS los demás roles, por lo que puede hacer todo.
     */
    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.fromHierarchy(
              "ROLE_ADMIN > ROLE_GERENTE\n"
            + "ROLE_GERENTE > ROLE_ALMACENISTA\n"
            + "ROLE_GERENTE > ROLE_AUDITOR\n"
            + "ROLE_ALMACENISTA > ROLE_VENDEDOR\n"
            + "ROLE_ALMACENISTA > ROLE_COMPRADOR\n"
            + "ROLE_VENDEDOR > ROLE_CONSULTOR\n"
            + "ROLE_COMPRADOR > ROLE_CONSULTOR\n"
            + "ROLE_AUDITOR > ROLE_CONSULTOR"
        );
    }

    @Bean
    static MethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy rh) {
        DefaultMethodSecurityExpressionHandler h = new DefaultMethodSecurityExpressionHandler();
        h.setRoleHierarchy(rh);
        return h;
    }

    @Bean
    public DaoAuthenticationProvider authProvider(UsuarioDetailsService uds, PasswordEncoder pe) {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(uds);
        p.setPasswordEncoder(pe);
        return p;
    }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, RoleHierarchy rh) throws Exception {
        DefaultWebSecurityExpressionHandler webHandler = new DefaultWebSecurityExpressionHandler();
        webHandler.setRoleHierarchy(rh);

        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/css/**", "/js/**",
                                 "/api/auth/**",
                                 "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/usuarios/**").hasRole("ADMIN")
                .requestMatchers("/proveedores/**").hasRole("ADMIN")
                .requestMatchers("/movimientos/nuevo", "/movimientos").hasRole("CONSULTOR") // jerarquía: todos los superiores acceden
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .permitAll())
            .logout(l -> l.logoutSuccessUrl("/login?logout").permitAll());
        return http.build();
    }
}
