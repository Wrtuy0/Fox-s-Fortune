/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package foxsfortune;

/**
 * Represents a hitbox in the game with position and dimensions, and provides a method to check
 * @author reesesanders
 */
public class Hitbox {

    private int x;
    // integer that stores the hitbox x position
    private int y;
    // integer that stores the hitbox y position
    private int length;
    // integer that stores how wide the hitbox is
    private int height;
    // integer that stores how tall the hitbox is

    public Hitbox() {
        // default constructor for a hitbox
        this.x = 0;
        // starts x at 0
        this.y = 0;
        // starts y at 0
        this.length = 0;
        // starts length at 0
        this.height = 0;
        // starts height at 0
    }

    public Hitbox(int x, int y, int length, int height) {
        // constructor that lets all hitbox values be sent in
        this.x = x;
        // stores the x position
        this.y = y;
        // stores the y position
        this.length = length;
        // stores the length/width
        this.height = height;
        // stores the height
    }
    //getters
    public int getX() {
        return x;
    }
    //setters
    public int setX(int x) {
        this.x = x;
        return x;
    }
    //getters
    public int getY() {
        return y;
    }
    //setters
    public int setY(int y) {
        this.y = y;
        return y;
    }
    //getters
    public int getLength() {
        return length;
    }
    //setters
    public int setLength(int length) {
        this.length = length;
        return length;
    }
    //getters
    public int getHeight() {
        return height;
    }
    //setters
    public int setHeight(int height) {
        this.height = height;
        return height;
    }
    //setters
    public boolean isIntersect(Hitbox h) {
        // checks if this hitbox is touching/intersecting another hitbox
        if (h == null) {
            // if there is no other hitbox then it cannot be intersecting
            return false;
        }
        return this.x < h.x + h.length &&
               this.x + this.length > h.x &&
               this.y < h.y + h.height &&
               this.y + this.height > h.y;
        // these comparisons check if the two rectangles overlap each other
    }

    @Override
    public String toString() {
        return "Hitbox{" +
               "x=" + x +
               ", y=" + y +
               ", length=" + length +
               ", height=" + height +
               '}';
    }
}
