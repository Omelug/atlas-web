package cz.gymtrebon.zaverecky.vjanecek.atlas.security;

import cz.gymtrebon.zaverecky.vjanecek.atlas.service.CustomUserDetaisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
    @EnableGlobalMethodSecurity(prePostEnabled = true)
    public class SecurityConfig extends WebSecurityConfigurerAdapter {

        @Autowired
        CustomUserDetaisService userDetailsService;

        @Autowired
        private JwtFilter jwtFilter;
        //private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(userDetailsService);
        }
        /*@Bean
        SecurityFilterChain apiSecurityFilter(HttpSecurity http) throws Exception {
            SecurityFilterChain httpsecurity =
                    http.requestMatcher("/api/**", "/app/**")
                            .authorizeRequests(auth ->{
                        auth.anyRequest().authenticated();
                    })
                    .sessionManagement(session ->{
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                    })
                    .httpBasic(Customizer.withDefaults())
                    .build();

            return httpsecurity;
        }*/

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

            http.cors().and().csrf().disable()
                    .authorizeRequests()
                    .antMatchers("/api/userinfo", "/api/login", "/styly.css", "/error/403").permitAll()
                    .and()
                    .formLogin()
                    .loginPage("/login")
                    .defaultSuccessUrl("/home", true)
                    .permitAll()
                    .and()
                    .logout()
                    .permitAll();

        }
        @Override
        @Bean
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }
        @Bean
        public PasswordEncoder passwordEncoder(){
        	return NoOpPasswordEncoder.getInstance();
        }

    }

