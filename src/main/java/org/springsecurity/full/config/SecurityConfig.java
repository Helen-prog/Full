package org.springsecurity.full.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    private AuthSuccess authSuccess = new AuthSuccess();

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();  // Чтобы не хранить пароль в «сыром» виде он предварительно хэшируется с помощью bCryptPasswordEncoder
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService());
        daoAuthenticationProvider.setPasswordEncoder(bCryptPasswordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(
                        request -> request
                                .requestMatchers("/**", "/register", "/signin", "/saveUser", "/item").permitAll()
                                .requestMatchers("/user/**").authenticated()
                                .requestMatchers("/admin/**").authenticated()


//                      /** означает любой url
//                      /user/** означает любые, в том числе вложенные url, начинающиеся с /user

//                      .authenticated() - разрешает доступ всем аутентифицированным пользователям
                )
                .csrf(csrf -> csrf.disable())
                .formLogin((form) -> form
                        .loginPage("/signin")
                        .loginProcessingUrl("/userLogin")
                        .defaultSuccessUrl("/")
                        .successHandler(authSuccess)
                        .permitAll()
                );
//                .logout((logout) -> logout
//                        .logoutUrl("/logout")
//                        .logoutSuccessUrl("/login?logout")
//                        .invalidateHttpSession(true)
//                        .deleteCookies("JSESSIONID"));
        return http.build();
    }

    @Bean
    WebSecurityCustomizer configureWebSecurity () {
        return (web) -> web.ignoring().requestMatchers("/img/**", "/css/**");
    }
}
