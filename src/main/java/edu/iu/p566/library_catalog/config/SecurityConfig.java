package edu.iu.p566.library_catalog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

@Configuration
public class SecurityConfig {

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails hritik = User.withUsername("hritik")
            .password("password")
            .roles("USER")
            .build();
        UserDetails harsh = User.withUsername("harsh")
            .password("password")
            .roles("USER")
            .build();
        UserDetails testuser = User.withUsername("testuser")
            .password("password")
            .roles("USER")
            .build();
        return new InMemoryUserDetailsManager(hritik, harsh, testuser);
    }

    @Bean
    @SuppressWarnings("deprecation")
    public static NoOpPasswordEncoder passwordEncoder() {
        return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        AuthenticationSuccessHandler successHandler = (request, response, authentication) -> {
            String target = request.getParameter("continue");
            if (target != null && !target.isBlank()) {
                SimpleUrlAuthenticationSuccessHandler simple = new SimpleUrlAuthenticationSuccessHandler("/");
                simple.setTargetUrlParameter("continue");
                simple.setAlwaysUseDefaultTargetUrl(false);
                simple.onAuthenticationSuccess(request, response, authentication);
            } else {
                SavedRequestAwareAuthenticationSuccessHandler saved = new SavedRequestAwareAuthenticationSuccessHandler();
                saved.setDefaultTargetUrl("/"); // fallback if no saved request
                saved.onAuthenticationSuccess(request, response, authentication);
            }
        };
        
        http
            .authorizeHttpRequests((requests) -> requests
                .requestMatchers(org.springframework.http.HttpMethod.GET,  "/books/*/checkout").authenticated()
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/books/*/rent").authenticated()
                .requestMatchers("/", "/search", "/login", "/css/**", "/images/**", "/error", "/favicon.ico", "/js/**", "/webjars/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/books/*").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin((form) -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .failureUrl("/login?error=true")
                .successHandler(successHandler)
                .permitAll()
            )
            .logout((logout) -> logout
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/login", "/logout")    
            
            );
        return http.build();
    }
}
