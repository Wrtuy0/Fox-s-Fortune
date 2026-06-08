/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package foxsfortune;

import javax.swing.JFrame;

/**
 * Main application frame for Fox's Fortune game.
 *
 * @author reesesanders
 */
public class FoxsFortune extends JFrame {

    private GamePanel gamePanel;

    public FoxsFortune() {
        setTitle("Fox's Fortune");
        // gives the window its title
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // closes the program when the window is closed
        setResizable(false);
        // makes it so the window size cannot be changed
        setLocation(0, 0);
        // puts the window in the upper left of the screen

        gamePanel = new GamePanel();
        // creates the panel where the game runs
        add(gamePanel);
        // adds the game panel onto the frame/window

        pack();
        // sizes the frame to the panel so the drawing area matches the image pixels
        setVisible(true);
        // makes the window show up
        gamePanel.requestFocusInWindow();
        // ensure the game panel has keyboard focus for input
        userManul();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {// main method where the program starts
        new FoxsFortune();
    }
    public static void userManul(){
        System.out.print("A:Move left\nW:jump up\nS:Move down\nD:Move right\nArrow Keys\n^ (Up Arrow):jump up\nv (Down Arrow):Move down\n< (Left Arrow):Move left\n> (Right Arrow):Move right\nZ:Cast long-range ability\nX:Cast standard ability\nC:Execute basic attack");
    }
}
