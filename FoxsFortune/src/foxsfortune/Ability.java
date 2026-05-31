/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package foxsfortune;

/**
 * @author reesesanders
 */
public class Ability extends Item {
    //attrubutes
    private String button;// a string that stores what button/key is connected to the ability
    private boolean collected;//a boolean that checks if the ability has already been collected or not

    // default constructor for ability with no button chosen yet  
    public Ability() {
        //attrubutes
        super();
        // calls the item constructor first because ability is an item
        this.button = "";
        // starts the button as blank
        this.collected = false;
        // starts as false because the player has not collected it yet
    }
    // constructor that lets a button be sent in right away
    public Ability(String button) {
        //attrubutes
        super();
        this.button = button;
        // stores the button that was passed in
        this.collected = false;
        // new abilities start as not collected
    }
    //getters
    public String getButton() {
        return button;
        // returns the button assigned to this ability
    }
    //setters   
    public void setButton(String button) {
        this.button = button;
        // changes the button for this ability
    }
    //getters
    public boolean getCollected() {
        return collected;
        // returns if the ability has been collected or not
    }
    //setters   
    public void setCollected(boolean collected) {
        this.collected = collected;
        // changes the collected status of the ability
    }

    @Override
    public String toString() {
        return "Ability{"
                + "button='" + button + '\''
                + ", collected=" + collected
                + '}';
        // puts the ability information into a string for testing
    }
}
