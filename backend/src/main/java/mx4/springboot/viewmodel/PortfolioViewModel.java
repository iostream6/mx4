/*
 * 2020.05.22  - Created
 */
package mx4.springboot.viewmodel;

import java.util.List;
import mx4.springboot.model.Portfolio;
import mx4.springboot.model.Portfolio.PortfolioUser;

/**
 *
 * @author Ilamah, Osho
 */
public class PortfolioViewModel extends Portfolio {

    private List<PortfolioUser> users;

       public List<PortfolioUser> getUsers() {
        return users;
    }

    public void setUsers(List<PortfolioUser> users) {
        this.users = users;
    }

}
