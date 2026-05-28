/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package foxsfortune;

/**
 * Represents a kamikaze ground enemy with blast damage and radius.
 *
 * Constructors:
 * - Kamikazi()
 * - Kamikazi(int blastDmg, int blastRadius)
 *
 * Attributes:
 * - int blastRadius
 * - int blastDmg
 *
 * Behaviour:
 * - void getBlastDmg()
 * - void getBlastRadius()
 * - int setBlastDmg(int blastDmg)
 * - int setBlastRadius(int blastRadius)
 * - String toString()
 *
 * @author reesesanders
 */
public class Kamikazi extends Ground {

    private int blastRadius;
    private int blastDmg;

    public Kamikazi() {
        super();
        this.blastRadius = 0;
        this.blastDmg = 0;
    }

    public Kamikazi(int blastDmg, int blastRadius) {
        super();
        this.blastRadius = blastRadius;
        this.blastDmg = blastDmg;
    }

    public void getBlastDmg() {
        System.out.println("Blast Damage: " + blastDmg);
    }

    public void getBlastRadius() {
        System.out.println("Blast Radius: " + blastRadius);
    }

    public int setBlastDmg(int blastDmg) {
        this.blastDmg = blastDmg;
        return blastDmg;
    }

    public int setBlastRadius(int blastRadius) {
        this.blastRadius = blastRadius;
        return blastRadius;
    }

    @Override
    public String toString() {
        return "Kamikazi{" +
                "blastDmg=" + blastDmg +
                ", blastRadius=" + blastRadius +
                '}';
    }
}
