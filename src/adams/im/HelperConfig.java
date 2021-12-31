package adams.im;

import java.io.Serializable;

public class HelperConfig implements Serializable {

    private static final long serialVersionUID = 111;

    private String adminID;
    private boolean darkMode;
    private String[] servers;

    public HelperConfig(String adminID, boolean darkMode, String[] servers){
        this.adminID = adminID;
        this.darkMode = darkMode;
        this.servers = servers;
    }

    public String getAdminID() {
        return adminID;
    }

    public void setAdminID(String adminID) {
        this.adminID = adminID;
    }

    public boolean isDarkMode() {
        return darkMode;
    }

    public void setDarkMode(boolean darkMode) {
        this.darkMode = darkMode;
    }

    public String getServers() {
        StringBuilder returnString = new StringBuilder();
        for(int i = 0; i < servers.length; i++){
            returnString.append(servers[i]).append("\n");
        }
        return returnString.toString();
    }

    public void setServers(String[] servers) {
        this.servers = servers;
    }
}
