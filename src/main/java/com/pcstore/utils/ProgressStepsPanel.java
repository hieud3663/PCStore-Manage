package com.pcstore.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
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

    // Customizable dimensions - made smaller
    private int stepRadius = 20;                               // Reduced from 32
    private int yCenter = 25;                                  // Reduced from 40
    private int xPadding = 30;                                 // Reduced from 40
    private int verticalTextOffset = 25;                       // Reduced from 40
    private int lineThickness = 3;                             // Reduced from 4
    private int glowSize = 3;                                  // Reduced from 5
    private int shadowOffset = 1;                              // Reduced from 2

    // Font sizes - made smaller
    private int stepNumberFontSize = 12;                       // Reduced from 16
    private int stepTextFontSize = 10;                         // Reduced from 13

    // Animation settings
    private int animationSpeed = 16; // milliseconds
    private float animationStep = 0.05f;

    // Listeners
    private List<StepChangeListener> stepChangeListeners = new ArrayList<>();

    // Size presets
    public enum SizePreset {
        TINY(12, 16, 15, 20, 20, 8, 9),
        SMALL(16, 20, 20, 25, 25, 10, 11),
        MEDIUM(20, 25, 25, 30, 30, 12, 13),
        LARGE(24, 32, 30, 35, 35, 14, 15),
        EXTRA_LARGE(32, 40, 40, 45, 45, 16, 17);

        public final int stepRadius;
        public final int yCenter;
        public final int xPadding;
        public final int verticalTextOffset;
        public final int preferredHeight;
        public final int stepNumberFontSize;
        public final int stepTextFontSize;

        SizePreset(int stepRadius, int yCenter, int xPadding, int verticalTextOffset, 
                  int preferredHeight, int stepNumberFontSize, int stepTextFontSize) {
            this.stepRadius = stepRadius;
            this.yCenter = yCenter;
            this.xPadding = xPadding;
            this.verticalTextOffset = verticalTextOffset;
            this.preferredHeight = preferredHeight;
            this.stepNumberFontSize = stepNumberFontSize;
            this.stepTextFontSize = stepTextFontSize;
        }
    }

    public ProgressStepsPanel(String[] steps, int currentStep) {
        this.steps = steps;
        this.currentStep = currentStep;
        setBackground(Color.WHITE);
        applySizePreset(SizePreset.SMALL); // Default to small size
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

        // Calculate positions to distribute steps evenly
        int totalStepsWidth = steps.length * stepRadius;
        int availableSpace = width - (2 * xPadding);
        int stepGap = Math.max(stepRadius, (availableSpace - totalStepsWidth) / Math.max(1, steps.length - 1));
        int xStart = xPadding;

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

                // Background line
                g2.setColor(pendingColor);
                g2.setStroke(new BasicStroke(lineThickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.draw(new Line2D.Float(lineX1, lineY, lineX2, lineY));

                // Completed portion of line
                if (isCompleted || isCurrent) {
                    g2.setColor(completedColor);
                    if (isCurrent) {
                        int animatedX2 = lineX1 + (int) ((lineX2 - lineX1) * animationProgress);
                        g2.draw(new Line2D.Float(lineX1, lineY, animatedX2, lineY));
                    } else {
                        g2.draw(new Line2D.Float(lineX1, lineY, lineX2, lineY));
                    }
                }
            }

            // Circle shadow
            g2.setColor(new Color(0, 0, 0, 20));
            g2.fillOval(x + shadowOffset, yCenter - stepRadius / 2 + shadowOffset, stepRadius, stepRadius);

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
            g2.setFont(new Font("SansSerif", Font.BOLD, stepNumberFontSize));
            String label = isCompleted ? "✓" : String.valueOf(i + 1);
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(label);
            int textHeight = fm.getAscent();
            g2.drawString(label, x + (stepRadius - textWidth) / 2, yCenter + textHeight / 4);

            // Text under step
            if (isCompleted || isCurrent) {
                g2.setColor(completedTextColor);
                g2.setFont(new Font("SansSerif", Font.BOLD, stepTextFontSize));
            } else {
                g2.setColor(textColor);
                g2.setFont(new Font("SansSerif", Font.PLAIN, stepTextFontSize));
            }

            int txtWidth = g2.getFontMetrics().stringWidth(steps[i]);
            int xText = x + (stepRadius - txtWidth) / 2;

            // If text is wider than the circle, center it under the circle
            if (txtWidth > stepRadius) {
                xText = x + (stepRadius - txtWidth) / 2;
            }

            g2.drawString(steps[i], xText, yCenter + verticalTextOffset);

            // Draw a "glow" effect for current step
            if (isCurrent) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f));
                g2.setColor(currentColor);
                g2.fillOval(x - glowSize, yCenter - stepRadius / 2 - glowSize, 
                           stepRadius + (2 * glowSize), stepRadius + (2 * glowSize));
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            }
        }
    }

    // Size Management Methods
    
    /**
     * Apply a size preset to the component
     * @param preset The size preset to apply
     */
    public void applySizePreset(SizePreset preset) {
        this.stepRadius = preset.stepRadius;
        this.yCenter = preset.yCenter;
        this.xPadding = preset.xPadding;
        this.verticalTextOffset = preset.verticalTextOffset;
        this.stepNumberFontSize = preset.stepNumberFontSize;
        this.stepTextFontSize = preset.stepTextFontSize;
        
        // Update other dimensions proportionally
        this.lineThickness = Math.max(2, preset.stepRadius / 8);
        this.glowSize = Math.max(2, preset.stepRadius / 8);
        this.shadowOffset = Math.max(1, preset.stepRadius / 20);
        
        setPreferredSize(new Dimension(calculatePreferredWidth(), preset.preferredHeight));
        repaint();
    }

    /**
     * Set custom size with all dimensions
     */
    public void setCustomSize(int stepRadius, int yCenter, int xPadding, int verticalTextOffset,
                             int stepNumberFontSize, int stepTextFontSize, int preferredHeight) {
        this.stepRadius = stepRadius;
        this.yCenter = yCenter;
        this.xPadding = xPadding;
        this.verticalTextOffset = verticalTextOffset;
        this.stepNumberFontSize = stepNumberFontSize;
        this.stepTextFontSize = stepTextFontSize;
        
        // Update other dimensions proportionally
        this.lineThickness = Math.max(2, stepRadius / 8);
        this.glowSize = Math.max(2, stepRadius / 8);
        this.shadowOffset = Math.max(1, stepRadius / 20);
        
        setPreferredSize(new Dimension(calculatePreferredWidth(), preferredHeight));
        repaint();
    }

    public void setCustomSize(int[] dimensions) {
        if (dimensions.length != 7) {
            throw new IllegalArgumentException("Expected 7 dimensions: stepRadius, yCenter, xPadding, verticalTextOffset, stepNumberFontSize, stepTextFontSize, preferredHeight");
        }
        setCustomSize(dimensions[0], dimensions[1], dimensions[2], dimensions[3], 
                      dimensions[4], dimensions[5], dimensions[6]);
    }

    /**
     * Scale all dimensions by a factor
     */
    public void scaleSize(double scaleFactor) {
        this.stepRadius = (int) (stepRadius * scaleFactor);
        this.yCenter = (int) (yCenter * scaleFactor);
        this.xPadding = (int) (xPadding * scaleFactor);
        this.verticalTextOffset = (int) (verticalTextOffset * scaleFactor);
        this.stepNumberFontSize = Math.max(8, (int) (stepNumberFontSize * scaleFactor));
        this.stepTextFontSize = Math.max(8, (int) (stepTextFontSize * scaleFactor));
        this.lineThickness = Math.max(1, (int) (lineThickness * scaleFactor));
        this.glowSize = Math.max(1, (int) (glowSize * scaleFactor));
        this.shadowOffset = Math.max(1, (int) (shadowOffset * scaleFactor));
        
        Dimension currentSize = getPreferredSize();
        setPreferredSize(new Dimension(
            (int) (currentSize.width * scaleFactor),
            (int) (currentSize.height * scaleFactor)
        ));
        repaint();
    }

    /**
     * Calculate preferred width based on current settings
     */
    private int calculatePreferredWidth() {
        if (steps.length == 0) return 200;
        
        // Calculate space needed for all steps
        int totalStepsWidth = steps.length * stepRadius;
        int minGapWidth = (steps.length - 1) * stepRadius; // Minimum gap between steps
        return totalStepsWidth + minGapWidth + (2 * xPadding);
    }

    /**
     * Auto-fit the component to its container
     */
    public void autoFitToContainer() {
        Container parent = getParent();
        if (parent != null) {
            Dimension parentSize = parent.getSize();
            if (parentSize.width > 0 && parentSize.height > 0) {
                // Calculate scale factor based on width
                double widthScale = (double) parentSize.width / calculatePreferredWidth();
                double heightScale = (double) parentSize.height / getPreferredSize().height;
                double scale = Math.min(widthScale, heightScale) * 0.9; // Leave some margin
                
                if (scale != 1.0) {
                    scaleSize(scale);
                }
            }
        }
    }

    /**
     * Set compact size (smaller than SMALL preset)
     */
    public void setCompactSize() {
        setCustomSize(14, 18, 20, 20, 10, 9, 40);
    }

    /**
     * Set mini size (very small)
     */
    public void setMiniSize() {
        setCustomSize(10, 15, 15, 18, 8, 8, 35);
    }

    

    // Getters for current dimensions
    public int getStepRadius() { return stepRadius; }
    public int getYCenter() { return yCenter; }
    public int getXPadding() { return xPadding; }
    public int getVerticalTextOffset() { return verticalTextOffset; }
    public int getStepNumberFontSize() { return stepNumberFontSize; }
    public int getStepTextFontSize() { return stepTextFontSize; }
    public int getLineThickness() { return lineThickness; }

    public void setStepRadius(int radius) {
        if (radius >= 8 && radius <= 50) {
            this.stepRadius = radius;
            updateDependentDimensions();
            setPreferredSize(new Dimension(calculatePreferredWidth(), getPreferredSize().height));
            repaint();
        }
    }
    
    public void setYCenter(int yCenter) {
        if (yCenter >= 10 && yCenter <= 100) {
            this.yCenter = yCenter;
            repaint();
        }
    }

    public void setXPadding(int xPadding) {
        if (xPadding >= 10 && xPadding <= 100) {
            this.xPadding = xPadding;
            setPreferredSize(new Dimension(calculatePreferredWidth(), getPreferredSize().height));
            repaint();
        }
    }

    public void setVerticalTextOffset(int offset) {
        if (offset >= 10 && offset <= 100) {
            this.verticalTextOffset = offset;
            repaint();
        }
    }

    public void setStepNumberFontSize(int size) {
        if (size >= 8 && size <= 24) {
            this.stepNumberFontSize = size;
            repaint();
        }
    }

    public void setStepTextFontSize(int size) {
        if (size >= 8 && size <= 20) {
            this.stepTextFontSize = size;
            repaint();
        }
    }

    public void setLineThickness(int thickness) {
        if (thickness >= 1 && thickness <= 10) {
            this.lineThickness = thickness;
            repaint();
        }
    }

    public void setGlowSize(int size) {
        if (size >= 1 && size <= 10) {
            this.glowSize = size;
            repaint();
        }
    }

    public void setShadowOffset(int offset) {
        if (offset >= 0 && offset <= 10) {
            this.shadowOffset = offset;
            repaint();
        }
    }
    

    private void updateDependentDimensions() {
        this.lineThickness = Math.max(2, stepRadius / 8);
        this.glowSize = Math.max(2, stepRadius / 8);
        this.shadowOffset = Math.max(1, stepRadius / 20);
    }

    // Original methods preserved...
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

    public void setSteps(String[] newSteps) {
        if (newSteps != null && newSteps.length > 0) {
            this.steps = newSteps;
            if (this.currentStep > newSteps.length) {
                this.currentStep = newSteps.length;
            }
            setPreferredSize(new Dimension(calculatePreferredWidth(), getPreferredSize().height));
            repaint();
        }
    }

    public void addStep(String step) {
        String[] newSteps = Arrays.copyOf(this.steps, this.steps.length + 1);
        newSteps[newSteps.length - 1] = step;
        this.steps = newSteps;
        setPreferredSize(new Dimension(calculatePreferredWidth(), getPreferredSize().height));
        repaint();
    }

    public void insertStep(String step, int position) {
        if (position < 0 || position > this.steps.length) {
            return;
        }

        String[] newSteps = new String[this.steps.length + 1];
        System.arraycopy(this.steps, 0, newSteps, 0, position);
        newSteps[position] = step;
        System.arraycopy(this.steps, position, newSteps, position + 1, this.steps.length - position);

        this.steps = newSteps;
        if (this.currentStep > position) {
            this.currentStep++;
        }
        setPreferredSize(new Dimension(calculatePreferredWidth(), getPreferredSize().height));
        repaint();
    }

    public void removeStep(int position) {
        if (position < 0 || position >= this.steps.length || this.steps.length <= 1) {
            return;
        }

        String[] newSteps = new String[this.steps.length - 1];
        System.arraycopy(this.steps, 0, newSteps, 0, position);
        System.arraycopy(this.steps, position + 1, newSteps, position, this.steps.length - position - 1);

        this.steps = newSteps;
        if (this.currentStep > position) {
            this.currentStep--;
        }
        if (this.currentStep > this.steps.length) {
            this.currentStep = this.steps.length;
        }
        setPreferredSize(new Dimension(calculatePreferredWidth(), getPreferredSize().height));
        repaint();
    }

    public void setAnimationProperties(int speed, float step) {
        this.animationSpeed = speed;
        this.animationStep = step;
        if (animationTimer != null) {
            animationTimer.setDelay(speed);
        }
    }

    public boolean nextStep() {
        if (currentStep < steps.length) {
            setCurrentStep(currentStep + 1);
            return true;
        }
        return false;
    }

    public boolean previousStep() {
        if (currentStep > 1) {
            setCurrentStep(currentStep - 1);
            return true;
        }
        return false;
    }

    public void reset() {
        setCurrentStep(1);
    }

    public void complete() {
        setCurrentStep(steps.length);
    }

    public int getCurrentStep() {
        return currentStep;
    }

    public String getCurrentStepName() {
        return steps[currentStep - 1];
    }

    public int getStepCount() {
        return steps.length;
    }

    public boolean isFirstStep() {
        return currentStep == 1;
    }

    public boolean isLastStep() {
        return currentStep == steps.length;
    }

    public float getAnimationProgress() {
        return animationProgress;
    }

    public boolean isAnimating() {
        return animationTimer.isRunning();
    }

    public void stopAnimation() {
        if (animationTimer.isRunning()) {
            animationTimer.stop();
            animationProgress = 1.0f;
            repaint();
        }
    }

    public void setColorTheme(Color completed, Color current, Color pending, Color text, Color completedText) {
        this.completedColor = completed;
        this.currentColor = current;
        this.pendingColor = pending;
        this.textColor = text;
        this.completedTextColor = completedText;
        repaint();
    }

    public void useBlueTheme() {
        setColorTheme(
                new Color(37, 99, 235),
                new Color(59, 130, 246),
                new Color(203, 213, 225),
                new Color(70, 70, 70),
                new Color(37, 99, 235)
        );
    }

    public void useGreenTheme() {
        setColorTheme(
                new Color(22, 163, 74),
                new Color(34, 197, 94),
                new Color(203, 213, 225),
                new Color(70, 70, 70),
                new Color(22, 163, 74)
        );
    }

    public void useDarkTheme() {
        setBackground(new Color(30, 30, 30));
        setColorTheme(
                new Color(59, 130, 246),
                new Color(96, 165, 250),
                new Color(100, 100, 100),
                new Color(220, 220, 220),
                new Color(96, 165, 250)
        );
    }

    public interface StepChangeListener {
        void onStepChanged(int oldStep, int newStep);
    }

    public void addStepChangeListener(StepChangeListener listener) {
        stepChangeListeners.add(listener);
    }

    public void removeStepChangeListener(StepChangeListener listener) {
        stepChangeListeners.remove(listener);
    }

    public static ProgressStepsPanel createPanel(String[] stepNames, int initialStep, int width, int height) {
        ProgressStepsPanel panel = new ProgressStepsPanel(stepNames, initialStep);
        panel.setPreferredSize(new Dimension(width, height));
        return panel;
    }

    public void setStepHtml(int stepIndex, String htmlText) {
        if (stepIndex >= 0 && stepIndex < steps.length) {
            steps[stepIndex] = htmlText;
            repaint();
        }
    }

    public void setStepsHtml(String[] htmlSteps) {
        if (htmlSteps != null && htmlSteps.length > 0) {
            this.steps = htmlSteps;
            repaint();
        }
    }

    public static String[] getUnicodeSupportingFonts() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        return ge.getAvailableFontFamilyNames();
    }

    public void setUnicodeFont(String fontFamily) {
        try {
            Font stepNumberFont = new Font(fontFamily, Font.BOLD, stepNumberFontSize);
            Font stepTextFont = new Font(fontFamily, Font.PLAIN, stepTextFontSize);
            putClientProperty("stepNumberFont", stepNumberFont);
            putClientProperty("stepTextFont", stepTextFont);
            repaint();
        } catch (Exception e) {
            System.err.println("Font not supported: " + e.getMessage());
        }
    }

}