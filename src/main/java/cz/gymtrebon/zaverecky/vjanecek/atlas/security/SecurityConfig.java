package cz.gymtrebon.zaverecky.vjanecek.atlas.security;

import cz.gymtrebon.zaverecky.vjanecek.atlas.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@RequiredArgsConstructor
    @EnableGlobalMethodSecurity(prePostEnabled = true)
    public class SecurityConfig extends WebSecurityConfigurerAdapter {

        private final CustomUserDetailsService userDetailsService;

        private final JwtFilter jwtFilter;

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

            http.cors().and()
                    .authorizeRequests()
                    .antMatchers("/registration","/api/userinfo", "/api/login", "/error/403","/home").permitAll()
                    .and()
                    .formLogin()
                    .loginPage("/login")
                    .defaultSuccessUrl("/loginUpdate", true)
                    .permitAll()
                    .and()
                    .logout()
                    .permitAll()
                    .and()
                    .csrf().disable();

        }
        @Override
        @Bean
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

    }

