package Frontend;

import Backend.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;


public class LoginWindow extends JFrame {
    public LoginWindow(){
        setTitle("Login Page");
        setSize(400,300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridLayout(4,2,10,10));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel usernameLabel = new JLabel("Email or Username");
        JTextField usernameField = new JTextField();

        JLabel passwordLabel = new JLabel("Password");
        JPasswordField passwordField = new JPasswordField();

        JLabel roleLabel = new JLabel("Role");
        String[] roles = {"Student", "Instructor", "Admin"};
        JComboBox roleComboBox = new JComboBox(roles);

        JButton exitButton = new JButton("EXIT");

        JButton loginButton = new JButton("Login");

        exitButton.addActionListener(e -> {
            new WelcomePage();
            dispose();
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String usernameOrEmail = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String role = roleComboBox.getSelectedItem().toString().toLowerCase();

                if(usernameOrEmail.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginWindow.this, "Please enter username/email and password.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                User user = JsonDatabaseManager.findByUsernameOrEmail(usernameOrEmail);
                if(user == null) {
                    JOptionPane.showMessageDialog(null, "User not found.");
                    return;
                }

                if(!user.getPasswordHash().equals(password)) {
                    JOptionPane.showMessageDialog(null, "Invalid password.");
                    return;
                }

                // now user is guaranteed to be a Student or Instructor instance
                if(user instanceof Student student && role.equalsIgnoreCase("student")) {
                    openStudentDashboardFrame(student);
                } else if(user instanceof Instructor instructor && role.equalsIgnoreCase("instructor")) {
                    openInstructorWindow(instructor);
                } else if(user instanceof Admin admin && role.equalsIgnoreCase("admin")){
                    openAdminWindow(admin);
                } else{
                    JOptionPane.showMessageDialog(null, "User not found.");
                    return;
                }

                dispose();

            }
        });

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
    private void openInstructorWindow(Instructor instructor){
        SwingUtilities.invokeLater(() -> new InstructorWindow(instructor));
    }
    private void openStudentDashboardFrame(Student student){
        SwingUtilities.invokeLater(() -> new StudentDashboardFrame(student));
    }
    private void openAdminWindow(Admin admin){
        SwingUtilities.invokeLater(() -> new AdminWindow(admin));
    }
}
