package Frontend;

import Backend.Certificate;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Frame to display a certificate
 */
public class CertificateViewFrame extends JFrame {
    private Certificate certificate;

    public CertificateViewFrame(Certificate certificate) {
        this.certificate = certificate;

        setTitle("Certificate - " + certificate.getCourseTitle());
        setSize(700, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Certificate panel
        JPanel certificatePanel = createCertificatePanel();
        mainPanel.add(certificatePanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private JPanel createCertificatePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(184, 134, 11), 5),
                new EmptyBorder(30, 40, 30, 40)
        ));

        // Header decoration
        JLabel decorLine1 = new JLabel("═══════════════════════════════════");
        decorLine1.setFont(new Font("Serif", Font.BOLD, 20));
        decorLine1.setForeground(new Color(184, 134, 11));
        decorLine1.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(decorLine1);

        panel.add(Box.createVerticalStrut(20));

        // Title
        JLabel titleLabel = new JLabel("CERTIFICATE OF COMPLETION");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 28));
        titleLabel.setForeground(new Color(25, 25, 112));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);

        panel.add(Box.createVerticalStrut(30));

        // "This certifies that"
        JLabel certifiesLabel = new JLabel("This certifies that");
        certifiesLabel.setFont(new Font("Serif", Font.ITALIC, 16));
        certifiesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(certifiesLabel);

        panel.add(Box.createVerticalStrut(15));

        // Student name
        JLabel nameLabel = new JLabel(certificate.getStudentName());
        nameLabel.setFont(new Font("Serif", Font.BOLD, 32));
        nameLabel.setForeground(new Color(0, 0, 139));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(nameLabel);

        panel.add(Box.createVerticalStrut(15));

        // Underline
        JLabel underline = new JLabel("_");
        underline.setFont(new Font("Serif", Font.PLAIN, 14));
        underline.setForeground(new Color(184, 134, 11));
        underline.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(underline);

        panel.add(Box.createVerticalStrut(20));

        // "has successfully completed"
        JLabel completedLabel = new JLabel("has successfully completed the course");
        completedLabel.setFont(new Font("Serif", Font.ITALIC, 16));
        completedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(completedLabel);

        panel.add(Box.createVerticalStrut(15));

        // Course title
        JLabel courseLabel = new JLabel(certificate.getCourseTitle());
        courseLabel.setFont(new Font("Serif", Font.BOLD, 24));
        courseLabel.setForeground(new Color(139, 0, 0));
        courseLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(courseLabel);

        panel.add(Box.createVerticalStrut(25));

        // Score and date panel
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        infoPanel.setBackground(Color.WHITE);

        JLabel scoreLabel = new JLabel("Final Score: " + certificate.getFinalScore() + "%");
        scoreLabel.setFont(new Font("Serif", Font.PLAIN, 14));
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        scoreLabel.setForeground(new Color(0, 100, 0));

        JLabel dateLabel = new JLabel("Date of Completion: " + certificate.getFormattedIssueDate());
        dateLabel.setFont(new Font("Serif", Font.PLAIN, 14));
        dateLabel.setHorizontalAlignment(SwingConstants.CENTER);

        infoPanel.add(scoreLabel);
        infoPanel.add(dateLabel);
        panel.add(infoPanel);

        panel.add(Box.createVerticalStrut(25));

        // Certificate ID
        JLabel certIdLabel = new JLabel("Certificate ID: " + certificate.getCertificateId());
        certIdLabel.setFont(new Font("Monospaced", Font.PLAIN, 10));
        certIdLabel.setForeground(Color.GRAY);
        certIdLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(certIdLabel);

        panel.add(Box.createVerticalStrut(15));

        // Footer decoration
        JLabel decorLine2 = new JLabel("═══════════════════════════════════");
        decorLine2.setFont(new Font("Serif", Font.BOLD, 20));
        decorLine2.setForeground(new Color(184, 134, 11));
        decorLine2.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(decorLine2);

        return panel;
    }
}