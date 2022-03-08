package adams.im;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.google.gson.Gson;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.filechooser.FileSystemView;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class HelperMain extends JPanel {

    JWindow window = new JWindow();
    static HelperConfig config;
    static HelperLogger logger = new HelperLogger(false);

    public HelperMain() {
        // Gridbackup
        initComponents();
        gridbackupListButton.addActionListener(e -> CopyToClipboard(checkServer() + "gridbackup list " + checkPlayer()));
        // HM List
        hangarListButton.addActionListener(e -> CopyToClipboard(checkServer() + "hm list " + checkPlayer()));
        // Owner List
        ownedGridsListButton.addActionListener(e -> CopyToClipboard(checkServer() + "listgridsowner  " + checkPlayer()));
        // Author List
        createdGridsListButton.addActionListener(e -> CopyToClipboard(checkServer() + "listgridsauthor  " + checkPlayer()));
        // Parse Gridbackup
        parseButton.addActionListener(e -> {
            gridbackupParse();
        });
        // clear gridbackup entry
        clearBackupButton.addActionListener(e -> gridBackupEntry.setText(""));
        // clear playerid entry
        clearPlayerButton.addActionListener(e -> PlayerName.setText(""));
        // scripter
        scripterButton.addActionListener(e -> CopyToClipboard(checkServer() + "admin setrank " + checkPlayer() + " 1"));
        // transfer
        transferGridButton.addActionListener(e -> CopyToClipboard("!" + " transfer " + checkPlayer()));
        //getsteamid
        getSteamID64Button.addActionListener(e -> CopyToClipboard(checkServer() + "getsteamid " + checkPlayer()));
        // admin playerlist
        listPlayersButton.addActionListener(e -> CopyToClipboard(checkServer() + "admin playerlist"));
        // admin mode
        adminModeButton.addActionListener(e -> CopyToClipboard(checkServer() + "admin setrank " + AdminID.getText() + " 4"));
        // not admin mode
        playerModeButton.addActionListener(e -> CopyToClipboard(checkServer() + "admin setrank " + AdminID.getText() + " 1"));
        // clear adminsettings
        clearAdminSettingsButton.addActionListener(e -> {
            String server = checkServer();

            if (server.charAt(0) == 'x') {
                server = "DX";
                CopyToClipboard(server + " admin clearadminsettings " + AdminID.getText());
            } else if (server.charAt(0) == 'd') {
                server = "D";
                CopyToClipboard(server + " admin clearadminsettings " + AdminID.getText());
            } else if (server.charAt(0) == 'R') {
                server = "R";
                CopyToClipboard(server + " admin clearadminsettings " + AdminID.getText());
                } else
                CopyToClipboard("DX" + " admin clearadminsettings " + AdminID.getText());

        });

        // nexus stuff
        onlineServersButton.addActionListener(e -> CopyToClipboard("/nexus onlineservers"));
        // more nexus stuff
        clusterUptimeButton.addActionListener(e -> CopyToClipboard("/nexus uptime"));
        // blocklimit limit
        checkBlocklimitsButton.addActionListener(e -> CopyToClipboard(checkServer() + "blocklimit playerlimit " + checkPlayer()));
        // blocklimit update
        updateBlocklimitsButton.addActionListener(e -> CopyToClipboard(checkServer() + "blocklimit update -player=" + checkPlayer()));

        darkModeCheckBox.addActionListener(e -> {
            config.setDarkMode(darkModeCheckBox.isSelected());
            AdminIDSaveNotify.setText("Restart for Dark Mode to take effect");
        });
    }

    public void gridbackupParse() {

        String[] lines = gridBackupEntry.getText().split("\\r?\\n"); // make array of lines

        ArrayList<String> lines2 = new ArrayList<>(Arrays.asList(lines)); // array into arraylistbecause easier
        ArrayList<String> lines3 = new ArrayList<>();

        try{
            for (String s : lines2) { // remove not gridbackups
            if (Character.isDigit(s.charAt(0))) {
                lines3.add(s);
            }
        }
        } catch(StringIndexOutOfBoundsException ex){
            JOptionPane.showMessageDialog(new JFrame(), "Parsing gridbackups failed:\n " + ex.toString());
            return;
        }
        ArrayList<GridBackup> Grids = new ArrayList<>();

        for (String s : lines3) {
            //Retrieve gridname
            int firstSpace;
            firstSpace = s.indexOf(" ");
            int firstUnderscore;
            firstUnderscore = s.indexOf("_");
            String gridName = s.substring((firstSpace + 1), (firstUnderscore));

            //Retrieve GridID
            int dash = s.indexOf("-", firstUnderscore);
            String GridIDstring;
            GridIDstring = s.substring((firstUnderscore + 1), (dash - 1));

            //Retrieve DateTime
            String dateTime = s.substring((dash + 2));

            //Convert String dateTime to Date
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
            Date backupDate = null;
            try {
                backupDate = format.parse(dateTime);
            } catch (ParseException e) {
                logger.log(e.toString());
            }
            format.applyPattern("yyyy-MM-dd kk:mm:ss");


            //Assign a grid object with these values
            GridBackup grid = new GridBackup(gridName, Long.parseLong(GridIDstring), backupDate);

            Grids.add(grid);
        }
        JDialog results = new GridbackupResults(Grids, checkServer(), checkPlayer(), logger);
        logger.log("Gridbackup parsing successful");
        results.setVisible(true);
    }


    public void CopyToClipboard(String command) {
        Toolkit tk = Toolkit.getDefaultToolkit();
        if (command.charAt(0) == '!') {
            command = command.replaceFirst(" ", "");
        }
        Clipboard clipboard1 = tk.getSystemClipboard();
        StringSelection outputString = new StringSelection(command);
        clipboard1.setContents(outputString, null);
        ClipboardOutput.setText("Copied " + command + " to clipboard.");
        tCopiedToClipboard.setText("Copied " + command + " to clipboard.");
        logger.log("Successfully copied " + command + " to clipboard");
    }

    public String checkServer() {

        return switch ((String) ServerSelect.getSelectedItem()) {
            case "DX" -> "DX ";
            case "DXL" -> "x0 ";
            case "DX1" -> "x1 ";
            case "DX2" -> "x2 ";
            case "DX3" -> "x3 ";
            case "DX4" -> "x4 ";
            case "DX5" -> "x5 ";
            case "DX6" -> "x6 ";
            case "D" -> "D ";
            case "D1" -> "d1 ";
            case "D2" -> "d2 ";
            case "D3" -> "d3 ";
            case "D4" -> "d4 ";
            case "D5" -> "d5 ";
            case "D6" -> "d6 ";
            case "##" -> "## ";

            default -> (ServerSelect.getSelectedItem().toString() + " ");
        };
    }

    public String checkPlayer() {
        String player = PlayerName.getText();
        return '"' + player + '"';
    }

    public static void writeConfig(HelperConfig config) {

        try {
            FileOutputStream fileOut = new FileOutputStream("helperConfig.txt");
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(config);
            objectOut.close();
            //System.out.println("The Object  was succesfully written to a file");
            logger.log("Config successfully written to file");
        } catch (Exception ex) {
            logger.log(ex.toString());
            JOptionPane.showMessageDialog(new JFrame(), "Config saving failed:\n " + ex.toString());
        }
    }

    public static HelperConfig readConfig() {

        try {
            FileInputStream fileIn = new FileInputStream("helperConfig.txt");
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);

            Object obj = objectIn.readObject();

            //System.out.println("The Object has been read from the file");
            objectIn.close();
            return (HelperConfig) obj;
        } catch (Exception ex) {
            logger.log(ex.toString());
            JOptionPane.showMessageDialog(new JFrame(), "Config loading failed:\n " + ex.toString());
            return null;
        }
    }
    private static boolean checkUpdates(int major, int minor, int rev){
        int thisMajor = major;
        int thisMinor = minor;
        int thisRev = rev;
        URL url = null;
        try {
            url = new URL("https://api.github.com/repos/realadamsben/CommandHelper2/releases");
        } catch (MalformedURLException e) {
            logger.log(e.toString());
            JOptionPane.showMessageDialog(new JFrame(), "Update check failed:\n " + e.toString());
        }
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(url.openStream()));
        } catch (IOException e) {
            logger.log(e.toString());
        }
        String git = null;
        try {
            git = in.readLine();
        } catch (IOException e) {
            logger.log(e.toString());
        }
        int latestTag = git.indexOf("\"tag_name\":\"");
        String latestRelease = git.substring(latestTag+13, latestTag+18);

        int remoteMajor = Integer.parseInt(latestRelease.substring(0,1));
        int remoteMinor = Integer.parseInt(latestRelease.substring(2,3));
        int remoteRev = Integer.parseInt(latestRelease.substring(4,5));
        // check if actual updates are needed

        int thisVersion = (thisMajor * 100) + (thisMinor * 10) + thisRev;
        int remoteVersion = (remoteMajor * 100) + (remoteMinor * 10) + remoteRev;

        if(remoteVersion > thisVersion){
            return true;
        } else {
            return false;
        }


    }

