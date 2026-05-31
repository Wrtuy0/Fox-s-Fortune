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

    private Player player; // player attrubute and object that gets moved and drawn on the screen
    private Set<Integer> keysPressed; // attrubuite to hold what was pressed set that stores all the keys currently being held down
    private final int PLAYER_SPEED = 5; // player base speed attrubute
    private final int PLAYER_SIZE = 30; // player pixel form size attrubute
    private final double GRAVITY = 0.6; // a gravity attrubute to keep the player from jumping to space forever
    private final double JUMP_FORCE = -15.0; // player base jump power  attrubute
    private final int GROUND_LEVEL = 850; // Near bottom of 900px window and the y position where the player should stop falling
    private Thread gameThread; // a thread that keeps the game loop running
    private boolean running; // a boolean that controls if the game loop should keep going
    private boolean canJump = true; // a player boolean to check if the player is on solid ground before alloing jumping

    public GamePanel() {//constructor 
        setBackground(Color.BLACK);
        // makes the background black
        setFocusable(true);
        // lets the panel receive keyboard input
        addKeyListener(this);
        // adds this class as the key listener

        // Initialize player in center of screen
        player = new Player();
        // creates the player object
        player.setName("Fox");
        // gives the player a name
        player.setXPos(getWidth() / 2);
        // starts the player around the middle of the panel horizontally
        player.setYPos(getHeight() / 2);
        // starts the player around the middle of the panel vertically
        keysPressed = new HashSet<>();
         // creates the set that stores pressed keys
        running = true;
        // makes the game loop allowed to run

        // Start game loop
        startGameLoop();
        // calls the method that starts updating the game
    }

    private void startGameLoop() {
        // this method starts the constant game loop
        gameThread = new Thread(() -> {
            // creates a new thread so the game can keep updating
            long lastUpdate = System.currentTimeMillis();
            // stores the last time the game updated
            final long updateInterval = 16; // ~60 FPS
            // around 16 milliseconds gives 60 updates per second aka frames per second

            while (running) {
                // keeps looping while the game is running
                long currentTime = System.currentTimeMillis();
                // gets the current time
                if (currentTime - lastUpdate >= updateInterval) {
                    // checks if enough time has passed for another update
                    update();
                    // updates the player movement and physics
                    repaint();
                    // redraws the panel
                    lastUpdate = currentTime;
                    // updates the last update time
                }

                try {
                    Thread.sleep(1);
                    // tiny sleep so the loop does not use too much CPU
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    // prints the error if the thread gets interrupted
                }
            }
        });
        gameThread.setDaemon(true);
        // makes the thread close when the program closes
        gameThread.start();
        // starts the thread
    }

    private void update() {
        // this method updates the player position every frame
        // Handle player movement based on keys pressed
        int newX = player.getXPos();
        // stores the player's current x position
        int newY = player.getYPos();
        // stores the player's current y position
        double yVelocity = player.getYVelocity();
        // stores the player's current vertical speed

        // Left/Right movement only
        if (keysPressed.contains(KeyEvent.VK_A) || keysPressed.contains(KeyEvent.VK_LEFT)) {
            // if A or left arrow is pressed the player moves left
            newX -= PLAYER_SPEED;
        }
        if (keysPressed.contains(KeyEvent.VK_D) || keysPressed.contains(KeyEvent.VK_RIGHT)) {
            // if D or right arrow is pressed the player moves right
            newX += PLAYER_SPEED;
        }

        // Handle jumping with W key
        if (keysPressed.contains(KeyEvent.VK_UP) || keysPressed.contains(KeyEvent.VK_W) && canJump) {
            // if up or W is pressed and jumping is allowed the player jumps
            yVelocity = JUMP_FORCE;
            // gives the player upward movement
            canJump = false;
            // stops the player from jumping again in the air
        }

        // Apply gravity
        yVelocity += GRAVITY;
        // gravity pulls the player down by increasing y velocity
        // Apply vertical velocity
        newY += (int) yVelocity;
        // adds the y velocity to the y position
        // Ground collision
        if (newY >= GROUND_LEVEL) {
            // if the player reaches the ground level, stop falling
            newY = GROUND_LEVEL;
            // locks the player on the ground
            yVelocity = 0;
            // removes downward speed
            canJump = true;
            // allows jumping again because the player is on ground
        }

        // Bounds checking (horizontal)
        newX = Math.max(0, Math.min(newX, getWidth() - PLAYER_SIZE));
        // keeps the player from leaving the left or right side of the screen

        // Prevent going above screen
        newY = Math.max(0, newY);
        // keeps the player from going above the top of the screen

        player.setXPos(newX);
        // saves the new x position into the player object
        player.setYPos(newY);
        // saves the new y position into the player object
        player.setYVelocity(yVelocity);
        // saves the updated y velocity

        // Update moving state
        player.setMoving(!keysPressed.isEmpty());
        // if any key is pressed, the player is counted as moving
    }

    @Override
    protected void paintComponent(Graphics g) {
        // this method draws everything on the panel
        super.paintComponent(g);
        // clears and redraws the panel correctly
        Graphics2D g2d = (Graphics2D) g;
        // changes Graphics into Graphics2D so drawing is easier

        // Render player
        if (player != null) {
            // only draw the player if the player exists
            g2d.setColor(Color.ORANGE);
            // sets the player color to orange
            g2d.fillRect(player.getXPos(), player.getYPos(), PLAYER_SIZE, PLAYER_SIZE);
            // draws the player as a square/rectangle

            // Draw player name
            g2d.setColor(Color.WHITE);
            // sets text color to white
            g2d.drawString(player.getName(), player.getXPos(), player.getYPos() - 5);
            // draws the player's name above the square
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // runs when a key is pressed
        keysPressed.add(e.getKeyCode());
        // adds the key code into the set of pressed keys
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // runs when a key is released
        keysPressed.remove(e.getKeyCode());
        // removes the key code from the set of pressed keys
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not needed for movement
        // this is empty because movement uses keyPressed and keyReleased instead
    }
}
