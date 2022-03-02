package adams.im;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HelperLogger {

    private boolean enabled;
    File logFile = new File("helperLog.log");
    PrintWriter write;

    public HelperLogger(boolean enabled) {
        this.enabled = enabled;
        PrintWriter write = null;
        try {
            write = new PrintWriter(logFile);
            this.write = write;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void log(String s){
        if(enabled) {
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
            format.applyPattern("yyyy-MM-dd kk:mm:ss");
            String dateString = format.format(date.getTime());
            write.append(dateString + " : " + s + "\n");
        }
    }
    public void save(){
        write.close();
    }

    public void setEnabled(boolean enabled){
        this.enabled = enabled;
    }

}
