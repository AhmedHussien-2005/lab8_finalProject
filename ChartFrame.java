package Frontend;

import Backend.PerformanceAnalytics.StudentPerformance;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ChartFrame extends JFrame {
    private String chartTitle;
    private List<StudentPerformance> data;
    private String courseTitle;

    public ChartFrame(String chartTitle, List<StudentPerformance> data, String courseTitle) {
        this.chartTitle = chartTitle;
        this.data = data;
        this.courseTitle = courseTitle;

        setTitle(chartTitle + " - " + courseTitle);
        setSize(900, 600);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Header
        JLabel titleLabel = new JLabel(chartTitle + " - " + courseTitle);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Chart panel
        ChartPanel chartPanel = new ChartPanel(data);
        mainPanel.add(chartPanel, BorderLayout.CENTER);

        // Close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    // Custom panel for drawing bar chart
    class ChartPanel extends JPanel {
        private List<StudentPerformance> data;

        public ChartPanel(List<StudentPerformance> data) {
            this.data = data;
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (data == null || data.isEmpty()) {
                g2d.drawString("No data available", getWidth() / 2 - 50, getHeight() / 2);
                return;
            }

            int margin = 60;
            int chartWidth = getWidth() - 2 * margin;
            int chartHeight = getHeight() - 2 * margin;

            // Draw axes
            g2d.setColor(Color.BLACK);
            g2d.drawLine(margin, margin, margin, getHeight() - margin); // Y-axis
            g2d.drawLine(margin, getHeight() - margin, getWidth() - margin, getHeight() - margin); // X-axis

            // Draw Y-axis labels (0-100%)
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            for (int i = 0; i <= 10; i++) {
                int y = getHeight() - margin - (i * chartHeight / 10);
                g2d.drawString((i * 10) + "%", margin - 35, y + 5);
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.drawLine(margin, y, getWidth() - margin, y);
                g2d.setColor(Color.BLACK);
            }

            // Draw bars
            int barWidth = chartWidth / (data.size() * 2);
            int spacing = barWidth / 2;

            for (int i = 0; i < data.size(); i++) {
                StudentPerformance perf = data.get(i);

                int x = margin + (i * (barWidth * 2 + spacing)) + spacing;

                // Completion bar (blue)
                int completionHeight = (int) (perf.getCompletionPercent() * chartHeight / 100.0);
                g2d.setColor(new Color(33, 150, 243));
                g2d.fillRect(x, getHeight() - margin - completionHeight, barWidth - 5, completionHeight);

                // Quiz score bar (green)
                int quizHeight = (int) (perf.getAverageQuizScore() * chartHeight / 100.0);
                g2d.setColor(new Color(76, 175, 80));
                g2d.fillRect(x + barWidth, getHeight() - margin - quizHeight, barWidth - 5, quizHeight);

                // Student name
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.PLAIN, 9));
                String name = perf.getStudentName();
                if (name.length() > 10) {
                    name = name.substring(0, 8) + "..";
                }
                g2d.drawString(name, x, getHeight() - margin + 15);
            }

            // Legend
            int legendX = getWidth() - margin - 150;
            int legendY = margin + 20;

            g2d.setColor(new Color(33, 150, 243));
            g2d.fillRect(legendX, legendY, 20, 15);
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            g2d.drawString("Completion %", legendX + 25, legendY + 12);

            g2d.setColor(new Color(76, 175, 80));
            g2d.fillRect(legendX, legendY + 25, 20, 15);
            g2d.setColor(Color.BLACK);
            g2d.drawString("Avg Quiz Score %", legendX + 25, legendY + 37);
        }
    }
}