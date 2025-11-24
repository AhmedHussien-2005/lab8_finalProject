package Frontend;

import Backend.Course;
import Backend.Instructor;
import Backend.JsonDatabaseManager;
import Backend.Lesson;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.UUID;

public class LessonManagementFrame extends JFrame {
    private final Instructor instructor;
    private final Course course;
    private DefaultListModel<String> listModel;
    private JList<String> lessonList;
    private final InstructorWindow parentWindow; // ADDED: Reference to parent

    // Original constructor for backward compatibility
    public LessonManagementFrame(Instructor instructor, Course course) {
        this(instructor, course, null);
    }

    // FIXED: New constructor that accepts parent window
    public LessonManagementFrame(Instructor instructor, Course course, InstructorWindow parentWindow) {
        this.instructor = instructor;
        this.course = course;
        this.parentWindow = parentWindow;

        setTitle("Manage Lessons - " + course.getTitle());
        setSize(750, 550);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Lesson list
        listModel = new DefaultListModel<>();
        refreshLessonList();

        lessonList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(lessonList);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        JButton addButton = new JButton("Add Lesson");
        JButton editButton = new JButton("Edit Lesson");
        JButton deleteButton = new JButton("Delete Lesson");
        JButton manageQuizButton = new JButton("Manage Quiz");
        JButton viewButton = new JButton("View Content");
        JButton closeButton = new JButton("Close");

        addButton.addActionListener(e -> addLesson());
        editButton.addActionListener(e -> {
            int selectedIndex = lessonList.getSelectedIndex();
            if (selectedIndex >= 0) {
                editLesson(selectedIndex);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a lesson.");
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedIndex = lessonList.getSelectedIndex();
            if (selectedIndex >= 0) {
                deleteLesson(selectedIndex);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a lesson.");
            }
        });

        manageQuizButton.addActionListener(e -> {
            int selectedIndex = lessonList.getSelectedIndex();
            if (selectedIndex >= 0) {
                Lesson lesson = course.getLessons().get(selectedIndex);
                new QuizManagementFrame(instructor, course, lesson).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a lesson.");
            }
        });

        viewButton.addActionListener(e -> {
            int selectedIndex = lessonList.getSelectedIndex();
            if (selectedIndex >= 0) {
                viewLessonContent(selectedIndex);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a lesson.");
            }
        });

        closeButton.addActionListener(e -> {
            // FIXED: Refresh parent window before closing
            if (parentWindow != null) {
                parentWindow.loadCourses();
            }
            dispose();
        });

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(manageQuizButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);

        // FIXED: Add window listener to refresh parent when window closes
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (parentWindow != null) {
                    parentWindow.loadCourses();
                }
            }
        });

        setVisible(true);
    }

    // FIXED: New method to refresh lesson list
    private void refreshLessonList() {
        listModel.clear();
        for (Lesson lesson : course.getLessons()) {
            String quizIndicator = lesson.getQuiz() != null ? " [Has Quiz]" : "";
            listModel.addElement(lesson.getTitle() + quizIndicator);
        }
    }

    private void addLesson() {
        JTextField titleField = new JTextField();
        JTextArea contentArea = new JTextArea(5, 20);
        JTextField resourceField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(4, 1, 5, 5));
        panel.add(new JLabel("Lesson Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Content:"));
        panel.add(new JScrollPane(contentArea));
        panel.add(new JLabel("Resources (comma-separated URLs):"));
        panel.add(resourceField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Lesson",
                JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String title = titleField.getText().trim();
            String content = contentArea.getText().trim();
            String resourceText = resourceField.getText().trim();

            if (title.isEmpty() || content.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Title and content are required.");
                return;
            }

            String lessonId = UUID.randomUUID().toString();
            Lesson newLesson = new Lesson(lessonId, title, content);

            if (!resourceText.isEmpty()) {
                String[] resources = resourceText.split(",");
                for (String resource : resources) {
                    newLesson.addResource(resource.trim());
                }
            }

            course.addLesson(newLesson);
            saveCourse();

            // FIXED: Refresh the list to show new lesson
            refreshLessonList();

            // FIXED: Refresh parent window to update lesson count
            if (parentWindow != null) {
                parentWindow.loadCourses();
            }

            JOptionPane.showMessageDialog(this, "Lesson added successfully!");
        }
    }

    private void editLesson(int index) {
        Lesson lesson = course.getLessons().get(index);

        JTextField titleField = new JTextField(lesson.getTitle());
        JTextArea contentArea = new JTextArea(lesson.getContent(), 5, 20);
        JTextField resourceField = new JTextField(String.join(", ", lesson.getResources()));

        JPanel panel = new JPanel(new GridLayout(4, 1, 5, 5));
        panel.add(new JLabel("Lesson Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Content:"));
        panel.add(new JScrollPane(contentArea));
        panel.add(new JLabel("Resources (comma-separated URLs):"));
        panel.add(resourceField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Lesson",
                JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            lesson.setTitle(titleField.getText().trim());
            lesson.setContent(contentArea.getText().trim());

            lesson.getResources().clear();
            String resourceText = resourceField.getText().trim();
            if (!resourceText.isEmpty()) {
                String[] resources = resourceText.split(",");
                for (String resource : resources) {
                    lesson.addResource(resource.trim());
                }
            }

            saveCourse();
            String quizIndicator = lesson.getQuiz() != null ? " [Has Quiz]" : "";
            listModel.set(index, lesson.getTitle() + quizIndicator);
            JOptionPane.showMessageDialog(this, "Lesson updated successfully!");
        }
    }

    private void deleteLesson(int index) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this lesson?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            Lesson lesson = course.getLessons().get(index);
            course.removeLesson(lesson.getLessonId());
            saveCourse();

            // FIXED: Refresh the list
            refreshLessonList();

            // FIXED: Refresh parent window to update lesson count
            if (parentWindow != null) {
                parentWindow.loadCourses();
            }

            JOptionPane.showMessageDialog(this, "Lesson deleted successfully!");
        }
    }

    private void viewLessonContent(int index) {
        Lesson lesson = course.getLessons().get(index);

        JTextArea contentArea = new JTextArea(lesson.getContent(), 10, 40);
        contentArea.setEditable(false);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(contentArea);

        String resources = lesson.getResources().isEmpty() ?
                "No resources" : "Resources:\n" + String.join("\n", lesson.getResources());

        String quizInfo = lesson.getQuiz() != null ?
                "\n\nQuiz: Yes (" + lesson.getQuiz().getQuestions().size() + " questions)" :
                "\n\nQuiz: No";

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(new JLabel(resources + quizInfo), BorderLayout.SOUTH);

        JOptionPane.showMessageDialog(this, panel, lesson.getTitle(),
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void saveCourse() {
        List<Course> courses = JsonDatabaseManager.loadCourses();
        for (int i = 0; i < courses.size(); i++) {
            if (courses.get(i).getCourseId().equals(course.getCourseId())) {
                courses.set(i, course);
                break;
            }
        }
        JsonDatabaseManager.saveCourses(courses);
    }
}