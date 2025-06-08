import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProgressStepsPanel extends JPanel {
    private String[] steps;
    private int currentStep;
    private float animationProgress = 1.0f;
    private Timer animationTimer;

    // Customizable colors
    private Color completedColor = new Color(37, 99, 235);     // Blue
    private Color currentColor = new Color(59, 130, 246);      // Lighter blue
    private Color pendingColor = new Color(203, 213, 225);     // Light gray
    private Color textColor = new Color(70, 70, 70);           // Dark gray
    private Color completedTextColor = new Color(37, 99, 235); // Blue

    // Customizable dimensions
    private int stepRadius = 32;
    private int yCenter = 40;
    private int xPadding = 40;
    private int verticalTextOffset = 40;

    // Animation settings
    private int animationSpeed = 16; // milliseconds
    private float animationStep = 0.05f;

    // Listeners
    private List<StepChangeListener> stepChangeListeners = new ArrayList<>();

    public ProgressStepsPanel(String[] steps, int currentStep) {
        this.steps = steps;
        this.currentStep = currentStep;
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(700, 100));

        // Enable animation when currentStep changes
        setupAnimation();
    }

    public ProgressStepsPanel() {
        this(new String[]{"Tạo phiếu", "Nhập kiểm kê", "Chốt kiểm kê", "Hoàn thành"}, 2);
    }

    private void setupAnimation() {
        animationTimer = new Timer(animationSpeed, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                animationProgress += animationStep;
                if (animationProgress >= 1.0f) {
                    animationProgress = 1.0f;
                    animationTimer.stop();
                }
                repaint();
            }
        });
    }

    public void setCurrentStep(int step) {
        if (step != this.currentStep && step > 0 && step <= steps.length) {
            int previousStep = this.currentStep;
            this.currentStep = step;
            animationProgress = 0.0f;
            animationTimer.start();
            notifyStepChanged(previousStep, step);
        }
    }

    // Notify all listeners about a step change
    private void notifyStepChanged(int oldStep, int newStep) {
        for (StepChangeListener listener : stepChangeListeners) {
            listener.onStepChanged(oldStep, newStep);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

        int width = getWidth();
        int stepRadius = 32;
        int yCenter = 40;

        // Calculate positions to distribute steps evenly
        int totalStepsWidth = steps.length * stepRadius;
        int availableSpace = width - 80; // padding on both sides
        int stepGap = (availableSpace - totalStepsWidth) / (steps.length - 1);
        int xStart = 40; // starting position

        for (int i = 0; i < steps.length; i++) {
            int x = xStart + i * (stepRadius + stepGap);
            boolean isCompleted = (i + 1) < currentStep;
            boolean isCurrent = (i + 1) == currentStep;
            boolean isAnimating = isCurrent && animationProgress < 1.0f;

            // Draw connecting lines
            if (i > 0) {
                int lineX1 = xStart + (i - 1) * (stepRadius + stepGap) + stepRadius;
                int lineX2 = x;
                int lineY = yCenter;

                // Background line (always light gray)
                g2.setColor(pendingColor);
                g2.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.draw(new Line2D.Float(lineX1, lineY, lineX2, lineY));

                // Completed portion of line
                if (isCompleted || isCurrent) {
                    g2.setColor(completedColor);
                    if (isCurrent) {
                        // Animated line for current step
                        int animatedX2 = lineX1 + (int) ((lineX2 - lineX1) * animationProgress);
                        g2.draw(new Line2D.Float(lineX1, lineY, animatedX2, lineY));
                    } else {
                        g2.draw(new Line2D.Float(lineX1, lineY, lineX2, lineY));
                    }
                }
            }

            // Circle shadow (subtle drop shadow)
            g2.setColor(new Color(0, 0, 0, 20));
            g2.fillOval(x + 1, yCenter - stepRadius / 2 + 2, stepRadius, stepRadius);

            // Circle
            if (isCompleted) {
                g2.setColor(completedColor);
            } else if (isCurrent) {
                g2.setColor(currentColor);
            } else {
                g2.setColor(pendingColor);
            }
            g2.fillOval(x, yCenter - stepRadius / 2, stepRadius, stepRadius);

            // Number or checkmark
            g2.setColor(Color.WHITE);
            // Use a font that has good Unicode support
            g2.setFont(new Font("SansSerif", Font.BOLD, 16));
            String label = isCompleted ? "✓" : String.valueOf(i + 1);
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(label);
            int textHeight = fm.getAscent();
            g2.drawString(label, x + (stepRadius - textWidth) / 2, yCenter + textHeight / 4);

            // Text under step
            if (isCompleted || isCurrent) {
                g2.setColor(completedTextColor);
                g2.setFont(new Font("SansSerif", Font.BOLD, 13));
            } else {
                g2.setColor(textColor);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
            }

            int txtWidth = g2.getFontMetrics().stringWidth(steps[i]);
            int xText = x + (stepRadius - txtWidth) / 2;

            // If text is wider than the circle, center it under the circle
            if (txtWidth > stepRadius) {
                xText = x + (stepRadius - txtWidth) / 2;
            }

            g2.drawString(steps[i], xText, yCenter + 40);

            // Draw a "glow" effect for current step
            if (isCurrent) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f));
                g2.setColor(currentColor);
                g2.fillOval(x - 5, yCenter - stepRadius / 2 - 5, stepRadius + 10, stepRadius + 10);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            }
        }
    }

    // Setter methods for customization
    public void setCompletedColor(Color color) {
        this.completedColor = color;
        repaint();
    }

    public void setCurrentColor(Color color) {
        this.currentColor = color;
        repaint();
    }

    public void setPendingColor(Color color) {
        this.pendingColor = color;
        repaint();
    }

    public void setTextColor(Color color) {
        this.textColor = color;
        repaint();
    }

    public void setCompletedTextColor(Color color) {
        this.completedTextColor = color;
        repaint();
    }


    /**
     * Update the steps array with new values
     *
     * @param newSteps New array of step names
     */
    public void setSteps(String[] newSteps) {
        if (newSteps != null && newSteps.length > 0) {
            this.steps = newSteps;
            // Adjust current step if needed
            if (this.currentStep > newSteps.length) {
                this.currentStep = newSteps.length;
            }
            repaint();
        }
    }

    /**
     * Add a step at the end
     *
     * @param step Step name to add
     */
    public void addStep(String step) {
        String[] newSteps = Arrays.copyOf(this.steps, this.steps.length + 1);
        newSteps[newSteps.length - 1] = step;
        this.steps = newSteps;
        repaint();
    }

    /**
     * Insert a step at the specified position
     *
     * @param step     Step name to insert
     * @param position Position (0-based index)
     */
    public void insertStep(String step, int position) {
        if (position < 0 || position > this.steps.length) {
            return;
        }

        String[] newSteps = new String[this.steps.length + 1];

        // Copy elements before the insertion point
        System.arraycopy(this.steps, 0, newSteps, 0, position);

        // Insert the new step
        newSteps[position] = step;

        // Copy elements after the insertion point
        System.arraycopy(this.steps, position, newSteps, position + 1, this.steps.length - position);

        this.steps = newSteps;

        // Adjust current step if needed
        if (this.currentStep > position) {
            this.currentStep++;
        }

        repaint();
    }

    /**
     * Remove a step at the specified position
     *
     * @param position Position (0-based index) to remove
     */
    public void removeStep(int position) {
        if (position < 0 || position >= this.steps.length || this.steps.length <= 1) {
            return; // Invalid position or can't remove the last step
        }

        String[] newSteps = new String[this.steps.length - 1];

        // Copy elements before the removal point
        System.arraycopy(this.steps, 0, newSteps, 0, position);

        // Copy elements after the removal point
        System.arraycopy(this.steps, position + 1, newSteps, position, this.steps.length - position - 1);

        this.steps = newSteps;

        // Adjust current step if needed
        if (this.currentStep > position) {
            this.currentStep--;
        }
        if (this.currentStep > this.steps.length) {
            this.currentStep = this.steps.length;
        }

        repaint();
    }

    /**
     * Customize the size of the step circles
     *
     * @param radius Radius in pixels
     */
    public void setStepRadius(int radius) {
        if (radius > 10) { // Enforce minimum size
            this.stepRadius = radius;
            repaint();
        }
    }

    /**
     * Set the vertical center position of the steps
     *
     * @param yCenter Y-coordinate for the center
     */
    public void setYCenter(int yCenter) {
        this.yCenter = yCenter;
        repaint();
    }

    /**
     * Set horizontal padding from edges
     *
     * @param padding Padding in pixels
     */
    public void setXPadding(int padding) {
        this.xPadding = padding;
        repaint();
    }

    /**
     * Set vertical offset for step text labels
     *
     * @param offset Vertical offset in pixels
     */
    public void setVerticalTextOffset(int offset) {
        this.verticalTextOffset = offset;
        repaint();
    }

    /**
     * Customize animation speed
     *
     * @param speed Timer delay in milliseconds
     * @param step  Progress increment per timer tick (0.0-1.0)
     */
    public void setAnimationProperties(int speed, float step) {
        this.animationSpeed = speed;
        this.animationStep = step;
        if (animationTimer != null) {
            animationTimer.setDelay(speed);
        }
    }

    /**
     * Move to the next step if possible
     *
     * @return true if successfully moved, false if already at last step
     */
    public boolean nextStep() {
        if (currentStep < steps.length) {
            setCurrentStep(currentStep + 1);
            return true;
        }
        return false;
    }

    /**
     * Move to the previous step if possible
     *
     * @return true if successfully moved, false if already at first step
     */
    public boolean previousStep() {
        if (currentStep > 1) {
            setCurrentStep(currentStep - 1);
            return true;
        }
        return false;
    }

    /**
     * Reset to the first step
     */
    public void reset() {
        setCurrentStep(1);
    }

    /**
     * Complete all steps
     */
    public void complete() {
        setCurrentStep(steps.length);
    }

    /**
     * Get the current step (1-based index)
     *
     * @return Current step number
     */
    public int getCurrentStep() {
        return currentStep;
    }

    /**
     * Get current step name
     *
     * @return Name of the current step
     */
    public String getCurrentStepName() {
        return steps[currentStep - 1];
    }

    /**
     * Get the total number of steps
     *
     * @return Number of steps
     */
    public int getStepCount() {
        return steps.length;
    }

    /**
     * Check if we're at the first step
     *
     * @return true if at first step
     */
    public boolean isFirstStep() {
        return currentStep == 1;
    }

    /**
     * Check if we're at the last step
     *
     * @return true if at last step
     */
    public boolean isLastStep() {
        return currentStep == steps.length;
    }

    /**
     * Get the animation progress for the current transition
     *
     * @return Animation progress (0.0-1.0)
     */
    public float getAnimationProgress() {
        return animationProgress;
    }

    /**
     * Check if animation is currently running
     *
     * @return true if animating
     */
    public boolean isAnimating() {
        return animationTimer.isRunning();
    }

    /**
     * Force stop any running animation
     */
    public void stopAnimation() {
        if (animationTimer.isRunning()) {
            animationTimer.stop();
            animationProgress = 1.0f;
            repaint();
        }
    }

    /**
     * Set all colors at once with a color theme
     *
     * @param completed     Color for completed steps
     * @param current       Color for current step
     * @param pending       Color for pending steps
     * @param text          Color for normal text
     * @param completedText Color for completed step text
     */
    public void setColorTheme(Color completed, Color current, Color pending, Color text, Color completedText) {
        this.completedColor = completed;
        this.currentColor = current;
        this.pendingColor = pending;
        this.textColor = text;
        this.completedTextColor = completedText;
        repaint();
    }

    /**
     * Set the panel to use a blue theme
     */
    public void useBlueTheme() {
        setColorTheme(
                new Color(37, 99, 235),    // Blue
                new Color(59, 130, 246),   // Lighter blue
                new Color(203, 213, 225),  // Light gray
                new Color(70, 70, 70),     // Dark gray
                new Color(37, 99, 235)     // Blue
        );
    }

    /**
     * Set the panel to use a green theme
     */
    public void useGreenTheme() {
        setColorTheme(
                new Color(22, 163, 74),    // Green
                new Color(34, 197, 94),    // Lighter green
                new Color(203, 213, 225),  // Light gray
                new Color(70, 70, 70),     // Dark gray
                new Color(22, 163, 74)     // Green
        );
    }

    /**
     * Set the panel to use a dark theme
     */
    public void useDarkTheme() {
        setBackground(new Color(30, 30, 30));
        setColorTheme(
                new Color(59, 130, 246),   // Blue
                new Color(96, 165, 250),   // Lighter blue
                new Color(100, 100, 100),  // Gray
                new Color(220, 220, 220),  // Light gray
                new Color(96, 165, 250)    // Lighter blue
        );
    }

    /**
     * Interface for step change event listeners
     */
    public interface StepChangeListener {
        void onStepChanged(int oldStep, int newStep);
    }

    /**
     * Add a listener for step change events
     *
     * @param listener The listener to add
     */
    public void addStepChangeListener(StepChangeListener listener) {
        stepChangeListeners.add(listener);
    }

    /**
     * Remove a step change listener
     *
     * @param listener The listener to remove
     */
    public void removeStepChangeListener(StepChangeListener listener) {
        stepChangeListeners.remove(listener);
    }

    /**
     * Create a new steps panel with the specified parameters
     *
     * @param stepNames   Array of step names
     * @param initialStep Starting step (1-based)
     * @param width       Panel width
     * @param height      Panel height
     * @return Configured ProgressStepsPanel
     */
    public static ProgressStepsPanel createPanel(String[] stepNames, int initialStep, int width, int height) {
        ProgressStepsPanel panel = new ProgressStepsPanel(stepNames, initialStep);
        panel.setPreferredSize(new Dimension(width, height));
        return panel;
    }

    /**
     * Replace step text with HTML to support special characters and formatting
     *
     * @param stepIndex The index of the step to modify (0-based)
     * @param htmlText  HTML formatted text for the step
     */
    public void setStepHtml(int stepIndex, String htmlText) {
        if (stepIndex >= 0 && stepIndex < steps.length) {
            steps[stepIndex] = htmlText;
            repaint();
        }
    }

    /**
     * Set all step labels using HTML formatting
     *
     * @param htmlSteps Array of HTML formatted text for all steps
     */
    public void setStepsHtml(String[] htmlSteps) {
        if (htmlSteps != null && htmlSteps.length > 0) {
            this.steps = htmlSteps;
            repaint();
        }
    }

    /**
     * Get available fonts that support Unicode characters
     *
     * @return Array of font names with good Unicode support
     */
    public static String[] getUnicodeSupportingFonts() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        return ge.getAvailableFontFamilyNames();
    }

    /**
     * Set a custom font for the component to ensure special character support
     *
     * @param fontFamily Font family name
     */
    public void setUnicodeFont(String fontFamily) {
        try {
            Font stepNumberFont = new Font(fontFamily, Font.BOLD, 16);
            Font stepTextFont = new Font(fontFamily, Font.PLAIN, 13);
            // Store font settings in component properties for use in paintComponent
            putClientProperty("stepNumberFont", stepNumberFont);
            putClientProperty("stepTextFont", stepTextFont);
            repaint();
        } catch (Exception e) {
            System.err.println("Font not supported: " + e.getMessage());
        }
    }


//    public static void main(String[] args) {
//
//        SwingUtilities.invokeLater(() -> {
//            JFrame frame = new JFrame("Progress Steps Panel");
//            ProgressStepsPanel stepsPanel = new ProgressStepsPanel(
//                    new String[]{"Step 1", "Step 2", "Step 3", "Step 4"}, 2);
//            stepsPanel.setPreferredSize(new Dimension(800, 100));
//            stepsPanel.setBackground(Color.WHITE);
//            stepsPanel.useBlueTheme();
//            stepsPanel.addStepChangeListener((oldStep, newStep) -> {
//                System.out.println("Step changed from " + oldStep + " to " + newStep);
//            });
//
//            frame.add(stepsPanel);
//            frame.pack();
//            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//            frame.setVisible(true);
//        });
//    }
}
