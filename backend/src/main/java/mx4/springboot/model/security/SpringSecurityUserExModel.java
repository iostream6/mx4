/*
 * 2020.04.25  - Created
 */
package mx4.springboot.model.security;

import java.util.Collection;
import mx4.springboot.model.User;
import org.springframework.security.core.GrantedAuthority;

/**
 * An extended Spring UserDetails model that holds other pertinent user infos.
 *
 * @author Ilamah, Osho
 */
public class SpringSecurityUserExModel extends org.springframework.security.core.userdetails.User {

    private final User user;

    public SpringSecurityUserExModel(final User user, boolean credentialsNonExpired, Collection<? extends GrantedAuthority> authorities) {
        super(user.getUsername(), user.getPassword(), user.isEnabled(), user.isAccountNonExpired(), credentialsNonExpired, user.isAccountNonLocked(), authorities);
        this.user = user;
    }

    public User getUser() {
        return user;
    }

}
