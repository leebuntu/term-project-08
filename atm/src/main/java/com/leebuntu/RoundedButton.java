package com.leebuntu;

import javax.swing.*;
import java.awt.*;

public class RoundedButton extends JButton {
    private Color backgroundColor = Color.LIGHT_GRAY;
    private Color textColor = Color.BLACK;


    public RoundedButton(String text) { super(text); decorate(); }
    protected void decorate() { setBorderPainted(false); setOpaque(false); }


    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
        repaint();
    }


    public void setTextColor(Color color) {
        this.textColor = color;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        int width = getWidth();
        int height = getHeight();
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (getModel().isArmed()) {
            graphics.setColor(backgroundColor.darker());
        } else if (getModel().isRollover()) {
            graphics.setColor(backgroundColor.brighter());
        } else {
            graphics.setColor(backgroundColor);
        }

        graphics.fillRoundRect(0, 0, width, height, 10, 10);

        graphics.setColor(textColor);
        graphics.setFont(getFont());
        FontMetrics fontMetrics = graphics.getFontMetrics();
        Rectangle stringBounds = fontMetrics.getStringBounds(this.getText(), graphics).getBounds();
        int textX = (width - stringBounds.width) / 2;
        int textY = (height - stringBounds.height) / 2 + fontMetrics.getAscent();
        graphics.drawString(getText(), textX, textY);

        graphics.dispose();
        super.paintComponent(g);
    }
}
