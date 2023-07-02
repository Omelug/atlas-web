package cz.gymtrebon.zaverecky.vjanecek.atlas.security;

import cz.gymtrebon.zaverecky.vjanecek.atlas.service.CustomUserDetails;
import cz.gymtrebon.zaverecky.vjanecek.atlas.service.CustomUserDetaisService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JWTUtility jwtUtility;

    @Autowired
    private CustomUserDetaisService userService;
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
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }else{
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Access denied");;
                return;
            }
        }

        chain.doFilter(request, response);
        /*String authorization = httpServletRequest.getHeader("Authorization");
        logger.info("Authorization: " + authorization);
        String token = null;
        String username = null;

        if(null != authorization && authorization.startsWith("Bearer ")) {
            token = authorization.substring(7);
            username = jwtUtility.getUsernameFromToken(token);
            logger.info("Authenticated user: " + username);

            if(null != username) {
                CustomUserDetails userDetails = userService.loadUserByUsername(username);

                if(jwtUtility.validateToken(token,userDetails)) {
                    logger.info("Authenticated user: " + username);

                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                            = new UsernamePasswordAuthenticationToken(userDetails,
                            null, userDetails.getAuthorities());

                    usernamePasswordAuthenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(httpServletRequest)
                    );
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    filterChain.doFilter(httpServletRequest, httpServletResponse);
                    return;
                }
            }
            handleUnauthorizedRequest(httpServletResponse);
            return;
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);*/
    }

    private void handleUnauthorizedRequest(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Unauthorized");
        // You can customize the response further, such as setting headers or returning JSON error message
    }
}
