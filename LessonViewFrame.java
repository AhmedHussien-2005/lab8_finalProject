package Frontend;

import Backend.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class LessonViewFrame extends JFrame {
    private Student student;
    private Course course;
    private DefaultListModel<String> listModel;
    private JList<String> lessonList;
    private StudentDashboardFrame parentDashboard;

    public LessonViewFrame(Student student, Course course) {
        this(student, course, null);
    }

    public LessonViewFrame(Student student, Course course, StudentDashboardFrame parent) {
        this.student = student;
        this.course = course;
        this.parentDashboard = parent;

        setTitle("Lessons - " + course.getTitle());
        setSize(700, 500);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Course info
        JPanel infoPanel = new JPanel(new BorderLayout());
        updateProgressLabel(infoPanel);
        mainPanel.add(infoPanel, BorderLayout.NORTH);

        // Lesson list
        listModel = new DefaultListModel<>();
        refreshLessonList();

        lessonList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(lessonList);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton viewButton = new JButton("View Lesson");
        JButton takeQuizButton = new JButton("Take Quiz");
        JButton markCompleteButton = new JButton("Mark as Completed");
        JButton closeButton = new JButton("Close");

        viewButton.addActionListener(e -> {
            int selectedIndex = lessonList.getSelectedIndex();
            if (selectedIndex >= 0) {
                viewLesson(selectedIndex);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a lesson.");
            }
        });

        takeQuizButton.addActionListener(e -> {
            int selectedIndex = lessonList.getSelectedIndex();
            if (selectedIndex >= 0) {
                takeQuiz(selectedIndex);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a lesson.");
            }
        });

        markCompleteButton.addActionListener(e -> {
            int selectedIndex = lessonList.getSelectedIndex();
            if (selectedIndex >= 0) {
                markLessonComplete(selectedIndex);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a lesson.");
            }
        });

        closeButton.addActionListener(e -> {
            // Refresh parent dashboard before closing
            if (parentDashboard != null) {
                parentDashboard.refreshDashboard();
            }
            dispose();
        });

        buttonPanel.add(viewButton);
        buttonPanel.add(takeQuizButton);
        buttonPanel.add(markCompleteButton);
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);

        // Add window listener to refresh parent dashboard when closing
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (parentDashboard != null) {
                    parentDashboard.refreshDashboard();
                }
            }
        });
        setVisible(true);
    }

    /**
     * Refresh the lesson list to show current completion status
     */
    private void refreshLessonList() {
        listModel.clear();
        for (Lesson lesson : course.getLessons()) {
            String status = student.isLessonCompleted(lesson.getLessonId()) ? " ✓" : "";
            String quizStatus = lesson.getQuiz() != null ? " [Quiz]" : "";
            listModel.addElement(lesson.getTitle() + quizStatus + status);
        }
    }

    private void viewLesson(int index) {
        Lesson lesson = course.getLessons().get(index);

        JTextArea contentArea = new JTextArea(lesson.getContent(), 15, 50);
        contentArea.setEditable(false);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(contentArea);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.add(scrollPane, BorderLayout.CENTER);

        if (!lesson.getResources().isEmpty()) {
            JPanel resourcePanel = new JPanel(new BorderLayout());
            resourcePanel.setBorder(BorderFactory.createTitledBorder("Resources"));
            JTextArea resourceArea = new JTextArea(String.join("\n", lesson.getResources()), 3, 50);
            resourceArea.setEditable(false);
            resourcePanel.add(new JScrollPane(resourceArea), BorderLayout.CENTER);
            panel.add(resourcePanel, BorderLayout.SOUTH);
        }

        JOptionPane.showMessageDialog(this, panel, lesson.getTitle(),
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void takeQuiz(int index) {
        Lesson lesson = course.getLessons().get(index);

        if (lesson.getQuiz() == null) {
            JOptionPane.showMessageDialog(this,
                    "This lesson does not have a quiz.",
                    "No Quiz",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Check if already passed
        if (student.hasPassedQuiz(lesson.getLessonId())) {
            int choice = JOptionPane.showConfirmDialog(this,
                    "You have already passed this quiz. Do you want to retake it?",
                    "Quiz Already Passed",
                    JOptionPane.YES_NO_OPTION);

            if (choice != JOptionPane.YES_OPTION) {
                return;
            }
        }

        // FIXED: Pass reference to this frame so quiz can refresh it
        new QuizFrame(student, course, lesson).setVisible(true);
    }

    private void markLessonComplete(int index) {
        Lesson lesson = course.getLessons().get(index);

        if (student.isLessonCompleted(lesson.getLessonId())) {
            JOptionPane.showMessageDialog(this, "This lesson is already marked as completed.");
            return;
        }

        // Check if lesson has required quiz
        if (lesson.getQuiz() != null && lesson.getQuiz().isRequired()) {
            if (!student.hasPassedQuiz(lesson.getLessonId())) {
                JOptionPane.showMessageDialog(this,
                        "You must pass the quiz before completing this lesson.",
                        "Quiz Required",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        student.markLessonCompleted(lesson.getLessonId());

        // Save student data
        List<User> users = JsonDatabaseManager.loadUsers();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserId().equals(student.getUserId())) {
                users.set(i, student);
                break;
            }
        }
        JsonDatabaseManager.saveUsers(users);

        // FIXED: Refresh the lesson list immediately
        refreshLessonList();

        // Update progress label
        int progress = student.getCourseProgress(course.getCourseId(), course.getLessons().size());
        JOptionPane.showMessageDialog(this,
                "Lesson marked as completed!\nCourse progress: " + progress + "%");

        // Refresh the info panel
        Container contentPane = getContentPane();
        JPanel mainPanel = (JPanel) contentPane.getComponent(0);
        JPanel infoPanel = (JPanel) mainPanel.getComponent(0);
        updateProgressLabel(infoPanel);
    }

    /**
     * Refresh this frame after quiz completion
     * Called by QuizFrame after student takes quiz
     */
    public void refreshAfterQuiz() {
        // Reload student data from database
        List<User> users = JsonDatabaseManager.loadUsers();
        for (User user : users) {
            if (user.getUserId().equals(student.getUserId()) && user instanceof Student) {
                this.student = (Student) user;
                break;
            }
        }

        // Refresh the lesson list
        refreshLessonList();

        // Update progress label
        Container contentPane = getContentPane();
        JPanel mainPanel = (JPanel) contentPane.getComponent(0);
        JPanel infoPanel = (JPanel) mainPanel.getComponent(0);
        updateProgressLabel(infoPanel);
    }

    /**
     * Update the progress label at the top
     */
    private void updateProgressLabel(JPanel infoPanel) {
        infoPanel.removeAll();

        int progress = student.getCourseProgress(course.getCourseId(), course.getLessons().size());

        // Count completed lessons for this course
        int completedCount = 0;
        for (Lesson lesson : course.getLessons()) {
            if (student.isLessonCompleted(lesson.getLessonId())) {
                completedCount++;
            }
        }

        JLabel infoLabel = new JLabel("Progress: " + progress + "% (" +
                completedCount + "/" + course.getLessons().size() + " lessons completed)");
        infoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        infoPanel.add(infoLabel, BorderLayout.NORTH);

        infoPanel.revalidate();
        infoPanel.repaint();
    }
}