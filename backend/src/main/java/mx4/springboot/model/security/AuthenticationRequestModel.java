/*
 * 2020.04.25
 */
package mx4.springboot.model.security;

/**
 *
 * @author Ilamah, Osho
 */
public class AuthenticationRequestModel {
    private String username, password;

    public AuthenticationRequestModel(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public AuthenticationRequestModel() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    
}
