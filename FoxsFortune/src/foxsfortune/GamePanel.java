package foxsfortune;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * Game panel for rendering and managing game logic.
 *
 * @author Reese Sanders, Logan Saywich and Aws Tariq and Copiliot AI for logic
 * and structure assistance (I belive the the specific model used was Raptor
 * Mini, but I am not sure)
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
    private int currentRoom = 0; // current room ID for room switching
    private boolean roomSwitchingEnabled = false; // prevent room changes until startup completes
    private final Map<Integer, RoomDefinition> roomDefinitions = new HashMap<>();
    private int playerHealth = MAX_HEALTH; // player health for simple enemy interaction
    private int hurtCooldown = 0; // prevents repeated damage in the same contact
    private int respawnProtection = 0; // temporary invulnerability after respawn
    private boolean isAlive = true; // whether the player is alive
    private boolean doubleJumpEnabled = false; // whether player can perform a second jump in the air
    private boolean hasDoubleJumped = false; // whether the player has already used the second jump
    private boolean jumpKeyPreviouslyPressed = false; // used to detect jump key presses instead of holds
    private double xVelocity = 0; // horizontal movement speed
    private double knockbackVelocityX = 0; // smooth horizontal push after enemy contact
    private final double KNOCKBACK_FORCE = 9.0; // starting horizontal knockback speed
    private final double KNOCKBACK_FRICTION = 0.72; // how quickly knockback slows down
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
    // animations
    private BufferedImage image;
    private final BufferedImage[] walkingImages = new BufferedImage[2];
    private int walkAnimationTick = 0;
    private int walkAnimationFrame = 0;
    private final int WALK_ANIMATION_FRAME_DELAY = 10;
    private BufferedImage backgroundImage;

    public enum EnemyType {
        BASIC,
        GROUND,
        FLYING,
        KAMIKAZI
    }

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

        setPreferredSize(new Dimension(1000, 900));
        // lock the game panel to the image's expected resolution

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
        // import the player's models from classpath resources
        image = loadPlayerModel("BiggerFoxModel.png");
        walkingImages[0] = loadPlayerModel("FoxWalkingRight1.png");
        walkingImages[1] = loadPlayerModel("FoxWalkingRight2.png");
        BufferedImage sizeImage = image != null ? image : getWalkingFrame();
        if (sizeImage != null) {
            playerWidth = sizeImage.getWidth();
            playerHeight = sizeImage.getHeight();
        }

        startInRoom(1);
        javax.swing.SwingUtilities.invokeLater(() -> roomSwitchingEnabled = true);

        // Start game loop
        startGameLoop();
        // calls the method that starts updating the game
    }

    private BufferedImage loadPlayerModel(String fileName) {
        String resourcePath = "/foxsfortune/images/player models/" + fileName;
        URL modelURL = FoxsFortune.class.getResource(resourcePath);
        if (modelURL == null) {
            try {
                java.io.File fallbackFile = new java.io.File("FoxsFortune/src/foxsfortune/images/player models/" + fileName);
                if (fallbackFile.exists()) {
                    modelURL = fallbackFile.toURI().toURL();
                }
            } catch (java.net.MalformedURLException e) {
                System.err.println("Error resolving fallback player model path: " + e);
            }
        }

        if (modelURL != null) {
            try {
                return ImageIO.read(modelURL);
            } catch (IOException e) {
                System.err.println("Error reading player model image: " + e);
            }
        } else {
            System.err.println("Warning: " + fileName + " not found in classpath resources.");
        }
        return null;
    }

    private BufferedImage getWalkingFrame() {
        return walkingImages[walkAnimationFrame] != null
                ? walkingImages[walkAnimationFrame]
                : walkingImages[1 - walkAnimationFrame];
    }

    private void initializeRoom1(String backgroundResource, int playerSpawnX, int playerSpawnY) {
        registerRoom(1, backgroundResource, playerSpawnX, playerSpawnY, 0, 2, () -> {
            // Add platforms to match the red collision lines in Room1Shell.png
            addPlatform(19, 620, 279, 10);      // left lower floor
            addPlatform(19, 620, 10, 162);       // left lower wall
            addPlatform(294, 620, 10, 90);       // inner left vertical wall
            addPlatform(294, 706, 105, 10);      // lower center platform
            addPlatform(19, 778, 483, 10);       // bottom interior floor

            addPlatform(321, 485, 77, 10);       // central mid ledge
            addPlatform(395, 485, 10, 225);      // central divider wall
            addPlatform(320, 447, 10, 45);
            addPlatform(595, 445, 10, 52);
            addPlatform(498, 490, 107, 10);
            addPlatform(498, 492, 10, 290);      // right vertical wall

            addPlatform(398, 627, 43, 20);       //small lower left platform
            addPlatform(462, 546, 40, 15);      // small lower right platform

            addPlatform(0, 447, 326, 10);        // left upper platform
            addPlatform(598, 445, 402, 10);      // right upper platform
            addPlatform(228, 347, 95, 19);      // central right platform
            addPlatform(604, 353, 96, 19);      // upper right platform
            addPlatform(77, 319, 84, 22);       // lower left mid platform

            addPlatform(113, 220, 42, 15);      // top left small ledge
            addPlatform(909, 240, 33, 16);      // top right small ledge
            addPlatform(255, 255, 56, 13);      // upper left mid platform
            addPlatform(740, 297, 64, 16);      // upper right mid platform

            addPlatform(890, 343, 93, 16);      // mid sky box

            addEnemy(40, 447 - ENEMY_SIZE, ENEMY_SIZE, ENEMY_SIZE, 0, 326, 1);
             
        });
    }

    private void initializeRoom2(String backgroundResource, int playerSpawnX, int playerSpawnY) {
        registerRoom(2, backgroundResource, playerSpawnX, playerSpawnY, 1, 0, () -> {
            // Add platforms to match the right-side Room2Shell.png layout
            addPlatform(114, 289, 42, 17);    // top left small
            addPlatform(615, 299, 39, 17);    // top middle small
            addPlatform(315, 324, 67, 17);    // upper left/mid small
            addPlatform(839, 327, 90, 23);    // upper right small
            addPlatform(75, 357, 108, 23);    // left floating
            addPlatform(553, 367, 126, 25);   // middle floating
            addPlatform(0, 447, 120, 10);     // far left top ledge
            addPlatform(175, 447, 525, 10);   // big middle top ledge
            addPlatform(778, 447, 222, 10);   // right room top ledge
            addPlatform(690, 487, 38, 18);    // small right tab, tightened
            addPlatform(760, 570, 20, 20);    // middle-right tab, tightened
            addPlatform(333, 582, 361, 10);   // inner middle ledge
            addPlatform(690, 647, 93, 10);    // right inner ledge
            addPlatform(470, 659, 92, 25);    // small lower middle platform
            addPlatform(330, 652, 60, 20);    // small lower left platform
            addPlatform(175, 665, 167, 10);    // small lower left platform
            addPlatform(690, 716, 295, 10);   // lower right floor
            addPlatform(200, 742, 497, 10);   // long bottom-middle red floor
            addPlatform(110, 447, 10, 400);   // left tall wall
            addPlatform(175, 447, 10, 225);   // inner left wall
            addPlatform(329, 582, 10, 90);    // middle step wall
            addPlatform(689, 447, 10, 139);   // upper middle-right wall
            addPlatform(778, 447, 10, 204);   // right room left wall
            addPlatform(690, 647, 10, 145);   // right tall wall
            addPlatform(974, 724, 10, 162);    // far right small wall
            addPlatform(195, 742, 10, 43);    // far right small wall
            addPlatform(200, 785, 490, 10);    // long red line across middle
            addPlatform(120, 844, 880, 10);    // lower left red floor
            
            //double jump spawn
            addDoubleJumpItem(100, 100);
        });
    }

    private void initializeRooms() {
        initializeRoom1("/foxsfortune/images/backgrounds/Room1Final.png", 140, 778 - playerHeight);
        initializeRoom2("/foxsfortune/images/backgrounds/Room2Final.png", 20, 447 - playerHeight);
    }

    private void startInRoom(int roomId) {
        initializeRooms();
        loadRoom(roomId);
        currentRoom = roomId;
        RoomDefinition room = roomDefinitions.get(roomId);
        if (room != null) {
            player.setXPos(room.playerSpawnX);
            player.setYPos(room.playerSpawnY);
            spawnX = room.playerSpawnX;
            spawnY = room.playerSpawnY;
            checkpointX = room.playerSpawnX;
            checkpointY = room.playerSpawnY;
            checkpointActive = false;
        }
    }

    private void registerRoom(int roomId, String backgroundResource, int playerSpawnX, int playerSpawnY, int leftRoomId, int rightRoomId, Runnable roomSetup) {
        roomDefinitions.put(roomId, new RoomDefinition(backgroundResource, playerSpawnX, playerSpawnY, leftRoomId, rightRoomId, roomSetup));
    }

    private void loadRoom(int roomId) {
        loadRoom(roomId, false);
    }

    private void loadRoom(int roomId, boolean preservePlayerState) {
        RoomDefinition room = roomDefinitions.get(roomId);
        if (room == null) {
            System.err.println("Room " + roomId + " is not registered.");
            return;
        }

        currentRoom = roomId;
        platforms.clear();
        checkpoints.clear();
        collectibles.clear();
        enemies.clear();
        backgroundImage = null;

        loadBackground(room.backgroundResource);
        room.roomSetup.run();
        spawnX = room.playerSpawnX;
        spawnY = room.playerSpawnY;
        checkpointActive = false;
        checkpointX = room.playerSpawnX;
        checkpointY = room.playerSpawnY;
        if (!preservePlayerState) {
            setPlayerSpawn(room.playerSpawnX, room.playerSpawnY);
            player.setYVelocity(0);
            knockbackVelocityX = 0;
            player.setMoving(false);
        }
    }

    private int switchRoomIfNeeded(int newX) {
        if (!roomSwitchingEnabled) {
            return newX;
        }

        RoomDefinition current = roomDefinitions.get(currentRoom);
        if (current == null) {
            return newX;
        }

        if (getWidth() <= playerWidth) {
            return newX;
        }

        if (newX >= getWidth() - playerWidth && current.rightRoomId != 0) {
            int savedY = player.getYPos();
            double savedYVelocity = player.getYVelocity();
            boolean savedCanJump = canJump;
            boolean savedHasDoubleJumped = hasDoubleJumped;
            boolean preserveVerticalState = savedYVelocity != 0;
            loadRoom(current.rightRoomId, true);
            int landingX = findBestLandingX(10, 1, 12);
            int landingY = preserveVerticalState
                    ? Math.max(0, Math.min(savedY, getHeight() - playerHeight))
                    : getRoomTransitionLandingY(landingX);
            player.setXPos(landingX);
            player.setYPos(landingY);
            player.setYVelocity(preserveVerticalState ? savedYVelocity : 0);
            if (preserveVerticalState) {
                canJump = savedCanJump;
                hasDoubleJumped = savedHasDoubleJumped;
            } else {
                canJump = true;
                hasDoubleJumped = false;
            }
            return landingX;
        } else if (newX <= 0 && current.leftRoomId != 0) {
            int savedY = player.getYPos();
            double savedYVelocity = player.getYVelocity();
            boolean savedCanJump = canJump;
            boolean savedHasDoubleJumped = hasDoubleJumped;
            boolean preserveVerticalState = savedYVelocity != 0;
            loadRoom(current.leftRoomId, true);
            int landingX = findBestLandingX(getWidth() - playerWidth - 10, -1, 12);
            int landingY = preserveVerticalState
                    ? Math.max(0, Math.min(savedY, getHeight() - playerHeight))
                    : getRoomTransitionLandingY(landingX);
            player.setXPos(landingX);
            player.setYPos(landingY);
            player.setYVelocity(preserveVerticalState ? savedYVelocity : 0);
            if (preserveVerticalState) {
                canJump = savedCanJump;
                hasDoubleJumped = savedHasDoubleJumped;
            } else {
                canJump = true;
                hasDoubleJumped = false;
            }
            return landingX;
        }

        return newX;
    }

    private void loadBackground(String backgroundResource) {
        URL bgURL = FoxsFortune.class.getResource(backgroundResource);
        if (bgURL == null) {
            try {
                String fallbackPath = backgroundResource.startsWith("/") ? backgroundResource.substring(1) : backgroundResource;
                java.io.File fallbackBg = new java.io.File("FoxsFortune/src/" + fallbackPath);
                if (fallbackBg.exists()) {
                    bgURL = fallbackBg.toURI().toURL();
                }
            } catch (java.net.MalformedURLException e) {
                System.err.println("Error resolving fallback background path: " + e);
            }
        }

        if (bgURL != null) {
            try {
                backgroundImage = ImageIO.read(bgURL);
            } catch (IOException e) {
                System.err.println("Error reading background image: " + e);
            }
        } else {
            System.err.println("Warning: " + backgroundResource + " not found in classpath resources.");
        }
    }

    private int getRoomTransitionLandingY(int x) {
        int platformTop = findLowestPlatformTopAtX(x, playerWidth);
        if (platformTop >= 0) {
            return Math.max(0, platformTop - playerHeight);
        }

        int nearbyPlatformTop = findLowestPlatformTopNearX(x, playerWidth, 120);
        if (nearbyPlatformTop >= 0) {
            return Math.max(0, nearbyPlatformTop - playerHeight);
        }

        int maxY = getHeight() > playerHeight ? getHeight() - playerHeight : GROUND_LEVEL;
        return Math.min(GROUND_LEVEL, maxY);
    }

    private int findLowestPlatformTopAtX(int x, int width) {
        int bestY = Integer.MIN_VALUE;
        for (Platform platform : platforms) {
            boolean overlapsHorizontally = x + width > platform.x && x < platform.x + platform.width;
            if (overlapsHorizontally) {
                bestY = Math.max(bestY, platform.y);
            }
        }
        return bestY == Integer.MIN_VALUE ? -1 : bestY;
    }

    private int findLowestPlatformTopNearX(int x, int width, int radius) {
        int bestY = Integer.MIN_VALUE;
        int minX = Math.max(0, x - radius);
        int maxX = Math.min(getWidth() - width, x + radius);
        for (int candidateX = minX; candidateX <= maxX; candidateX++) {
            int platformTop = findLowestPlatformTopAtX(candidateX, width);
            if (platformTop >= 0) {
                bestY = Math.max(bestY, platformTop);
            }
        }
        return bestY == Integer.MIN_VALUE ? -1 : bestY;
    }

    private int findSafeLandingX(int startX, int direction) {
        int maxOffset = 120;
        int bestX = startX;
        int bestY = Integer.MIN_VALUE;
        for (int offset = 0; offset <= maxOffset; offset++) {
            int candidateX = startX + offset * direction;
            candidateX = Math.max(0, Math.min(getWidth() - playerWidth, candidateX));
            int landingY = getRoomTransitionLandingY(candidateX);
            if (landingY > bestY) {
                bestY = landingY;
                bestX = candidateX;
            }
        }
        return bestX;
    }

    private int findBestLandingX(int startX, int direction, int radius) {
        int bestX = startX;
        int bestY = Integer.MIN_VALUE;
        int minX = Math.max(0, startX - radius);
        int maxX = Math.min(getWidth() - playerWidth, startX + radius);
        for (int candidateX = minX; candidateX <= maxX; candidateX++) {
            int landingY = getRoomTransitionLandingY(candidateX);
            if (landingY > bestY) {
                bestY = landingY;
                bestX = candidateX;
            }
        }
        return bestX;
    }

    private static class RoomDefinition {

        final String backgroundResource;
        final int playerSpawnX;
        final int playerSpawnY;
        final int leftRoomId;
        final int rightRoomId;
        final Runnable roomSetup;

        RoomDefinition(String backgroundResource, int playerSpawnX, int playerSpawnY, int leftRoomId, int rightRoomId, Runnable roomSetup) {
            this.backgroundResource = backgroundResource;
            this.playerSpawnX = playerSpawnX;
            this.playerSpawnY = playerSpawnY;
            this.leftRoomId = leftRoomId;
            this.rightRoomId = rightRoomId;
            this.roomSetup = roomSetup;
        }
    }

    private boolean overlapsPlatform(int x, int y, Platform platform) {
        return x < platform.x + platform.width
                && x + playerWidth > platform.x
                && y < platform.y + platform.height
                && y + playerHeight > platform.y;
    }

    private CollisionCorrection pushPlayerOutOfPlatforms(int x, int y, double yVelocity) {
        int correctedX = x;
        int correctedY = y;
        boolean landed = false;
        boolean hitHead = false;

        for (int passes = 0; passes < platforms.size(); passes++) {
            boolean correctedThisPass = false;

            for (Platform platform : platforms) {
                if (!overlapsPlatform(correctedX, correctedY, platform)) {
                    continue;
                }

                int pushLeft = correctedX + playerWidth - platform.x;
                int pushRight = platform.x + platform.width - correctedX;
                int pushUp = correctedY + playerHeight - platform.y;
                int pushDown = platform.y + platform.height - correctedY;

                int smallestPush = Math.min(Math.min(pushLeft, pushRight), Math.min(pushUp, pushDown));
                if (smallestPush == pushUp && yVelocity >= 0) {
                    correctedY = platform.y - playerHeight;
                    landed = true;
                } else if (smallestPush == pushDown && yVelocity < 0) {
                    correctedY = platform.y + platform.height;
                    hitHead = true;
                } else if (pushLeft < pushRight) {
                    correctedX = platform.x - playerWidth;
                } else {
                    correctedX = platform.x + platform.width;
                }

                correctedThisPass = true;
                break;
            }

            if (!correctedThisPass) {
                break;
            }
        }

        return new CollisionCorrection(correctedX, correctedY, landed, hitHead);
    }

    private static class CollisionCorrection {

        final int x;
        final int y;
        final boolean landed;
        final boolean hitHead;

        CollisionCorrection(int x, int y, boolean landed, boolean hitHead) {
            this.x = x;
            this.y = y;
            this.landed = landed;
            this.hitHead = hitHead;
        }
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
        if (xVelocity > 0) {
            facingRight = true;
        } else if (xVelocity < 0) {
            facingRight = false;
        }

        int previousTop = player.getYPos();
        int previousBottom = previousTop + playerHeight;
        int previousLeft = player.getXPos();
        int previousRight = previousLeft + playerWidth;

        boolean wasSupported = previousTop == GROUND_LEVEL;
        for (Platform platform : platforms) {
            boolean horizontalOverlap = previousRight > platform.x && previousLeft < platform.x + platform.width;
            if (horizontalOverlap && previousBottom == platform.y) {
                wasSupported = true;
                break;
            }
        }
        if (!wasSupported) {
            canJump = false;
        }

        double horizontalVelocity = xVelocity + knockbackVelocityX;
        int horizontalMove = (int) Math.round(horizontalVelocity);

        // Apply horizontal movement first
        newX += horizontalMove;
        for (Platform platform : platforms) {
            boolean verticalOverlap = previousBottom > platform.y && previousTop < platform.y + platform.height;
            if (!verticalOverlap) {
                continue;
            }

            if (horizontalMove > 0 && previousRight <= platform.x && newX + playerWidth > platform.x) {
                newX = platform.x - playerWidth;
                xVelocity = 0;
                knockbackVelocityX = 0;
            } else if (horizontalMove < 0 && previousLeft >= platform.x + platform.width && newX < platform.x + platform.width) {
                newX = platform.x + platform.width;
                xVelocity = 0;
                knockbackVelocityX = 0;
            }
        }

        knockbackVelocityX *= KNOCKBACK_FRICTION;
        if (Math.abs(knockbackVelocityX) < 0.25) {
            knockbackVelocityX = 0;
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

        CollisionCorrection platformCorrection = pushPlayerOutOfPlatforms(newX, newY, yVelocity);
        newX = platformCorrection.x;
        newY = platformCorrection.y;
        if (platformCorrection.landed) {
            yVelocity = 0;
            canJump = true;
            hasDoubleJumped = false;
            landedOnPlatform = true;
        } else if (platformCorrection.hitHead) {
            yVelocity = 0;
        }

        // Enemy movement and simple collision
        for (EnemyEntity enemy : enemies) {
            if (enemy.hasPath()) {
                Point target = enemy.getCurrentPathTarget();
                int deltaX = target.x - enemy.x;
                int deltaY = target.y - enemy.y;
                double distance = Math.hypot(deltaX, deltaY);
                if (distance <= enemy.speed || distance == 0) {
                    enemy.x = target.x;
                    enemy.y = target.y;
                    enemy.advancePathIndex();
                } else {
                    enemy.x += (int) Math.round(deltaX / distance * enemy.speed);
                    enemy.y += (int) Math.round(deltaY / distance * enemy.speed);
                }
            } else {
                enemy.x += enemy.dx;
                if (enemy.x < enemy.patrolMinX || enemy.x + enemy.width > enemy.patrolMaxX) {
                    enemy.dx = -enemy.dx;
                    enemy.x += enemy.dx;
                }
            }

            boolean overlapX = newX < enemy.x + enemy.width && newX + playerWidth > enemy.x;
            boolean overlapY = newY < enemy.y + enemy.height && newY + playerHeight > enemy.y;
            if (overlapX && overlapY && hurtCooldown <= 0 && respawnProtection <= 0) {
                hurtPlayer(1);
                int enemyCenter = enemy.x + enemy.width / 2;
                int playerCenter = newX + playerWidth / 2;
                knockbackVelocityX = playerCenter < enemyCenter ? -KNOCKBACK_FORCE : KNOCKBACK_FORCE;
                // slight upward knock to visually separate from the enemy
                yVelocity = JUMP_FORCE / 2;
                platformCorrection = pushPlayerOutOfPlatforms(newX, newY, yVelocity);
                newX = platformCorrection.x;
                newY = platformCorrection.y;
                // give short invulnerability after knockback
                respawnProtection = 30;
            }
        }

        // Handle attack timers and collisions
        if (attackCooldown > 0) {
            attackCooldown--;
        }
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

        // Handle room transitions at the screen edges
        int previousRoom = currentRoom;
        newX = switchRoomIfNeeded(newX);
        if (previousRoom != currentRoom) {
            // Room changed; preserve the position and physics state that the new room load established.
            newY = player.getYPos();
            yVelocity = player.getYVelocity();
            platformCorrection = pushPlayerOutOfPlatforms(newX, newY, yVelocity);
            newX = platformCorrection.x;
            newY = platformCorrection.y;
            if (platformCorrection.landed) {
                yVelocity = 0;
                canJump = true;
                hasDoubleJumped = false;
            } else if (platformCorrection.hitHead) {
                yVelocity = 0;
            }
        }

        // Bounds checking (horizontal)
        newX = Math.max(0, Math.min(newX, getWidth() - playerWidth));
        // keeps the player from leaving the left or right side of the screen

        // Prevent going above screen
        newY = Math.max(0, newY);
        // keeps the player from going above the top of the screen

        platformCorrection = pushPlayerOutOfPlatforms(newX, newY, yVelocity);
        newX = platformCorrection.x;
        newY = platformCorrection.y;
        if (platformCorrection.landed) {
            yVelocity = 0;
            canJump = true;
            hasDoubleJumped = false;
        } else if (platformCorrection.hitHead) {
            yVelocity = 0;
        }
        newX = Math.max(0, Math.min(newX, getWidth() - playerWidth));
        newY = Math.max(0, newY);

        player.setXPos(newX);
        // saves the new x position into the player object
        player.setYPos(newY);
        // saves the new y position into the player object
        player.setYVelocity(yVelocity);
        // saves the updated y velocity

        // Update moving state
        player.setMoving(!keysPressed.isEmpty() || knockbackVelocityX != 0);
        // if any key is pressed, the player is counted as moving
        if (isAlive && xVelocity != 0) {
            walkAnimationTick++;
            if (walkAnimationTick >= WALK_ANIMATION_FRAME_DELAY) {
                walkAnimationTick = 0;
                walkAnimationFrame = 1 - walkAnimationFrame;
            }
        } else {
            walkAnimationTick = 0;
            walkAnimationFrame = 0;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        // this method draws everything on the panel
        super.paintComponent(g);
        // clears and redraws the panel correctly
        Graphics2D g2d = (Graphics2D) g;
        // changes Graphics into Graphics2D so drawing is easier

        // Render background image if available
        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        }

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
            BufferedImage currentPlayerImage = image;
            if (xVelocity != 0 && getWalkingFrame() != null) {
                currentPlayerImage = getWalkingFrame();
            }

            if (currentPlayerImage != null) {
                if (facingRight) {
                    g2d.drawImage(currentPlayerImage, player.getXPos(), player.getYPos(), playerWidth, playerHeight, null);
                } else {
                    g2d.drawImage(currentPlayerImage,
                            player.getXPos() + playerWidth, player.getYPos(),
                            player.getXPos(), player.getYPos() + playerHeight,
                            0, 0, currentPlayerImage.getWidth(), currentPlayerImage.getHeight(),
                            null);
                }
            } else {
                g2d.setColor(Color.ORANGE);
                // sets the player color to orange
                g2d.fillRect(player.getXPos(), player.getYPos(), playerWidth, playerHeight);
                // draws the player as a square/rectangle
            }
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

    private void addGroundEnemySpawnNode(int x, int y) {
        spawnEnemy(EnemyType.GROUND, x, y, ENEMY_SIZE, ENEMY_SIZE, null, 1);
    }

    public void spawnEnemy(EnemyType enemyType, int x, int y, int width, int height, List<Point> path, int speed) {
        List<Point> pathCopy = (path == null) ? null : new ArrayList<>(path);
        EnemyEntity enemy = new EnemyEntity(x, y, width, height, 0, 0, speed, pathCopy, enemyType);
        switch (enemyType) {
            case GROUND -> {
                enemy.setHealth(3);
                enemy.setDamage(2);
                enemy.setName("Ground");
            }
            case FLYING -> {
                enemy.setHealth(2);
                enemy.setDamage(1);
                enemy.setName("Flying");
            }
            case KAMIKAZI -> {
                enemy.setHealth(1);
                enemy.setDamage(3);
                enemy.setName("Kamikazi");
            }
            default -> {
                enemy.setHealth(1);
                enemy.setDamage(1);
                enemy.setName("Enemy");
            }
        }
        enemies.add(enemy);
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
            knockbackVelocityX = 0;
            player.setMoving(false);
        }
    }

    private void respawnPlayerAtCheckpoint() {
        int respawnX = checkpointActive ? checkpointX : spawnX;
        int respawnY = checkpointActive ? checkpointY : spawnY;
        player.setXPos(respawnX);
        player.setYPos(respawnY);
        player.setYVelocity(0);
        knockbackVelocityX = 0;
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
        knockbackVelocityX = 0;
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
        int dy;
        int speed;
        final List<Point> path;
        int pathIndex;
        int pathDirection;
        final EnemyType enemyType;

        EnemyEntity(int x, int y, int width, int height, int patrolMinX, int patrolMaxX, int dx) {
            this(x, y, width, height, patrolMinX, patrolMaxX, dx, null, EnemyType.BASIC);
        }

        EnemyEntity(int x, int y, int width, int height, int patrolMinX, int patrolMaxX, int speed, List<Point> path, EnemyType enemyType) {
            super(1, 1, false);
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.patrolMinX = patrolMinX;
            this.patrolMaxX = patrolMaxX;
            this.dx = speed;
            this.dy = 0;
            this.speed = speed;
            this.path = (path == null || path.isEmpty()) ? null : new ArrayList<>(path);
            this.pathIndex = 0;
            this.pathDirection = 1;
            this.enemyType = enemyType;
            setName("Enemy");
        }

        boolean hasPath() {
            return path != null && !path.isEmpty();
        }

        Point getCurrentPathTarget() {
            return hasPath() ? path.get(pathIndex) : null;
        }

        void advancePathIndex() {
            if (path == null || path.isEmpty()) {
                return;
            }
            pathIndex += pathDirection;
            if (pathIndex >= path.size()) {
                pathIndex = path.size() - 2;
                pathDirection = -1;
            } else if (pathIndex < 0) {
                pathIndex = 1;
                pathDirection = 1;
            }
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
