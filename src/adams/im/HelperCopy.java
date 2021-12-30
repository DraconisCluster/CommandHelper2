package adams.im;

import javax.imageio.ImageIO;
import javax.swing.*;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;


public class HelperCopy {
    private JPanel Window;
    private JTabbedPane tabbedPane1;
    @SuppressWarnings("rawtypes")
    private JComboBox ServerSelect;
    private JTextField PlayerName;
    private JButton gridbackupListButton;
    private JButton hangarListButton;
    private JButton ownedGridsListButton;
    private JButton createdGridsListButton;
    private JButton clearAdminSettingsButton;
    private JButton adminModeButton;
    private JButton playerModeButton;
    private JButton scripterButton;
    private JButton transferGridButton;
    private JButton getSteamID64Button;
    private JButton listPlayersButton;
    private JButton onlineServersButton;
    private JButton checkBlocklimitsButton;
    private JButton updateBlocklimitsButton;
    private JButton clusterUptimeButton;
    private JLabel ClipboardOutput;
    private JTextField AdminID;
    private JButton AdminIDSaveButton;
    private JCheckBox darkModeCheckBox;
    private JLabel AdminIDSaveNotify;
    private JTextArea gridBackupEntry;
    private JButton clearBackupButton;
    private JButton parseButton;
    private JButton clearPlayerButton;

