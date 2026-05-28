/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package foxsfortune;

/**
 * Represents a ground enemy with gravity.
 *
 * Constructors:
 * - Ground()
 * - Ground(int gravity)
 *
 * Attributes:
 * - int gravity
 *
 * Behaviour:
 * - void getGravity()
 * - int setGravity(int gravity)
 * - String toString()
 *
 * @author reesesanders
 */
public class Ground extends Enemy {

    private int gravity;

    public Ground() {
        super();
        this.gravity = 0;
    }

    public Ground(int gravity) {
        super();
        this.gravity = gravity;
    }

    public void getGravity() {
        System.out.println("Gravity: " + gravity);
    }

    public int setGravity(int gravity) {
        this.gravity = gravity;
        return gravity;
    }

    @Override
    public String toString() {
        return "Ground{" +
                "gravity=" + gravity +
                '}';
    }
}
