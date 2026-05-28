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
    private int y;
    private int length;
    private int height;

    public Hitbox() {
        this.x = 0;
        this.y = 0;
        this.length = 0;
        this.height = 0;
    }

    public Hitbox(int x, int y, int length, int height) {
        this.x = x;
        this.y = y;
        this.length = length;
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public int setX(int x) {
        this.x = x;
        return x;
    }

    public int getY() {
        return y;
    }

    public int setY(int y) {
        this.y = y;
        return y;
    }

    public int getLength() {
        return length;
    }

    public int setLength(int length) {
        this.length = length;
        return length;
    }

    public int getHeight() {
        return height;
    }

    public int setHeight(int height) {
        this.height = height;
        return height;
    }

    public boolean isIntersect(Hitbox h) {
        if (h == null) {
            return false;
        }
        return this.x < h.x + h.length &&
               this.x + this.length > h.x &&
               this.y < h.y + h.height &&
               this.y + this.height > h.y;
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
