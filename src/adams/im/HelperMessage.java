package adams.im;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class HelperMessage {
    String author;
    HelperContent[] content;
    String msg;
    String timestamp;

    public String toString(){
        StringBuilder s = new StringBuilder();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        format.applyPattern("yyyy-MM-dd kk:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        String dateString = format.format(new Date(Long.parseLong(timestamp)*1000));
        s.append("\n" + author + " at ");
        s.append(dateString + " UTC " + "\n" );
        //s.append(msg + "\n");
        if(content != null) {
            for (HelperContent cnt : content) {
                s.append(cnt.toString() + "\n");
            }
        }
        return s.toString();
    }
}
