package Frontend;

import Backend.Admin;
import Backend.Instructor;
import Backend.Student;
import Backend.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class SignupWindow extends JFrame {

    public SignupWindow() {
        setTitle("SignUp Page");
        setSize(400,300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridLayout(5,2,10,10));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));


        JLabel emailLabel = new JLabel("Email");
        JTextField emailField = new JTextField();

        JLabel usernameLabel = new JLabel("Username");
        JTextField usernameField = new JTextField();

        JLabel passwordLabel = new JLabel("Password");
        JPasswordField passwordField = new JPasswordField();

        JLabel roleLabel = new JLabel("Role");
        String[] roles = {"Student", "Instructor", "Admin"};
        JComboBox roleComboBox = new JComboBox(roles);

        JButton exitButton = new JButton("EXIT");

        JButton loginButton = new JButton("SignUp");

        exitButton.addActionListener(e -> {
            new WelcomePage();
            dispose();
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String role = roleComboBox.getSelectedItem().toString();

                if(email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(SignupWindow.this, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String id = String.valueOf(new Random().nextInt(9000) + 1000);

                User user;
                if(role.equalsIgnoreCase("Student")){
                    user = new Student(id, username, email, password);
                }
                else if(role.equalsIgnoreCase("Instructor")){
                    user = new Instructor(id, username, email, password);
                }
                else{
                    user = new Admin(id, username, email, password);
                }

                if (user.signup()) {
                    JOptionPane.showMessageDialog(SignupWindow.this, "SignUp successful!");
                    openLoginWindow(); // go to next window
                    dispose(); // close login window
                } else {
                    JOptionPane.showMessageDialog(SignupWindow.this, "Invalid Email, username or password.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(roleLabel);
        panel.add(roleComboBox);
        panel.add(exitButton);
        panel.add(loginButton);

        add(panel);

        setVisible(true);
    }

    private void openLoginWindow(){
        SwingUtilities.invokeLater(() -> new LoginWindow());
    }

    /*
    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> new SignupWindow());
    }
    */
}
