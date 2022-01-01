package adams.im;



import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class GridbackupResults extends JDialog {

    private JTable dataResultsTable;
    private JButton copySelected = new JButton("Copy Selected");
    private JCheckBox inGame = new JCheckBox("In Game");
    private JLabel ClipboardOutput = new JLabel();


    public GridbackupResults(ArrayList<GridBackup> Grids, String server, String player) {

        String[] gridName = new String[Grids.size()];
        String[] entityID = new String[Grids.size()];
        String[] date = new String[Grids.size()];

        for (int i = 0; i < Grids.size(); i++) {
            gridName[i] = Grids.get(i).getgridName();
            entityID[i] = Grids.get(i).getEntityId();
            date[i] = Grids.get(i).getDate();
        }
        String[] columnNames = {"Name", "EntityID", "Backup Time"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (int i = 0; i < gridName.length; i++) {
            Object[] row = {gridName[i], entityID[i], date[i]};
            model.addRow(row);
        }

        this.setTitle("Grid Backup Parse Results");

        Container pane = this.getContentPane();

            setLocationRelativeTo(null);
            setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

            add(ClipboardOutput);
            add(copySelected);
            add(inGame);

            dataResultsTable = new JTable(model);
            add(new JScrollPane(dataResultsTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED),
                BorderLayout.CENTER);

            setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            // setModalityType(ModalityType.APPLICATION_MODAL);
            pack();
            setVisible(true);

            copySelected.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = 0;
                row = dataResultsTable.getSelectedRow();

                String server2 = "";
                String fromConsole = "";
                if(inGame.isSelected()) {
                    server2 = "!";
                } else {
                    server2 = server;
                    fromConsole = " true false";
                }
                CopyToClipboard(server2 + " gridbackup restore " + player + entityID[row] + " " + 1 + fromConsole);
            }
        });

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

}


