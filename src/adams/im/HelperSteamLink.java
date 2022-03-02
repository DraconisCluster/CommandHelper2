package adams.im;

public class HelperSteamLink {
    String modID;

    public HelperSteamLink(String modID){
        this.modID = modID;
    }

    public String toString(){
        return "https://steamcommunity.com/workshop/filedetails/?id=" + modID;
    }
}
