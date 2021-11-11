package io.solar.security;

import io.solar.entity.User;
import io.solar.service.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Optional;

@Component
public class JwtFilter extends GenericFilterBean {

    private static final String AUTH_TOKEN = "auth_token";

    private JwtProvider jwtProvider;

    public JwtFilter(JwtProvider jwtProvider, UserService userService) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String token = request.getHeader(AUTH_TOKEN);
        if (token != null) {
            Optional<User> user =  jwtProvider.verifyToken(token);
            if(user.isPresent()) {
                if(jwtProvider.hasTooShortExpiration(token)) {
                    //TODO Here we should decide what to do
                }
                UserDetails userDetails = User.retrieveUserDetails(user.get());
                Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
