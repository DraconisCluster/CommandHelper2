package adams.im;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

public class GridbackupResults extends JDialog {

    private JTable dataResultsTable;
    private JButton copySelected = new JButton("Copy Selected");
    private JCheckBox inGame = new JCheckBox("In Game");
    private JLabel ClipboardOutput = new JLabel();

    private HelperLogger logger;


    public GridbackupResults(ArrayList<GridBackup> Grids, String server, String player, HelperLogger logger) {

        this.logger = logger;

        String[] gridName = new String[Grids.size()];
        String[] entityID = new String[Grids.size()];
        String[] date = new String[Grids.size()];

        for (int i = 0; i < Grids.size(); i++) {
            gridName[i] = Grids.get(i).getgridName();
            entityID[i] = Grids.get(i).getEntityId();
            date[i] = Grids.get(i).getDate();
        }

        // set column names for table
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

            // make new table, and sort by date newest to oldest
            dataResultsTable = new JTable(model);
            TableRowSorter<TableModel> sort = new TableRowSorter<TableModel>(dataResultsTable.getModel());
            dataResultsTable.setRowSorter(sort);
            java.util.List<RowSorter.SortKey> sortList = new ArrayList<>();
            sortList.add(new RowSorter.SortKey(2, SortOrder.DESCENDING));
            sort.setSortKeys(sortList);

            add(new JScrollPane(dataResultsTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED),
                BorderLayout.CENTER);
            setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            //setModalityType(ModalityType.APPLICATION_MODAL);;
            pack();
            setVisible(true);

            dataResultsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                getCommand(server, player, entityID);
                }
            });

            copySelected.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getCommand(server, player, entityID);
            }
        });
    }

    public void getCommand(String server, String player, String[] entityID ){
        int row = dataResultsTable.convertRowIndexToModel(dataResultsTable.getSelectedRow());

        logger.log("Gridbackup row " + row + " selected");

        String server2 = "";
        String fromConsole = "";
        if(inGame.isSelected()) {
            server2 = "!";
        } else {
            server2 = server;
            fromConsole = " true false";
        }
        CopyToClipboard(server2 + "gridbackup restore " + player + " " + entityID[row] + " " + 1 + fromConsole);
    }

    public void CopyToClipboard(String command) {
        Toolkit tk = Toolkit.getDefaultToolkit();
        Clipboard clipboard1 = tk.getSystemClipboard();
        StringSelection outputString = new StringSelection(command);
        clipboard1.setContents(outputString, null);
        ClipboardOutput.setText("Copied " + command + " to clipboard.");
        logger.log("Successfully copied " + command + " to clipboard.");
    }
}


