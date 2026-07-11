package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.function.Function;

/**
 * Renders a table cell as a rounded, colored "badge" - e.g. a red pill
 * for High priority, a green pill for Completed status - instead of
 * plain text, so priority/status are readable at a glance.
 */
public class BadgeCellRenderer extends DefaultTableCellRenderer {

    private final Function<String, Color> colorFn;

    public BadgeCellRenderer(Function<String, Color> colorFn) {
        this.colorFn = colorFn;
        setHorizontalAlignment(SwingConstants.CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                     boolean hasFocus, int row, int column) {
        JLabel label = (JLabel) super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);

        String text = value == null ? "" : value.toString();
        Color badgeColor = colorFn.apply(text);

        label.setText(text);
        label.setOpaque(false);
        label.setForeground(Color.WHITE);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        label.putClientProperty("badgeColor", badgeColor);

        return new BadgeLabel(text, badgeColor, isSelected, table.getSelectionBackground(), table.getBackground());
    }

    /** A JLabel that paints its own rounded, colored background before the text. */
    private static class BadgeLabel extends JLabel {
        private final Color badgeColor;
        private final boolean selected;
        private final Color selectionBg;
        private final Color rowBg;

        BadgeLabel(String text, Color badgeColor, boolean selected, Color selectionBg, Color rowBg) {
            super(text);
            this.badgeColor = badgeColor;
            this.selected = selected;
            this.selectionBg = selectionBg;
            this.rowBg = rowBg;
            setHorizontalAlignment(SwingConstants.CENTER);
            setForeground(Color.WHITE);
            setFont(getFont().deriveFont(Font.BOLD, 11f));
            setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Fill the full cell with the row's background/selection color first.
            g2.setColor(selected ? selectionBg : rowBg);
            g2.fillRect(0, 0, getWidth(), getHeight());

            // Then draw a centered rounded pill in the badge color.
            FontMetrics fm = g2.getFontMetrics(getFont());
            int textWidth = fm.stringWidth(getText());
            int pillWidth = Math.min(getWidth() - 6, textWidth + 24);
            int pillHeight = Math.min(getHeight() - 6, fm.getHeight() + 6);
            int x = (getWidth() - pillWidth) / 2;
            int y = (getHeight() - pillHeight) / 2;

            g2.setColor(badgeColor);
            g2.fillRoundRect(x, y, pillWidth, pillHeight, pillHeight, pillHeight);
            g2.dispose();

            super.paintComponent(g);
        }
    }
}
