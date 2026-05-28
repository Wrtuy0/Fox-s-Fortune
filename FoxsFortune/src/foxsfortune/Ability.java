/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package foxsfortune;

/**
 * @author reesesanders
 */
public class Ability extends Item {

    private String button;
    private boolean collected;

    public Ability() {
        super();
        this.button = "";
        this.collected = false;
    }

    public Ability(String button) {
        super();
        this.button = button;
        this.collected = false;
    }

    public String getButton() {
        return button;
    }

    public void setButton(String button) {
        this.button = button;
    }

    public boolean getCollected() {
        return collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    @Override
    public String toString() {
        return "Ability{" +
                "button='" + button + '\'' +
                ", collected=" + collected +
                '}';
    }
}
