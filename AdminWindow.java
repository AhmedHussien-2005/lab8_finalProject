package Frontend;

import Backend.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AdminWindow extends JFrame {
    private Admin admin;
    private JTabbedPane tabbedPane;

    // Models to allow refreshing lists dynamically
    private DefaultListModel<String> pendingModel;
    private DefaultListModel<String> usersModel;
    private DefaultListModel<String> coursesModel;

    public AdminWindow(Admin admin) {
        this.admin = admin;
        setTitle("Skill Forge - Admin Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        // --- Header ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        JLabel welcomeLabel = new JLabel("Admin Dashboard: " + admin.getUsername());
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(welcomeLabel, BorderLayout.WEST);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            new LoginWindow().setVisible(true);
            dispose();
        });
        headerPanel.add(logoutButton, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // --- Tabs ---
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Pending Requests", createPendingRequestsPanel());
        tabbedPane.addTab("Manage Users", createManageUsersPanel());
        tabbedPane.addTab("Manage Courses", createAllCoursesPanel());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel);
        setVisible(true);
    }

    // ---------------------------------------------------------
    // TAB 1: Pending Requests (Approve/Reject)
    // ---------------------------------------------------------
    private JPanel createPendingRequestsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        pendingModel = new DefaultListModel<>();
        JList<String> list = new JList<>(pendingModel);
        refreshPendingList();

        JScrollPane scrollPane = new JScrollPane(list);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton approveBtn = new JButton("Approve Course");
        JButton rejectBtn = new JButton("Reject Course");
        JButton refreshBtn = new JButton("Refresh List");

        approveBtn.setBackground(new Color(144, 238, 144)); // Light Green
        rejectBtn.setBackground(new Color(255, 182, 193));  // Light Pink

        approveBtn.addActionListener(e -> {
            String selected = list.getSelectedValue();
            if (selected != null) {
                // Extract ID from string: "CourseID | Title..."
                String courseId = selected.split(" \\| ")[0];
                if(admin.setCourseStatus(courseId, true)) {
                    JOptionPane.showMessageDialog(this, "Course Approved!");
                    refreshPendingList(); // Refresh UI
                    refreshAllCoursesList(); // Update the other tab
                } else {
                    JOptionPane.showMessageDialog(this, "Error updating status.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Select a course to approve.");
            }
        });

        rejectBtn.addActionListener(e -> {
            String selected = list.getSelectedValue();
            if (selected != null) {
                String courseId = selected.split(" \\| ")[0];
                if(admin.setCourseStatus(courseId, false)) {
                    JOptionPane.showMessageDialog(this, "Course Rejected.");
                    refreshPendingList();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Select a course to reject.");
            }
        });

        refreshBtn.addActionListener(e -> refreshPendingList());

        btnPanel.add(approveBtn);
        btnPanel.add(rejectBtn);
        btnPanel.add(refreshBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    // ---------------------------------------------------------
    // TAB 2: Manage Users (Delete)
    // ---------------------------------------------------------
    private JPanel createManageUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        usersModel = new DefaultListModel<>();
        JList<String> list = new JList<>(usersModel);
        refreshUserList();

        JScrollPane scrollPane = new JScrollPane(list);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton deleteUserBtn = new JButton("Delete User");
        JButton refreshBtn = new JButton("Refresh");

        deleteUserBtn.addActionListener(e -> {
            String selected = list.getSelectedValue();
            if (selected != null) {
                // Format: "ID | Role | Username"
                String userId = selected.split(" \\| ")[0];

                // Prevent Admin self-deletion
                if(userId.equals(admin.getUserId())) {
                    JOptionPane.showMessageDialog(this, "You cannot delete yourself.");
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete User ID: " + userId + "?",
                        "Confirm Deletion", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    if(admin.deleteUser(userId)) {
                        JOptionPane.showMessageDialog(this, "User removed successfully.");
                        refreshUserList();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to remove user (User might not exist).");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a user.");
            }
        });

        refreshBtn.addActionListener(e -> refreshUserList());

        btnPanel.add(deleteUserBtn);
        btnPanel.add(refreshBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    // ---------------------------------------------------------
    // TAB 3: Manage All Courses (Delete)
    // ---------------------------------------------------------
    private JPanel createAllCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        coursesModel = new DefaultListModel<>();
        JList<String> list = new JList<>(coursesModel);
        refreshAllCoursesList();

        JScrollPane scrollPane = new JScrollPane(list);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton deleteCourseBtn = new JButton("Delete Course");
        JButton refreshBtn = new JButton("Refresh");

        deleteCourseBtn.addActionListener(e -> {
            String selected = list.getSelectedValue();
            if (selected != null) {
                String courseId = selected.split(" \\| ")[0];

                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure? This will delete the course and remove it from all students.",
                        "Confirm Deletion", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    if(admin.deleteCourse(courseId)) {
                        JOptionPane.showMessageDialog(this, "Course deleted.");
                        refreshAllCoursesList();
                        refreshPendingList(); // In case it was pending
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to delete course.");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a course.");
            }
        });

        refreshBtn.addActionListener(e -> refreshAllCoursesList());

        btnPanel.add(deleteCourseBtn);
        btnPanel.add(refreshBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    // ---------------------------------------------------------
    // Helper Methods for Data Refreshing
    // ---------------------------------------------------------
    private void refreshPendingList() {
        pendingModel.clear();
        // Uses the specific admin method
        List<Course> pending = admin.getPendingCourses();
        for (Course c : pending) {
            // Display Format: ID | Title | Instructor
            pendingModel.addElement(c.getCourseId() + " | " + c.getTitle() + " | Inst: " + c.getInstructorId());
        }
    }

    private void refreshUserList() {
        usersModel.clear();
        // We load directly from DB Manager to get everyone
        List<User> users = JsonDatabaseManager.loadUsers();
        for (User u : users) {
            usersModel.addElement(u.getUserId() + " | " + u.getRole() + " | " + u.getUsername());
        }
    }

    private void refreshAllCoursesList() {
        coursesModel.clear();
        List<Course> courses = JsonDatabaseManager.loadCourses();
        for (Course c : courses) {
            coursesModel.addElement(c.getCourseId() + " | " + c.getTitle() + " [" + c.getStatus() + "]");
        }
    }
}