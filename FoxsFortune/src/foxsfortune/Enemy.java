/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package foxsfortune;

/**
 * Represents an enemy entity with health, damage, and boss state.
 *
 * Constructors:
 * - Enemy()
 * - Enemy(int health, int damage, boolean miniBoss)
 *
 * Attributes:
 * - int health
 * - int damage
 * - boolean miniBoss
 *
 * Behaviour:
 * - int getHealth()
 * - void setHealth(int health)
 * - int getDamage()
 * - void setDamage(int damage)
 * - boolean getMiniBoss()
 * - void setMiniBoss(boolean miniBoss)
 * - String toString()
 *
 * @author reesesanders
 */
public class Enemy extends Entity {

    private int health;
    private int damage;
    private boolean miniBoss;

    public Enemy() {
        super();
        this.health = 0;
        this.damage = 0;
        this.miniBoss = false;
    }

    public Enemy(int health, int damage, boolean miniBoss) {
        super();
        this.health = health;
        this.damage = damage;
        this.miniBoss = miniBoss;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public boolean getMiniBoss() {
        return miniBoss;
    }

    public void setMiniBoss(boolean miniBoss) {
        this.miniBoss = miniBoss;
    }

    @Override
    public String toString() {
        return "Enemy{" +
                "health=" + health +
                ", damage=" + damage +
                ", miniBoss=" + miniBoss +
                '}';
    }
}
