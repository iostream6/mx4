package mx4.springboot.services;

/*
 * 2020.04.10  - Created
 * 2020.05.31  - Updated handleLogoutRequest to use String parameter and renamed Blacklist transfer object
 * 2020.06.02  - Added refresh token support
 * 2020.06.05  - Refresh token cookie delete advice is now sent to client from server on logout
 * TODO - Internationalization
 */
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import mx4.springboot.model.User;
import mx4.springboot.persistence.UserRepository;
import mx4.springboot.model.security.AuthenticationRequestModel;
import mx4.springboot.model.security.SpringSecurityUserExModel;
import mx4.springboot.security.utils.TokenManager;
import mx4.springboot.security.utils.TokenManager.Token;
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
import mx4.springboot.persistence.TokenRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CookieValue;

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
    private TokenRepository tokenRepository;

    @Autowired
    private DataRepositoryUserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenManager tokenManager;

    @PostMapping(value = {"/authenticate", "/signin"})
    public ResponseEntity<?> handleAuthenticationRequest(@RequestBody AuthenticationRequestModel arm, HttpServletResponse response) throws Exception {
        Map<String, Object> responseData = new HashMap();
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(arm.getUsername(), arm.getPassword()));
        } catch (AuthenticationException e) {
            responseData.put("success", false);
            if (e instanceof DisabledException) {
                responseData.put("username-error", "Account is disabled");
            } else if (e instanceof LockedException) {
                responseData.put("username-error", "Account is locked");
            } else {
                //BadCredentialsException, UsernameNotFoundException, etc
                responseData.put("username-error", "Invalid username for password");
                responseData.put("password-error", "Invalid password for username");
            }
            return ResponseEntity.ok(responseData);
        }

        final String[] tokens = tokenManager.createWebTokens(userDetailsService.loadUserByUsername(arm.getUsername()));

        responseData.put("success", true);
        responseData.put("jwt", tokens[0]);

        final Token refreshToken = new Token();
        refreshToken.setValue(tokens[1]);
        refreshToken.setValidUntil(tokenManager.isValidUntil(tokens[1]));
        refreshToken.setType(TokenManager.Type.REFRESH);
        refreshToken.setKey(tokens[0]); //allows us to be able to delete/blacklist this 

        tokenRepository.save(refreshToken);

        final Cookie cookie = new Cookie("refresh-token", tokens[1]);
        cookie.setMaxAge(tokenManager.getRefreshDuration() / 1000);
        //cookie.setSecure(true); //TODO commented out in dev mode, please enable in production!
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        response.addCookie(cookie);

        return ResponseEntity.status(HttpStatus.OK).body(responseData);
    }

    @PostMapping("/signout")
    public Map<String, Object> handleLogoutRequest(@RequestBody String accessToken, HttpServletResponse response) {
        final Token tokenToBlacklist = new Token();
        tokenToBlacklist.setValue(accessToken);
        tokenToBlacklist.setValidUntil(tokenManager.isValidUntil(accessToken));
        //Blacklist the token - if it was invalid/expired, the expireAfterSeconds value in DB will be zero, meaning it can be deleted straight away
        //                      if it was not expired and was valid, the token is blacklisted!
        tokenToBlacklist.setType(TokenManager.Type.BLACKLISTED);
        tokenToBlacklist.setKey("");//not needed

        tokenRepository.save(tokenToBlacklist); // will prevent this token from being usable ever again. Once the token expiration date reaches, the mongoDB collection will automagically drop this document

        //block the refresh token from being used by blacklisting it
        Optional<Token> rto = tokenRepository.findByKey(accessToken);

        if (rto.isPresent()) {
            final Token rt = rto.get();
            rt.setType(TokenManager.Type.BLACKLISTED);
            rt.setValidUntil(tokenToBlacklist.getValidUntil());
            tokenRepository.save(rt);

            final Cookie cookie = new Cookie("refresh-token", rt.getValue());
            cookie.setMaxAge(0); //force to be deleted immediately
            //cookie.setSecure(true); //TODO commented out in dev mode, please enable in production!
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            
            response.addCookie(cookie);
        }

        Map<String, Object> clientInfo = new HashMap();
        clientInfo.put("success", true);
        return clientInfo;
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> handleRefreshRequest(@CookieValue(name = "refresh-token") String refreshToken, HttpServletResponse response) {
        Optional<Token> rto = tokenRepository.findByValueAndType(refreshToken, "REFRESH");
        Map<String, Object> responseData = new HashMap();
        if (rto.isPresent()) {
            final Token rt = rto.get();
            final Date currentDate = new Date();
            final Date expiryDate = rt.getValidUntil();
            if (currentDate.before(expiryDate)) {
                // refresh is allowed! 
                //first step is delete the refresh token so that it cant be reused
                tokenRepository.delete(rt);
                final String sub = tokenManager.getSub(refreshToken);
                try {
                    final SpringSecurityUserExModel user = userDetailsService.loadUserByUsername(sub);
                    final String[] tokens = tokenManager.createWebTokens(user);

                    responseData.put("success", true);
                    responseData.put("jwt", tokens[0]);

                    final Token newRefreshToken = new Token();
                    newRefreshToken.setValue(tokens[1]);
                    newRefreshToken.setValidUntil(tokenManager.isValidUntil(tokens[1]));
                    newRefreshToken.setType(TokenManager.Type.REFRESH);
                    newRefreshToken.setKey(tokens[0]); //allows us to be able to delete/blacklist this 

                    tokenRepository.save(newRefreshToken);

                    final Cookie cookie = new Cookie("refresh-token", tokens[1]);
                    cookie.setMaxAge(tokenManager.getRefreshDuration() / 1000);
                    //cookie.setSecure(true); //TODO commented out in dev mode, please enable in production!
                    cookie.setHttpOnly(true);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                    return ResponseEntity.status(HttpStatus.OK).body(responseData);
                } catch (ItemNotFoundException ee) {
                }
            }
        }
        responseData.put("success", false);
        return ResponseEntity.ok(responseData);
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
            clientInfo.put("email-error", "There is already a user registered with the email provided");
        }

        if (userRepository.findByUsername(user.getUsername()) != null) {
            clientInfo.put("username-error", "There is already a user registered with the username provided");
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