<<<<<<< Updated upstream
    private static void doUpdate(int major, int minor, int rev){
        URL url = null;
        try {
            url = new URL("https://api.github.com/repos/realadamsben/CommandHelper2/releases");
        } catch (MalformedURLException e) {
            logger.log(e.toString());
        }
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(url.openStream()));
        } catch (IOException e) {
            logger.log(e.toString());
        }
        String git = null;
        try {
            git = in.readLine();
        } catch (IOException e) {
            logger.log(e.toString());
        }
        int updatePosition = git.indexOf("\"browser_download_url\":");
        String urlString = git.substring(updatePosition+24);
        String updateURL = urlString.substring(0, urlString.indexOf('"'));
        logger.log(updateURL);

        InputStream dl = null;
        try {
            dl = new URL(updateURL).openStream();
        } catch (IOException e) {
            logger.log(e.toString());
        }
        try {
            Files.copy(dl, Paths.get("Command_Helper_2_.exe"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            logger.log(e.toString());
        }
    }


    public static void main(String[] args) throws IOException {
=======
    public static void main(String[] args){
>>>>>>> Stashed changes

        // release information
        int major = 1;
        int minor = 3;
<<<<<<< Updated upstream
        int rev = 2;
        String message = "condensing shit you don't need into one tab!";
=======
        int rev = 4;
        String message = "with parsing old tickets!";
>>>>>>> Stashed changes

        File configFile = new File("helperConfig.txt");

        if(!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                logger.log(e.toString());
                JOptionPane.showMessageDialog(new JFrame(), "Config loading failed:\n " + e.toString());
            }
            config = new HelperConfig();
            writeConfig(config);
            logger.log("Config file does not exist, creating new config file");
        } else {
            logger.log("Config file exists, skipping new config creation");
        }
        config = readConfig();

        logger.setEnabled(config.isDebugLogging());

        if(config.isDarkMode()) {
            FlatDarkLaf.setup();
            logger.log("Started with Dark Mode");
        } else{
            FlatIntelliJLaf.setup();
            logger.log("Started with Light Mode");
        }

        JFrame frame = new JFrame("Command Helper 2 v" + major + "." + minor + "." + rev + " - now " + message);
        frame.setContentPane(new HelperMain().tabbedPane1);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        JFileChooser test = new JFileChooser();

        File updater = new File("HelperUpdater.jar");
        boolean update = checkUpdates(major, minor, rev);
        if(update){
            if(updater.exists()){
                JOptionPane.showMessageDialog(frame, "Update is available, click ok to apply");
<<<<<<< Updated upstream
                Process proc = Runtime.getRuntime().exec("java -jar HelperUpdater.jar");
=======
                try {
                    InputStream dl = null;
                    dl = new URL("https://github.com/realadamsben/CommandHelper2/releases/download/Updater/HelperUpdater.jar").openStream();
                    Files.copy(dl, Paths.get("HelperUpdater.jar"), StandardCopyOption.REPLACE_EXISTING);
                    Process proc = Runtime.getRuntime().exec("java -jar HelperUpdater.jar " + update[1] + " " + update[2] + " " + update[3]);
                } catch (IOException e) {
                    logger.log(e.toString());
                    JOptionPane.showMessageDialog(new JFrame(), "Updater starting failed:\n " + e.toString());
                }
>>>>>>> Stashed changes
                System.exit(0);
            } else{
                JOptionPane.showMessageDialog(frame, "Update is available but updater does not exist, click ok to download");
                InputStream dl = null;
                try {
                    dl = new URL("https://github.com/realadamsben/CommandHelper2/releases/download/Updater/HelperUpdater.jar").openStream();
                } catch (IOException e) {
                    logger.log(e.toString());
                }
                try {
                    Files.copy(dl, Paths.get("HelperUpdater.jar"), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    logger.log(e.toString());
                }
                JOptionPane.showMessageDialog(frame, "Updater downloaded, click ok to update");
                try {
                    Process proc = Runtime.getRuntime().exec("java -jar HelperUpdater.jar");
                } catch (IOException e) {
                    logger.log(e.toString());
                    JOptionPane.showMessageDialog(new JFrame(), "Updater starting failed:\n " + e.toString());

                }
                System.exit(0);
            }
        }

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                logger.save();
                System.exit(0);
            }
        });

    }

    private void darkModeCheckBoxStateChanged(ChangeEvent e) {

    }

    private void darkMode(ActionEvent e) {
    }

    private void tScripter(ActionEvent e) {
        CopyToClipboard("/t scripter");
    }

    private void tVote(ActionEvent e) {
        CopyToClipboard("/t vote");
    }

    private void tName(ActionEvent e) {
        CopyToClipboard("/t name");
    }

    private void tSupply(ActionEvent e) {
        CopyToClipboard("/t supply");
    }

    private void tFixship(ActionEvent e) {
        CopyToClipboard("/t fixship");
    }

    private void tSDNN(ActionEvent e) {
        CopyToClipboard("/t sdnn");
    }

    private void tWikiEdit(ActionEvent e) {
        CopyToClipboard("/t wikiedit");
    }

    private void tWiki(ActionEvent e) {
        CopyToClipboard("/t wiki");
    }

    private void tCleanup(ActionEvent e) {
        CopyToClipboard("/t cleanup ");
    }

    private void tLog(ActionEvent e) {
        CopyToClipboard("/t log");
    }

    private void tLoadCrash(ActionEvent e) {
        CopyToClipboard("/t loadcrash");
    }

    private void tConntrouble(ActionEvent e) {
        CopyToClipboard("/t conntrouble");
    }

    private void tDIE(ActionEvent e) {
        CopyToClipboard("/t die");
    }

    private void tWait(ActionEvent e) {
        CopyToClipboard("/t wait");
    }

    private void tWait2(ActionEvent e) {
        CopyToClipboard("/t wait2");
    }

    private void tTime(ActionEvent e) {
        CopyToClipboard("/t time");
    }

    private void AdminStuffSave(ActionEvent e) {
        config.setAdminID(AdminID.getText());
        config.setDarkMode(darkModeCheckBox.isSelected());
        String[] servers = serversTextArea.getText().split("\\r?\\n|\\r");
        config.setServers(servers);
        config.setDebugLogging(debugLogging.isSelected());
        writeConfig(config);
        AdminIDSaveNotify.setText("Settings saved, restart for changes to take effect.");
        logger.log("Configuration saved");
    }

    public void settingsLoadConfigs(ChangeEvent e) {
        AdminID.setText(config.getAdminID());
        serversTextArea.setText(config.getServers());
        darkModeCheckBox.setSelected(config.isDarkMode());
        debugLogging.setSelected(config.isDebugLogging());
        logger.log("Admin settings set from config");

    }

    private void ServerSelectPopupMenuWillBecomeVisible(PopupMenuEvent e) {
        ServerSelect.setModel(new DefaultComboBoxModel<>(config.getServersArray()));
    }

    private void ServerSelect(ActionEvent e) {
        // TODO add your code here
    }

    private void ServerSelectItemStateChanged(ItemEvent e) {
        // TODO add your code here
    }

    private void adminConfigDefault(ActionEvent e) {
        config.restoreDefaults();
        AdminID.setText(config.getAdminID());
        serversTextArea.setText(config.getServers());
        darkModeCheckBox.setSelected(config.isDarkMode());
        debugLogging.setSelected(config.isDebugLogging());
        AdminIDSaveNotify.setText("Restart to apply default configs");
        logger.log("Configs restored to default");
        writeConfig(config);
    }

    private void debugLoggingStateChanged(ChangeEvent e) {
        config.setDebugLogging(debugLogging.isSelected());
    }

    private void GPSParse(ActionEvent e) {
        Scanner in = null;
        if(GPSInput.getText().charAt(0) != '<'){
            File GPSInFile = new File(GPSInput.getText());
            logger.log("GPS Converter using file");
            try {
                in = new Scanner(GPSInFile);
                logger.log("File found successfully");
            } catch (FileNotFoundException ex) {
                logger.log(ex.toString());
            }
        } else {
            in = new Scanner(GPSInput.getText());
            logger.log("GPS Converter using text box");
        }
        ArrayList<HelperGPS> gpsArray = new ArrayList<>();
        int i = 0;
        while(in.hasNextLine()) {
            i++;
            String currentLine = in.nextLine();
            String[] gpsSplit = currentLine.split("<Position x=|y=|z=|/|>|\n");
            System.out.println(Arrays.toString(gpsSplit));
            gpsArray.add(new HelperGPS(GPSName.getText() + i, gpsSplit[1].substring(1, gpsSplit[1].length()-1), gpsSplit[2].substring(1, gpsSplit[2].length()-1), gpsSplit[3].substring(1, gpsSplit[3].length()-1), GPSColor.getText()));
        }
        StringBuilder s = new StringBuilder();
        for(int j = 0; j < gpsArray.size(); j++){
            s.append(gpsArray.get(j).toString() + "\n");
        }
        GPSOutput.setText(s.toString());
        logger.log("GPS Conversion complete");
        logger.log(gpsArray.toString());
    }

    private void SteamLinkParse(ActionEvent e) {
        Scanner in = null;
        in = new Scanner(SteamLinkIn.getText());
        ArrayList<HelperSteamLink> linkArray = new ArrayList<>();
        while(in.hasNextLine()){
            String currentLine = in.nextLine();
            int dash = currentLine.indexOf('-');
            String modID = currentLine.substring(0, dash);
            linkArray.add(new HelperSteamLink(modID));
        }
        StringBuilder s = new StringBuilder();
        for(int j = 0; j < linkArray.size(); j++){
            s.append(linkArray.get(j).toString() + "\n");
        }
        SteamLinkOut.setText(s.toString());
        logger.log("Steamlink conversion complete");
        logger.log(linkArray.toString());
    }


    private void BulkEditParse() {
        Scanner in = null;
        in = new Scanner(SteamLinkOut.getText());
        ArrayList<String> editArray = new ArrayList<>();
        while(in.hasNextLine()){
            String currentLine = in.nextLine();
            int id = currentLine.indexOf("?id=");
            int and = currentLine.length();
            if(currentLine.contains("&")) {
                and = currentLine.indexOf('&');
            }
            String modID = (currentLine.substring(id + 4, and));
            editArray.add(modID);
        }
        StringBuilder s = new StringBuilder();
        for(int j = 0; j < editArray.size(); j++){
            s.append(editArray.get(j).toString() + "-Steam\n");
        }
        SteamLinkIn.setText(s.toString());
        logger.log("BulkEdit conversion complete");
        logger.log(editArray.toString());
    }

    private void selectFile() {
        JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        int returnValue = fileChooser.showOpenDialog(null);
        if(returnValue == JFileChooser.APPROVE_OPTION){
            File selectedFile = fileChooser.getSelectedFile();
            filePath.setText(selectedFile.getAbsolutePath());
        }
    }

    private void pullPosition(){
        File sandbox = null;
        sandbox = new File(filePath.getText());

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            logger.log(e.toString());
            JOptionPane.showMessageDialog(new JFrame(), "Parsing sandbox failed:\n " + e.toString());
        }
        Document doc = null;
        try {
            doc = db.parse(sandbox);
        } catch (SAXException e) {
            logger.log(e.toString());
        } catch (IOException e) {
            logger.log(e.toString());
        }

        doc.getDocumentElement().normalize();
        // list for fac information
        ArrayList<String> facTagList = new ArrayList<>();
        ArrayList<String> facIDList = new ArrayList<>();
        // list for gpss
        ArrayList<HelperGPS> gpsList = new ArrayList<>();

        // organise the factions into two lists for easy shit later
        NodeList factionList = doc.getElementsByTagName("MyObjectBuilder_Faction");
        for(int i = 0; i < factionList.getLength(); i++) {
            Node faction = factionList.item(i);
            if(faction.getNodeType() == Node.ELEMENT_NODE){
                Element eElement = (Element) faction;
                if(eElement.getElementsByTagName("AcceptHumans").item(0).getTextContent().equalsIgnoreCase("false")){
                  facTagList.add(eElement.getElementsByTagName("Tag").item(0).getTextContent());
                  facIDList.add(eElement.getElementsByTagName("FactionId").item(0).getTextContent());
                }
            }
        }
        logger.log(facIDList.toString());
        logger.log(facTagList.toString());
        // so we know which station # for the faction
        int[] stationsPerFac = new int[facIDList.size()];
        Arrays.fill(stationsPerFac, 1);
        // get all myobjectbuilder_stations
        NodeList stationList = doc.getElementsByTagName("MyObjectBuilder_Station");
        for(int i = 0; i < stationList.getLength(); i++){
            Node station = stationList.item(i);

            // get positions and owners of NPC stations
            if(station.getNodeType() == Node.ELEMENT_NODE){
                Element eElement = (Element) station;
                // i only declare this shit so line 657 isnt long as fuck
                String x = eElement.getElementsByTagName("Position").item(0).getAttributes().item(0).getTextContent();
                String y = eElement.getElementsByTagName("Position").item(0).getAttributes().item(1).getTextContent();
                String z = eElement.getElementsByTagName("Position").item(0).getAttributes().item(2).getTextContent();
                // figure out faction tag prefix and stationtype if enabled
                for(int j = 0; j < facIDList.size(); j++){
                    if(facIDList.get(j).equalsIgnoreCase(eElement.getElementsByTagName("FactionId").item(0).getTextContent())){
                        StringBuilder s = new StringBuilder();
                        s.append(facTagList.get(j));
                        if(includeType.isSelected()) {
                            s.append(" ");
                            s.append(eElement.getElementsByTagName("StationType").item(0).getTextContent());
                        }
                        s.append(" #");
                        s.append(stationsPerFac[j]);
                        gpsList.add(new HelperGPS(s.toString(), x, y, z ));
                        stationsPerFac[j] = stationsPerFac[j] + 1;
                    }
                }
            }
        }
        logger.log(gpsList.toString());
        // set text in textbox to actual list of gpss
        StringBuilder s = new StringBuilder();
        for(int i = 0; i < gpsList.size(); i++){
            s.append(gpsList.get(i).toString());
            s.append("\n");
        }
        sandboxGPSOutput.setText(s.toString());
    }

    private void debugLogging() {
        config.setDebugLogging(debugLogging.isSelected());
    }

