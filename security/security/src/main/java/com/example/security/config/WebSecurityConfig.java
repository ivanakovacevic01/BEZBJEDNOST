package com.example.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri("http://localhost:9090/realms/SpringBootDemoKeyCloak/protocol/openid-connect/certs").build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        //grantedAuthoritiesConverter.setAuthoritiesClaimName("realm_access.roles");
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Collection<GrantedAuthority> authorities = new HashSet<>();
            System.out.println("usao "+jwt);

            Collection<GrantedAuthority> grantedAuthorities = grantedAuthoritiesConverter.convert(jwt);
            if (grantedAuthorities != null) {
                authorities.addAll(grantedAuthorities);
                System.out.println("usao "+grantedAuthorities);

            }

            Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
            if (resourceAccess != null && resourceAccess.containsKey("springboot-keycloak-demo-client")) {
                Map<String, Object> clientAccess = (Map<String, Object>) resourceAccess.get("springboot-keycloak-demo-client");
                Collection<String> clientRoles = (Collection<String>) clientAccess.get("roles");
                if (clientRoles != null) {
                    clientRoles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));
                    System.out.println("usao "+clientRoles);
                }
            }

            return authorities;
        });

        return jwtConverter;
    }



    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers("/api/auth/login").permitAll()
                .antMatchers("/api/auth/resetPassword").permitAll()
                .antMatchers("/api/auth/setNewPassword").permitAll()
                .antMatchers("/api/auth/verify").permitAll()
                .antMatchers("/api/auth/passwordlesslogin").permitAll()
                .antMatchers("/api/vpn").permitAll()
                .antMatchers("/api/auth/passwordlessAuth").permitAll()
                .antMatchers("/api/registerRequest").permitAll()
                .antMatchers("/api/auth/regeneratingJwtToken").permitAll()
                .antMatchers("/api/user/activate").permitAll()
                .antMatchers("/api/user/changePassword").permitAll()
                .antMatchers("/api/advertisement/**").authenticated()
                .antMatchers("/api/advertisementRequest/**").authenticated()
                .antMatchers("/api/permission/**").hasAuthority("ROLE_ADMIN")
                .antMatchers("/api/user/employeeProfile/**").authenticated()
                .antMatchers("/api/user/update/**").authenticated()
                .antMatchers("/api/user/adminProfile/**").authenticated()
                .antMatchers("/api/user/clientProfile/**").authenticated()
                .antMatchers("/api/user/creatingUser/**").authenticated()
                .antMatchers("/api/user/employees").authenticated()
                .antMatchers("/api/user/clients").authenticated()
                .antMatchers("/api/user/getAll/**").authenticated()
                .antMatchers("/api/registerRequests").authenticated()
                .antMatchers("/api/registrationRequest/**").authenticated()
                .antMatchers("/api/advertisement/visiting/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .cors().and()
                .csrf().disable();
    }
}
