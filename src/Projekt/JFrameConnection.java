package Projekt;

import javax.swing.*;
import java.sql.SQLException;

public class JFrameConnection extends JFrame {
    public static Functions functions = null;

    public JFrameConnection(){
        try {
            if (functions == null)
                functions = new Functions();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
