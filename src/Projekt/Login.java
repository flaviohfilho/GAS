package Projekt;

import javax.swing.*;

public class Login extends JPanel {

    public static JFrame frame = new JFrame("Login");

    private JLabel workerId_text;
    private JTextField workerId;
    private JLabel password_text;
    private JPasswordField password;
    public JButton loginButton;

    public Login() {
        this.setLayout(null);
        this.setSize(300, 300);

        this.workerId_text = new JLabel("WorkerID: ");
        workerId_text.setSize(200, 25);
        workerId_text.setLocation(10, 0);

        this.workerId = new JTextField();
        workerId.setSize(200, 25);
        workerId.setLocation(10, 25);

        this.password_text = new JLabel("Password: ");
        password_text.setSize(200, 25);
        password_text.setLocation(10, 50);

        this.password = new JPasswordField();
        password.setSize(200, 25);
        password.setLocation(10, 75);

        this.loginButton = new JButton("Login");
        loginButton.setSize(100, 25);
        loginButton.setLocation(110, 110);

        this.add(workerId_text);
        this.add(workerId);
        this.add(password_text);
        this.add(password);
        this.add(loginButton);
    }

    public String getWorkerId() {
        return workerId.getText();
    }

    public String getPassword() {
        return Program.functions.get_SHA_512_SecurePassword(new String(password.getPassword()));
    }

    public static void openLogin(){
        frame.setContentPane(new Login());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(250,180);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    };
}
