package Frontend;

import Backend.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class QuizResultFrame extends JFrame {
    private Student student;
    private Course course;
    private Lesson lesson;
    private Quiz quiz;
    private QuizResult result;
    private LessonViewFrame parentFrame; // ADDED: Reference to parent

    // Original constructor for backward compatibility
    public QuizResultFrame(Student student, Course course, Lesson lesson,
                           Quiz quiz, QuizResult result) {
        this(student, course, lesson, quiz, result, null);
    }

    // FIXED: Constructor that accepts parent frame
    public QuizResultFrame(Student student, Course course, Lesson lesson,
                           Quiz quiz, QuizResult result, LessonViewFrame parentFrame) {
        this.student = student;
        this.course = course;
        this.lesson = lesson;
        this.quiz = quiz;
        this.result = result;
        this.parentFrame = parentFrame;

        setTitle("Quiz Results - " + lesson.getTitle());
        setSize(700, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Header with score
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(result.isPassed() ? new Color(76, 175, 80) : new Color(244, 67, 54));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel resultLabel = new JLabel(result.isPassed() ? "PASSED!" : "FAILED");
        resultLabel.setFont(new Font("Arial", Font.BOLD, 24));
        resultLabel.setForeground(Color.WHITE);
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel scoreLabel = new JLabel("Score: " + result.getScore() + "%");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 20));
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel passingLabel = new JLabel("Passing Score: " + quiz.getPassingScore() + "%");
        passingLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        passingLabel.setForeground(Color.WHITE);
        passingLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // ADDED: Message about marking lesson complete
        String completionMessage = "";
        if (result.isPassed() && quiz.isRequired()) {
            completionMessage = "You can now mark this lesson as completed!";
        }
        JLabel completionLabel = new JLabel(completionMessage);
        completionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        completionLabel.setForeground(Color.WHITE);
        completionLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel headerContent = new JPanel(new GridLayout(4, 1));
        headerContent.setOpaque(false);
        headerContent.add(resultLabel);
        headerContent.add(scoreLabel);
        headerContent.add(passingLabel);
        headerContent.add(completionLabel);

        headerPanel.add(headerContent, BorderLayout.CENTER);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Questions with answers
        JPanel questionsPanel = new JPanel();
        questionsPanel.setLayout(new BoxLayout(questionsPanel, BoxLayout.Y_AXIS));

        List<Question> questions = quiz.getQuestions();
        List<Integer> studentAnswers = result.getStudentAnswers();

        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            int studentAnswer = studentAnswers.get(i);
            int correctAnswer = question.getCorrectAnswerIndex();
            boolean isCorrect = studentAnswer == correctAnswer;

            JPanel questionPanel = new JPanel();
            questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));
            questionPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(isCorrect ? Color.GREEN : Color.RED, 2),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));

            // Question text
            JLabel questionLabel = new JLabel((i + 1) + ". " + question.getQuestionText());
            questionLabel.setFont(new Font("Arial", Font.BOLD, 14));
            questionPanel.add(questionLabel);
            questionPanel.add(Box.createVerticalStrut(10));

            // Options
            List<String> options = question.getOptions();
            for (int j = 0; j < options.size(); j++) {
                JLabel optionLabel = new JLabel(options.get(j));
                optionLabel.setFont(new Font("Arial", Font.PLAIN, 12));

                if (j == correctAnswer) {
                    optionLabel.setText("✓ " + optionLabel.getText() + " (Correct Answer)");
                    optionLabel.setForeground(new Color(0, 128, 0));
                    optionLabel.setFont(new Font("Arial", Font.BOLD, 12));
                }

                if (j == studentAnswer && !isCorrect) {
                    optionLabel.setText("✗ " + optionLabel.getText() + " (Your Answer)");
                    optionLabel.setForeground(Color.RED);
                }

                questionPanel.add(optionLabel);
            }

            questionsPanel.add(questionPanel);
            questionsPanel.add(Box.createVerticalStrut(15));
        }

        JScrollPane scrollPane = new JScrollPane(questionsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton retryButton = new JButton("Retry Quiz");
        JButton closeButton = new JButton("Close");

        if (result.isPassed()) {
            retryButton.setEnabled(false);
        }

        retryButton.addActionListener(e -> {
            new QuizFrame(student, course, lesson).setVisible(true);
            dispose();
        });

        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(retryButton);
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }
}