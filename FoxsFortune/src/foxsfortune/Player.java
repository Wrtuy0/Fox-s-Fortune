/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package foxsfortune;

/**
 * Represents a player entity with a button assignment.
 *
 * Constructors:
 * - Player()
 *
 * Attributes:
 * - String button
 *
 * Behaviour:
 * - void isButtonClick(String button)
 *
 * @author reesesanders
 */
public class Player extends Entity {

    private String button;
    private double yVelocity;

    public Player() {
        super();
        this.button = "";
        this.yVelocity = 0;
    }

    public void isButtonClick(String button) {
        this.button = button;
        System.out.println("Button clicked: " + button);
    }

    public double getYVelocity() {
        return yVelocity;
    }

    public void setYVelocity(double yVelocity) {
        this.yVelocity = yVelocity;
    }

    @Override
    public String toString() {
        return "Player{" +
                "button='" + button + '\'' +
                ", yVelocity=" + yVelocity +
                '}';
    }
}
