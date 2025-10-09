import java.awt.*;
import javax.swing.*;

public class Renderer {
    // All of the objects we are going to use.
    JFrame frame;
    JPanel panel;
    Board playerBoard;
    Board enemyBoard;
    JLabel statusLabel;

    // The constructor in which we setup the game.
    public Renderer() {
        frame = new JFrame("BattleShip");
        frame.setSize(1050, 560);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        
        panel = new JPanel(new GridLayout(1, 2, 50, 0));
        playerBoard = new Board();
        panel.add(playerBoard);
        enemyBoard = new Board();
        panel.add(enemyBoard);
        frame.add(panel, BorderLayout.CENTER);

        statusLabel = new JLabel("Here comes status of the game, very usefull");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        frame.add(statusLabel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    void renderGame() {

    }
}
