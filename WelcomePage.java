package Frontend;

import javax.swing.*;
import java.awt.*;

public class WelcomePage extends JFrame{

    public WelcomePage() {
        setTitle("Welcome Page");
        setSize(300,250);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(1, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JButton signUpButton = new JButton("Sign Up");
        JButton loginButton = new JButton("Login");

        Font btnFont = new Font("Arial", Font.BOLD, 20);
        signUpButton.setFont(btnFont);
        loginButton.setFont(btnFont);

        signUpButton.addActionListener(e ->{
            new SignupWindow();
            dispose();
        });
        loginButton.addActionListener(e ->{
            new LoginWindow();
            dispose();
        });

        panel.add(signUpButton);
        panel.add(loginButton);

        add(panel);
        setVisible(true);
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> new WelcomePage());
    }
}
