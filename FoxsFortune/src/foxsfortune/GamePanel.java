package foxsfortune;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 * Game panel for rendering and managing game logic.
 *
 * @author reesesanders
 */
public class GamePanel extends JPanel {

    public GamePanel() {
        setBackground(Color.BLACK);
        setFocusable(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Render game elements here
        
    }

    @Override
    public void update(Graphics g) {
        paintComponent(g);
    }
}
