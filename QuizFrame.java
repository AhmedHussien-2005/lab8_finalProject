package Frontend;

import Backend.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class QuizFrame extends JFrame {
    private Student student;
    private Course course;
    private Lesson lesson;
    private Quiz quiz;
    private List<ButtonGroup> answerGroups;
    private List<JRadioButton[]> radioButtons;

    public QuizFrame(Student student, Course course, Lesson lesson) {
        this.student = student;
        this.course = course;
        this.lesson = lesson;
        this.quiz = lesson.getQuiz();

        setTitle("Quiz - " + lesson.getTitle());
        setSize(700, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        if (quiz == null || quiz.getQuestions().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No quiz available for this lesson.",
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
            return;
        }

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Quiz: " + lesson.getTitle());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        JLabel infoLabel = new JLabel("Passing Score: " + quiz.getPassingScore() + "%");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(infoLabel, BorderLayout.SOUTH);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Questions panel with scroll
        JPanel questionsPanel = new JPanel();
        questionsPanel.setLayout(new BoxLayout(questionsPanel, BoxLayout.Y_AXIS));

        answerGroups = new ArrayList<>();
        radioButtons = new ArrayList<>();

        List<Question> questions = quiz.getQuestions();
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);

            JPanel questionPanel = new JPanel();
            questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));
            questionPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.GRAY),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));

            JLabel questionLabel = new JLabel((i + 1) + ". " + question.getQuestionText());
            questionLabel.setFont(new Font("Arial", Font.BOLD, 14));
            questionPanel.add(questionLabel);
            questionPanel.add(Box.createVerticalStrut(10));

            ButtonGroup group = new ButtonGroup();
            JRadioButton[] buttons = new JRadioButton[question.getOptions().size()];

            for (int j = 0; j < question.getOptions().size(); j++) {
                JRadioButton radioButton = new JRadioButton(question.getOptions().get(j));
                radioButton.setFont(new Font("Arial", Font.PLAIN, 12));
                buttons[j] = radioButton;
                group.add(radioButton);
                questionPanel.add(radioButton);
            }

            answerGroups.add(group);
            radioButtons.add(buttons);

            questionsPanel.add(questionPanel);
            questionsPanel.add(Box.createVerticalStrut(15));
        }

        JScrollPane scrollPane = new JScrollPane(questionsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton submitButton = new JButton("Submit Quiz");
        JButton cancelButton = new JButton("Cancel");

        submitButton.setBackground(new Color(76, 175, 80));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFont(new Font("Arial", Font.BOLD, 14));

        submitButton.addActionListener(e -> submitQuiz());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private void submitQuiz() {
        // Collect student answers
        List<Integer> studentAnswers = new ArrayList<>();

        for (int i = 0; i < radioButtons.size(); i++) {
            JRadioButton[] buttons = radioButtons.get(i);
            int selectedAnswer = -1;

            for (int j = 0; j < buttons.length; j++) {
                if (buttons[j].isSelected()) {
                    selectedAnswer = j;
                    break;
                }
            }

            if (selectedAnswer == -1) {
                JOptionPane.showMessageDialog(this,
                        "Please answer all questions before submitting.",
                        "Incomplete Quiz",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            studentAnswers.add(selectedAnswer);
        }

        // Calculate score
        int score = quiz.calculateScore(studentAnswers);
        boolean passed = quiz.isPassed(score);

        // Create quiz result
        QuizResult result = new QuizResult(
                student.getUserId(),
                quiz.getQuizId(),
                lesson.getLessonId(),
                course.getCourseId(),
                score,
                studentAnswers,
                passed
        );

        // Record the attempt
        student.recordQuizAttempt(result);

        // FIXED: If passed and lesson not completed, mark as completed
        // Using only lessonId as the parameter
        if (passed && !student.isLessonCompleted(lesson.getLessonId())) {
            student.markLessonCompleted(lesson.getLessonId());
        }

        // Save student data
        List<User> users = JsonDatabaseManager.loadUsers();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserId().equals(student.getUserId())) {
                users.set(i, student);
                break;
            }
        }
        JsonDatabaseManager.saveUsers(users);

        // Show results
        new QuizResultFrame(student, course, lesson, quiz, result).setVisible(true);
        dispose();
    }
}