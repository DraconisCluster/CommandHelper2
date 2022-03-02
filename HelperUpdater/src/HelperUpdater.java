import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class HelperUpdater {

    public static void main(String[] args){
        URL url = null;
        try {
            url = new URL("https://api.github.com/repos/realadamsben/CommandHelper2/releases");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(url.openStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String git = null;
        try {
            git = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int updatePosition = git.indexOf("\"browser_download_url\":");
        String urlString = git.substring(updatePosition+24);
        String updateURL = urlString.substring(0, urlString.indexOf('"'));

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
