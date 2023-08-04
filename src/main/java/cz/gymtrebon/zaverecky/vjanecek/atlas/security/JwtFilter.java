package cz.gymtrebon.zaverecky.vjanecek.atlas.security;

import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.User;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.UDRlinkRepository;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.UserRepository;
import cz.gymtrebon.zaverecky.vjanecek.atlas.service.CustomUserDetails;
import cz.gymtrebon.zaverecky.vjanecek.atlas.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JWTUtility jwtUtility;

    private final UserRepository userRepository;
    private final UDRlinkRepository udrlinkRepository;
    private final CustomUserDetailsService userDetailsService;
    private final CustomUserDetailsService userService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        logger.info("Request: " + request);
        String authorization = request.getHeader("Authorization");
        logger.info("Authorization: " + authorization);

        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);
            String username = jwtUtility.getUsernameFromToken(token);
            logger.info("Try authenticate user: " + username);

            CustomUserDetails userDetails = userService.loadUserByUsername(username);

            if (username != null && jwtUtility.validateToken(token, userDetails)) {
                String databaseName = request.getParameter("database");
                logger.info("Attempting to use database " + databaseName);

                User user = userRepository.findByName(username).orElse(null);
                if (user != null && !udrlinkRepository.findAllByUserNameAndDatabaseName(username, databaseName).isEmpty()) {
                    user.setCurrentDB_name(databaseName);
                    userRepository.save(user);
                    userDetailsService.updateCustomUserDetails(user.getName());
                }

                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }else{
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Access denied");
                return;
            }
        }

        chain.doFilter(request, response);

    }

}
