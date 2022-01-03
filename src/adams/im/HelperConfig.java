package adams.im;

import java.io.Serial;
import java.io.Serializable;

public class HelperConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 111;

    private String adminID;
    private boolean darkMode;
    private String[] servers;

    public HelperConfig(String adminID, boolean darkMode, String[] servers){
        this.adminID = adminID;
        this.darkMode = darkMode;
        this.servers = servers;
    }

    public HelperConfig() {
        restoreDefaults();
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
        for (String server : servers) {
            returnString.append(server).append("\n");
        }
        String returnString2 = returnString.toString();
        return returnString2.trim();
    }
    public String[] getServersArray() {
        return servers;
    }

    public void setServers(String[] servers) {
        this.servers = servers;
    }

    public void restoreDefaults(){
        this.adminID = "your steamID64";
        this.darkMode = false;
        this.servers = new String[]{"!", "DXL", "DX1", "DX2", "DX3", "DX4", "DX5", "DX6", "--", "D", "D1", "D2", "D3", "D4", "D5", "D6"};
    }
}
