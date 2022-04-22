package adams.im;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.TableColumn;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.intellij.uiDesigner.core.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
/*
 * Created by JFormDesigner on Fri Mar 04 15:24:58 EST 2022
 */



/**
 * @author Ben Adams
 */
public class HelperBlockEditor extends JPanel {

    private HelperLogger logger;

    public HelperBlockEditor(HelperLogger logger) {
        initComponents();
        this.logger = logger;
        editorTable.addColumn(new TableColumn(0, 1));
        editorTable.addColumn(new TableColumn(1));
        editorTable.setEditingColumn(0);

    }

    private void loadCubeBlocks() {
        JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        int returnValue = fileChooser.showOpenDialog(null);
        if(returnValue == JFileChooser.APPROVE_OPTION){
            File selectedFile = fileChooser.getSelectedFile();
            filePath.setText(selectedFile.getAbsolutePath());
        }
        populateTable();
    }

    private void populateTable(){
        File sbc = new File(filePath.getText());

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            logger.log(e.toString());
        }
        Document doc = null;
        try {
            doc = db.parse(sbc);
        } catch (SAXException e) {
            logger.log(e.toString());
        } catch (IOException e) {
            logger.log(e.toString());
        }
        doc.getDocumentElement().normalize();

        ArrayList<CubeBlock> cubeBlocks = new ArrayList<>();
        // organise the factions into two lists for easy shit later
        NodeList blockList = doc.getElementsByTagName("Definition");
        for(int i = 0; i < blockList.getLength(); i++) {
            Node block = blockList.item(i);
            if(block.getNodeType() == Node.ELEMENT_NODE){
                Element eElement = (Element) block;
                cubeBlocks.add(new CubeBlock(eElement.getElementsByTagName("TypeId").item(0).getTextContent(), eElement.getElementsByTagName("SubtypeId").item(0).getTextContent()));
                cubeBlocks.get(i).setDefinitionType(eElement.getElementsByTagName("Definition").item(0).getAttributes().item(0).getTextContent());
                cubeBlocks.get(i).setDisplayName(eElement.getElementsByTagName("DisplayName").item(0).getTextContent());
                cubeBlocks.get(i).setDescription(eElement.getElementsByTagName("Description").item(0).getTextContent());
                cubeBlocks.get(i).setCubeSize(eElement.getElementsByTagName("CubeSize").item(0).getTextContent());
                cubeBlocks.get(i).setSize(new int[] {Integer.parseInt(eElement.getElementsByTagName("Size").item(0).getAttributes().item(0).getTextContent()), Integer.parseInt(eElement.getElementsByTagName("Size").item(0).getAttributes().item(1).getTextContent()), Integer.parseInt(eElement.getElementsByTagName("Size").item(0).getAttributes().item(2).getTextContent())});
                ArrayList<String> components = new ArrayList<>();

                // if thruster
                if(eElement.getElementsByTagName("TypeId").item(0).getTextContent().equalsIgnoreCase("thrust")){
                    cubeBlocks.get(i).setThrusterType(eElement.getElementsByTagName("ThrusterType").item(0).getTextContent());
                    cubeBlocks.get(i).setMaxPowerConsumption(Double.parseDouble(eElement.getElementsByTagName("MaxPowerConsumption").item(0).getTextContent()));
                    cubeBlocks.get(i).setMinPowerConsumption(Double.parseDouble(eElement.getElementsByTagName("MinPowerConsumption").item(0).getTextContent()));
                    cubeBlocks.get(i).setForceMagnitude(Integer.parseInt(eElement.getElementsByTagName("ForceMagnitude").item(0).getTextContent()));
                    cubeBlocks.get(i).setSlowdownFactor(Integer.parseInt(eElement.getElementsByTagName("SlowdownFactor").item(0).getTextContent()));
                }
                }
            }
        }



    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        blockPicker = new JComboBox();
        scrollPane1 = new JScrollPane();
        editorTable = new JTable();
        loadCubeBlocks = new JButton();
        filePath = new JTextField();
        saveCubeBlocks = new JButton();

        //======== this ========
        setLayout(new GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
        add(blockPicker, new GridConstraints(0, 0, 1, 1,
            GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null, null, null));

        //======== scrollPane1 ========
        {

            //---- editorTable ----
            editorTable.setCellSelectionEnabled(true);
            scrollPane1.setViewportView(editorTable);
        }
        add(scrollPane1, new GridConstraints(1, 0, 1, 3,
            GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null, null, null));

        //---- loadCubeBlocks ----
        loadCubeBlocks.setText("Load File");
        loadCubeBlocks.addActionListener(e -> loadCubeBlocks());
        add(loadCubeBlocks, new GridConstraints(2, 0, 1, 1,
            GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null, null, null));

        //---- filePath ----
        filePath.setText("C:\\Users\\adams\\Desktop\\!!!Sigma Draconis\\CubeBlocks_FusionDrive.sbc");
        add(filePath, new GridConstraints(2, 1, 1, 1,
            GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null, null, null));

        //---- saveCubeBlocks ----
        saveCubeBlocks.setText("Save File");
        add(saveCubeBlocks, new GridConstraints(2, 2, 1, 1,
            GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null, null, null));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JComboBox blockPicker;
    private JScrollPane scrollPane1;
    private JTable editorTable;
    private JButton loadCubeBlocks;
    private JTextField filePath;
    private JButton saveCubeBlocks;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
