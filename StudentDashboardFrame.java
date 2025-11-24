package Frontend;

import Backend.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class StudentDashboardFrame extends JFrame {
    private Student student;
    private JTabbedPane tabbedPane;

    public StudentDashboardFrame(Student student) {
        this.student = student;
        setTitle("Skill Forge - Student Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        JLabel welcomeLabel = new JLabel("Welcome, " + student.getUsername());
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(welcomeLabel, BorderLayout.WEST);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());
        headerPanel.add(logoutButton, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Browse Courses", createBrowseCoursesPanel());
        tabbedPane.addTab("My Courses", createMyCoursesPanel());
        tabbedPane.addTab("Certificates", createCertificatesPanel()); // NEW TAB

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel);
        setVisible(true);
    }

    private JPanel createBrowseCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> courseList = new JList<>(listModel);

        List<Course> courses = JsonDatabaseManager.loadCourses();
        for (Course course : courses) {
            if(course.getStatus().equalsIgnoreCase("Approved")) {
                listModel.addElement(course.getCourseId() + ": " + course.getTitle());
            }
        }

        JScrollPane scrollPane = new JScrollPane(courseList);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton enrollButton = new JButton("Enroll");
        JButton viewDetailsButton = new JButton("View Details");

        enrollButton.addActionListener(e -> {
            String selected = courseList.getSelectedValue();
            if (selected != null) {
                String courseId = selected.split(":")[0];
                enrollInCourse(courseId);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a course.");
            }
        });

        viewDetailsButton.addActionListener(e -> {
            String selected = courseList.getSelectedValue();
            if (selected != null) {
                String courseId = selected.split(":")[0];
                showCourseDetails(courseId);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a course.");
            }
        });

        buttonPanel.add(viewDetailsButton);
        buttonPanel.add(enrollButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createMyCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> courseList = new JList<>(listModel);

        List<Course> allCourses = JsonDatabaseManager.loadCourses();
        for (String courseId : student.getEnrolledCourses()) {
            for (Course course : allCourses) {
                if (course.getCourseId().equals(courseId)) {
                    int progress = student.getCourseProgress(courseId, course.getLessons().size());
                    String completionStatus = CourseCompletionTracker.getCompletionStatus(student, course);
                    listModel.addElement(course.getTitle() + " - " + completionStatus);
                    break;
                }
            }
        }

        JScrollPane scrollPane = new JScrollPane(courseList);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton viewLessonsButton = new JButton("View Lessons");
        JButton generateCertButton = new JButton("Generate Certificate");

        viewLessonsButton.addActionListener(e -> {
            int selectedIndex = courseList.getSelectedIndex();
            if (selectedIndex >= 0) {
                String courseId = student.getEnrolledCourses().get(selectedIndex);
                viewCourseLessons(courseId);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a course.");
            }
        });

        generateCertButton.addActionListener(e -> {
            int selectedIndex = courseList.getSelectedIndex();
            if (selectedIndex >= 0) {
                String courseId = student.getEnrolledCourses().get(selectedIndex);
                generateCertificate(courseId);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a course.");
            }
        });

        buttonPanel.add(viewLessonsButton);
        buttonPanel.add(generateCertButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    /**
     * NEW: Create certificates panel
     */
    private JPanel createCertificatesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        if (student.getCertificates().isEmpty()) {
            JPanel emptyPanel = new JPanel(new GridBagLayout());
            JLabel emptyLabel = new JLabel("You haven't earned any certificates yet.");
            emptyLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            emptyLabel.setForeground(Color.GRAY);
            emptyPanel.add(emptyLabel);
            panel.add(emptyPanel, BorderLayout.CENTER);
        } else {
            DefaultListModel<String> listModel = new DefaultListModel<>();
            JList<String> certList = new JList<>(listModel);

            for (Certificate cert : student.getCertificates()) {
                listModel.addElement(cert.toString());
            }

            JScrollPane scrollPane = new JScrollPane(certList);
            panel.add(scrollPane, BorderLayout.CENTER);

            JButton viewButton = new JButton("View Certificate");
            viewButton.addActionListener(e -> {
                int selectedIndex = certList.getSelectedIndex();
                if (selectedIndex >= 0) {
                    Certificate cert = student.getCertificates().get(selectedIndex);
                    new CertificateViewFrame(cert).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Please select a certificate.");
                }
            });

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(viewButton);
            panel.add(buttonPanel, BorderLayout.SOUTH);
        }

        return panel;
    }

    private void enrollInCourse(String courseId) {
        if (student.getEnrolledCourses().contains(courseId)) {
            JOptionPane.showMessageDialog(this, "You are already enrolled in this course.");
            return;
        }

        List<Course> courses = JsonDatabaseManager.loadCourses();
        for (Course course : courses) {
            if (course.getCourseId().equals(courseId)) {
                student.enrollInCourse(courseId);
                course.addStudent(student.getUserId());

                JsonDatabaseManager.saveCourses(courses);
                List<User> users = JsonDatabaseManager.loadUsers();
                for (int i = 0; i < users.size(); i++) {
                    if (users.get(i).getUserId().equals(student.getUserId())) {
                        users.set(i, student);
                        break;
                    }
                }
                JsonDatabaseManager.saveUsers(users);

                JOptionPane.showMessageDialog(this, "Enrolled successfully!");
                refreshTabs();
                return;
            }
        }
    }

    private void showCourseDetails(String courseId) {
        List<Course> courses = JsonDatabaseManager.loadCourses();
        for (Course course : courses) {
            if (course.getCourseId().equals(courseId)) {
                String details = "Title: " + course.getTitle() + "\n" +
                        "Description: " + course.getDescription() + "\n" +
                        "Number of Lessons: " + course.getLessons().size() + "\n" +
                        "Enrolled Students: " + course.getStudents().size();
                JOptionPane.showMessageDialog(this, details, "Course Details", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }
    }

    private void viewCourseLessons(String courseId) {
        List<Course> courses = JsonDatabaseManager.loadCourses();
        for (Course course : courses) {
            if (course.getCourseId().equals(courseId)) {
                new LessonViewFrame(student, course, this).setVisible(true);
                return;
            }
        }
    }

    /**
     * NEW: Generate certificate for completed course
     */
    private void generateCertificate(String courseId) {
        List<Course> courses = JsonDatabaseManager.loadCourses();
        Course course = null;

        for (Course c : courses) {
            if (c.getCourseId().equals(courseId)) {
                course = c;
                break;
            }
        }

        if (course == null) {
            JOptionPane.showMessageDialog(this, "Course not found.");
            return;
        }

        // Check if already has certificate
        if (CourseCompletionTracker.hasCertificate(student, courseId)) {
            Certificate existingCert = CourseCompletionTracker.getCertificate(student, courseId);
            new CertificateViewFrame(existingCert).setVisible(true);
            return;
        }

        // Check if course is completed
        if (!CourseCompletionTracker.isCourseCompleted(student, course)) {
            String message = "You haven't completed this course yet.\n\n";
            message += "Requirements:\n";
            message += "• Complete all lessons\n";
            message += "• Pass all required quizzes\n\n";
            message += "Current progress: " +
                    CourseCompletionTracker.getCompletionPercentage(student, course) + "%";

            JOptionPane.showMessageDialog(this, message,
                    "Course Not Completed", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Generate certificate
        Certificate certificate = CourseCompletionTracker.generateCertificate(student, course);

        if (certificate != null) {
            JOptionPane.showMessageDialog(this,
                    "Congratulations! Certificate generated successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            // Refresh tabs to show new certificate
            refreshTabs();

            // Show certificate
            new CertificateViewFrame(certificate).setVisible(true);
        }
    }

    public void refreshDashboard() {
        // Reload student data from database
        List<User> users = JsonDatabaseManager.loadUsers();
        for (User user : users) {
            if (user.getUserId().equals(student.getUserId()) && user instanceof Student) {
                this.student = (Student) user;
                break;
            }
        }
        refreshTabs();
    }

    private void refreshTabs() {
        tabbedPane.removeAll();
        tabbedPane.addTab("Browse Courses", createBrowseCoursesPanel());
        tabbedPane.addTab("My Courses", createMyCoursesPanel());
        tabbedPane.addTab("Certificates", createCertificatesPanel());
    }

    private void logout() {
        new LoginWindow().setVisible(true);
        dispose();
    }
}