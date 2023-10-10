package cz.gymtrebon.zaverecky.vjanecek.atlas.controller;

import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.LoggerLine;
import cz.gymtrebon.zaverecky.vjanecek.atlas.log.LogTyp;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.CustomLoggerRepository;
import cz.gymtrebon.zaverecky.vjanecek.atlas.security.JWTRequest;
import cz.gymtrebon.zaverecky.vjanecek.atlas.security.JWTResponse;
import cz.gymtrebon.zaverecky.vjanecek.atlas.security.JWTUtility;
import cz.gymtrebon.zaverecky.vjanecek.atlas.service.CustomUserDetails;
import cz.gymtrebon.zaverecky.vjanecek.atlas.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class UserRegistrationController {

    private final JWTUtility jwtUtility;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userService;
    private final CustomLoggerRepository customLoggerRepository;

    @PostMapping("/login")
    public JWTResponse loginUser(@RequestBody JWTRequest jwtRequest) {
        log.info("Login: " + jwtRequest.getName());
        log.info("Password: " + jwtRequest.getPassword());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            jwtRequest.getName(),
                            jwtRequest.getPassword()
                    )
            );

        }catch (BadCredentialsException e){
            customLoggerRepository.save(new LoggerLine(LogTyp.ERROR, jwtRequest.getName(), "User failed to login with password " + jwtRequest.getPassword()));
            return new JWTResponse();
        }

        final CustomUserDetails userDetails = userService.loadUserByUsername(jwtRequest.getName());
        final String token = jwtUtility.generateToken(userDetails);
        return new JWTResponse(token);
    }

    @GetMapping("/userinfo")
    public ResponseEntity<String> userinfo(@RequestHeader (name="Authorization") String token) {
        CustomUserDetails userDetails = userService.loadUserByUsername(jwtUtility.getUsernameFromToken(token));
       if (validate(token, userDetails, "EDITOR") || validate(token, userDetails, "ADMIN")){
           ResponseEntity.ok("User has EDITOR role");
       }
       return noAuthError();
    }
    private ResponseEntity<String> noAuthError(){
        return ResponseEntity.ok("User does not have EDITOR role");
    }
    private boolean validate(String token, CustomUserDetails userDetails, String role) {
        if (jwtUtility.validateToken(token, userDetails) ){
            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
            return authorities.stream().anyMatch(auth -> auth.getAuthority().equals(role));
        }
    return false;
    }
}