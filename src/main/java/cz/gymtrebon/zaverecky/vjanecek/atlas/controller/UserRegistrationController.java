package cz.gymtrebon.zaverecky.vjanecek.atlas.controller;

import cz.gymtrebon.zaverecky.vjanecek.atlas.security.JWTRequest;
import cz.gymtrebon.zaverecky.vjanecek.atlas.security.JWTResponse;
import cz.gymtrebon.zaverecky.vjanecek.atlas.security.JWTUtility;
import cz.gymtrebon.zaverecky.vjanecek.atlas.security.JwtFilter;
import cz.gymtrebon.zaverecky.vjanecek.atlas.service.CustomUserDetails;
import cz.gymtrebon.zaverecky.vjanecek.atlas.service.CustomUserDetaisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class UserRegistrationController {

    @Autowired
    private JWTUtility jwtUtility;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private CustomUserDetaisService userService;

    @PostMapping("/login")
    public JWTResponse loginUser(@RequestBody JWTRequest jwtRequest) throws Exception {

        log.info("Login: " + jwtRequest.getUsername());
        log.info("Password: " + jwtRequest.getPassword());

        //netusim na co to je, ale asi je to dulezite, aby dal nebyly errory
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            jwtRequest.getUsername(),
                            jwtRequest.getPassword()
                    )
            );

        }catch (BadCredentialsException e){
            return new JWTResponse();
            //throw new Exception("AUTH FAILED",e);
        }

        final CustomUserDetails userDetails = userService.loadUserByUsername(jwtRequest.getUsername());

        final String token =
                jwtUtility.generateToken(userDetails);

        return new JWTResponse(token);

    }

    @GetMapping("/userinfo")
    public ResponseEntity userinfo(@RequestHeader (name="Authorization") String token) throws Exception {
        CustomUserDetails userDetails = userService.loadUserByUsername(jwtUtility.getUsernameFromToken(token));
       if (validate(token, userDetails, "EDITOR")){
           ResponseEntity.ok("User has EDITOR role");
       }
       return noAuthError();
    }
    private ResponseEntity noAuthError(){
        return ResponseEntity.ok("User does not have EDITOR role");
    }
    private boolean validate(String token, CustomUserDetails userDetails, String role) {
        if (jwtUtility.validateToken(token, userDetails) ){
            Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>) userDetails.getAuthorities();
            // check if the user has the role authority
            boolean hasEditorRole = authorities.stream()
                    .anyMatch(auth -> auth.getAuthority().equals(role));
            if (hasEditorRole) {
                return true;
            }
        }
    return false;
    }
    private boolean validate(String token, String role) {
        CustomUserDetails userDetails = userService.loadUserByUsername(jwtUtility.getUsernameFromToken(token));
       return validate(token, userDetails, role);
    }


}