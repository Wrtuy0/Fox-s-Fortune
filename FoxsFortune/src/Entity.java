/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 * Represents an entity in the game with a name, position, movement state, and hitbox.
 *

 * @author reesesanders
 */

import foxsfortune.Hitbox;

public class Entity {

    private String name;
    private int xPos;
    private int yPos;
    private boolean moving;
    private Hitbox hitbox;

    public Entity() {
        this.name = "";
        this.xPos = 0;
        this.yPos = 0;
        this.moving = false;
        this.hitbox = null;
    }

    public Entity(String name, int xPos, int yPos, boolean moving, Hitbox hitbox) {
        this.name = name;
        this.xPos = xPos;
        this.yPos = yPos;
        this.moving = moving;
        this.hitbox = hitbox;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getXPos() {
        return xPos;
    }

    public void setXPos(int xPos) {
        this.xPos = xPos;
    }

    public int getYPos() {
        return yPos;
    }

    public void setYPos(int yPos) {
        this.yPos = yPos;
    }

    public boolean isMoving() {
        return moving;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    public Hitbox getHitbox() {
        return hitbox;
    }

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
