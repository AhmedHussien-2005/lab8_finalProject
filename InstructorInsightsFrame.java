package Frontend;

import Backend.*;
import Backend.PerformanceAnalytics.StudentPerformance;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InstructorInsightsFrame extends JFrame {
    private Instructor instructor;
    private Course course;

    public InstructorInsightsFrame(Instructor instructor, Course course) {
        this.instructor = instructor;
        this.course = course;

        setTitle("Course Insights - " + course.getTitle());
        setSize(900, 700);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Performance Analytics: " + course.getTitle());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        headerPanel.add(closeButton, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Tabbed pane for different views
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Overview", createOverviewPanel());
        tabbedPane.addTab("Student Performance", createStudentPerformancePanel());
        tabbedPane.addTab("Lesson Analytics", createLessonAnalyticsPanel());
        tabbedPane.addTab("Quiz Statistics", createQuizStatisticsPanel()); // NEW TAB

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
    }

    private JPanel createOverviewPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Course statistics
        double avgCompletion = PerformanceAnalytics.getCourseAverageCompletion(course.getCourseId());
        double avgQuizScore = PerformanceAnalytics.getCourseAverageQuizScore(course.getCourseId());
        int enrolledCount = course.getStudents().size();
        int totalLessons = course.getLessons().size();

        // Create stat cards
        panel.add(createStatCard("Enrolled Students", String.valueOf(enrolledCount), Color.BLUE));
        panel.add(createStatCard("Total Lessons", String.valueOf(totalLessons), Color.GREEN));
        panel.add(createStatCard("Average Completion",
                String.format("%.1f%%", avgCompletion), new Color(255, 152, 0)));
        panel.add(createStatCard("Average Quiz Score",
                String.format("%.1f%%", avgQuizScore), new Color(156, 39, 176)));

        return panel;
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 28));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createStudentPerformancePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Get student performances
        List<StudentPerformance> performances =
                PerformanceAnalytics.getStudentPerformances(course.getCourseId());

        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (StudentPerformance perf : performances) {
            listModel.addElement(perf.toString());
        }

        JList<String> studentList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(studentList);

        JLabel infoLabel = new JLabel("Student Progress and Quiz Scores");
        infoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(infoLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        // View chart button
        JButton viewChartButton = new JButton("View Performance Chart");
        viewChartButton.addActionListener(e -> {
            new ChartFrame("Student Performance", performances, course.getTitle()).setVisible(true);
        });
        panel.add(viewChartButton, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createLessonAnalyticsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Get lesson quiz averages
        Map<String, Double> lessonAverages =
                PerformanceAnalytics.getLessonQuizAverages(course.getCourseId());

        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (Map.Entry<String, Double> entry : lessonAverages.entrySet()) {
            listModel.addElement(entry.getKey() + " - Average: " +
                    String.format("%.1f%%", entry.getValue()));
        }

        JList<String> lessonList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(lessonList);

        JLabel infoLabel = new JLabel("Average Quiz Scores per Lesson");
        infoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(infoLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * NEW: Panel showing which students took quizzes and their scores
     */
    private JPanel createQuizStatisticsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header
        JLabel headerLabel = new JLabel("Quiz Statistics by Lesson");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(headerLabel, BorderLayout.NORTH);

        // Main content area with lessons and their quiz takers
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        List<User> allUsers = JsonDatabaseManager.loadUsers();

        // For each lesson that has a quiz
        for (Lesson lesson : course.getLessons()) {
            if (lesson.getQuiz() != null) {
                JPanel lessonPanel = createLessonQuizPanel(lesson, allUsers);
                contentPanel.add(lessonPanel);
                contentPanel.add(Box.createVerticalStrut(15));
            }
        }

        if (contentPanel.getComponentCount() == 0) {
            JLabel noQuizzesLabel = new JLabel("No quizzes have been created yet.");
            noQuizzesLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            contentPanel.add(noQuizzesLabel);
        }

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Create a panel for one lesson showing students who took the quiz
     */
    private JPanel createLessonQuizPanel(Lesson lesson, List<User> allUsers) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Lesson title
        JLabel titleLabel = new JLabel("Lesson: " + lesson.getTitle());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Get all students who took this quiz
        Map<String, QuizResult> studentBestScores = new HashMap<>();
        int totalAttempts = 0;
        int passedCount = 0;

        for (User user : allUsers) {
            if (user instanceof Student) {
                Student student = (Student) user;
                if (student.getEnrolledCourses().contains(course.getCourseId())) {
                    // Get best result for this lesson
                    QuizResult bestResult = student.getBestQuizResult(lesson.getLessonId());
                    if (bestResult != null) {
                        studentBestScores.put(student.getUsername(), bestResult);
                        totalAttempts += student.getQuizResultsForLesson(lesson.getLessonId()).size();
                        if (bestResult.isPassed()) {
                            passedCount++;
                        }
                    }
                }
            }
        }

        // Statistics panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 10, 5));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        double avgScore = 0.0;
        if (!studentBestScores.isEmpty()) {
            int totalScore = 0;
            for (QuizResult result : studentBestScores.values()) {
                totalScore += result.getScore();
            }
            avgScore = (double) totalScore / studentBestScores.size();
        }

        statsPanel.add(new JLabel("Students Attempted: " + studentBestScores.size()));
        statsPanel.add(new JLabel("Passed: " + passedCount + " | Failed: " + (studentBestScores.size() - passedCount)));
        statsPanel.add(new JLabel("Average Score: " + String.format("%.1f%%", avgScore)));

        panel.add(statsPanel, BorderLayout.CENTER);

        // Students list
        if (!studentBestScores.isEmpty()) {
            JPanel studentsPanel = new JPanel();
            studentsPanel.setLayout(new BoxLayout(studentsPanel, BoxLayout.Y_AXIS));
            studentsPanel.setBorder(BorderFactory.createTitledBorder("Student Scores"));

            for (Map.Entry<String, QuizResult> entry : studentBestScores.entrySet()) {
                String studentName = entry.getKey();
                QuizResult result = entry.getValue();

                String status = result.isPassed() ? "✓ PASSED" : "✗ FAILED";
                Color statusColor = result.isPassed() ? new Color(0, 128, 0) : Color.RED;

                JPanel studentRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
                JLabel nameLabel = new JLabel(studentName + ": " + result.getScore() + "% ");
                JLabel statusLabel = new JLabel(status);
                statusLabel.setForeground(statusColor);
                statusLabel.setFont(new Font("Arial", Font.BOLD, 12));

                studentRow.add(nameLabel);
                studentRow.add(statusLabel);
                studentsPanel.add(studentRow);
            }

            panel.add(studentsPanel, BorderLayout.SOUTH);
        } else {
            JLabel noAttemptsLabel = new JLabel("No students have taken this quiz yet.");
            noAttemptsLabel.setFont(new Font("Arial", Font.ITALIC, 12));
            noAttemptsLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
            panel.add(noAttemptsLabel, BorderLayout.SOUTH);
        }

        return panel;
    }
}