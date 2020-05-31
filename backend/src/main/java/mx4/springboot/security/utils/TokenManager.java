/*
 * 2020.04.25  - Created
 * 2020.05.31  - Standardized role claims
 */
package mx4.springboot.security.utils;

import mx4.springboot.model.security.SpringSecurityUserExModel;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import mx4.springboot.model.User;
import mx4.springboot.services.DataRepositoryUserDetailsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

/**
 *
 * @author Ilamah, Osho
 */
@Component
public class TokenManager {

    @Value("${app.jwt.secret}")
    private String SECRET;

    @Value("${app.jwt.expiration.millis}")
    private int JWT_EXPIRATION_MILLISECONDS;

    public SpringSecurityUserExModel getSpringSecurityUserExModelFormToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token) // if invalid > SignatureException MalformedJwtException ExpiredJwtException UnsupportedJwtException IllegalArgumentException
                    .getBody();

            User user = new User();

            user.setId((String) claims.get("id"));
            user.setUsername((String) claims.get("sub"));
            user.setUserCode((String) claims.get("usercode"));

            user.setEnabled((Boolean) claims.get("enabled"));
            user.setAccountNonLocked((Boolean) claims.get("nonlocked"));
            user.setAccountNonExpired((Boolean) claims.get("nonexpired"));

            final List<String> roleMaps = (List<String>) claims.get("roles");

            Set<User.Role> userRoles = new LinkedHashSet<>(2); //using linked hashset to ensure the same order and DB unpersist uses this as well
            roleMaps.stream().forEachOrdered(role -> {
                userRoles.add(DataRepositoryUserDetailsService.getRole(role));
            });

            user.setRoles(userRoles);
            //we dont want null password (SpringSecurityUserExModel constructor will throw exception) - although this password will not be used for anything in reality
            user.setPassword("");

            List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
            user.getRoles().forEach((role) -> {
                grantedAuthorities.add(new SimpleGrantedAuthority(role.getRole()));
            });

            if (user.getId() != null && user.getRoles() != null) {
                return new SpringSecurityUserExModel(user, false, grantedAuthorities);
            }
        } catch (Exception e) {
            //TODO do logging
            System.out.println("Invalid token provided\n" + e.getMessage());
        }
        return null;
    }

    public String createToken(SpringSecurityUserExModel springUser) {
        final long CURRENT_TIME = System.currentTimeMillis();
        Map<String, Object> claims = new HashMap();

        ArrayList<String> rolesInfos = new ArrayList<>(2);

        final Iterator<User.Role> roles = springUser.getUser().getRoles().iterator();

        while (roles.hasNext()) {
            final User.Role role = roles.next();
            rolesInfos.add(role.getRole());
        }
        claims.put("roles", rolesInfos);
        claims.put("id", springUser.getUser().getId());
        claims.put("usercode", springUser.getUser().getUserCode());
        //
        claims.put("enabled", springUser.getUser().isEnabled());
        claims.put("nonlocked", springUser.getUser().isAccountNonLocked());
        claims.put("nonexpired", springUser.getUser().isAccountNonExpired());

        //Jwts.parser().parse(SECRET).
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(springUser.getUser().getUsername())
                .setIssuedAt(new Date(CURRENT_TIME))
                .setExpiration(new Date(CURRENT_TIME + JWT_EXPIRATION_MILLISECONDS))//is it internally converted to seconds, as JS uses seconds rather than millis since epoch?
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    public Date isValidUntil(final String token) {
        try {
            Function<Claims, Date> claimsResolver = Claims::getExpiration;
            return claimsResolver.apply(Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody());
        } catch (Exception e) {
            return new Date(0);
        }
    }

    /**
     * This entity is used for TTL JWT blacklist persistence
     *
     * @author Ilamah, Osho
     */
    public static class Blacklist {

        private String token;
        private Date validUntil;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public Date getValidUntil() {
            return validUntil;
        }

        public void setValidUntil(Date validUntil) {
            this.validUntil = validUntil;
        }
    }

    /**
     * This class is used for persisting refresh tokens info
     */
    public static class Refresh {

    }
}
