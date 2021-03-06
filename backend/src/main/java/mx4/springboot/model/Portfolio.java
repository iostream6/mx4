/*
 * 2020.03.31  - Created
 */
package mx4.springboot.model;

/**
 *
 * @author Ilamah, Osho
 */
public class Portfolio {

    private String id;
    private String name;

    /**
     * A short code identifying this portfolio. Example: IBKR
     */
    private String code;
    
    private String brokerId;
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getBrokerId() {
        return brokerId;
    }

    public void setBrokerId(String brokerId) {
        this.brokerId = brokerId;
    }

    public static class PortfolioUser {

        private String id;

        private String portfolioID;

        private String userId;

        private boolean read;

        private boolean write;

        private double stake;

        public PortfolioUser() {
        }

        public PortfolioUser(String portfolioID, String userId, boolean read, boolean write, double stake) {
            this.portfolioID = portfolioID;
            this.userId = userId;
            this.read = read;
            this.write = write;
            this.stake = stake;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPortfolioID() {
            return portfolioID;
        }

        public void setPortfolioID(String portfolioID) {
            this.portfolioID = portfolioID;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public boolean isRead() {
            return read;
        }

        public void setRead(boolean read) {
            this.read = read;
        }

        public boolean isWrite() {
            return write;
        }

        public void setWrite(boolean write) {
            this.write = write;
        }

        public double getStake() {
            return stake;
        }

        public void setStake(double stake) {
            this.stake = stake;
        }

    }

}
