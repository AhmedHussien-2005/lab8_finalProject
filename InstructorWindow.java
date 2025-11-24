package Frontend;

import Backend.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class InstructorWindow extends JFrame {
    private Instructor instructor;
    private JPanel coursesPanel;
    private DefaultListModel<String> listModel;
    private JList<String> courseList;

    public InstructorWindow(Instructor instructor){
        this.instructor = instructor;

        setTitle("Instructor Dashboard - " + instructor.getUsername());
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Your Courses");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            new WelcomePage();
            dispose();
        });
        headerPanel.add(logoutButton, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Course list
        listModel = new DefaultListModel<>();
        loadCourses();

        courseList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(courseList);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton createCourseButton = new JButton("Create New Course");
        JButton manageLessonsButton = new JButton("Manage Lessons");
        JButton viewStudentsButton = new JButton("View Students");
        JButton deleteCourseButton = new JButton("Delete Course");

        createCourseButton.addActionListener(e -> createNewCourse());

        viewStudentsButton.addActionListener(e -> {
            int selectedIndex = courseList.getSelectedIndex();
            if (selectedIndex >= 0) {
                viewEnrolledStudents(selectedIndex);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a course.");
            }
        });

        manageLessonsButton.addActionListener(e -> {
            int selectedIndex = courseList.getSelectedIndex();
            if (selectedIndex >= 0) {
                String courseId = instructor.getCreatedCourses().get(selectedIndex);
                Course course = getCourseById(courseId);
                if (course != null) {
                    new LessonManagementFrame(instructor, course).setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a course.");
            }
        });

        deleteCourseButton.addActionListener(e -> {
            int selectedIndex = courseList.getSelectedIndex();
            if (selectedIndex >= 0) {
                deleteCourse(selectedIndex);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a course.");
            }
        });

        buttonPanel.add(createCourseButton);
        buttonPanel.add(manageLessonsButton);
        buttonPanel.add(viewStudentsButton);
        buttonPanel.add(deleteCourseButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
        setVisible(true);
    }

    protected void loadCourses() {
        listModel.clear();
        List<Course> allCourses = JsonDatabaseManager.loadCourses();

        for (String courseId : instructor.getCreatedCourses()) {
            for (Course course : allCourses) {
                if (course.getCourseId().equals(courseId)) {
                    listModel.addElement(course.getTitle() + " (" + course.getLessons().size() + " lessons)");
                    break;
                }
            }
        }
    }

    private void createNewCourse() {
        JTextField titleField = new JTextField();
        JTextArea descriptionArea = new JTextArea(5, 20);

        JPanel panel = new JPanel(new GridLayout(3, 1, 5, 5));
        panel.add(new JLabel("Course Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Description:"));
        panel.add(new JScrollPane(descriptionArea));

        int result = JOptionPane.showConfirmDialog(this, panel, "Create New Course",
                JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String title = titleField.getText().trim();
            String description = descriptionArea.getText().trim();

            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Course title is required.");
                return;
            }

            // Generate unique course ID
            String courseId = "C" + System.currentTimeMillis();

            // Create new course
            Course newCourse = new Course(courseId, title, description, instructor.getUserId());

            // Save course to database
            List<Course> courses = JsonDatabaseManager.loadCourses();
            courses.add(newCourse);
            JsonDatabaseManager.saveCourses(courses);

            // Add course to instructor's created courses
            instructor.addCreatedCourseId(courseId);

            // Update instructor in database
            List<User> users = JsonDatabaseManager.loadUsers();
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getUserId().equals(instructor.getUserId())) {
                    users.set(i, instructor);
                    break;
                }
            }
            JsonDatabaseManager.saveUsers(users);

            // Refresh the course list
            loadCourses();

            JOptionPane.showMessageDialog(this,
                    "Course created successfully!\nYou can now add lessons to it.");
        }
    }

    private void deleteCourse(int index) {
        String courseId = instructor.getCreatedCourses().get(index);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this course?\nThis will also remove it from all enrolled students.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Remove from courses database
            List<Course> courses = JsonDatabaseManager.loadCourses();
            courses.removeIf(c -> c.getCourseId().equals(courseId));
            JsonDatabaseManager.saveCourses(courses);

            // Remove from instructor's created courses
            instructor.removeCreatedCourseId(courseId);

            // Update instructor in database
            List<User> users = JsonDatabaseManager.loadUsers();
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getUserId().equals(instructor.getUserId())) {
                    users.set(i, instructor);
                    break;
                }
            }

            // Remove from all students who enrolled
            for (User user : users) {
                if (user instanceof Student) {
                    Student student = (Student) user;
                    if (student.getEnrolledCourses().contains(courseId)) {
                        student.getEnrolledCourses().remove(courseId);
                        student.getCompletedLessons().remove(courseId);
                    }
                }
            }
            JsonDatabaseManager.saveUsers(users);

            // Refresh the course list
            loadCourses();

            JOptionPane.showMessageDialog(this, "Course deleted successfully!");
        }
    }

    private Course getCourseById(String courseId) {
        List<Course> courses = JsonDatabaseManager.loadCourses();
        for (Course course : courses) {
            if (course.getCourseId().equals(courseId)) {
                return course;
            }
        }
        return null;
    }

    private void viewEnrolledStudents(int index) {
        // 1. Get the course ID and Object
        String courseId = instructor.getCreatedCourses().get(index);
        Course course = getCourseById(courseId);

        if (course == null) return;

        List<String> studentIds = course.getStudents();

        if (studentIds.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No students enrolled in this course yet.");
            return;
        }

        // 2. Load all users to find the matching students
        List<User> allUsers = JsonDatabaseManager.loadUsers();
        DefaultListModel<String> studentListModel = new DefaultListModel<>();

        int totalLessons = course.getLessons().size();

        for (String id : studentIds) {
            for (User u : allUsers) {
                // Check if user ID matches AND if they are actually a Student object
                if (u.getUserId().equals(id) && u instanceof Student) {
                    Student s = (Student) u;

                    // Calculate progress (optional, but nice to see)
                    int progress = 0;
                    if(totalLessons > 0) {
                        progress = s.getCourseProgress(courseId, totalLessons);
                    }

                    // Add formatted string to list
                    studentListModel.addElement(String.format("%s (%s) - Progress: %d%%",
                            s.getUsername(), s.getEmail(), progress));
                    break;
                }
            }
        }

        // 3. Display in a Scrollable Popup
        JList<String> jList = new JList<>(studentListModel);
        JScrollPane scroll = new JScrollPane(jList);
        scroll.setPreferredSize(new Dimension(400, 300));

        JOptionPane.showMessageDialog(this, scroll,
                "Enrolled Students - " + course.getTitle(),
                JOptionPane.PLAIN_MESSAGE);
    }
}