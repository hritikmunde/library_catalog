package edu.iu.p566.library_catalog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
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

    // @Bean
    // public InMemoryUserDetailsManager userDetailsService() {
    //     UserDetails hritik = User.withUsername("hritik")
    //         .password("password")
    //         .roles("USER")
    //         .build();
    //     UserDetails harsh = User.withUsername("harsh")
    //         .password("password")
    //         .roles("USER")
    //         .build();
    //     UserDetails testuser = User.withUsername("testuser")
    //         .password("password")
    //         .roles("USER")
    //         .build();
    //     return new InMemoryUserDetailsManager(hritik, harsh, testuser);
    // }
    
    @Bean
    public InMemoryUserDetailsManager userDetailsService(
        @Value("${ADMIN_USERNAME:}") String adminUser,
        @Value("${ADMIN_PASSWORD:}") String adminPass
    ) {
        var users = new java.util.ArrayList<UserDetails>();
        users.add(User.withUsername("hritik").password("{noop}password").roles("USER").build());
        users.add(User.withUsername("harsh").password("{noop}password").roles("USER").build());
        users.add(User.withUsername("testuser").password("{noop}password").roles("USER").build());

        // Fallback admin for when env vars aren’t set (e.g., on your laptop)
        if (adminUser == null || adminUser.isBlank() || adminPass == null || adminPass.isBlank()) {
            adminUser = "admin";
            adminPass = "admin";
        }
        users.add(User.withUsername(adminUser).password("{noop}" + adminPass).roles("ADMIN").build());

        return new InMemoryUserDetailsManager(users);
    }

    @Bean
    @Order(1)
    public SecurityFilterChain h2ConsoleChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/h2-console/**")               // <- string matcher, no bean lookup
            .authorizeHttpRequests(auth -> auth
               // .anyRequest().hasRole("ADMIN")  
               .anyRequest().permitAll()              // or .permitAll() in dev if you prefer
            )
            .csrf(AbstractHttpConfigurer::disable)               // H2 posts to itself
            .headers(h -> h.frameOptions(f -> f.sameOrigin()))   // frames
            .httpBasic(AbstractHttpConfigurer::disable)          // no auth while testing
            .formLogin(AbstractHttpConfigurer::disable); 
        return http.build();
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
            
            )
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions
                    .sameOrigin()
                )
            );
        return http.build();
    }
}
