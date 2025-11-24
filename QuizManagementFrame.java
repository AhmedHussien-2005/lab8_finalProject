package Frontend;

import Backend.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Frame for instructors to create and manage quizzes for lessons
 * Allows adding/editing/deleting questions and quiz settings
 */
public class QuizManagementFrame extends JFrame {
    private Instructor instructor;
    private Course course;
    private Lesson lesson;
    private Quiz quiz;

    public QuizManagementFrame(Instructor instructor, Course course, Lesson lesson) {
        this.instructor = instructor;
        this.course = course;
        this.lesson = lesson;
        this.quiz = lesson.getQuiz();

        setTitle("Manage Quiz - " + lesson.getTitle());
        setSize(750, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Header
        JLabel titleLabel = new JLabel("Quiz Management for: " + lesson.getTitle());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        if (quiz == null) {
            // No quiz exists - show create option
            mainPanel.add(createNoQuizPanel(), BorderLayout.CENTER);
        } else {
            // Quiz exists - show management interface
            mainPanel.add(createQuizManagementPanel(), BorderLayout.CENTER);
        }

        // Close button at bottom
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(closeButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    /**
     * Panel shown when no quiz exists for this lesson
     */
    private JPanel createNoQuizPanel() {
        JPanel panel = new JPanel(new GridBagLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        JLabel messageLabel = new JLabel("This lesson does not have a quiz yet.");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton createQuizButton = new JButton("Create Quiz for This Lesson");
        createQuizButton.setFont(new Font("Arial", Font.BOLD, 14));
        createQuizButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        createQuizButton.addActionListener(e -> createQuiz());

        contentPanel.add(messageLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(createQuizButton);

        panel.add(contentPanel);
        return panel;
    }

    /**
     * Panel shown when quiz exists - displays questions and management options
     */
    private JPanel createQuizManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Quiz settings panel at top
        JPanel settingsPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        settingsPanel.setBorder(BorderFactory.createTitledBorder("Quiz Settings"));
        settingsPanel.add(new JLabel("Passing Score: " + quiz.getPassingScore() + "%"));
        settingsPanel.add(new JLabel("Required to Complete Lesson: " + (quiz.isRequired() ? "Yes" : "No")));
        panel.add(settingsPanel, BorderLayout.NORTH);

        // Questions list in center
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (int i = 0; i < quiz.getQuestions().size(); i++) {
            Question q = quiz.getQuestions().get(i);
            listModel.addElement((i + 1) + ". " + q.getQuestionText());
        }
        JList<String> questionsList = new JList<>(listModel);
        questionsList.setFont(new Font("Arial", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(questionsList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Questions (" + quiz.getQuestions().size() + ")"));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Buttons panel at bottom
        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 10, 10));

        JButton addQuestionButton = new JButton("Add Question");
        JButton editQuestionButton = new JButton("Edit Question");
        JButton deleteQuestionButton = new JButton("Delete Question");
        JButton editSettingsButton = new JButton("Edit Settings");
        JButton viewQuestionButton = new JButton("View Details");
        JButton deleteQuizButton = new JButton("Delete Quiz");

        // Set colors for important actions
        addQuestionButton.setBackground(new Color(76, 175, 80));
        addQuestionButton.setForeground(Color.WHITE);
        deleteQuizButton.setBackground(new Color(244, 67, 54));
        deleteQuizButton.setForeground(Color.WHITE);

        // Add action listeners
        addQuestionButton.addActionListener(e -> addQuestion(listModel));

        editQuestionButton.addActionListener(e -> {
            int selected = questionsList.getSelectedIndex();
            if (selected >= 0) {
                editQuestion(selected, listModel);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a question to edit.");
            }
        });

        deleteQuestionButton.addActionListener(e -> {
            int selected = questionsList.getSelectedIndex();
            if (selected >= 0) {
                deleteQuestion(selected, listModel);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a question to delete.");
            }
        });

        viewQuestionButton.addActionListener(e -> {
            int selected = questionsList.getSelectedIndex();
            if (selected >= 0) {
                viewQuestionDetails(selected);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a question to view.");
            }
        });

        editSettingsButton.addActionListener(e -> editSettings());

        deleteQuizButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete this entire quiz?\nThis action cannot be undone.",
                    "Confirm Quiz Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                lesson.setQuiz(null);
                saveCourse();
                JOptionPane.showMessageDialog(this, "Quiz deleted successfully!");
                dispose();
                new QuizManagementFrame(instructor, course, lesson).setVisible(true);
            }
        });

        buttonPanel.add(addQuestionButton);
        buttonPanel.add(editQuestionButton);
        buttonPanel.add(deleteQuestionButton);
        buttonPanel.add(editSettingsButton);
        buttonPanel.add(viewQuestionButton);
        buttonPanel.add(deleteQuizButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Create a new quiz for the lesson
     */
    private void createQuiz() {
        JTextField passingScoreField = new JTextField("70");
        JCheckBox requiredCheckBox = new JCheckBox("Required to complete lesson", true);

        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.add(new JLabel("Set the passing score percentage (0-100):"));
        panel.add(passingScoreField);
        panel.add(new JLabel("Is this quiz required?"));
        panel.add(requiredCheckBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Create Quiz",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                int passingScore = Integer.parseInt(passingScoreField.getText().trim());

                if (passingScore < 0 || passingScore > 100) {
                    JOptionPane.showMessageDialog(this,
                            "Passing score must be between 0 and 100.",
                            "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String quizId = "QZ" + System.currentTimeMillis();
                quiz = new Quiz(quizId, lesson.getLessonId(), passingScore);
                quiz.setRequired(requiredCheckBox.isSelected());
                lesson.setQuiz(quiz);
                saveCourse();

                JOptionPane.showMessageDialog(this,
                        "Quiz created successfully!\nNow you can add questions to it.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new QuizManagementFrame(instructor, course, lesson).setVisible(true);

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a valid number for passing score.",
                        "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * NEW: Validate that all options are unique
     */
    private boolean validateUniqueOptions(List<String> options, String actionName) {
        // Check for empty options
        for (int i = 0; i < options.size(); i++) {
            if (options.get(i).isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Option " + (i + 1) + " cannot be empty.",
                        "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        // Check for duplicate options (case-insensitive)
        Set<String> uniqueOptions = new HashSet<>();
        for (int i = 0; i < options.size(); i++) {
            String optionLower = options.get(i).trim().toLowerCase();
            if (uniqueOptions.contains(optionLower)) {
                // Find which option is duplicated
                for (int j = 0; j < i; j++) {
                    if (options.get(j).trim().equalsIgnoreCase(options.get(i).trim())) {
                        JOptionPane.showMessageDialog(this,
                                "Duplicate answer option detected!\n\n" +
                                        "Option " + (j + 1) + " and Option " + (i + 1) + " are the same:\n" +
                                        "\"" + options.get(i).trim() + "\"\n\n" +
                                        "Each answer option must be unique.",
                                "Duplicate Options", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                }
            }
            uniqueOptions.add(optionLower);
        }

        return true;
    }

    /**
     * Add a new question to the quiz
     */
    private void addQuestion(DefaultListModel<String> listModel) {
        JTextField questionTextField = new JTextField();
        JTextField option1Field = new JTextField();
        JTextField option2Field = new JTextField();
        JTextField option3Field = new JTextField();
        JTextField option4Field = new JTextField();
        String[] choices = {"Option 1", "Option 2", "Option 3", "Option 4"};
        JComboBox<String> correctAnswerCombo = new JComboBox<>(choices);

        JPanel panel = new JPanel(new GridLayout(7, 2, 5, 5));
        panel.add(new JLabel("Question Text:"));
        panel.add(questionTextField);
        panel.add(new JLabel("Option 1:"));
        panel.add(option1Field);
        panel.add(new JLabel("Option 2:"));
        panel.add(option2Field);
        panel.add(new JLabel("Option 3:"));
        panel.add(option3Field);
        panel.add(new JLabel("Option 4:"));
        panel.add(option4Field);
        panel.add(new JLabel("Correct Answer:"));
        panel.add(correctAnswerCombo);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Question",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String questionText = questionTextField.getText().trim();

            List<String> options = new ArrayList<>();
            options.add(option1Field.getText().trim());
            options.add(option2Field.getText().trim());
            options.add(option3Field.getText().trim());
            options.add(option4Field.getText().trim());

            // Validate question text
            if (questionText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Question text cannot be empty.",
                        "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // FIXED: Validate unique options
            if (!validateUniqueOptions(options, "Add Question")) {
                return;
            }

            int correctIndex = correctAnswerCombo.getSelectedIndex();
            String questionId = "Q" + UUID.randomUUID().toString();

            Question question = new Question(questionId, questionText, options, correctIndex);
            quiz.addQuestion(question);
            saveCourse();

            listModel.addElement(quiz.getQuestions().size() + ". " + questionText);
            JOptionPane.showMessageDialog(this, "Question added successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Edit an existing question
     */
    private void editQuestion(int index, DefaultListModel<String> listModel) {
        Question question = quiz.getQuestions().get(index);

        JTextField questionTextField = new JTextField(question.getQuestionText());
        JTextField[] optionFields = new JTextField[4];

        for (int i = 0; i < 4; i++) {
            if (i < question.getOptions().size()) {
                optionFields[i] = new JTextField(question.getOptions().get(i));
            } else {
                optionFields[i] = new JTextField();
            }
        }

        String[] choices = {"Option 1", "Option 2", "Option 3", "Option 4"};
        JComboBox<String> correctAnswerCombo = new JComboBox<>(choices);
        correctAnswerCombo.setSelectedIndex(question.getCorrectAnswerIndex());

        JPanel panel = new JPanel(new GridLayout(7, 2, 5, 5));
        panel.add(new JLabel("Question Text:"));
        panel.add(questionTextField);

        for (int i = 0; i < 4; i++) {
            panel.add(new JLabel("Option " + (i + 1) + ":"));
            panel.add(optionFields[i]);
        }

        panel.add(new JLabel("Correct Answer:"));
        panel.add(correctAnswerCombo);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Question",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String questionText = questionTextField.getText().trim();
            List<String> options = new ArrayList<>();

            for (int i = 0; i < 4; i++) {
                options.add(optionFields[i].getText().trim());
            }

            // Validate question text
            if (questionText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Question text cannot be empty.",
                        "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // FIXED: Validate unique options
            if (!validateUniqueOptions(options, "Edit Question")) {
                return;
            }

            question.setQuestionText(questionText);
            question.setOptions(options);
            question.setCorrectAnswerIndex(correctAnswerCombo.getSelectedIndex());

            saveCourse();
            listModel.set(index, (index + 1) + ". " + question.getQuestionText());
            JOptionPane.showMessageDialog(this, "Question updated successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Delete a question from the quiz
     */
    private void deleteQuestion(int index, DefaultListModel<String> listModel) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this question?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            Question question = quiz.getQuestions().get(index);
            quiz.removeQuestion(question.getQuestionId());
            saveCourse();
            listModel.remove(index);

            // Renumber remaining questions in the list
            for (int i = index; i < listModel.size(); i++) {
                String text = listModel.get(i);
                text = (i + 1) + text.substring(text.indexOf("."));
                listModel.set(i, text);
            }

            JOptionPane.showMessageDialog(this, "Question deleted successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * View detailed information about a question
     */
    private void viewQuestionDetails(int index) {
        Question question = quiz.getQuestions().get(index);

        StringBuilder details = new StringBuilder();
        details.append("Question: ").append(question.getQuestionText()).append("\n\n");
        details.append("Options:\n");

        for (int i = 0; i < question.getOptions().size(); i++) {
            String marker = (i == question.getCorrectAnswerIndex()) ? " ✓ (Correct)" : "";
            details.append((i + 1)).append(". ").append(question.getOptions().get(i)).append(marker).append("\n");
        }

        JTextArea textArea = new JTextArea(details.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Arial", Font.PLAIN, 13));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 200));

        JOptionPane.showMessageDialog(this, scrollPane,
                "Question " + (index + 1) + " Details", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Edit quiz settings (passing score and required status)
     */
    private void editSettings() {
        JTextField passingScoreField = new JTextField(String.valueOf(quiz.getPassingScore()));
        JCheckBox requiredCheckBox = new JCheckBox("Required to complete lesson", quiz.isRequired());

        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.add(new JLabel("Passing Score (%):"));
        panel.add(passingScoreField);
        panel.add(new JLabel("Is this quiz required?"));
        panel.add(requiredCheckBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Quiz Settings",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                int passingScore = Integer.parseInt(passingScoreField.getText().trim());

                if (passingScore < 0 || passingScore > 100) {
                    JOptionPane.showMessageDialog(this,
                            "Passing score must be between 0 and 100.",
                            "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                quiz.setPassingScore(passingScore);
                quiz.setRequired(requiredCheckBox.isSelected());
                saveCourse();

                JOptionPane.showMessageDialog(this, "Settings updated successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new QuizManagementFrame(instructor, course, lesson).setVisible(true);

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a valid number for passing score.",
                        "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Save the course with updated quiz data
     */
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