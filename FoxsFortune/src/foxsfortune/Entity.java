/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package foxsfortune;

/**
 * Represents an entity in the game with a name, position, movement state, and hitbox.
 *
 * @author reesesanders
 */

public class Entity {
    //attrubutes
    private String name;  // a string that stores the name of the entity
    private int xPos; // integer that stores the x position on the screen
    private int yPos; // integer that stores the y position on the screen
    private boolean moving; // a boolean that checks if the entity is moving or not
    private Hitbox hitbox; // a hitbox object used for collision checks

    public Entity() {
        // default constructor for any basic entity
        this.name = "";// starts the name as blank
        this.xPos = 0;// starts x position at 0
        this.yPos = 0;// starts y position at 0
        this.moving = false;// starts as not moving
        this.hitbox = null;// starts with no hitbox until one is added
    }

    public Entity(String name, int xPos, int yPos, boolean moving, Hitbox hitbox) {
        // constructor that lets all entity data be recived in at once
        this.name = name;// stores the name
        this.xPos = xPos;// stores the x position
        this.yPos = yPos;// stores the y position
        this.moving = moving;// stores if it is moving or not
        this.hitbox = hitbox;// stores the hitbox object
    }
    //getters
    public String getName() {
        return name;
    }
    //setters
    public void setName(String name) {
        this.name = name;
    }
    //getters
    public int getXPos() {
        return xPos;
    }
    //setters
    public void setXPos(int xPos) {
        this.xPos = xPos;
    }
    //getters
    public int getYPos() {
        return yPos;
    }
    //setters
    public void setYPos(int yPos) {
        this.yPos = yPos;
    }
    //getters
    public boolean isMoving() {
        return moving;
    }
    //setters
    public void setMoving(boolean moving) {
        this.moving = moving;
    }
    //getters
    public Hitbox getHitbox() {
        return hitbox;
    }
    //setters
    public void setHitbox(Hitbox hitbox) {
        this.hitbox = hitbox;
    }

    @Override
    public String toString() {
        return "Entity{" +
                "name='" + name + '\'' +
                ", xPos=" + xPos +
                ", yPos=" + yPos +
                ", moving=" + moving +
                ", hitbox=" + hitbox +
                '}';
    }
}
