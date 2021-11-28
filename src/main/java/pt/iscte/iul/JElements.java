package pt.iscte.iul;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class JElements implements ActionListener {

    private final JFrame frame;
    private final JSpinner spinner;
    private final JButton button;
    private final ArrayList<TrelloAPI.HoursPerUser> hoursPerUsers;
    private JTable table;
    private String[][] data;

    public JElements(JFrame frame, ArrayList<TrelloAPI.HoursPerUser> hoursPerUsers) {
        this.frame = frame;
        this.hoursPerUsers = hoursPerUsers;

        //Spinner
        SpinnerModel model = new SpinnerNumberModel(20, 20, 100, 1);
        this.spinner = new JSpinner(model);
        this.spinner.setBounds(((frame.getWidth() - 200)), ((frame.getHeight() / 2) - 65), 100, 40);

        addTable(this.hoursPerUsers, this.frame , this.spinner);

        //Button
        this.button = new JButton("Calculate new Cost");
        this.button.setBounds((frame.getWidth() - 220), ((frame.getHeight() / 2) - 25), 150, 50);
        this.button.setVisible(true);
        this.frame.add(button);
        this.button.repaint();
        this.button.addActionListener(this);

        this.frame.add(spinner);

        this.frame.setVisible(true);
        this.frame.repaint();
    }

    //Table
    public void addTable(ArrayList<TrelloAPI.HoursPerUser> hoursPerUsers, JFrame frame , JSpinner spinner){
        double totalEstimated = 0;
        double totalSpent = 0;

        this.data = new String[hoursPerUsers.size() + 2][4];
        String[] names = {"Member", "Estimated Hours", "Spent Hours", "Cost (€)"};

        data[0] = names;

        for (int cont = 0; cont != hoursPerUsers.size(); cont++) {
            data[cont + 1] = new String[]{hoursPerUsers.get(cont).getUser(),
                    String.valueOf(hoursPerUsers.get(cont).getEstimatedHours()),
                    String.valueOf(hoursPerUsers.get(cont).getSpentHours()),
                    String.valueOf(hoursPerUsers.get(cont).getSpentHours()*20)};
            totalEstimated += hoursPerUsers.get(cont).getEstimatedHours();
            totalSpent += hoursPerUsers.get(cont).getSpentHours();
        }

        double totalCost = totalSpent * (int)spinner.getValue();

        data[hoursPerUsers.size() + 1] = new String[]{"Total", String.valueOf(totalEstimated),
                String.valueOf(totalSpent),
                String.valueOf(totalCost)};

        this.table = new JTable(data, names);
        table.setBounds(((frame.getWidth() / 2) - 20), ((frame.getHeight() / 2) - 70), 400, 100);
        table.setVisible(true);
        table.setEnabled(false);
        table.setGridColor(Color.BLACK);
        table.setShowGrid(true);

        frame.add(table);
        this.frame.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        double totalMoney = 0;
        int multiplier = (int)spinner.getValue();
        for(int i =1; i !=hoursPerUsers.size()+1;i++){
            double obj = this.hoursPerUsers.get(i-1).getSpentHours() * multiplier;
            this.table.setValueAt(String.valueOf(obj),i,3);
            totalMoney += obj;
        }

        this.table.setValueAt(String.valueOf(totalMoney),hoursPerUsers.size() + 1,3);
        this.frame.repaint();

    }
}
