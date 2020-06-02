/*
 * 2020.04.25  - Created
 */
package mx4.springboot.security.filters;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mx4.springboot.model.security.SpringSecurityUserExModel;
import mx4.springboot.security.utils.TokenManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import mx4.springboot.persistence.TokenRepository;

/**
 *
 * @author Ilamah, Osho
 */
@Component
public class JWTRequestFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization", BEARER = "Bearer ";

    @Autowired
    private TokenRepository jwtBlacklistRepo;

    @Autowired
    private TokenManager tokenManager;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain fc) throws ServletException, IOException {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            final String authorizationHeader = req.getHeader(AUTHORIZATION_HEADER);
            if (authorizationHeader != null && authorizationHeader.startsWith(BEARER)) {
                final String token = authorizationHeader.substring(7);
                // if the token is expired or otherwise invalid, the next line will fail/throw exception and/return null, successful execution indicates that the token is unexpired
                SpringSecurityUserExModel tokenUser = tokenManager.getSpringSecurityUserExModelFormToken(token);
                if (tokenUser != null) {
                    //ordinarilly, we do not need to hit the DB to further authenticate the user as the above indicates that the token is geniune and not yet expired.
                    //but the user may have signed out and the token presented by an malevolent actor - to avod this, we check if the token is blacklisted (i.e. signout but yet to expire. 
                    //in this implementation, we are using mondoDB TTL collection, but it appears redis/memcache would be more scalable for a blacklist
                    //
                    // https://stackoverflow.com/questions/21978658/invalidating-json-web-tokens
                    // https://dzone.com/articles/stateless-authentication-with-json-web-tokens
                    //
                    if (jwtBlacklistRepo.findByValueAndType(token, "BLACKLISTED").isPresent() == false) {
                        //this token has not been blacklisted. It is authentic, unexpired and unblacklisted
                        //
                        ////No need to hit Database -- if we want to maintain server side sessions though, here might be an opportunity to load the associated user session from DB keyed by the jwt token
                        //
                        //    SpringSecurityUserExModel dbUser = userDetailsService.loadUserByUsername(tokenUser.getUsername());
                        //    if (tokenManager.validateToken(token, tokenUser, dbUser)) {
                        //        UsernamePasswordAuthenticationToken pat = new UsernamePasswordAuthenticationToken(dbUser, null, dbUser.getAuthorities());
                        //        pat.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                        //        SecurityContextHolder.getContext().setAuthentication(pat);
                        //    }
                        //
                        //
                        //add authenticated user credentials to security context
                        UsernamePasswordAuthenticationToken pat = new UsernamePasswordAuthenticationToken(tokenUser, null, tokenUser.getAuthorities());
                        pat.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                        SecurityContextHolder.getContext().setAuthentication(pat);
                    }
                }
            }
        }
        fc.doFilter(req, res);
    }
}
