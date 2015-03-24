package presentation.gui.windows.main;

import presentation.controllers.MainController;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class NewWorldWindow extends JDialog {

    private JTextField txtName, txtSeed;
    private JComboBox cmbWorldType, cmbGameType, cmbWorldProvider, cmbDifficulty;
    private JSpinner spinX, spinY, spinZ;
    private JCheckBox chkHardcore;

    private final MainController mainController;

    public NewWorldWindow(JFrame parent, MainController controller) {
        super(parent, "New world", true);

        this.mainController = controller;

        setLocation(200, 200);
        setSize(250, 250);

        buildGUI();
    }

    private void buildGUI() {

        setLayout(new GridLayout(8, 2));

        add(new Label("Schematic name"));
        add(txtName = new JTextField("Untitled"));

        add(new Label("Size (x, y, z)"));

            JPanel pnlCords = new JPanel(new GridLayout(1, 3));
            SpinnerNumberModel numberModelX = new SpinnerNumberModel(16, 1, Short.MAX_VALUE, 1);
            SpinnerNumberModel numberModelY = new SpinnerNumberModel(16, 1, Short.MAX_VALUE, 1);
            SpinnerNumberModel numberModelZ = new SpinnerNumberModel(16, 1, Short.MAX_VALUE, 1);

            pnlCords.add(spinX = new JSpinner(numberModelX));
            pnlCords.add(spinY = new JSpinner(numberModelY));
            pnlCords.add(spinZ = new JSpinner(numberModelZ));

        add(pnlCords);

        add(new Label("World type"));
        add(cmbWorldType = new JComboBox(new IdStringMapping[]{new IdStringMapping(0, "Default"),
                new IdStringMapping(1, "Flat"), new IdStringMapping(2, "Large biomes"),
                new IdStringMapping(3, "Amplified"), new IdStringMapping(8, "Default_1_1")}));

        add(new Label("Game type"));
        add(cmbGameType = new JComboBox(new String[]{"CREATIVE", "SURVIVAL", "ADVENTURE", "NOT_SET"}));

        add(new Label("Seed"));

            NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
            DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
            decimalFormat.setGroupingUsed(false);
            txtSeed = new JFormattedTextField(decimalFormat);
            txtSeed.setText("0");

        add(txtSeed);

        add(new Label("Dimension"));
        add(cmbWorldProvider = new JComboBox(new IdStringMapping[]{new IdStringMapping(0, "Overworld"),
            new IdStringMapping(-1, "Nether"), new IdStringMapping(1, "End")}));

        add(new Label("Difficulty"));
        add(cmbDifficulty = new JComboBox(new IdStringMapping[]{
            new IdStringMapping(0, "Peaceful"), new IdStringMapping(1, "Easy"),
            new IdStringMapping(2, "Normal"), new IdStringMapping(3, "Hard")}));

        add(chkHardcore = new JCheckBox("Hardcore"));

        JButton btnCreate = new JButton("Create");
        add(btnCreate);

        txtName.addCaretListener(e -> btnCreate.setEnabled(!txtName.getText().isEmpty()));

        btnCreate.addActionListener(e -> {

            String name = txtName.getText();
            int xSize = (int) spinX.getValue();
            int ySize = (int) spinY.getValue();
            int zSize = (int) spinZ.getValue();
            IdStringMapping worldType = (IdStringMapping) cmbWorldType.getSelectedItem();
            String gameType = (String) cmbGameType.getSelectedItem();
            long seed = Long.valueOf(txtSeed.getText());
            int worldProvider = ((IdStringMapping) cmbWorldProvider.getSelectedItem()).id;
            boolean hardcore = chkHardcore.isSelected();
            int difficulty = ((IdStringMapping) cmbDifficulty.getSelectedItem()).id;

            mainController.createNewWorld(name, (short) xSize, (short) ySize, (short) zSize,
                    worldType.id, worldType.name, gameType, seed, worldProvider, hardcore, difficulty);

            setVisible(false);
            dispose();
        });

        setVisible(true);
    }

    class IdStringMapping {
        public final int id;
        public final String name;

        public IdStringMapping(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
