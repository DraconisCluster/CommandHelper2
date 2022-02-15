package adams.im;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;


public class HelperCopy extends JPanel {

    JWindow window = new JWindow();
    static HelperConfig config;

    public HelperCopy() {
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
        parseButton.addActionListener(e -> gridbackupParse());
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

        darkModeCheckBox.addActionListener(e -> JOptionPane.showMessageDialog(window, "Click save and restart to apply dark mode"));
    }

    public void gridbackupParse() {

        String[] lines = gridBackupEntry.getText().split("\\r?\\n"); // make array of lines

        ArrayList<String> lines2 = new ArrayList<>(Arrays.asList(lines)); // array into arraylistbecause easier
        ArrayList<String> lines3 = new ArrayList<>();

        for (String s : lines2) { // remove not gridbackups
            if (Character.isDigit(s.charAt(0))) {
                lines3.add(s);
            }
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
            GridIDstring = s.substring((firstUnderscore + 1), (dash - 2));

            //Retrieve DateTime
            String dateTime = s.substring((dash + 2));

            //Assign an object to these values
            GridBackup grid = new GridBackup(gridName, Long.parseLong(GridIDstring), dateTime);

            Grids.add(grid);
        }
        JDialog results = new GridbackupResults(Grids, checkServer(), checkPlayer());
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

        } catch (Exception ex) {
            ex.printStackTrace();
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
            ex.printStackTrace();
            return null;
        }
    }



    public static void main(String[] args) throws IOException {

        File configFile = new File("helperConfig.txt");
        if(!configFile.exists()) {
            configFile.createNewFile();
            config = new HelperConfig();
            writeConfig(config);
        }
        config = readConfig();

        if(config.isDarkMode()) {
            FlatDarkLaf.setup();
        } else FlatIntelliJLaf.setup();


        JFrame frame = new JFrame("Command Helper 2 v1.2.3 - Now with Java 17 support");
        frame.setContentPane(new HelperCopy().tabbedPane1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
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
        writeConfig(config);
        AdminIDSaveNotify.setText("Settings saved, restart for dark mode to take effect.");
    }

    public void settingsLoadConfigs(ChangeEvent e) {
        AdminID.setText(config.getAdminID());
        serversTextArea.setText(config.getServers());
        darkModeCheckBox.setSelected(config.isDarkMode());
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
        AdminIDSaveNotify.setText("Restart to apply default configs");
        writeConfig(config);
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Ben Adams
        tabbedPane1 = new JTabbedPane();
        var panel1 = new JPanel();
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
        var panel4 = new JPanel();
        var toolBar4 = new JToolBar();
        clearBackupButton = new JButton();
        parseButton = new JButton();
        var scrollPane1 = new JScrollPane();
        gridBackupEntry = new JTextArea();
        var panel5 = new JPanel();
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
        var panel6 = new JPanel();
        AdminID = new JTextField();
        var label3 = new JLabel();
        label5 = new JLabel();
        label4 = new JLabel();
        scrollPane2 = new JScrollPane();
        serversTextArea = new JTextArea();
        var vSpacer1 = new Spacer();
        var vSpacer2 = new Spacer();
        var vSpacer3 = new Spacer();
        var vSpacer4 = new Spacer();
        var hSpacer2 = new Spacer();
        var vSpacer5 = new Spacer();
        AdminIDSaveButton = new JButton();
        darkModeCheckBox = new JCheckBox();
        adminConfigDefault = new JButton();
        var vSpacer7 = new Spacer();
        AdminIDSaveNotify = new JLabel();

        //======== this ========
        setAlignmentX(0.1F);
        setFocusTraversalPolicyProvider(true);
        setForeground(new Color(95, 100, 102));
        setMaximumSize(new Dimension(825, 280));
        setMinimumSize(new Dimension(825, 280));
        setPreferredSize(new Dimension(825, 280));
        setBorder ( new javax . swing. border .CompoundBorder ( new javax . swing. border .TitledBorder ( new javax . swing. border
        .EmptyBorder ( 0, 0 ,0 , 0) ,  "JF\u006frmD\u0065sig\u006eer \u0045val\u0075ati\u006fn" , javax. swing .border . TitledBorder. CENTER ,javax
        . swing. border .TitledBorder . BOTTOM, new java. awt .Font ( "Dia\u006cog", java .awt . Font. BOLD ,
        12 ) ,java . awt. Color .red ) , getBorder () ) );  addPropertyChangeListener( new java. beans
        .PropertyChangeListener ( ){ @Override public void propertyChange (java . beans. PropertyChangeEvent e) { if( "\u0062ord\u0065r" .equals ( e.
        getPropertyName () ) )throw new RuntimeException( ) ;} } );
        setLayout(new BorderLayout());

        //======== tabbedPane1 ========
        {
            tabbedPane1.setEnabled(true);
            tabbedPane1.setToolTipText("Commands, might be useful for tickets or something");
            tabbedPane1.addChangeListener(e -> settingsLoadConfigs(e));

            //======== panel1 ========
            {
                panel1.setLayout(new GridLayoutManager(15, 5, new Insets(0, 0, 0, 0), -1, -1));

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
                panel1.add(panel2, new GridConstraints(1, 3, 14, 2,
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
                        GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_FIXED,
                        null, null, null));

                    //---- adminModeButton ----
                    adminModeButton.setText("Admin Mode");
                    adminModeButton.setToolTipText("Set admin rank");
                    adminModeButton.setVerifyInputWhenFocusTarget(true);
                    panel3.add(adminModeButton, new GridConstraints(0, 0, 1, 1,
                        GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_FIXED,
                        null, null, null));

                    //---- playerModeButton ----
                    playerModeButton.setBorderPainted(true);
                    playerModeButton.setText("Player Mode");
                    playerModeButton.setToolTipText("Remove admin rank");
                    panel3.add(playerModeButton, new GridConstraints(1, 0, 1, 1,
                        GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_FIXED,
                        null, null, null));
                    panel3.add(hSpacer1, new GridConstraints(3, 0, 1, 1,
                        GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                        GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK,
                        null, null, null));
                }
                panel1.add(panel3, new GridConstraints(1, 0, 14, 3,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    null, null, null));
                panel1.add(PlayerName, new GridConstraints(0, 3, 1, 1,
                    GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
                    GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                    GridConstraints.SIZEPOLICY_FIXED,
                    null, null, null));

                //---- label1 ----
                label1.setText("Playername/SteamID64:   ");
                panel1.add(label1, new GridConstraints(0, 2, 1, 1,
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
                panel1.add(ServerSelect, new GridConstraints(0, 1, 1, 1,
                    GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
                    GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_FIXED,
                    null, null, null));

                //---- label2 ----
                label2.setText("  Server:   ");
                panel1.add(label2, new GridConstraints(0, 0, 1, 1,
                    GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                    GridConstraints.SIZEPOLICY_FIXED,
                    GridConstraints.SIZEPOLICY_FIXED,
                    null, null, null));

                //---- clearPlayerButton ----
                clearPlayerButton.setText("Clear");
                clearPlayerButton.setToolTipText("Clear playername");
                panel1.add(clearPlayerButton, new GridConstraints(0, 4, 1, 1,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_FIXED,
                    null, null, null));
            }
            tabbedPane1.addTab("Commands", panel1);

            //======== panel4 ========
            {
                panel4.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));

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
                panel4.add(toolBar4, new GridConstraints(1, 0, 1, 1,
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
                panel4.add(scrollPane1, new GridConstraints(0, 0, 1, 1,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                    null, null, null));
            }
            tabbedPane1.addTab("Grid Backup", panel4);

            //======== panel5 ========
            {
                panel5.setLayout(new GridLayoutManager(8, 7, new Insets(0, 0, 0, 0), 0, 0));

                //---- tScripter ----
                tScripter.setText("/t scripter");
                tScripter.addActionListener(e -> tScripter(e));
                panel5.add(tScripter, new GridConstraints(0, 0, 1, 1,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    null, null, null));

                //---- tVote ----
                tVote.setText("/t vote");
                tVote.addActionListener(e -> {
			tScripter(e);
			tVote(e);
		});
                panel5.add(tVote, new GridConstraints(0, 2, 1, 1,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    null, null, null));

                //---- tName ----
                tName.setText("/t name");
                tName.addActionListener(e -> {
			tScripter(e);
			tName(e);
		});
                panel5.add(tName, new GridConstraints(0, 4, 1, 1,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    null, null, null));

                //---- tSupply ----
                tSupply.setText("/t supply");
                tSupply.addActionListener(e -> {
			tScripter(e);
			tSupply(e);
		});
                panel5.add(tSupply, new GridConstraints(0, 6, 1, 1,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    null, null, null));

                //---- tFixship ----
                tFixship.setText("/t fixship");
                tFixship.addActionListener(e -> {
			tScripter(e);
			tFixship(e);
		});
                panel5.add(tFixship, new GridConstraints(1, 0, 1, 1,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    null, null, null));

                //---- tSDNN ----
                tSDNN.setText("/t sdnn");
                tSDNN.addActionListener(e -> {
			tScripter(e);
			tSDNN(e);
		});
                panel5.add(tSDNN, new GridConstraints(1, 2, 1, 1,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    null, null, null));

                //---- tWikiEdit ----
                tWikiEdit.setText("/t wikiedit");
                tWikiEdit.addActionListener(e -> {
			tScripter(e);
			tWikiEdit(e);
		});
                panel5.add(tWikiEdit, new GridConstraints(1, 4, 1, 1,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    null, null, null));

                //---- tWiki ----
                tWiki.setText("/t wiki");
                tWiki.addActionListener(e -> {
			tScripter(e);
			tWiki(e);
		});
                panel5.add(tWiki, new GridConstraints(1, 6, 1, 1,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    null, null, null));

                //---- tCleanup ----
                tCleanup.setText("/t cleanup");
                tCleanup.addActionListener(e -> {
			tScripter(e);
			tCleanup(e);
		});
                panel5.add(tCleanup, new GridConstraints(3, 0, 1, 1,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    null, null, null));

                //---- tLog ----
                tLog.setText("/t log");
                tLog.addActionListener(e -> {
			tScripter(e);
			tLog(e);
		});
                panel5.add(tLog, new GridConstraints(3, 2, 1, 1,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    null, null, null));

                //---- tLoadCrash ----
                tLoadCrash.setText("/t loadcrash");
                tLoadCrash.addActionListener(e -> {
			tScripter(e);
			tLoadCrash(e);
		});
                panel5.add(tLoadCrash, new GridConstraints(3, 4, 1, 1,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    null, null, null));

                //---- tConntrouble ----
                tConntrouble.setText("/t conntrouble");
                tConntrouble.addActionListener(e -> {
			tScripter(e);
			tConntrouble(e);
		});
                panel5.add(tConntrouble, new GridConstraints(3, 6, 1, 1,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    null, null, null));

                //---- tDIE ----
                tDIE.setText("/t die");
                tDIE.addActionListener(e -> {
			tScripter(e);
			tDIE(e);
		});
                panel5.add(tDIE, new GridConstraints(5, 0, 1, 1,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    null, null, null));

                //---- tWait ----
                tWait.setText("/t wait");
                tWait.addActionListener(e -> {
			tScripter(e);
			tWait(e);
		});
                panel5.add(tWait, new GridConstraints(5, 2, 1, 1,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    null, null, null));

                //---- tWait2 ----
                tWait2.setText("/t wait2");
                tWait2.addActionListener(e -> {
			tScripter(e);
			tWait2(e);
		});
                panel5.add(tWait2, new GridConstraints(5, 4, 1, 1,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    null, null, null));

                //---- tTime ----
                tTime.setText("/t time");
                tTime.addActionListener(e -> {
			tScripter(e);
			tTime(e);
		});
                panel5.add(tTime, new GridConstraints(5, 6, 1, 1,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    null, null, null));

                //---- tCopiedToClipboard ----
                tCopiedToClipboard.setText("#");
                panel5.add(tCopiedToClipboard, new GridConstraints(7, 0, 1, 7,
                    GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    null, null, null));
            }
            tabbedPane1.addTab("xelA Tags", panel5);

            //======== panel6 ========
            {
                panel6.setLayout(new GridLayoutManager(10, 15, new Insets(0, 0, 0, 0), -1, -1));
                panel6.add(AdminID, new GridConstraints(2, 1, 1, 14,
                    GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
                    GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                    GridConstraints.SIZEPOLICY_FIXED,
                    null, null, null));

                //---- label3 ----
                label3.setText("Enter  your SteamID64 and click save.");
                panel6.add(label3, new GridConstraints(0, 1, 2, 14,
                    GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                    GridConstraints.SIZEPOLICY_FIXED,
                    GridConstraints.SIZEPOLICY_FIXED,
                    null, null, null));

                //---- label5 ----
                label5.setText("Customize servers available in server select here");
                panel6.add(label5, new GridConstraints(3, 1, 1, 1,
                    GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    null, null, null));

                //---- label4 ----
                label4.setText("Unless using default configs, use the prefixes for the servers (x0 instead of DXL, x1 instead of DX1, etc.)");
                panel6.add(label4, new GridConstraints(3, 2, 1, 1,
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
                panel6.add(scrollPane2, new GridConstraints(4, 1, 4, 1,
                    GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_BOTH,
                    GridConstraints.SIZEPOLICY_FIXED,
                    GridConstraints.SIZEPOLICY_FIXED,
                    null, null, null));
                panel6.add(vSpacer1, new GridConstraints(6, 14, 2, 1,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK,
                    GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                    null, null, null));
                panel6.add(vSpacer2, new GridConstraints(7, 1, 1, 13,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK,
                    GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                    null, null, null));
                panel6.add(vSpacer3, new GridConstraints(6, 1, 1, 13,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK,
                    GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                    null, null, null));
                panel6.add(vSpacer4, new GridConstraints(4, 13, 1, 1,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK,
                    GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                    null, null, null));
                panel6.add(hSpacer2, new GridConstraints(4, 12, 1, 1,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                    GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK,
                    null, null, null));
                panel6.add(vSpacer5, new GridConstraints(6, 0, 1, 1,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK,
                    GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                    null, null, null));

                //---- AdminIDSaveButton ----
                AdminIDSaveButton.setText("Save");
                AdminIDSaveButton.addActionListener(e -> AdminStuffSave(e));
                panel6.add(AdminIDSaveButton, new GridConstraints(8, 1, 1, 1,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_FIXED,
                    new Dimension(50, 20), null, null));

                //---- darkModeCheckBox ----
                darkModeCheckBox.setSelected(false);
                darkModeCheckBox.setText("Dark Mode");
                darkModeCheckBox.addChangeListener(e -> darkModeCheckBoxStateChanged(e));
                darkModeCheckBox.addActionListener(e -> darkMode(e));
                panel6.add(darkModeCheckBox, new GridConstraints(8, 2, 1, 1,
                    GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_FIXED,
                    new Dimension(120, 50), new Dimension(120, 50), new Dimension(120, 50)));

                //---- adminConfigDefault ----
                adminConfigDefault.setText("Restore Default Configs");
                adminConfigDefault.addActionListener(e -> adminConfigDefault(e));
                panel6.add(adminConfigDefault, new GridConstraints(8, 3, 1, 1,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    null, null, null));
                panel6.add(vSpacer7, new GridConstraints(8, 7, 1, 1,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK,
                    GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                    null, null, null));

                //---- AdminIDSaveNotify ----
                AdminIDSaveNotify.setText("#");
                panel6.add(AdminIDSaveNotify, new GridConstraints(9, 1, 1, 1,
                    GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                    GridConstraints.SIZEPOLICY_FIXED,
                    GridConstraints.SIZEPOLICY_FIXED,
                    null, null, null));
            }
            tabbedPane1.addTab("Settings", panel6);
        }
        add(tabbedPane1, BorderLayout.CENTER);
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
    private JTextField AdminID;
    private JLabel label5;
    private JLabel label4;
    private JScrollPane scrollPane2;
    private JTextArea serversTextArea;
    private JButton AdminIDSaveButton;
    private JCheckBox darkModeCheckBox;
    private JButton adminConfigDefault;
    private JLabel AdminIDSaveNotify;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
