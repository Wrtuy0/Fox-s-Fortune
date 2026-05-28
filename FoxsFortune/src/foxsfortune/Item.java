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

    private Boolean interactable;

    public Item() {
        super();
        this.interactable = Boolean.FALSE;
    }

    public Item(Boolean interactable) {
        super();
        this.interactable = interactable;
    }

    public void getInteractable() {
        System.out.println("Interactable: " + interactable);
    }

    public Boolean setInteractable(Boolean b) {
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
