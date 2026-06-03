package foxsfortune;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.imageio.ImageIO;
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
    private final int PLAYER_SIZE = 30; // default player size if sprite is unavailable
    private int playerWidth = PLAYER_SIZE; // actual player width based on sprite
    private int playerHeight = PLAYER_SIZE; // actual player height based on sprite
    private final int COLLECTIBLE_SIZE = 20; // collectible size in pixels
    private final double GRAVITY = 0.6; // a gravity attrubute to keep the player from jumping to space forever
    private final double JUMP_FORCE = -15.0; // player base jump power  attrubute
    private final int GROUND_LEVEL = 850; // Near bottom of 900px window and the y position where the player should stop falling
    private int spawnX; // current player spawn x position
    private int spawnY; // current player spawn y position
    private int checkpointX; // current checkpoint x position
    private int checkpointY; // current checkpoint y position
    private boolean checkpointActive = false; // whether a checkpoint has been reached
    private final List<Platform> platforms = new ArrayList<>(); // platform list for collision and rendering
    private final List<Checkpoint> checkpoints = new ArrayList<>(); // checkpoint list for respawn points
    private final List<Collectible> collectibles = new ArrayList<>(); // collectible list for collection and rendering
    private final List<EnemyEntity> enemies = new ArrayList<>(); // basic enemies that patrol
    private final int ENEMY_SIZE = 30; // enemy pixel size
    private final int MAX_HEALTH = 5; // maximum player health
    private int collectedCount = 0; // how many collectibles the player has picked up
    private int playerHealth = MAX_HEALTH; // player health for simple enemy interaction
    private int hurtCooldown = 0; // prevents repeated damage in the same contact
    private int respawnProtection = 0; // temporary invulnerability after respawn
    private boolean isAlive = true; // whether the player is alive
    private boolean doubleJumpEnabled = false; // whether player can perform a second jump in the air
    private boolean hasDoubleJumped = false; // whether the player has already used the second jump
    private boolean jumpKeyPreviouslyPressed = false; // used to detect jump key presses instead of holds
    private double xVelocity = 0; // horizontal movement speed
    private boolean facingRight = true; // which way the player faces
    // Attack state
    private boolean attackActive = false;
    private int attackTimer = 0;
    private final int ATTACK_DURATION = 6; // frames the attack is active
    private int attackCooldown = 0;
    private final int ATTACK_COOLDOWN = 20; // frames between attacks
    private final int ATTACK_RANGE = 40; // pixels
    private final int ATTACK_DAMAGE = 1;
    private Thread gameThread; // a thread that keeps the game loop running
    private boolean running; // a boolean that controls if the game loop should keep going
    private boolean canJump = true; // a player boolean to check if the player is on solid ground before alloing jumping
    //animations
    private BufferedImage image;

    public GamePanel() {//constructor 
        setBackground(Color.BLACK);
        // makes the background black
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        // lets the panel receive keyboard input and avoids focus traversal swallowing keys
        addKeyListener(this);
        // adds this class as the key listener
        requestFocusInWindow();
        // request focus so key events are delivered

        // Initialize player object
        player = new Player();
        // creates the player object
        player.setName("Fox");
        // gives the player a name
        keysPressed = new HashSet<>();
         // creates the set that stores pressed keys
        running = true;
        player.setXPos(100);
        player.setYPos(100);
        // makes the game loop allowed to run
        // import the player's model from classpath resources
        URL foxURL = FoxsFortune.class.getResource("/foxsfortune/BiggerFoxModel.png");
        if (foxURL == null) {
            // Fallback for running directly from the project directory during development.
            try {
                java.io.File fallbackFile = new java.io.File("FoxsFortune/src/foxsfortune/BiggerFoxModel.png");
                if (fallbackFile.exists()) {
                    foxURL = fallbackFile.toURI().toURL();
                }
            } catch (java.net.MalformedURLException e) {
                System.err.println("Error resolving fallback player model path: " + e);
            }
        }

        if (foxURL != null) {
            try {
                image = ImageIO.read(foxURL);
                if (image != null) {
                    playerWidth = image.getWidth();
                    playerHeight = image.getHeight();
                }
            } catch (IOException e) {
                System.err.println("Error reading player model image: " + e);
            }
        } else {
            System.err.println("Warning: BiggerFoxModel.png not found in classpath resources.");
        }

        // Add platforms. Place them by calling addPlatform(x, y, width, height).
        addPlatform(200, 750, 200, 20);
        addPlatform(450, 620, 150, 20);
        addPlatform(150, 620, 100, 20);

        // Start position on the first platform by default
        if (!platforms.isEmpty()) {
            Platform startPlatform = platforms.get(0);
            setPlayerSpawn(startPlatform.x + 10, startPlatform.y - playerHeight);
        } else {
            setPlayerSpawn(getWidth() / 2, getHeight() / 2);
        }

        addCollectible(260, 700, 1);
        addCollectible(520, 560, 2);
        addCollectible(130, 580, 3);
        addHealthPickup(380, 700);
        addDoubleJumpItem(700, 700);
        addEnemy(220, 720, ENEMY_SIZE, ENEMY_SIZE, 200, 370, 2);
        addCheckpoint(520, 600, 20, 20);

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
        // update facing based on horizontal input
        if (xVelocity > 0) facingRight = true;
        else if (xVelocity < 0) facingRight = false;

        int previousTop = player.getYPos();
        int previousBottom = previousTop + playerHeight;
        int previousLeft = player.getXPos();
        int previousRight = previousLeft + playerWidth;

        // Apply horizontal movement first
        newX += (int) xVelocity;
        for (Platform platform : platforms) {
            boolean verticalOverlap = previousBottom > platform.y && previousTop < platform.y + platform.height;
            if (!verticalOverlap) {
                continue;
            }

            if (xVelocity > 0 && previousRight <= platform.x && newX + playerWidth > platform.x) {
                newX = platform.x - playerWidth;
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

        if (!isAlive) {
            return;
        }


        // Apply gravity
        yVelocity += GRAVITY;
        // gravity pulls the player down by increasing y velocity
        newY += (int) yVelocity;
        int newTop = newY;
        int newLeft = newX;
        int newRight = newX + playerWidth;
        int newBottom = newY + playerHeight;

        // Platform collision
        boolean landedOnPlatform = false;
        for (Platform platform : platforms) {
            boolean horizontalOverlap = newRight > platform.x && newLeft < platform.x + platform.width;
            if (!horizontalOverlap) {
                continue;
            }

            if (yVelocity > 0 && previousBottom <= platform.y && newBottom > platform.y) {
                newY = platform.y - playerHeight;
                yVelocity = 0;
                canJump = true;
                hasDoubleJumped = false;
                landedOnPlatform = true;
            } else if (yVelocity < 0 && previousTop >= platform.y + platform.height && newTop < platform.y + platform.height) {
                newY = platform.y + platform.height;
                yVelocity = 0;
            }

            newTop = newY;
            newBottom = newY + playerHeight;
        }

        // Enemy movement and simple collision
        for (EnemyEntity enemy : enemies) {
            enemy.x += enemy.dx;
            if (enemy.x < enemy.patrolMinX || enemy.x + enemy.width > enemy.patrolMaxX) {
                enemy.dx = -enemy.dx;
                enemy.x += enemy.dx;
            }

            boolean overlapX = newX < enemy.x + enemy.width && newX + playerWidth > enemy.x;
            boolean overlapY = newY < enemy.y + enemy.height && newY + playerHeight > enemy.y;
            if (overlapX && overlapY && hurtCooldown <= 0 && respawnProtection <= 0) {
                hurtPlayer(1);
                // apply knockback away from the enemy so the player isn't immediately re-hit
                int knockbackDistance = 40; // pixels to push the player
                int enemyCenter = enemy.x + enemy.width / 2;
                int playerCenter = newX + playerWidth / 2;
                if (playerCenter < enemyCenter) {
                    newX = Math.max(0, newX - knockbackDistance);
                } else {
                    newX = Math.min(getWidth() - playerWidth, newX + knockbackDistance);
                }
                // slight upward knock to visually separate from the enemy
                yVelocity = JUMP_FORCE / 2;
                // give short invulnerability after knockback
                respawnProtection = 30;
            }
        }

        // Handle attack timers and collisions
        if (attackCooldown > 0) attackCooldown--;
        if (attackActive) {
            attackTimer--;
            // compute attack hitbox
            int attackX;
            if (facingRight) {
                attackX = newX + playerWidth;
            } else {
                attackX = newX - ATTACK_RANGE;
            }
            int attackY = newY;
            int attackW = ATTACK_RANGE;
            int attackH = playerHeight;

            // check enemies for hit
            for (int i = enemies.size() - 1; i >= 0; i--) {
                EnemyEntity enemy = enemies.get(i);
                boolean overlapX = attackX < enemy.x + enemy.width && attackX + attackW > enemy.x;
                boolean overlapY = attackY < enemy.y + enemy.height && attackY + attackH > enemy.y;
                if (overlapX && overlapY) {
                    int newHealth = enemy.getHealth() - ATTACK_DAMAGE;
                    enemy.setHealth(newHealth);
                    if (newHealth <= 0) {
                        enemies.remove(i);
                    }
                }
            }

            if (attackTimer <= 0) {
                attackActive = false;
            }
        }

        if (hurtCooldown > 0) {
            hurtCooldown--;
        }
        if (respawnProtection > 0) {
            respawnProtection--;
        }

        // Collectible pickup
        for (int i = collectibles.size() - 1; i >= 0; i--) {
            Collectible collectible = collectibles.get(i);
            int collX = collectible.getXPos();
            int collY = collectible.getYPos();
            boolean overlapX = newX < collX + COLLECTIBLE_SIZE && newX + playerWidth > collX;
            boolean overlapY = newY < collY + COLLECTIBLE_SIZE && newY + playerHeight > collY;
            if (overlapX && overlapY) {
                String name = collectible.getName();
                if ("Double Jump Item".equals(name)) {
                    doubleJumpEnabled = true;
                } else if ("Health Pickup".equals(name)) {
                    healPlayer(1);
                } else {
                    collectedCount++;
                }
                collectibles.remove(i);
            }
        }

        // Checkpoint activation
        for (Checkpoint checkpoint : checkpoints) {
            boolean overlapX = newX < checkpoint.x + checkpoint.width && newX + playerWidth > checkpoint.x;
            boolean overlapY = newY < checkpoint.y + checkpoint.height && newY + playerHeight > checkpoint.y;
            if (overlapX && overlapY) {
                int respawnX = checkpoint.x - (playerWidth - checkpoint.width) / 2;
                int respawnY = checkpoint.y - playerHeight;
                setCheckpoint(respawnX, respawnY);
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
        newX = Math.max(0, Math.min(newX, getWidth() - playerWidth));
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

        // Render checkpoints
        g2d.setColor(Color.GREEN);
        for (Checkpoint checkpoint : checkpoints) {
            g2d.fillRect(checkpoint.x, checkpoint.y, checkpoint.width, checkpoint.height);
        }
        // draws checkpoint markers that change the respawn location

        // Render collectibles
        for (Collectible collectible : collectibles) {
            String name = collectible.getName();
            if ("Double Jump Item".equals(name)) {
                g2d.setColor(Color.CYAN);
            } else if ("Health Pickup".equals(name)) {
                g2d.setColor(Color.GREEN);
            } else {
                g2d.setColor(Color.YELLOW);
            }
            g2d.fillOval(collectible.getXPos(), collectible.getYPos(), COLLECTIBLE_SIZE, COLLECTIBLE_SIZE);
        }
        // draws collectibles the player can pick up

        // Render enemies
        g2d.setColor(Color.RED);
        for (EnemyEntity enemy : enemies) {
            g2d.fillRect(enemy.x, enemy.y, enemy.width, enemy.height);
        }
        // draws simple patrolling enemies

        // Render attack hitbox if active
        if (attackActive) {
            g2d.setColor(Color.WHITE);
            int drawX = facingRight ? player.getXPos() + playerWidth : player.getXPos() - ATTACK_RANGE;
            g2d.fillRect(drawX, player.getYPos(), ATTACK_RANGE, playerHeight);
        }

        // Render player
        if (player != null) {
            // only draw the player if the player exists
            if (image != null) {
                if (facingRight) {
                    g2d.drawImage(image, player.getXPos(), player.getYPos(), playerWidth, playerHeight, null);
                } else {
                    g2d.drawImage(image,
                            player.getXPos() + playerWidth, player.getYPos(),
                            player.getXPos(), player.getYPos() + playerHeight,
                            0, 0, image.getWidth(), image.getHeight(),
                            null);
                }
            } else {
                g2d.setColor(Color.ORANGE);
                // sets the player color to orange
                g2d.fillRect(player.getXPos(), player.getYPos(), playerWidth, playerHeight);
                // draws the player as a square/rectangle
            }

            // Draw player name
            g2d.setColor(Color.WHITE);
            // sets text color to white
            g2d.drawString(player.getName(), player.getXPos(), player.getYPos() - 5);
            // draws the player's name above the square
        }

        // Draw score and health
        g2d.setColor(Color.WHITE);
        g2d.drawString("Collectibles: " + collectedCount, 10, 20);
        g2d.drawString("Health: " + playerHealth + " / " + MAX_HEALTH, 10, 40);

        if (!isAlive) {
            g2d.setColor(Color.RED);
            g2d.drawString("Game Over", getWidth() / 2 - 40, getHeight() / 2);
            g2d.drawString("Press R to respawn", getWidth() / 2 - 70, getHeight() / 2 + 20);
        }

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

    private void addHealthPickup(int x, int y) {
        Collectible collectible = new Collectible(0);
        collectible.setXPos(x);
        collectible.setYPos(y);
        collectible.setName("Health Pickup");
        collectibles.add(collectible);
    }

    private void addEnemy(int x, int y, int width, int height, int patrolMinX, int patrolMaxX, int dx) {
        enemies.add(new EnemyEntity(x, y, width, height, patrolMinX, patrolMaxX, dx));
    }

    private void addCheckpoint(int x, int y, int width, int height) {
        checkpoints.add(new Checkpoint(x, y, width, height));
    }

    public void setCheckpoint(int x, int y) {
        checkpointX = x;
        checkpointY = y;
        checkpointActive = true;
    }

    private void hurtPlayer(int amount) {
        if (!isAlive) {
            return;
        }
        playerHealth = Math.max(0, playerHealth - amount);
        hurtCooldown = 30;
        if (playerHealth <= 0) {
            playerHealth = 0;
            isAlive = false;
            player.setYVelocity(0);
            player.setMoving(false);
        }
    }

    private void respawnPlayerAtCheckpoint() {
        int respawnX = checkpointActive ? checkpointX : spawnX;
        int respawnY = checkpointActive ? checkpointY : spawnY;
        player.setXPos(respawnX);
        player.setYPos(respawnY);
        player.setYVelocity(0);
        player.setMoving(false);
        playerHealth = MAX_HEALTH;
        // give a longer safety window after respawn to avoid instant damage
        hurtCooldown = 120;
        respawnProtection = 120;
        isAlive = true;
        canJump = true;
        hasDoubleJumped = false;
        keysPressed.clear();
    }

    private void healPlayer(int amount) {
        if (!isAlive) {
            return;
        }
        playerHealth = Math.min(MAX_HEALTH, playerHealth + amount);
    }

    public void setPlayerSpawn(int x, int y) {
        spawnX = x;
        spawnY = y;
        if (player != null) {
            player.setXPos(x);
            player.setYPos(y);
        }
    }

    private void resetGame() {
        isAlive = true;
        playerHealth = MAX_HEALTH;
        collectedCount = 0;
        hurtCooldown = 0;
        doubleJumpEnabled = false;
        hasDoubleJumped = false;
        canJump = true;
        keysPressed.clear();

        checkpointActive = false;
        checkpointX = spawnX;
        checkpointY = spawnY;

        setPlayerSpawn(spawnX, spawnY);
        player.setYVelocity(0);
        player.setMoving(false);

        collectibles.clear();
        enemies.clear();

        addCollectible(260, 700, 1);
        addCollectible(520, 560, 2);
        addCollectible(130, 580, 3);
        addHealthPickup(380, 700);
        addDoubleJumpItem(700, 700);
        addEnemy(220, 720, ENEMY_SIZE, ENEMY_SIZE, 200, 370, 2);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!isAlive && e.getKeyCode() == KeyEvent.VK_R) {
            respawnPlayerAtCheckpoint();
            return;
        }
        // runs when a key is pressed
        keysPressed.add(e.getKeyCode());
        // adds the key code into the set of pressed keys
        // Attack on 'C' key
        if (e.getKeyCode() == KeyEvent.VK_C) {
            if (!attackActive && attackCooldown <= 0 && isAlive) {
                attackActive = true;
                attackTimer = ATTACK_DURATION;
                attackCooldown = ATTACK_COOLDOWN;
            }
        }
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

    private static class EnemyEntity extends Enemy {
        int x;
        int y;
        final int width;
        final int height;
        final int patrolMinX;
        final int patrolMaxX;
        int dx;

        EnemyEntity(int x, int y, int width, int height, int patrolMinX, int patrolMaxX, int dx) {
            super(1, 1, false);
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.patrolMinX = patrolMinX;
            this.patrolMaxX = patrolMaxX;
            this.dx = dx;
            setName("Enemy");
        }
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
