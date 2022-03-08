package adams.im;

import com.google.gson.annotations.SerializedName;
public class HelperUser {
    boolean bot;
    String badge;
    String avatar;
    String username;

    public String toString(){
        return bot + " " + badge + " " + avatar + " " + username;
    }

}


