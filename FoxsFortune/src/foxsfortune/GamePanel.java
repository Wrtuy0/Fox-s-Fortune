package foxsfortune;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

/**
 * Game panel for rendering and managing game logic.
 *
 * @author reesesanders
 */
public class GamePanel extends JPanel implements KeyListener {

    private Player player;
    private Set<Integer> keysPressed;
    private final int PLAYER_SPEED = 5;
    private final int PLAYER_SIZE = 30;
    private Thread gameThread;
    private boolean running;

    public GamePanel() {
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        
        // Initialize player in center of screen
        player = new Player();
        player.setName("Fox");
        player.setXPos(getWidth() / 2);
        player.setYPos(getHeight() / 2);
        
        keysPressed = new HashSet<>();
        running = true;
        
        // Start game loop
        startGameLoop();
    }

    private void startGameLoop() {
        gameThread = new Thread(() -> {
            long lastUpdate = System.currentTimeMillis();
            final long updateInterval = 16; // ~60 FPS
            
            while (running) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastUpdate >= updateInterval) {
                    update();
                    repaint();
                    lastUpdate = currentTime;
                }
                
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        gameThread.setDaemon(true);
        gameThread.start();
    }

    private void update() {
        // Handle player movement based on keys pressed
        int newX = player.getXPos();
        int newY = player.getYPos();
        
        // WASD or Arrow keys for movement
        if (keysPressed.contains(KeyEvent.VK_W) || keysPressed.contains(KeyEvent.VK_UP)) {
            newY -= PLAYER_SPEED;
        }
        if (keysPressed.contains(KeyEvent.VK_S) || keysPressed.contains(KeyEvent.VK_DOWN)) {
            newY += PLAYER_SPEED;
        }
        if (keysPressed.contains(KeyEvent.VK_A) || keysPressed.contains(KeyEvent.VK_LEFT)) {
            newX -= PLAYER_SPEED;
        }
        if (keysPressed.contains(KeyEvent.VK_D) || keysPressed.contains(KeyEvent.VK_RIGHT)) {
            newX += PLAYER_SPEED;
        }
        
        // Bounds checking
        newX = Math.max(0, Math.min(newX, getWidth() - PLAYER_SIZE));
        newY = Math.max(0, Math.min(newY, getHeight() - PLAYER_SIZE));
        
        player.setXPos(newX);
        player.setYPos(newY);
        
        // Update moving state
        player.setMoving(!keysPressed.isEmpty());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Render player
        if (player != null) {
            g2d.setColor(Color.ORANGE);
            g2d.fillRect(player.getXPos(), player.getYPos(), PLAYER_SIZE, PLAYER_SIZE);
            
            // Draw player name
            g2d.setColor(Color.WHITE);
            g2d.drawString(player.getName(), player.getXPos(), player.getYPos() - 5);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keysPressed.add(e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keysPressed.remove(e.getKeyCode());
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not needed for movement
    }
}
