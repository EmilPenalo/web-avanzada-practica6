package webavanzada.pucmm.practica6.configurations;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, MvcRequestMatcher.Builder mvc) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests( authorization -> authorization
                        .requestMatchers(mvc.pattern("/")).permitAll()
                        .requestMatchers(mvc.pattern("/login")).permitAll()
                        .requestMatchers(mvc.pattern("/logout")).permitAll()
                        .requestMatchers(mvc.pattern("/users/**")).hasAnyRole("ADMIN")
                        .requestMatchers(mvc.pattern("/access/**")).permitAll()

                        .requestMatchers(AntPathRequestMatcher.antMatcher("/css/**"), AntPathRequestMatcher.antMatcher("/js/**"), AntPathRequestMatcher.antMatcher("/webjars/**"), AntPathRequestMatcher.antMatcher("*.html")).permitAll()

                        .anyRequest().authenticated()
                )

                .formLogin((form) -> form
                        .loginPage("/login")
                        .failureUrl("/login?error")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout((logout) -> logout
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .logoutRequestMatcher(AntPathRequestMatcher.antMatcher("/logout"))
                        .permitAll())

                .build();
    }
}
