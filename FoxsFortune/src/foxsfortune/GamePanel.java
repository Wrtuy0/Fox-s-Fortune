package foxsfortune;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JPanel;

/**
 * Game panel for rendering and managing game logic.
 *
 * @author Reese Sanders, Logan Saywich and Aws Tariq
 */
public class GamePanel extends JPanel implements KeyListener {

    private Player player; // player attrubute and object that gets moved and drawn on the screen
    private Set<Integer> keysPressed; // attrubuite to hold what was pressed set that stores all the keys currently being held down
    private final int PLAYER_SPEED = 5; // player base speed attrubute
    private final int PLAYER_SIZE = 30; // player pixel form size attrubute
    private final int COLLECTIBLE_SIZE = 20; // collectible size in pixels
    private final double GRAVITY = 0.6; // a gravity attrubute to keep the player from jumping to space forever
    private final double JUMP_FORCE = -15.0; // player base jump power  attrubute
    private final int GROUND_LEVEL = 850; // Near bottom of 900px window and the y position where the player should stop falling
    private final List<Platform> platforms = new ArrayList<>(); // platform list for collision and rendering
    private final List<Collectible> collectibles = new ArrayList<>(); // collectible list for collection and rendering
    private int collectedCount = 0; // how many collectibles the player has picked up
    private boolean doubleJumpEnabled = false; // whether player can perform a second jump in the air
    private boolean hasDoubleJumped = false; // whether the player has already used the second jump
    private boolean jumpKeyPreviouslyPressed = false; // used to detect jump key presses instead of holds
    private double xVelocity = 0; // horizontal movement speed
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

        // Add platforms. Place them by calling addPlatform(x, y, width, height).
        addPlatform(200, 750, 200, 20);
        addPlatform(450, 620, 150, 20);
        addPlatform(150, 620, 100, 20);

        addCollectible(260, 700, 1);
        addCollectible(520, 560, 2);
        addCollectible(130, 580, 3);
        addDoubleJumpItem(700, 700);

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

        // Horizontal movement velocity
        boolean leftPressed = keysPressed.contains(KeyEvent.VK_A) || keysPressed.contains(KeyEvent.VK_LEFT);
        boolean rightPressed = keysPressed.contains(KeyEvent.VK_D) || keysPressed.contains(KeyEvent.VK_RIGHT);
        if (leftPressed && !rightPressed) {
            xVelocity = -PLAYER_SPEED;
        } else if (rightPressed && !leftPressed) {
            xVelocity = PLAYER_SPEED;
        } else {
            xVelocity = 0;
        }

        int previousTop = player.getYPos();
        int previousBottom = previousTop + PLAYER_SIZE;
        int previousLeft = player.getXPos();
        int previousRight = previousLeft + PLAYER_SIZE;

        // Apply horizontal movement first
        newX += (int) xVelocity;
        for (Platform platform : platforms) {
            boolean verticalOverlap = previousBottom > platform.y && previousTop < platform.y + platform.height;
            if (!verticalOverlap) {
                continue;
            }

            if (xVelocity > 0 && previousRight <= platform.x && newX + PLAYER_SIZE > platform.x) {
                newX = platform.x - PLAYER_SIZE;
                xVelocity = 0;
            } else if (xVelocity < 0 && previousLeft >= platform.x + platform.width && newX < platform.x + platform.width) {
                newX = platform.x + platform.width;
                xVelocity = 0;
            }
        }

        // Handle jumping with W/up key press
        boolean jumpPressed = keysPressed.contains(KeyEvent.VK_UP) || keysPressed.contains(KeyEvent.VK_W);
        if (jumpPressed && !jumpKeyPreviouslyPressed) {
            if (canJump) {
                yVelocity = JUMP_FORCE;
                canJump = false;
                hasDoubleJumped = false;
            } else if (doubleJumpEnabled && !hasDoubleJumped) {
                yVelocity = JUMP_FORCE;
                hasDoubleJumped = true;
            }
        }
        jumpKeyPreviouslyPressed = jumpPressed;

        // Apply gravity
        yVelocity += GRAVITY;
        // gravity pulls the player down by increasing y velocity
        newY += (int) yVelocity;
        int newTop = newY;
        int newLeft = newX;
        int newRight = newX + PLAYER_SIZE;
        int newBottom = newY + PLAYER_SIZE;

        // Platform collision
        boolean landedOnPlatform = false;
        for (Platform platform : platforms) {
            boolean horizontalOverlap = newRight > platform.x && newLeft < platform.x + platform.width;
            if (!horizontalOverlap) {
                continue;
            }

            if (yVelocity > 0 && previousBottom <= platform.y && newBottom > platform.y) {
                newY = platform.y - PLAYER_SIZE;
                yVelocity = 0;
                canJump = true;
                hasDoubleJumped = false;
                landedOnPlatform = true;
            } else if (yVelocity < 0 && previousTop >= platform.y + platform.height && newTop < platform.y + platform.height) {
                newY = platform.y + platform.height;
                yVelocity = 0;
            }

            newTop = newY;
            newBottom = newY + PLAYER_SIZE;
        }

        // Collectible pickup
        for (int i = collectibles.size() - 1; i >= 0; i--) {
            Collectible collectible = collectibles.get(i);
            int collX = collectible.getXPos();
            int collY = collectible.getYPos();
            boolean overlapX = newX < collX + COLLECTIBLE_SIZE && newX + PLAYER_SIZE > collX;
            boolean overlapY = newY < collY + COLLECTIBLE_SIZE && newY + PLAYER_SIZE > collY;
            if (overlapX && overlapY) {
                if ("Double Jump Item".equals(collectible.getName())) {
                    doubleJumpEnabled = true;
                }
                collectedCount++;
                collectibles.remove(i);
            }
        }

        // Ground collision
        if (!landedOnPlatform && newY >= GROUND_LEVEL) {
            // if the player reaches the ground level, stop falling
            newY = GROUND_LEVEL;
            // locks the player on the ground
            yVelocity = 0;
            // removes downward speed
            canJump = true;
            hasDoubleJumped = false;
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

        // Render platforms
        g2d.setColor(Color.GRAY);
        for (Platform platform : platforms) {
            g2d.fillRect(platform.x, platform.y, platform.width, platform.height);
        }
        // draws simple platforms for the player to land on

        // Render collectibles
        for (Collectible collectible : collectibles) {
            if ("Double Jump Item".equals(collectible.getName())) {
                g2d.setColor(Color.CYAN);
            } else {
                g2d.setColor(Color.YELLOW);
            }
            g2d.fillOval(collectible.getXPos(), collectible.getYPos(), COLLECTIBLE_SIZE, COLLECTIBLE_SIZE);
        }
        // draws collectibles the player can pick up

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

        // Draw score
        g2d.setColor(Color.WHITE);
        g2d.drawString("Collectibles: " + collectedCount, 10, 20);

    }

    private void addPlatform(int x, int y, int width, int height) {
        platforms.add(new Platform(x, y, width, height));
    }

    private void addCollectible(int x, int y, int collectibleNum) {
        Collectible collectible = new Collectible(collectibleNum);
        collectible.setXPos(x);
        collectible.setYPos(y);
        collectible.setName("Collectible " + collectibleNum);
        collectibles.add(collectible);
    }

    private void addDoubleJumpItem(int x, int y) {
        Collectible collectible = new Collectible(0);
        collectible.setXPos(x);
        collectible.setYPos(y);
        collectible.setName("Double Jump Item");
        collectibles.add(collectible);
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

    private static class Platform {
        final int x;
        final int y;
        final int width;
        final int height;

        Platform(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }
}
