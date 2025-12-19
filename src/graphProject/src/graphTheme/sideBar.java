package graphTheme;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;

public class sideBar extends JPanel {
    public JTextArea matrixArea, labelArea, resultArea;
    public JComboBox<String> fromCombo, toCombo;
    public JButton findPathBtn, visualizeBtn;

    public sideBar() {
        setLayout(null);
        setOpaque(false);
        setPreferredSize(new Dimension(1199, 741));

        EmptyBorder marginTeks = new EmptyBorder(10, 10, 10, 10);

        matrixArea = new JTextArea("0 4 2 0\n4 0 1 5\n2 1 0 3\n0 5 3 0");
        matrixArea.setBorder(marginTeks);
        JScrollPane spM = new JScrollPane(matrixArea);
        spM.setBounds(20, 80, 238, 156);
        add(spM);
//        matrixArea.setOpaque(true);
//        matrixArea.setBackground(new Color(255, 0, 0, 50));

        labelArea = new JTextArea("Jakarta Singapur Bangkok Manila");
        labelArea.setBorder(marginTeks);
        JScrollPane spL = new JScrollPane(labelArea);
        spL.setBounds(20, 283, 238, 156);
        add(spL);

        resultArea = new JTextArea("Ready to explore?\nSelect your destination and click 'Find Path'");
        resultArea.setEditable(false);
        resultArea.setOpaque(true);
        resultArea.setBorder(marginTeks);
        JScrollPane spR = new JScrollPane(resultArea);
        spR.setBounds(20, 495, 238, 200);
        spR.setOpaque(false);
        spR.getViewport().setOpaque(false);
        add(spR);

        fromCombo = new JComboBox<>();
        fromCombo.setBounds(342, 82, 195, 29);
        add(fromCombo);

        toCombo = new JComboBox<>();
        toCombo.setBounds(592, 82, 202, 29);
        add(toCombo);

        findPathBtn = new JButton();
        findPathBtn.setBounds(1015, 80, 152, 30);
        findPathBtn.setContentAreaFilled(false);
        findPathBtn.setBorderPainted(false);
        findPathBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        add(findPathBtn);

        visualizeBtn = new JButton();
        visualizeBtn.setBounds(1070, 600, 82, 82);
        visualizeBtn.setContentAreaFilled(false);
        visualizeBtn.setBorderPainted(false);
        visualizeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        add(visualizeBtn);
    }

    public void updateCombos(String[] labels) {
        fromCombo.removeAllItems();
        toCombo.removeAllItems();
        for (String s : labels) {
            fromCombo.addItem(s);
            toCombo.addItem(s);
        }
    }


}