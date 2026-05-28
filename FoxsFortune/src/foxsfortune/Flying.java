/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package foxsfortune;

/**
 * Represents a flying enemy tracking player coordinates.
 *
 * Constructors:
 * - Flying()
 * - Flying(int playerX, int playerY)
 *
 * Attributes:
 * - int playerX
 * - int playerY
 *
 * Behaviour:
 * - int getPlayerX()
 * - void setPlayerX(int playerX)
 * - int getPlayerY()
 * - void setPlayerY(int playerY)
 * - int lineToPlayer()
 * - String toString()
 *
 * @author reesesanders
 */
public class Flying extends Enemy {

    private int playerX;
    private int playerY;

    public Flying() {
        super();
        this.playerX = 0;
        this.playerY = 0;
    }

    public Flying(int playerX, int playerY) {
        super();
        this.playerX = playerX;
        this.playerY = playerY;
    }

    public int getPlayerX() {
        return playerX;
    }

    public void setPlayerX(int playerX) {
        this.playerX = playerX;
    }

    public int getPlayerY() {
        return playerY;
    }

    public void setPlayerY(int playerY) {
        this.playerY = playerY;
    }

    public int lineToPlayer() {
        return Math.abs(playerX) + Math.abs(playerY);
    }

    @Override
    public String toString() {
        return "Flying{" +
                "playerX=" + playerX +
                ", playerY=" + playerY +
                '}';
    }
}
