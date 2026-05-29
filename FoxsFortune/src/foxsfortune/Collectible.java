/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package foxsfortune;

import java.text.DecimalFormat;
import javax.swing.JOptionPane;

/**
 * @author reesesanders
 */
public class Collectible extends Item {

    private int collectibleNum;
    private String lore;

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

    public static String Dialog(int version) {
        String forest = null, newDawn, musicForTheDead, curruptionWithin;
        if (version==1){// the beginning of the game Dialog
        newDawn = "Listen to that. It's like the trees are telling us something.\nOr maybe they're just reminding us that you're going to get a cold.\nI don't mind. The wind loves to play with hair anyway, so we might as well enjoy it.\nDid you feel that?\nNo, what is it?\nThe wind is shifting.";
        forest = newDawn;
        }
        if (version==2){// after player kills their first  enemy when their score increased by 1
        musicForTheDead = "Ugh, what is that? It looks hideous and inedible.\nThat's what you're worried about? The fact that you can't eat it?\nNo, I was just thinking it was a waste.\nOh brother, never mind that.\n";
        musicForTheDead += "Did you not notice all life in the forest has ceased to be?\nNow that you mention it, yeah.\nSomething is incredibly wrong here. I suggest that we be more wary of our surroundings.";
        forest = musicForTheDead;
        }
        if (version==3){// death scene Dialog
        curruptionWithin = "Hey, what is that? It's sticking to me!\nWell, I guess this is our end. Hey, back off! Hey, what the— stop! What are you doing? Nooooo!\nHmm, Brother? You there? Are you okay? What happened?\nJoin us. Don't fight us. Join the assimilation. Become part of us just as we become part of you. Accept us.\nBrother, noooooooooooooooooooooooo!";
        forest = curruptionWithin;
        }
        return forest;
    }
}
