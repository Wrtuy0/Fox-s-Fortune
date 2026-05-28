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
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        gamePanel = new GamePanel();
        add(gamePanel);

        setSize(1000, 900);
        setVisible(true);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new FoxsFortune();
    }
}
