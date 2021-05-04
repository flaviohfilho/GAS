package Projekt;


import javax.swing.*;
import java.sql.SQLException;

public class Program extends JFrameConnection {
    static Login login = new Login();
    static Program program = new Program();

    public static void main(String[] args) {
        //functions.newDB();

        openFrame(login,250,180);

        login.loginButton.addActionListener(e ->{
            try {
                String query = "SELECT workerID,password FROM LOGIN WHERE workerID = ? AND password = ?";
                if(functions.verifyQuery(query, login.getWorkerId(),login.getPassword())){
                    program.dispose();
                    GasMain.openGasMain((Integer.parseInt(login.getWorkerId())));
                }
                else {
                    JOptionPane.showMessageDialog(program,"Wrong username or password" );
                }

            } catch (SQLException er) {
                JOptionPane.showMessageDialog(program, er.getMessage());
            }

        });
    }

    public static void openFrame(JPanel panel, int width, int height){
        program.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        program.setSize(width, height);
        program.setResizable(false);
        program.setContentPane(panel);
        program.setLocationRelativeTo(null);
        program.setVisible(true);

    }

}


