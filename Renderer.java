import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;

/**
 * The most important class, it holds the window and also some of the logic which controls the game.
 * 
 * @author Lars van Luipen
 * @author 
 */
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
        frame.setSize(1050, 600);
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

    /**
     * A method which can be called to repaint both the player and enemy board.
     */
    void renderGame() {
        playerBoard.repaint();
        enemyBoard.repaint();
    }

    /**
     * A method which starts the game loop, the turn goes to "Player" as opposed to "".
     */
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
                // true if there wasn't already a pin, so the shot landed
                int result = enemyBoard.shootAtSelectedLocation();
                if (result == 0) { // thus a miss
                    // The turn goes to opponent
                    turn = "Opponent";
                    statusLabel.setText("The enemy gets to shoot");
                } else if (result == 1) { // Thus a shot at a square where there was already a pin
                    // The turn doesn't go to the opponent.
                    statusLabel.setText("You have already shot there. " 
                        + "Click a different square and press c to shoot");
                } else { // A hit
                    // The turn doesn't go to the opponent
                    // Also put which ships have been destroyed into the statuslabel
                    String status = ("You have hit an enemy ship, you can shoot again. " 
                        + "Click another square and press c to shoot.");
                    int[] lengthsOfShips = enemyBoard.whichShipsDestroyed();
                    if (lengthsOfShips.length > 0) {
                        status = status + " You have shot ships with lengths: ";
                    }
                    for (int length : lengthsOfShips) {
                        status = status + length + " ";
                    }
                    statusLabel.setText(status);
                }
            
            } else if (turn.equals("Opponent")) {
                boolean shotLanded = false;
                Random random = new Random();

                while (!shotLanded) {
                    int x = random.nextInt(10);
                    int y = random.nextInt(10);

                    boolean alreadyShot = false;
                    for (Point miss : playerBoard.misses) {
                        if (miss.x == x && miss.y == y) {
                            alreadyShot = true;
                            break;
                        }
                    }
                    if (!alreadyShot) {
                        for (Point hit : playerBoard.hits) {
                            if (hit.x == x && hit.y == y) {
                                alreadyShot = true;
                                break;
                            }
                        }
                    }

                    if (!alreadyShot) {
                        shotLanded = true;
                        if (playerBoard.checkIfShipAtLocation(x, y) != -1) { 
                            playerBoard.hits.add(new Point(x, y));
                            turn = "Opponent";
                            statusLabel.setText("The enemy HIT! They shoot again. Press 'c'");
                        } else {
                            playerBoard.misses.add(new Point(x, y));
                            turn = "Player";
                            statusLabel.setText("The enemy missed. " 
                                + "Your turn. Click a square and press 'c'");
                        }
                    }
                }
            }
            renderGame();
            if (playerBoard.haveAllShipsBeenDestroyed()) {
                statusLabel.setText("Game Over, you have Lost!");
                turn = "No turn";
            } else if (enemyBoard.haveAllShipsBeenDestroyed()) {
                statusLabel.setText("Game Over, you have Won!");
                turn = "No turn";
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}
