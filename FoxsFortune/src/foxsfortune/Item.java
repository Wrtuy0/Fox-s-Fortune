/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package foxsfortune;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 * @author reesesanders
 */
public class Item extends Entity {
   // attrubutes
    private Boolean interactable;
    // a Boolean that checks if the player can interact with the item
 


    public Item() {
        // a Boolean that checks if the player can interact with the item
        super();
        // calls the entity constructor because item is an entity
        this.interactable = Boolean.FALSE;
        // starts as false because the item is not interactable by default
    }

    public Item(Boolean interactable) {
        // constructor that lets interactable be set right away
        super();
        // calls the entity constructor first
        this.interactable = interactable;
        // stores the interactable value that was sent in
    }

    public void getInteractable() {
        // prints if the item is interactable or not
        System.out.println("Interactable: " + interactable);
    }

    public Boolean setInteractable(Boolean b) {
        // changes if the item is interactable and returns the new value
        this.interactable = b;
        return interactable;
    }

    @Override
    public String toString() {
        return "Item{" +
                "interactable=" + interactable +
                '}';
    }
}
