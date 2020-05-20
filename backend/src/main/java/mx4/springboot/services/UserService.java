package mx4.springboot.services;

/*
 * 2020.04.10  - Created
 * TODO - Internationalization
 */
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx4.springboot.model.User;
import mx4.springboot.persistence.JWTBlacklistRepository;
import mx4.springboot.persistence.UserRepository;
import mx4.springboot.model.security.AuthenticationRequestModel;
import mx4.springboot.security.utils.TokenManager;
import mx4.springboot.security.utils.TokenManager.Jwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Ilamah, Osho
 */
@RestController
public class UserService {

    //private static final String TYPE = "User";
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTBlacklistRepository jwtBlacklistRepo;

    @Autowired
    private DataRepositoryUserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenManager tokenManager;

    @PostMapping(value = {"/authenticate", "/signin"})
    public ResponseEntity<?> handleAuthenticationRequest(@RequestBody AuthenticationRequestModel arm) throws Exception {
        Map<String, Object> responseData = new HashMap();
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(arm.getUsername(), arm.getPassword()));
        } catch (AuthenticationException e) {
            responseData.put("success", false);
            if (e instanceof DisabledException) {
                responseData.put("username", "Account is disabled");
            } else if (e instanceof LockedException) {
                responseData.put("username", "Account is locked");
            } else {
                //BadCredentialsException, UsernameNotFoundException, etc
                responseData.put("username", "Invalid username for password");
                responseData.put("password", "Invalid password for username");
            }
            return ResponseEntity.ok(responseData);
        }

//        SpringSecurityUserExModel user = userDetailsService.loadUserByUsername(arm.getUsername());
//        if (!user.isAccountNonExpired()) {
//            responseData.put("username", "Account is expired");
//        }
        final String token = tokenManager.createToken(userDetailsService.loadUserByUsername(arm.getUsername()));
        responseData.put("success", true);
        responseData.put("jwt", token);

        return ResponseEntity.ok(responseData);
    }

    @PostMapping("/signout")
    public Map<String, Object> handleLogoutRequest(@RequestBody Jwt jwt) {

        //we ignore whatever valid is specified by the user (if at all) and determine the actual value based on the Jwt
        jwt.setValidUntil(tokenManager.isValidUntil(jwt.getToken()));
        //Blacklist the token - if it was invalid/expired, the expireAfterSeconds value in DB will be zero, meaning it can be deleted straight away
        //                      if it was not expired and was valid, the token is blacklisted!
        jwtBlacklistRepo.save(jwt);

        Map<String, Object> clientInfo = new HashMap();
        clientInfo.put("success", true);
        return clientInfo;
    }

    @PostMapping("/signup")
    public Map<String, Object> create(@RequestBody User user) {

        Map<String, Object> clientInfo = new HashMap();

        //validate email - expect the client to have checked this
        //if ((user.getUsername().trim().length() < 3)) {
        //    clientInfo.put("username", "Username is too short");
        //}
        //validate email - expect the client to have checked this
        //if (!isValidEmail(user.getEmail())) {
        //    clientInfo.put("email", "Email is invalid");
        //}
        //
        //
        //always save email in lower case to avoid need to check for upper and lower case when determining if in use
        //username is case sensistive so we can leave, but clearly email is not
        user.setEmail(user.getEmail().toLowerCase());

        if (userRepository.findByEmail(user.getEmail()) != null) {
            clientInfo.put("email", "There is already a user registered with the email provided");
        }

        if (userRepository.findByUsername(user.getUsername()) != null) {
            clientInfo.put("username", "There is already a user registered with the username provided");
        }

        if (clientInfo.size() > 0) {
            clientInfo.put("success", false);
            return clientInfo;
        } else {
            userDetailsService.createUser(user, 1); // basic user type
            //clientInfo.put("id", user.getId());
            clientInfo.put("success", true);
            return clientInfo;
        }
    }

    @GetMapping("admin/users")
//    @Secured({"ROLE_USER"})
    public List<User> readAll() {
        return userRepository.findAll();
    }

//    @GetMapping("/admin")
//    public String getUsername() {
//    SecurityContext securityContext = SecurityContextHolder.getContext();
//    return securityContext.getAuthentication().getName();
//}
}