<<<<<<< Updated upstream
=======
    private void start() {
        JFrame frame = new JFrame("Test");
        frame.setContentPane(new HelperBlockEditor(logger));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void selectFileTicket() {
        JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        int returnValue = fileChooser.showOpenDialog(null);
        if(returnValue == JFileChooser.APPROVE_OPTION){
            File selectedFile = fileChooser.getSelectedFile();
            fileLocationTicket.setText(selectedFile.getAbsolutePath());
        }
    }

    private void ticketParseGo() {

        File ticket = new File(fileLocationTicket.getText());
        Reader reader  = null;
        try {
            reader = new FileReader(ticket);
        } catch (FileNotFoundException e) {
            logger.log(e.toString());
            JOptionPane.showMessageDialog(new JFrame(), "Ticket parsing failed:\n " + e.toString());
        }
        Gson gson = new Gson();
        HelperTicket helpTicket = gson.fromJson(reader, HelperTicket.class);
        String ticketString = helpTicket.toString();

        // this is such a shitty way of doing this lmfao
        Path ticketPath = Path.of(String.valueOf(ticket.getAbsolutePath()));
        String json = null;
        try {
            json = Files.readString(ticketPath);
        } catch (IOException e) {
            logger.log(e.toString());
        }
        int firstUsers = json.indexOf("users");
        int firstMessages = json.indexOf("messages");
        String usersString = json.substring(firstUsers + 8, firstMessages -3);

        System.out.println(usersString);

        ArrayList<String> idList = new ArrayList<>();
        ArrayList<String> nameList = new ArrayList<>();

        while(usersString.contains("username")){
            // get id string
            String idString = usersString.substring(0, usersString.indexOf(':')+1);
            if(idString.contains("}")) {
                idString = idString.substring(idString.indexOf('}'));
            } else{
                idString = idString;
            }
            idList.add(idString.substring(idString.indexOf('"')+1, idString.lastIndexOf('"')));
            // get name string
            String nameString = usersString.substring(usersString.indexOf("username")+12);
            nameList.add(nameString.substring(0, nameString.indexOf('"')));

            usersString = usersString.substring(usersString.indexOf(nameString));
        }

        for(int i = 0; i < idList.size(); i++){
            ticketString = ticketString.replaceAll(idList.get(i), nameList.get(i));
        }

        ticketTextPane.setText(ticketString);

    }

>>>>>>> Stashed changes

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Ben Adams
        tabbedPane1 = new JTabbedPane();
        var Commands = new JPanel();
        var panel2 = new JPanel();
        var toolBar1 = new JToolBar();
        gridbackupListButton = new JButton();
        hangarListButton = new JButton();
        ownedGridsListButton = new JButton();
        createdGridsListButton = new JButton();
        var toolBar2 = new JToolBar();
        scripterButton = new JButton();
        transferGridButton = new JButton();
        getSteamID64Button = new JButton();
        listPlayersButton = new JButton();
        var toolBar3 = new JToolBar();
        onlineServersButton = new JButton();
        clusterUptimeButton = new JButton();
        checkBlocklimitsButton = new JButton();
        updateBlocklimitsButton = new JButton();
        ClipboardOutput = new JLabel();
        var panel3 = new JPanel();
        clearAdminSettingsButton = new JButton();
        adminModeButton = new JButton();
        playerModeButton = new JButton();
        var hSpacer1 = new Spacer();
        PlayerName = new JTextField();
        var label1 = new JLabel();
        ServerSelect = new JComboBox<>();
        var label2 = new JLabel();
        clearPlayerButton = new JButton();
        var GridBackup = new JPanel();
        var toolBar4 = new JToolBar();
        clearBackupButton = new JButton();
        parseButton = new JButton();
        var scrollPane1 = new JScrollPane();
        gridBackupEntry = new JTextArea();
        var Tags = new JPanel();
        tScripter = new JButton();
        tVote = new JButton();
        tName = new JButton();
        tSupply = new JButton();
        tFixship = new JButton();
        tSDNN = new JButton();
        tWikiEdit = new JButton();
        tWiki = new JButton();
        tCleanup = new JButton();
        tLog = new JButton();
        tLoadCrash = new JButton();
        tConntrouble = new JButton();
        tDIE = new JButton();
        tWait = new JButton();
        tWait2 = new JButton();
        tTime = new JButton();
        tCopiedToClipboard = new JLabel();
        OldTickets = new JPanel();
        label10 = new JLabel();
        scrollPane9 = new JScrollPane();
        ticketTextPane = new JTextPane();
        var vSpacer5 = new Spacer();
        var hSpacer6 = new Spacer();
        var hSpacer7 = new Spacer();
        selectFileTicket = new JButton();
        fileLocationTicket = new JTextField();
        ticketParseGo = new JButton();
        var Settings = new JPanel();
        AdminID = new JTextField();
        var label3 = new JLabel();
        label5 = new JLabel();
        label4 = new JLabel();
        scrollPane2 = new JScrollPane();
        serversTextArea = new JTextArea();
        var vSpacer1 = new Spacer();
        var vSpacer4 = new Spacer();
        var hSpacer2 = new Spacer();
        debugLogging = new JCheckBox();
        darkModeCheckBox = new JCheckBox();
        AdminIDSaveButton = new JButton();
        adminConfigDefault = new JButton();
        var vSpacer3 = new Spacer();
        var vSpacer2 = new Spacer();
        AdminIDSaveNotify = new JLabel();
        tabbedPane2 = new JTabbedPane();
        StationGPSCovnert = new JPanel();
        scrollPane8 = new JScrollPane();
        sandboxGPSOutput = new JTextArea();
        var hSpacer5 = new Spacer();
        var vSpacer14 = new Spacer();
        var vSpacer13 = new Spacer();
        selectFileButton = new JButton();
        scrollPane7 = new JScrollPane();
        filePath = new JTextArea();
        pullPositionButton = new JButton();
        includeType = new JCheckBox();
        BulkEditCovnert = new JPanel();
        label8 = new JLabel();
        scrollPane5 = new JScrollPane();
        SteamLinkIn = new JTextPane();
        var vSpacer11 = new Spacer();
        var hSpacer4 = new Spacer();
        label9 = new JLabel();
        scrollPane6 = new JScrollPane();
        SteamLinkOut = new JTextPane();
        var vSpacer12 = new Spacer();
        SteamLinkParse = new JButton();
        BulkEditParse = new JButton();
        GPSConvert = new JPanel();
        label6 = new JLabel();
        scrollPane3 = new JScrollPane();
        GPSInput = new JTextPane();
        var vSpacer10 = new Spacer();
        var hSpacer3 = new Spacer();
        label7 = new JLabel();
        scrollPane4 = new JScrollPane();
        GPSOutput = new JTextPane();
        var vSpacer7 = new Spacer();
        GPSParseButton = new JButton();
        GPSName = new JTextField();
        GPSColor = new JTextField();

        //======== this ========
        setAlignmentX(0.1F);
        setFocusTraversalPolicyProvider(true);
        setForeground(new Color(95, 100, 102));
        setMaximumSize(new Dimension(825, 280));
        setMinimumSize(new Dimension(825, 280));
        setPreferredSize(new Dimension(825, 280));
        setBorder (new javax. swing. border. CompoundBorder( new javax .swing .border .TitledBorder (new javax
        . swing. border. EmptyBorder( 0, 0, 0, 0) , "JFor\u006dDesi\u0067ner \u0045valu\u0061tion", javax. swing
        . border. TitledBorder. CENTER, javax. swing. border. TitledBorder. BOTTOM, new java .awt .
        Font ("Dia\u006cog" ,java .awt .Font .BOLD ,12 ), java. awt. Color. red
        ) , getBorder( )) );  addPropertyChangeListener (new java. beans. PropertyChangeListener( ){ @Override
        public void propertyChange (java .beans .PropertyChangeEvent e) {if ("bord\u0065r" .equals (e .getPropertyName (
        ) )) throw new RuntimeException( ); }} );
        setLayout(new BorderLayout());

        //======== tabbedPane1 ========
        {
            tabbedPane1.setEnabled(true);
            tabbedPane1.setToolTipText("Commands, might be useful for tickets or something");
            tabbedPane1.addChangeListener(e -> settingsLoadConfigs(e));

            //======== Commands ========
            {
                Commands.setLayout(new GridLayoutManager(15, 5, new Insets(10, 10, 10, 10), -1, -1));

                //======== panel2 ========
                {
                    panel2.setLayout(new GridLayoutManager(5, 1, new Insets(0, 0, 0, 0), -1, -1));

                    //======== toolBar1 ========
                    {
                        toolBar1.setFloatable(false);
                        toolBar1.addSeparator();

                        //---- gridbackupListButton ----
                        gridbackupListButton.setMaximumSize(new Dimension(125, 50));
                        gridbackupListButton.setMinimumSize(new Dimension(125, 50));
                        gridbackupListButton.setPreferredSize(new Dimension(125, 50));
                        gridbackupListButton.setText("Gridbackup List");
                        gridbackupListButton.setToolTipText("List gridbackups");
                        toolBar1.add(gridbackupListButton);
                        toolBar1.addSeparator();

                        //---- hangarListButton ----
                        hangarListButton.setMaximumSize(new Dimension(125, 50));
                        hangarListButton.setMinimumSize(new Dimension(125, 50));
                        hangarListButton.setPreferredSize(new Dimension(125, 50));
                        hangarListButton.setText("Hangar List");
                        hangarListButton.setToolTipText("List hangar");
                        toolBar1.add(hangarListButton);
                        toolBar1.addSeparator();

                        //---- ownedGridsListButton ----
                        ownedGridsListButton.setMaximumSize(new Dimension(125, 50));
                        ownedGridsListButton.setMinimumSize(new Dimension(125, 50));
                        ownedGridsListButton.setPreferredSize(new Dimension(125, 50));
                        ownedGridsListButton.setText("Owned Grids List");
                        ownedGridsListButton.setToolTipText("List the grids a player has ownership of");
                        toolBar1.add(ownedGridsListButton);
                        toolBar1.addSeparator();

                        //---- createdGridsListButton ----
                        createdGridsListButton.setMaximumSize(new Dimension(125, 50));
                        createdGridsListButton.setMinimumSize(new Dimension(125, 50));
                        createdGridsListButton.setPreferredSize(new Dimension(125, 50));
                        createdGridsListButton.setText("Created Grids List");
                        createdGridsListButton.setToolTipText("List the grids a player has created");
                        toolBar1.add(createdGridsListButton);
                        toolBar1.addSeparator();
                    }
                    panel2.add(toolBar1, new GridConstraints(0, 0, 1, 1,
                        GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                        GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                        GridConstraints.SIZEPOLICY_FIXED,
                        null, null, null));

                    //======== toolBar2 ========
                    {
                        toolBar2.setFloatable(false);
                        toolBar2.addSeparator();

                        //---- scripterButton ----
                        scripterButton.setContentAreaFilled(true);
                        scripterButton.setMaximumSize(new Dimension(125, 50));
                        scripterButton.setMinimumSize(new Dimension(125, 50));
                        scripterButton.setPreferredSize(new Dimension(125, 50));
                        scripterButton.setText("Scripter");
                        scripterButton.setToolTipText("Set scripter rank");
                        toolBar2.add(scripterButton);
                        toolBar2.addSeparator();

                        //---- transferGridButton ----
                        transferGridButton.setMaximumSize(new Dimension(125, 50));
                        transferGridButton.setMinimumSize(new Dimension(125, 50));
                        transferGridButton.setPreferredSize(new Dimension(125, 50));
                        transferGridButton.setText("Transfer Grid");
                        transferGridButton.setToolTipText("Run while looking at a grid to transfer to player");
                        toolBar2.add(transferGridButton);
                        toolBar2.addSeparator();

                        //---- getSteamID64Button ----
                        getSteamID64Button.setMaximumSize(new Dimension(125, 50));
                        getSteamID64Button.setMinimumSize(new Dimension(125, 50));
                        getSteamID64Button.setPreferredSize(new Dimension(125, 50));
                        getSteamID64Button.setText("Get SteamID64");
                        getSteamID64Button.setToolTipText("Get player steamid");
                        toolBar2.add(getSteamID64Button);
                        toolBar2.addSeparator();

                        //---- listPlayersButton ----
                        listPlayersButton.setMaximumSize(new Dimension(125, 50));
                        listPlayersButton.setMinimumSize(new Dimension(125, 50));
                        listPlayersButton.setPreferredSize(new Dimension(125, 50));
                        listPlayersButton.setText("List Players");
                        listPlayersButton.setToolTipText("Get a list of players on the server");
                        toolBar2.add(listPlayersButton);
                        toolBar2.addSeparator();
                    }
                    panel2.add(toolBar2, new GridConstraints(1, 0, 1, 1,
                        GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                        GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                        GridConstraints.SIZEPOLICY_FIXED,
                        null, null, null));

                    //======== toolBar3 ========
                    {
                        toolBar3.setFloatable(false);
                        toolBar3.addSeparator();

                        //---- onlineServersButton ----
                        onlineServersButton.setInheritsPopupMenu(false);
                        onlineServersButton.setMaximumSize(new Dimension(125, 50));
                        onlineServersButton.setMinimumSize(new Dimension(125, 50));
                        onlineServersButton.setPreferredSize(new Dimension(125, 50));
                        onlineServersButton.setText("Online Servers");
                        onlineServersButton.setToolTipText("See online servers");
                        toolBar3.add(onlineServersButton);
                        toolBar3.addSeparator();

                        //---- clusterUptimeButton ----
                        clusterUptimeButton.setMaximumSize(new Dimension(125, 50));
                        clusterUptimeButton.setMinimumSize(new Dimension(125, 50));
                        clusterUptimeButton.setPreferredSize(new Dimension(125, 50));
                        clusterUptimeButton.setText("Controller Uptime");
                        clusterUptimeButton.setToolTipText("Get uptime of the Nexus controllers");
                        toolBar3.add(clusterUptimeButton);
                        toolBar3.addSeparator();

                        //---- checkBlocklimitsButton ----
                        checkBlocklimitsButton.setMaximumSize(new Dimension(125, 50));
                        checkBlocklimitsButton.setMinimumSize(new Dimension(125, 50));
                        checkBlocklimitsButton.setPreferredSize(new Dimension(125, 50));
                        checkBlocklimitsButton.setText("Check Blocklimits");
                        checkBlocklimitsButton.setToolTipText("Check blocklimits of a player");
                        toolBar3.add(checkBlocklimitsButton);
                        toolBar3.addSeparator();

                        //---- updateBlocklimitsButton ----
                        updateBlocklimitsButton.setMaximumSize(new Dimension(125, 50));
                        updateBlocklimitsButton.setMinimumSize(new Dimension(125, 50));
                        updateBlocklimitsButton.setPreferredSize(new Dimension(125, 50));
                        updateBlocklimitsButton.setText("Update Blocklimits");
                        updateBlocklimitsButton.setToolTipText("Update a players blocklimts");
                        toolBar3.add(updateBlocklimitsButton);
                        toolBar3.addSeparator();
                    }
                    panel2.add(toolBar3, new GridConstraints(2, 0, 1, 1,
                        GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                        GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                        GridConstraints.SIZEPOLICY_FIXED,
                        null, null, null));

                    //---- ClipboardOutput ----
                    ClipboardOutput.setText("#");
                    panel2.add(ClipboardOutput, new GridConstraints(3, 0, 1, 1,
                        GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                        GridConstraints.SIZEPOLICY_FIXED,
                        GridConstraints.SIZEPOLICY_FIXED,
                        null, null, null));
                }
                Commands.add(panel2, new GridConstraints(1, 3, 14, 2,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    null, null, null));

                //======== panel3 ========
                {
                    panel3.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));

                    //---- clearAdminSettingsButton ----
                    clearAdminSettingsButton.setText("Clear Admin Settings");
                    clearAdminSettingsButton.setToolTipText("YOU MUST RUN THIS AFTER LEAVING ADMIN MODE");
                    panel3.add(clearAdminSettingsButton, new GridConstraints(2, 0, 1, 1,
                        GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_FIXED,
                        null, null, null));

                    //---- adminModeButton ----
                    adminModeButton.setText("Admin Mode");
                    adminModeButton.setToolTipText("Set admin rank");
                    adminModeButton.setVerifyInputWhenFocusTarget(true);
                    panel3.add(adminModeButton, new GridConstraints(0, 0, 1, 1,
                        GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_FIXED,
                        null, null, null));

                    //---- playerModeButton ----
                    playerModeButton.setBorderPainted(true);
                    playerModeButton.setText("Player Mode");
                    playerModeButton.setToolTipText("Remove admin rank");
                    panel3.add(playerModeButton, new GridConstraints(1, 0, 1, 1,
                        GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_FIXED,
                        null, null, null));
                    panel3.add(hSpacer1, new GridConstraints(3, 0, 1, 1,
                        GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                        GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK,
                        null, null, null));
                }
                Commands.add(panel3, new GridConstraints(1, 0, 14, 3,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    null, null, null));
                Commands.add(PlayerName, new GridConstraints(0, 3, 1, 1,
                    GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
                    GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                    GridConstraints.SIZEPOLICY_FIXED,
                    null, null, null));

                //---- label1 ----
                label1.setText("Playername/SteamID64:   ");
                Commands.add(label1, new GridConstraints(0, 2, 1, 1,
                    GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                    GridConstraints.SIZEPOLICY_FIXED,
                    GridConstraints.SIZEPOLICY_FIXED,
                    null, null, null));

                //---- ServerSelect ----
                ServerSelect.setDoubleBuffered(true);
                ServerSelect.setLightWeightPopupEnabled(true);
                ServerSelect.setMaximumRowCount(20);
                ServerSelect.setToolTipText("Select a server to run the command on");
                ServerSelect.setModel(new DefaultComboBoxModel<>(new String[] {
                    "!"
                }));
                ServerSelect.addActionListener(e -> ServerSelect(e));
                ServerSelect.addItemListener(e -> ServerSelectItemStateChanged(e));
                ServerSelect.addPopupMenuListener(new PopupMenuListener() {
                    @Override
                    public void popupMenuCanceled(PopupMenuEvent e) {}
                    @Override
                    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}
                    @Override
                    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                        ServerSelectPopupMenuWillBecomeVisible(e);
                    }
                });
                Commands.add(ServerSelect, new GridConstraints(0, 1, 1, 1,
                    GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
                    GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_FIXED,
                    null, null, null));

                //---- label2 ----
                label2.setText("  Server:   ");
                Commands.add(label2, new GridConstraints(0, 0, 1, 1,
                    GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                    GridConstraints.SIZEPOLICY_FIXED,
                    GridConstraints.SIZEPOLICY_FIXED,
                    null, null, null));

                //---- clearPlayerButton ----
                clearPlayerButton.setText("Clear");
                clearPlayerButton.setToolTipText("Clear playername");
                Commands.add(clearPlayerButton, new GridConstraints(0, 4, 1, 1,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_FIXED,
                    null, null, null));
            }
            tabbedPane1.addTab("Commands", Commands);

            //======== GridBackup ========
            {
                GridBackup.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));

                //======== toolBar4 ========
                {
                    toolBar4.setFloatable(false);
                    toolBar4.addSeparator();

                    //---- clearBackupButton ----
                    clearBackupButton.setMaximumSize(new Dimension(78, 50));
                    clearBackupButton.setMinimumSize(new Dimension(78, 50));
                    clearBackupButton.setPreferredSize(new Dimension(78, 50));
                    clearBackupButton.setText("Clear");
                    toolBar4.add(clearBackupButton);
                    toolBar4.addSeparator();

                    //---- parseButton ----
                    parseButton.setMaximumSize(new Dimension(78, 50));
                    parseButton.setMinimumSize(new Dimension(78, 50));
                    parseButton.setPreferredSize(new Dimension(78, 50));
                    parseButton.setText("Parse");
                    toolBar4.add(parseButton);
                    toolBar4.addSeparator();
                }
                GridBackup.add(toolBar4, new GridConstraints(1, 0, 1, 1,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                    GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                    GridConstraints.SIZEPOLICY_FIXED,
                    null, null, null));

                //======== scrollPane1 ========
                {

                    //---- gridBackupEntry ----
                    gridBackupEntry.setText("");
                    scrollPane1.setViewportView(gridBackupEntry);
                }
                GridBackup.add(scrollPane1, new GridConstraints(0, 0, 1, 1,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                    null, null, null));
            }
            tabbedPane1.addTab("Grid Backup", GridBackup);

            //======== Tags ========
            {
                Tags.setLayout(new GridLayoutManager(8, 7, new Insets(10, 10, 10, 10), 0, 0));

                //---- tScripter ----
                tScripter.setText("/t scripter");
                tScripter.addActionListener(e -> tScripter(e));
                Tags.add(tScripter, new GridConstraints(0, 0, 1, 1,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    null, null, null));

                //---- tVote ----
                tVote.setText("/t vote");
                tVote.addActionListener(e -> {
			tScripter(e);
			tVote(e);
		});
                Tags.add(tVote, new GridConstraints(0, 2, 1, 1,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    null, null, null));

                //---- tName ----
                tName.setText("/t name");
                tName.addActionListener(e -> {
			tScripter(e);
			tName(e);
		});
                Tags.add(tName, new GridConstraints(0, 4, 1, 1,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    null, null, null));

                //---- tSupply ----
                tSupply.setText("/t supply");
                tSupply.addActionListener(e -> {
			tScripter(e);
			tSupply(e);
		});
                Tags.add(tSupply, new GridConstraints(0, 6, 1, 1,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    null, null, null));

                //---- tFixship ----
                tFixship.setText("/t fixship");
                tFixship.addActionListener(e -> {
			tScripter(e);
			tFixship(e);
		});
                Tags.add(tFixship, new GridConstraints(2, 0, 1, 1,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    null, null, null));

                //---- tSDNN ----
                tSDNN.setText("/t sdnn");
                tSDNN.addActionListener(e -> {
			tScripter(e);
			tSDNN(e);
		});
                Tags.add(tSDNN, new GridConstraints(2, 2, 1, 1,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    null, null, null));

                //---- tWikiEdit ----
                tWikiEdit.setText("/t wikiedit");
                tWikiEdit.addActionListener(e -> {
			tScripter(e);
			tWikiEdit(e);
		});
                Tags.add(tWikiEdit, new GridConstraints(2, 4, 1, 1,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    null, null, null));

                //---- tWiki ----
                tWiki.setText("/t wiki");
                tWiki.addActionListener(e -> {
			tScripter(e);
			tWiki(e);
		});
                Tags.add(tWiki, new GridConstraints(2, 6, 1, 1,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    null, null, null));

                //---- tCleanup ----
                tCleanup.setText("/t cleanup");
                tCleanup.addActionListener(e -> {
			tScripter(e);
			tCleanup(e);
		});
                Tags.add(tCleanup, new GridConstraints(4, 0, 1, 1,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    null, null, null));

                //---- tLog ----
                tLog.setText("/t log");
                tLog.addActionListener(e -> {
			tScripter(e);
			tLog(e);
		});
                Tags.add(tLog, new GridConstraints(4, 2, 1, 1,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    null, null, null));

                //---- tLoadCrash ----
                tLoadCrash.setText("/t loadcrash");
                tLoadCrash.addActionListener(e -> {
			tScripter(e);
			tLoadCrash(e);
		});
                Tags.add(tLoadCrash, new GridConstraints(4, 4, 1, 1,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    null, null, null));

                //---- tConntrouble ----
                tConntrouble.setText("/t conntrouble");
                tConntrouble.addActionListener(e -> {
			tScripter(e);
			tConntrouble(e);
		});
                Tags.add(tConntrouble, new GridConstraints(4, 6, 1, 1,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    null, null, null));

                //---- tDIE ----
                tDIE.setText("/t die");
                tDIE.addActionListener(e -> {
			tScripter(e);
			tDIE(e);
		});
                Tags.add(tDIE, new GridConstraints(6, 0, 1, 1,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    null, null, null));

                //---- tWait ----
                tWait.setText("/t wait");
                tWait.addActionListener(e -> {
			tScripter(e);
			tWait(e);
		});
                Tags.add(tWait, new GridConstraints(6, 2, 1, 1,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    null, null, null));

                //---- tWait2 ----
                tWait2.setText("/t wait2");
                tWait2.addActionListener(e -> {
			tScripter(e);
			tWait2(e);
		});
                Tags.add(tWait2, new GridConstraints(6, 4, 1, 1,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    null, null, null));

                //---- tTime ----
                tTime.setText("/t time");
                tTime.addActionListener(e -> {
			tScripter(e);
			tTime(e);
		});
                Tags.add(tTime, new GridConstraints(6, 6, 1, 1,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    null, null, null));

                //---- tCopiedToClipboard ----
                tCopiedToClipboard.setText("#");
                Tags.add(tCopiedToClipboard, new GridConstraints(7, 0, 1, 7,
                    GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    null, null, null));
            }
            tabbedPane1.addTab("xelA Tags", Tags);

            //======== OldTickets ========
            {
                OldTickets.setLayout(new GridLayoutManager(6, 10, new Insets(10, 10, 10, 10), -1, -1));

                //---- label10 ----
                label10.setText("Output");
                OldTickets.add(label10, new GridConstraints(0, 0, 1, 1,
                    GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    null, null, null));

                //======== scrollPane9 ========
                {
                    scrollPane9.setViewportView(ticketTextPane);
                }
                OldTickets.add(scrollPane9, new GridConstraints(1, 0, 3, 10,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    null, null, null));
                OldTickets.add(vSpacer5, new GridConstraints(3, 8, 1, 1,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK,
                    GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                    null, null, null));
                OldTickets.add(hSpacer6, new GridConstraints(4, 1, 1, 1,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                    GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK,
                    null, null, null));
                OldTickets.add(hSpacer7, new GridConstraints(4, 2, 1, 1,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                    GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK,
                    null, null, null));

                //---- selectFileTicket ----
                selectFileTicket.setText("Select File Location");
                selectFileTicket.addActionListener(e -> selectFileTicket());
                OldTickets.add(selectFileTicket, new GridConstraints(5, 0, 1, 1,
                    GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    null, null, null));
                OldTickets.add(fileLocationTicket, new GridConstraints(5, 1, 1, 1,
                    GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    null, new Dimension(500, 10), null));

                //---- ticketParseGo ----
                ticketParseGo.setText("GO");
                ticketParseGo.addActionListener(e -> ticketParseGo());
                OldTickets.add(ticketParseGo, new GridConstraints(5, 2, 1, 1,
                    GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    null, null, null));
            }
            tabbedPane1.addTab("Old Tickets", OldTickets);

            //======== Settings ========
            {
                Settings.setLayout(new GridLayoutManager(11, 35, new Insets(10, 10, 10, 10), -1, -1));
                Settings.add(AdminID, new GridConstraints(2, 1, 1, 34,
                    GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
                    GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                    GridConstraints.SIZEPOLICY_FIXED,
                    null, null, null));

                //---- label3 ----
                label3.setText("Enter  your SteamID64 and click save.");
                Settings.add(label3, new GridConstraints(0, 1, 2, 34,
                    GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                    GridConstraints.SIZEPOLICY_FIXED,
                    GridConstraints.SIZEPOLICY_FIXED,
                    null, null, null));

                //---- label5 ----
                label5.setText("Customize servers available in server select here");
                Settings.add(label5, new GridConstraints(3, 1, 1, 1,
                    GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    null, null, null));

                //---- label4 ----
                label4.setText("Unless using default configs, use the prefixes for the servers (x0 instead of DXL, x1 instead of DX1, etc.)");
                Settings.add(label4, new GridConstraints(3, 2, 1, 1,
                    GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    null, null, null));

                //======== scrollPane2 ========
                {

                    //---- serversTextArea ----
                    serversTextArea.setLineWrap(true);
                    serversTextArea.setWrapStyleWord(true);
                    serversTextArea.setRows(8);
                    serversTextArea.setColumns(1);
                    scrollPane2.setViewportView(serversTextArea);
                }
                Settings.add(scrollPane2, new GridConstraints(4, 1, 4, 1,
                    GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_BOTH,
                    GridConstraints.SIZEPOLICY_FIXED,
                    GridConstraints.SIZEPOLICY_FIXED,
                    null, null, null));
                Settings.add(vSpacer1, new GridConstraints(6, 34, 2, 1,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK,
                    GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                    null, null, null));
                Settings.add(vSpacer4, new GridConstraints(4, 33, 1, 1,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK,
                    GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                    null, null, null));
                Settings.add(hSpacer2, new GridConstraints(4, 32, 1, 1,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                    GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK,
                    null, null, null));

                //---- debugLogging ----
                debugLogging.setText("Debug Logging");
                debugLogging.addActionListener(e -> debugLogging());
                Settings.add(debugLogging, new GridConstraints(6, 2, 1, 1,
                    GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    null, null, null));

                //---- darkModeCheckBox ----
                darkModeCheckBox.setSelected(false);
                darkModeCheckBox.setText("Dark Mode");
                darkModeCheckBox.addChangeListener(e -> darkModeCheckBoxStateChanged(e));
                darkModeCheckBox.addActionListener(e -> darkMode(e));
                Settings.add(darkModeCheckBox, new GridConstraints(7, 2, 1, 1,
                    GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_FIXED,
                    new Dimension(120, 50), new Dimension(120, 50), new Dimension(120, 50)));

                //---- AdminIDSaveButton ----
                AdminIDSaveButton.setText("Save");
                AdminIDSaveButton.addActionListener(e -> AdminStuffSave(e));
                Settings.add(AdminIDSaveButton, new GridConstraints(8, 1, 1, 1,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_FIXED,
                    new Dimension(50, 20), null, null));

                //---- adminConfigDefault ----
                adminConfigDefault.setText("Restore Default Configs");
                adminConfigDefault.addActionListener(e -> adminConfigDefault(e));
                Settings.add(adminConfigDefault, new GridConstraints(8, 2, 1, 1,
                    GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    null, null, null));
                Settings.add(vSpacer3, new GridConstraints(8, 6, 1, 29,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK,
                    GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                    null, null, null));
                Settings.add(vSpacer2, new GridConstraints(8, 18, 1, 13,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK,
                    GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                    null, null, null));

                //---- AdminIDSaveNotify ----
                AdminIDSaveNotify.setText("#");
                Settings.add(AdminIDSaveNotify, new GridConstraints(9, 1, 1, 1,
                    GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                    GridConstraints.SIZEPOLICY_FIXED,
                    GridConstraints.SIZEPOLICY_FIXED,
                    null, null, null));
            }
            tabbedPane1.addTab("Settings", Settings);

            //======== tabbedPane2 ========
            {

                //======== StationGPSCovnert ========
                {
                    StationGPSCovnert.setLayout(new GridLayoutManager(6, 22, new Insets(10, 10, 10, 10), -1, -1));

                    //======== scrollPane8 ========
                    {
                        scrollPane8.setViewportView(sandboxGPSOutput);
                    }
                    StationGPSCovnert.add(scrollPane8, new GridConstraints(0, 0, 5, 20,
                        GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        null, null, null));
                    StationGPSCovnert.add(hSpacer5, new GridConstraints(1, 2, 1, 1,
                        GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                        GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK,
                        null, null, null));
                    StationGPSCovnert.add(vSpacer14, new GridConstraints(3, 20, 1, 1,
                        GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK,
                        GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                        null, null, null));
                    StationGPSCovnert.add(vSpacer13, new GridConstraints(4, 20, 1, 1,
                        GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK,
                        GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                        null, null, null));

                    //---- selectFileButton ----
                    selectFileButton.setText("Select Sandbox Location");
                    selectFileButton.addActionListener(e -> selectFile());
                    StationGPSCovnert.add(selectFileButton, new GridConstraints(5, 0, 1, 1,
                        GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        null, null, null));

                    //======== scrollPane7 ========
                    {
                        scrollPane7.setViewportView(filePath);
                    }
                    StationGPSCovnert.add(scrollPane7, new GridConstraints(5, 1, 1, 1,
                        GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        new Dimension(500, 25), null, null));

                    //---- pullPositionButton ----
                    pullPositionButton.setText("GO");
                    pullPositionButton.addActionListener(e -> pullPosition());
                    StationGPSCovnert.add(pullPositionButton, new GridConstraints(5, 2, 1, 1,
                        GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        null, null, null));

                    //---- includeType ----
                    includeType.setText("Include Station Type");
                    StationGPSCovnert.add(includeType, new GridConstraints(5, 3, 1, 1,
                        GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        null, null, null));
                }
                tabbedPane2.addTab("Station GPS from Sandbox", StationGPSCovnert);

                //======== BulkEditCovnert ========
                {
                    BulkEditCovnert.setLayout(new GridLayoutManager(7, 6, new Insets(10, 10, 10, 10), 5, -1));

                    //---- label8 ----
                    label8.setText("Bulk Edit List Here");
                    BulkEditCovnert.add(label8, new GridConstraints(0, 0, 1, 4,
                        GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        null, null, null));

                    //======== scrollPane5 ========
                    {
                        scrollPane5.setViewportView(SteamLinkIn);
                    }
                    BulkEditCovnert.add(scrollPane5, new GridConstraints(1, 0, 1, 5,
                        GridConstraints.ANCHOR_WEST, GridConstraints.FILL_BOTH,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        null, null, null));
                    BulkEditCovnert.add(vSpacer11, new GridConstraints(1, 5, 1, 1,
                        GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK,
                        GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                        null, null, null));
                    BulkEditCovnert.add(hSpacer4, new GridConstraints(2, 1, 1, 3,
                        GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                        GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK,
                        null, null, null));

                    //---- label9 ----
                    label9.setText("Steam Workshop Links Here");
                    BulkEditCovnert.add(label9, new GridConstraints(3, 0, 1, 4,
                        GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        null, null, null));

                    //======== scrollPane6 ========
                    {
                        scrollPane6.setViewportView(SteamLinkOut);
                    }
                    BulkEditCovnert.add(scrollPane6, new GridConstraints(4, 0, 1, 5,
                        GridConstraints.ANCHOR_WEST, GridConstraints.FILL_BOTH,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        null, null, null));
                    BulkEditCovnert.add(vSpacer12, new GridConstraints(4, 5, 1, 1,
                        GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK,
                        GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                        null, null, null));

                    //---- SteamLinkParse ----
                    SteamLinkParse.setText("Convert to Steam Links");
                    SteamLinkParse.addActionListener(e -> SteamLinkParse(e));
                    BulkEditCovnert.add(SteamLinkParse, new GridConstraints(5, 0, 1, 1,
                        GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        null, null, null));

                    //---- BulkEditParse ----
                    BulkEditParse.setText("Convert to Bulk Edit");
                    BulkEditParse.addActionListener(e -> BulkEditParse());
                    BulkEditCovnert.add(BulkEditParse, new GridConstraints(5, 1, 1, 1,
                        GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        null, null, null));
                }
                tabbedPane2.addTab("Bulk Edit Conversion", BulkEditCovnert);

                //======== GPSConvert ========
                {
                    GPSConvert.setLayout(new GridLayoutManager(7, 5, new Insets(10, 10, 10, 10), 5, -1));

                    //---- label6 ----
                    label6.setText("Enter positions from sandbox or a filepath");
                    GPSConvert.add(label6, new GridConstraints(0, 0, 1, 3,
                        GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        null, null, null));

                    //======== scrollPane3 ========
                    {
                        scrollPane3.setViewportView(GPSInput);
                    }
                    GPSConvert.add(scrollPane3, new GridConstraints(1, 0, 1, 3,
                        GridConstraints.ANCHOR_WEST, GridConstraints.FILL_BOTH,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        null, null, null));
                    GPSConvert.add(vSpacer10, new GridConstraints(1, 4, 1, 1,
                        GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK,
                        GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                        null, null, null));
                    GPSConvert.add(hSpacer3, new GridConstraints(2, 2, 1, 1,
                        GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                        GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK,
                        null, null, null));

                    //---- label7 ----
                    label7.setText("GPSs will come out here");
                    GPSConvert.add(label7, new GridConstraints(3, 0, 1, 3,
                        GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        null, null, null));

                    //======== scrollPane4 ========
                    {
                        scrollPane4.setViewportView(GPSOutput);
                    }
                    GPSConvert.add(scrollPane4, new GridConstraints(4, 0, 1, 3,
                        GridConstraints.ANCHOR_WEST, GridConstraints.FILL_BOTH,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        null, null, null));
                    GPSConvert.add(vSpacer7, new GridConstraints(4, 4, 1, 1,
                        GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK,
                        GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                        null, null, null));

                    //---- GPSParseButton ----
                    GPSParseButton.setText("Convert to GPS");
                    GPSParseButton.addActionListener(e -> GPSParse(e));
                    GPSConvert.add(GPSParseButton, new GridConstraints(5, 0, 1, 1,
                        GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        null, null, null));

                    //---- GPSName ----
                    GPSName.setText("Station #");
                    GPSName.setToolTipText("GPS Name");
                    GPSConvert.add(GPSName, new GridConstraints(5, 1, 1, 1,
                        GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        null, null, null));

                    //---- GPSColor ----
                    GPSColor.setText("FF75C9F1");
                    GPSColor.setToolTipText("GPS Color");
                    GPSConvert.add(GPSColor, new GridConstraints(5, 2, 1, 1,
                        GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        null, null, null));
                }
                tabbedPane2.addTab("GPS Conversion", GPSConvert);
            }
            tabbedPane1.addTab("Admin", tabbedPane2);
        }
        add(tabbedPane1, BorderLayout.NORTH);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Ben Adams
    private JTabbedPane tabbedPane1;
    private JButton gridbackupListButton;
    private JButton hangarListButton;
    private JButton ownedGridsListButton;
    private JButton createdGridsListButton;
    private JButton scripterButton;
    private JButton transferGridButton;
    private JButton getSteamID64Button;
    private JButton listPlayersButton;
    private JButton onlineServersButton;
    private JButton clusterUptimeButton;
    private JButton checkBlocklimitsButton;
    private JButton updateBlocklimitsButton;
    private JLabel ClipboardOutput;
    private JButton clearAdminSettingsButton;
    private JButton adminModeButton;
    private JButton playerModeButton;
    private JTextField PlayerName;
    private JComboBox<String> ServerSelect;
    private JButton clearPlayerButton;
    private JButton clearBackupButton;
    private JButton parseButton;
    private JTextArea gridBackupEntry;
    private JButton tScripter;
    private JButton tVote;
    private JButton tName;
    private JButton tSupply;
    private JButton tFixship;
    private JButton tSDNN;
    private JButton tWikiEdit;
    private JButton tWiki;
    private JButton tCleanup;
    private JButton tLog;
    private JButton tLoadCrash;
    private JButton tConntrouble;
    private JButton tDIE;
    private JButton tWait;
    private JButton tWait2;
    private JButton tTime;
    private JLabel tCopiedToClipboard;
    private JPanel OldTickets;
    private JLabel label10;
    private JScrollPane scrollPane9;
    private JTextPane ticketTextPane;
    private JButton selectFileTicket;
    private JTextField fileLocationTicket;
    private JButton ticketParseGo;
    private JTextField AdminID;
    private JLabel label5;
    private JLabel label4;
    private JScrollPane scrollPane2;
    private JTextArea serversTextArea;
    private JCheckBox debugLogging;
    private JCheckBox darkModeCheckBox;
    private JButton AdminIDSaveButton;
    private JButton adminConfigDefault;
    private JLabel AdminIDSaveNotify;
    private JTabbedPane tabbedPane2;
    private JPanel StationGPSCovnert;
    private JScrollPane scrollPane8;
    private JTextArea sandboxGPSOutput;
    private JButton selectFileButton;
    private JScrollPane scrollPane7;
    private JTextArea filePath;
    private JButton pullPositionButton;
    private JCheckBox includeType;
    private JPanel BulkEditCovnert;
    private JLabel label8;
    private JScrollPane scrollPane5;
    private JTextPane SteamLinkIn;
    private JLabel label9;
    private JScrollPane scrollPane6;
    private JTextPane SteamLinkOut;
    private JButton SteamLinkParse;
    private JButton BulkEditParse;
    private JPanel GPSConvert;
    private JLabel label6;
    private JScrollPane scrollPane3;
    private JTextPane GPSInput;
    private JLabel label7;
    private JScrollPane scrollPane4;
    private JTextPane GPSOutput;
    private JButton GPSParseButton;
    private JTextField GPSName;
    private JTextField GPSColor;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

}
