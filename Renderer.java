import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Renderer implements KeyListener {
    // All of the objects we are going to use.
    JFrame frame;
    JPanel panel;
    PlayerBoard playerBoard;
    EnemyBoard enemyBoard;
    JLabel statusLabel;

    String turn = "";
    /**
     * The constructor in which we setup the game.
     * 
     */
    public Renderer() {
        frame = new JFrame("BattleShip");
        frame.setSize(1100, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        
        panel = new JPanel(new GridLayout(1, 2, 0, 0));
        playerBoard = new PlayerBoard();
        panel.add(playerBoard);
        enemyBoard = new EnemyBoard();
        panel.add(enemyBoard);
        frame.add(panel, BorderLayout.CENTER);

        statusLabel = new JLabel(
                "Setup: click on a ship to grab and click again to place it. " 
                + "Shift-click to rotate. Press 'c' to continue"
        );
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        frame.add(statusLabel, BorderLayout.SOUTH);
        frame.addKeyListener(this);
        frame.setVisible(true);
    }

    void renderGame() {
        playerBoard.repaint();
        enemyBoard.repaint();
    }

    void startGameLoop() {
        turn = "Player";
        playerBoard.setGameStarted();
        statusLabel.setText("Click on a tile on the enemy grid and press 'c' to confirm");
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == 'c') {
            if (turn.equals("")) {
                startGameLoop();
            } else if (turn.equals("Player")) {
                System.out.println("Shoot at enemy");
                enemyBoard.shootAtSelectedLocation();
                renderGame();
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}
