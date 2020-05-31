/*
 * 2020.04.08  - Created
 * 2020.05.31  - Added getRole method to allow easier deserialize of role info 
 */
package mx4.springboot.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import mx4.springboot.model.User;
import mx4.springboot.persistence.UserRepository;
import mx4.springboot.model.security.SpringSecurityUserExModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 *
 * @author Ilamah, Osho
 */
@Service
public class DataRepositoryUserDetailsService implements UserDetailsService {

    private static final String TYPE = "User";

    private static final List<User.Role> rolesList;

    @Autowired
    private UserRepository userRepository;

    //@Autowired
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Override
    public SpringSecurityUserExModel loadUserByUsername(String username) throws ItemNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            List<GrantedAuthority> authorities = getUserAuthority(user.getRoles());
            return buildUserForAuthentication(user, authorities);
        }
        //throw new ItemNotFoundException(username, TYPE);
        throw new UsernameNotFoundException("User not found!!");
    }

    /**
     * Creates a new user.Expects that the calling client has already performed validation
     *
     * @param user
     * @param roles
     */
    public void createUser(User user, int... roles) {//package level exposure
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setEnabled(false); //Will only be activated after confirm via email
        user.setAccountNonLocked(true);
        user.setAccountNonExpired(true);

        List<User.Role> userRoles = new ArrayList<>(2);
        for (int i : roles) {
            userRoles.add(rolesList.get(i));
        }
        user.setRoles(new LinkedHashSet<>(userRoles));//using linked hashset to ensure the same order and DB unpersist uses this as well
        userRepository.save(user);
    }

    //converts the user roles as GrantedAuthority collection
    private List<GrantedAuthority> getUserAuthority(Set<User.Role> userRoles) {
        Set<GrantedAuthority> roles = new HashSet<>();
        userRoles.forEach((role) -> {
            roles.add(new SimpleGrantedAuthority(role.getRole()));
        });

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>(roles);
        return grantedAuthorities;
    }

    //connects MongoDB user to Spring Security user
    private SpringSecurityUserExModel buildUserForAuthentication(User user, List<GrantedAuthority> authorities) {
        final SpringSecurityUserExModel springSecurityUser = new SpringSecurityUserExModel(user, true, authorities);
        return springSecurityUser;
    }

    public static User.Role getRole(final String role) {
        for (User.Role r : rolesList) {
            if (r.getRole().equals(role)) {
                final User.Role rr = new User.Role();
                rr.setId(r.getId());
                rr.setRole(r.getRole());
                return rr;
            }
        }
        return null;
    }

    //initialize all supported currencies as a singleton list
    static {
        final List<User.Role> roles = new ArrayList();
        User.Role role = new User.Role();
        //initialize Admin role:
        role.setRole("ADMIN");
        role.setId("A6223201");
        roles.add(role);

        //initilize User role
        role = new User.Role();
        role.setRole("USER");
        role.setId("U7742201");
        roles.add(role);

        rolesList = Collections.unmodifiableList(roles);

    }
}
