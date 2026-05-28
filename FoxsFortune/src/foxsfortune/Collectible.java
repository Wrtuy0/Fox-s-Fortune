/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package foxsfortune;

/**
 * @author reesesanders
 */
public class Collectible extends Item {

    private int collectibleNum;

    public Collectible() {
        super();
        this.collectibleNum = 0;
    }

    public Collectible(int collectibleNum) {
        super();
        this.collectibleNum = collectibleNum;
    }

    public void getCollectibleNum() {
        System.out.println("Collectible Number: " + collectibleNum);
    }

    public int setCollectibleNum(int collectibleNum) {
        this.collectibleNum = collectibleNum;
        return collectibleNum;
    }
}