    public HelperCopy() {
        // Gridbackup
        gridbackupListButton.addActionListener(e -> CopyToClipboard(checkServer() + "gridbackup list " + checkPlayer()));
        // HM List
        hangarListButton.addActionListener(e -> CopyToClipboard(checkServer() + "hm list " + checkPlayer()));
        // Owner List
        ownedGridsListButton.addActionListener(e -> CopyToClipboard(checkServer() + "listgridsowner " + checkPlayer()));
        // Author List
        createdGridsListButton.addActionListener(e -> CopyToClipboard(checkServer() + "listgridsauthor " + checkPlayer()));
        // NO DARK MODE
        darkModeCheckBox.addActionListener(e -> JOptionPane.showMessageDialog(Window, "NO DARK MODE! FUCK YOU!"));
        // Parse Gridbackup
        parseButton.addActionListener(e -> gridbackupParse());
        // clear gridbackup entry
        clearBackupButton.addActionListener(e -> gridBackupEntry.setText(""));
        // clear playerid entry
        clearPlayerButton.addActionListener(e -> PlayerName.setText(""));
        // scripter
        scripterButton.addActionListener(e -> CopyToClipboard(checkServer() + "admin setrank  " + checkPlayer() + " 1"));
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
            } else  {
                CopyToClipboard("DX" + " admin clearadminsettings " + AdminID.getText());
            }

        });
        // nexus stuff
        onlineServersButton.addActionListener(e -> CopyToClipboard("/nexus onlineservers"));
        // more nexus stuff
        clusterUptimeButton.addActionListener(e -> CopyToClipboard("/nexus uptime"));
        // blocklimit limit
        checkBlocklimitsButton.addActionListener(e -> CopyToClipboard(checkServer() + "blocklimit playerlimit " + checkPlayer()));
        // blocklimit update
        updateBlocklimitsButton.addActionListener(e -> CopyToClipboard(checkServer() + "blocklimit update -player=" + checkPlayer()));
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
        System.out.println(Grids);
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
    }

    public String checkServer() {

        return switch ((String) ServerSelect.getSelectedItem()) {
            case "DXL" -> "x0 ";
            case "DX1" -> "x1 ";
            case "DX2" -> "x2 ";
            case "DX3" -> "x3 ";
            case "DX4" -> "x4 ";
            case "DX5" -> "x5 ";
            case "DX6" -> "x6 ";
            case "D1" -> "d1 ";
            case "D2" -> "d2 ";
            case "D3" -> "d3 ";
            case "D4" -> "d4 ";
            case "D5" -> "d5 ";
            case "D6" -> "d6 ";
            case "##" -> "## ";
            default -> "!";
        };
    }

    public String checkPlayer() {
        String player = PlayerName.getText();
        return '"' + player + '"';
    }

    public static void main(String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {

        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");

        JFrame frame = new JFrame("Command Helper 2 v1.0.0");

        frame.setContentPane(new HelperCopy().Window);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);


    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        Window = new JPanel();
        Window.setLayout(new BorderLayout(0, 0));
        Window.setAlignmentX(0.1f);
        Window.setFocusTraversalPolicyProvider(true);
        Window.setForeground(new Color(-10525594));
        Window.setMaximumSize(new Dimension(825, 280));
        Window.setMinimumSize(new Dimension(825, 280));
        Window.setPreferredSize(new Dimension(825, 280));
        tabbedPane1 = new JTabbedPane();
        tabbedPane1.setEnabled(true);
        tabbedPane1.setToolTipText("Commands, might be useful for tickets or something");
        Window.add(tabbedPane1, BorderLayout.CENTER);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(15, 5, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Commands", panel1);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(1, 3, 14, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JToolBar toolBar1 = new JToolBar();
        toolBar1.setFloatable(false);
        panel2.add(toolBar1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 50), null, 0, false));
        final JToolBar.Separator toolBar$Separator1 = new JToolBar.Separator();
        toolBar1.add(toolBar$Separator1);
        gridbackupListButton = new JButton();
        gridbackupListButton.setMaximumSize(new Dimension(125, 50));
        gridbackupListButton.setMinimumSize(new Dimension(125, 50));
        gridbackupListButton.setPreferredSize(new Dimension(125, 50));
        gridbackupListButton.setText("Gridbackup List");
        gridbackupListButton.setToolTipText("List gridbackups");
        toolBar1.add(gridbackupListButton);
        final JToolBar.Separator toolBar$Separator2 = new JToolBar.Separator();
        toolBar1.add(toolBar$Separator2);
        hangarListButton = new JButton();
        hangarListButton.setMaximumSize(new Dimension(125, 50));
        hangarListButton.setMinimumSize(new Dimension(125, 50));
        hangarListButton.setPreferredSize(new Dimension(125, 50));
        hangarListButton.setText("Hangar List");
        hangarListButton.setToolTipText("List hangar");
        toolBar1.add(hangarListButton);
        final JToolBar.Separator toolBar$Separator3 = new JToolBar.Separator();
        toolBar1.add(toolBar$Separator3);
        ownedGridsListButton = new JButton();
        ownedGridsListButton.setMaximumSize(new Dimension(125, 50));
        ownedGridsListButton.setMinimumSize(new Dimension(125, 50));
        ownedGridsListButton.setPreferredSize(new Dimension(125, 50));
        ownedGridsListButton.setText("Owned Grids List");
        ownedGridsListButton.setToolTipText("List the grids a player has ownership of");
        toolBar1.add(ownedGridsListButton);
        final JToolBar.Separator toolBar$Separator4 = new JToolBar.Separator();
        toolBar1.add(toolBar$Separator4);
        createdGridsListButton = new JButton();
        createdGridsListButton.setMaximumSize(new Dimension(125, 50));
        createdGridsListButton.setMinimumSize(new Dimension(125, 50));
        createdGridsListButton.setPreferredSize(new Dimension(125, 50));
        createdGridsListButton.setText("Created Grids List");
        createdGridsListButton.setToolTipText("List the grids a player has created");
        toolBar1.add(createdGridsListButton);
        final JToolBar.Separator toolBar$Separator5 = new JToolBar.Separator();
        toolBar1.add(toolBar$Separator5);
        final JToolBar toolBar2 = new JToolBar();
        toolBar2.setFloatable(false);
        panel2.add(toolBar2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 50), null, 0, false));
        final JToolBar.Separator toolBar$Separator6 = new JToolBar.Separator();
        toolBar2.add(toolBar$Separator6);
        scripterButton = new JButton();
        scripterButton.setContentAreaFilled(true);
        scripterButton.setMaximumSize(new Dimension(125, 50));
        scripterButton.setMinimumSize(new Dimension(125, 50));
        scripterButton.setPreferredSize(new Dimension(125, 50));
        scripterButton.setText("Scripter");
        scripterButton.setToolTipText("Set scripter rank");
        toolBar2.add(scripterButton);
        final JToolBar.Separator toolBar$Separator7 = new JToolBar.Separator();
        toolBar2.add(toolBar$Separator7);
        transferGridButton = new JButton();
        transferGridButton.setMaximumSize(new Dimension(125, 50));
        transferGridButton.setMinimumSize(new Dimension(125, 50));
        transferGridButton.setPreferredSize(new Dimension(125, 50));
        transferGridButton.setText("Transfer Grid");
        transferGridButton.setToolTipText("Run while looking at a grid to transfer to player");
        toolBar2.add(transferGridButton);
        final JToolBar.Separator toolBar$Separator8 = new JToolBar.Separator();
        toolBar2.add(toolBar$Separator8);
        getSteamID64Button = new JButton();
        getSteamID64Button.setMaximumSize(new Dimension(125, 50));
        getSteamID64Button.setMinimumSize(new Dimension(125, 50));
        getSteamID64Button.setPreferredSize(new Dimension(125, 50));
        getSteamID64Button.setText("Get SteamID64");
        getSteamID64Button.setToolTipText("Get player steamid");
        toolBar2.add(getSteamID64Button);
        final JToolBar.Separator toolBar$Separator9 = new JToolBar.Separator();
        toolBar2.add(toolBar$Separator9);
        listPlayersButton = new JButton();
        listPlayersButton.setMaximumSize(new Dimension(125, 50));
        listPlayersButton.setMinimumSize(new Dimension(125, 50));
        listPlayersButton.setPreferredSize(new Dimension(125, 50));
        listPlayersButton.setText("List Players");
        listPlayersButton.setToolTipText("Get a list of players on the server");
        toolBar2.add(listPlayersButton);
        final JToolBar.Separator toolBar$Separator10 = new JToolBar.Separator();
        toolBar2.add(toolBar$Separator10);
        final JToolBar toolBar3 = new JToolBar();
        toolBar3.setFloatable(false);
        panel2.add(toolBar3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 50), null, 0, false));
        final JToolBar.Separator toolBar$Separator11 = new JToolBar.Separator();
        toolBar3.add(toolBar$Separator11);
        onlineServersButton = new JButton();
        onlineServersButton.setInheritsPopupMenu(false);
        onlineServersButton.setMaximumSize(new Dimension(125, 50));
        onlineServersButton.setMinimumSize(new Dimension(125, 50));
        onlineServersButton.setPreferredSize(new Dimension(125, 50));
        onlineServersButton.setText("Online Servers");
        onlineServersButton.setToolTipText("See online servers");
        toolBar3.add(onlineServersButton);
        final JToolBar.Separator toolBar$Separator12 = new JToolBar.Separator();
        toolBar3.add(toolBar$Separator12);
        clusterUptimeButton = new JButton();
        clusterUptimeButton.setMaximumSize(new Dimension(125, 50));
        clusterUptimeButton.setMinimumSize(new Dimension(125, 50));
        clusterUptimeButton.setPreferredSize(new Dimension(125, 50));
        clusterUptimeButton.setText("Controller Uptime");
        clusterUptimeButton.setToolTipText("Get uptime of the Nexus controllers");
        toolBar3.add(clusterUptimeButton);
        final JToolBar.Separator toolBar$Separator13 = new JToolBar.Separator();
        toolBar3.add(toolBar$Separator13);
        checkBlocklimitsButton = new JButton();
        checkBlocklimitsButton.setMaximumSize(new Dimension(125, 50));
        checkBlocklimitsButton.setMinimumSize(new Dimension(125, 50));
        checkBlocklimitsButton.setPreferredSize(new Dimension(125, 50));
        checkBlocklimitsButton.setText("Check Blocklimits");
        checkBlocklimitsButton.setToolTipText("Check blocklimits of a player");
        toolBar3.add(checkBlocklimitsButton);
        final JToolBar.Separator toolBar$Separator14 = new JToolBar.Separator();
        toolBar3.add(toolBar$Separator14);
        updateBlocklimitsButton = new JButton();
        updateBlocklimitsButton.setMaximumSize(new Dimension(125, 50));
        updateBlocklimitsButton.setMinimumSize(new Dimension(125, 50));
        updateBlocklimitsButton.setPreferredSize(new Dimension(125, 50));
        updateBlocklimitsButton.setText("Update Blocklimits");
        updateBlocklimitsButton.setToolTipText("Update a players blocklimts");
        toolBar3.add(updateBlocklimitsButton);
        final JToolBar.Separator toolBar$Separator15 = new JToolBar.Separator();
        toolBar3.add(toolBar$Separator15);
        ClipboardOutput = new JLabel();
        ClipboardOutput.setText("");
        panel2.add(ClipboardOutput, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel3, new GridConstraints(1, 0, 14, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        clearAdminSettingsButton = new JButton();
        clearAdminSettingsButton.setText("Clear Admin Settings");
        clearAdminSettingsButton.setToolTipText("YOU MUST RUN THIS AFTER LEAVING ADMIN MODE");
        panel3.add(clearAdminSettingsButton, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 60), null, 0, false));
        adminModeButton = new JButton();
        adminModeButton.setText("Admin Mode");
        adminModeButton.setToolTipText("Set admin rank");
        adminModeButton.setVerifyInputWhenFocusTarget(true);
        panel3.add(adminModeButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 60), null, 0, false));
        playerModeButton = new JButton();
        playerModeButton.setBorderPainted(true);
        playerModeButton.setText("Player Mode");
        playerModeButton.setToolTipText("Remove admin rank");
        panel3.add(playerModeButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 60), null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel3.add(spacer1, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        PlayerName = new JTextField();
        panel1.add(PlayerName, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Playername/SteamID64:   ");
        panel1.add(label1, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        ServerSelect = new JComboBox();
        ServerSelect.setDoubleBuffered(true);
        ServerSelect.setLightWeightPopupEnabled(true);
        ServerSelect.setMaximumRowCount(20);
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("!");
        defaultComboBoxModel1.addElement("DX");
        defaultComboBoxModel1.addElement("DXL");
        defaultComboBoxModel1.addElement("DX1");
        defaultComboBoxModel1.addElement("DX2");
        defaultComboBoxModel1.addElement("DX3");
        defaultComboBoxModel1.addElement("DX4");
        defaultComboBoxModel1.addElement("DX5");
        defaultComboBoxModel1.addElement("DX6");
        defaultComboBoxModel1.addElement("--");
        defaultComboBoxModel1.addElement("D");
        defaultComboBoxModel1.addElement("D1");
        defaultComboBoxModel1.addElement("D2");
        defaultComboBoxModel1.addElement("D3");
        defaultComboBoxModel1.addElement("D4");
        defaultComboBoxModel1.addElement("D5");
        defaultComboBoxModel1.addElement("--");
        defaultComboBoxModel1.addElement("##");
        ServerSelect.setModel(defaultComboBoxModel1);
        ServerSelect.setToolTipText("Select a server to run the command on");
        panel1.add(ServerSelect, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("  Server:   ");
        panel1.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        clearPlayerButton = new JButton();
        clearPlayerButton.setText("Clear");
        clearPlayerButton.setToolTipText("Clear playername");
        panel1.add(clearPlayerButton, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Grid Backup", panel4);
        final JToolBar toolBar4 = new JToolBar();
        toolBar4.setFloatable(false);
        panel4.add(toolBar4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 50), null, 0, false));
        final JToolBar.Separator toolBar$Separator16 = new JToolBar.Separator();
        toolBar4.add(toolBar$Separator16);
        clearBackupButton = new JButton();
        clearBackupButton.setMaximumSize(new Dimension(78, 50));
        clearBackupButton.setMinimumSize(new Dimension(78, 50));
        clearBackupButton.setPreferredSize(new Dimension(78, 50));
        clearBackupButton.setText("Clear");
        toolBar4.add(clearBackupButton);
        final JToolBar.Separator toolBar$Separator17 = new JToolBar.Separator();
        toolBar4.add(toolBar$Separator17);
        parseButton = new JButton();
        parseButton.setMaximumSize(new Dimension(78, 50));
        parseButton.setMinimumSize(new Dimension(78, 50));
        parseButton.setPreferredSize(new Dimension(78, 50));
        parseButton.setText("Parse");
        toolBar4.add(parseButton);
        final JToolBar.Separator toolBar$Separator18 = new JToolBar.Separator();
        toolBar4.add(toolBar$Separator18);
        final JScrollPane scrollPane1 = new JScrollPane();
        panel4.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        gridBackupEntry = new JTextArea();
        gridBackupEntry.setText("");
        scrollPane1.setViewportView(gridBackupEntry);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("xelA Tags", panel5);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(8, 6, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Settings", panel6);
        AdminID = new JTextField();
        panel6.add(AdminID, new GridConstraints(2, 0, 1, 6, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Enter  your SteamID64 and click save");
        panel6.add(label3, new GridConstraints(0, 0, 2, 6, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel6.add(spacer2, new GridConstraints(5, 5, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel6.add(spacer3, new GridConstraints(6, 0, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        panel6.add(spacer4, new GridConstraints(5, 0, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer5 = new Spacer();
        panel6.add(spacer5, new GridConstraints(4, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer6 = new Spacer();
        panel6.add(spacer6, new GridConstraints(4, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        AdminIDSaveButton = new JButton();
        AdminIDSaveButton.setText("Save");
        panel6.add(AdminIDSaveButton, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(50, 20), null, null, 0, false));
        darkModeCheckBox = new JCheckBox();
        darkModeCheckBox.setSelected(false);
        darkModeCheckBox.setText("Dark Mode");
        panel6.add(darkModeCheckBox, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(120, 50), new Dimension(120, 50), new Dimension(120, 50), 0, false));
        AdminIDSaveNotify = new JLabel();
        AdminIDSaveNotify.setText("");
        panel6.add(AdminIDSaveNotify, new GridConstraints(3, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return Window;
    }

}
