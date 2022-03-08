import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class HelperUpdater {

    public static void main(String[] args){
        if(args.length == 0){

            JOptionPane.showMessageDialog(new JFrame(), "Updater run standalone, downloading version 1.3.3" );

            String updateURL = "https://github.com/realadamsben/CommandHelper2/releases/download/v1.3.3/Command_Helper_2.exe";

            doUpdate(updateURL);
        } else {

            String updateURL = "https://github.com/realadamsben/CommandHelper2/releases/download/v" + args[0] + "." + args[1] + "." + args[2] + "/Command_Helper_2.exe";

            doUpdate(updateURL);
        }
    }

    private static void doUpdate(String updateURL) {
        File oldHelper = new File("Command_Helper_2.exe");
        oldHelper.delete();

        InputStream dl = null;
        try {
            dl = new URL(updateURL).openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Files.copy(dl, Paths.get("Command_Helper_2.exe"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
