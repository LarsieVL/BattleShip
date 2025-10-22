import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

/**
 * The most important class, it holds the window and also some of the logic which controls the game.
 * 
 * @author Lars van Luipen
 * @author 
 */
public class Renderer implements KeyListener, ComputerAlgorithm {
    // All of the objects we are going to use.
    JFrame frame;
    JPanel panel;
    PlayerBoard playerBoard;
    EnemyBoard enemyBoard;
    JLabel statusLabel;
    String turn = "";

    /**
     * Random number generator for the AI.
     */
        private Random aiRandom = new Random();
        // --- END OF ADDED CODE ---

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
                doBestMove();
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

    @Override
    public void doRandomMove() {
        boolean shotLanded = false;
        Random random = new Random();

        while (!shotLanded) {
            int x = random.nextInt(10);
            int y = random.nextInt(10);

            boolean alreadyShot = isAlreadyShot(x, y);


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

    @Override
    public void doBestMove() {
        doProbabilityMove();
    }

 /*
  * This is the heatmap logic
  */
    private void doProbabilityMove() {
        //Initialize the map, lengthsToCheck and shipsdestroyed.
        int[][] map = new int[10][10];
        ArrayList<Integer> lengthsToCheck = new ArrayList<>();
        int[] shipsDestroyed = playerBoard.whichShipsDestroyed();
        lengthsToCheck.add(2);
        lengthsToCheck.add(3);
        lengthsToCheck.add(3);
        lengthsToCheck.add(4);
        lengthsToCheck.add(5);
        for (int ship : shipsDestroyed) {
            lengthsToCheck.remove((Integer) ship); // Corrected to remove the object, not by index
        }
        for (int length : lengthsToCheck) {
            for (int y = 0; y < 10; y++) {
                for (int x = 0; x < 11 - length; x++) {
                    if (shipCanBeHere(x, y, length, "Horizontal")) {
                        for (int part = 0; part < length; part++) {
                             map[y][x + part] += 1;
                        }
                    }
                }
            }
            for (int y = 0; y < 11 - length; y++) {
                for (int x = 0; x < 10; x++) {
                    if (shipCanBeHere(x, y, length, "Vertical")) {
                        for (int part = 0; part < length; part++) {
                            map[y + part][x] += 1;
                        }
                    }
                }
            }
        }
        ArrayList<Point> activeHits = new ArrayList<>();
        for (Point hit : playerBoard.hits) {
            if (!isHitOnSunkShip(hit)) {
                activeHits.add(hit);
            }
         }
        for (Point hit : activeHits) {
            boolean isVertical = false;
            boolean isHorizontal = false;

            if (activeHits.contains(new Point(hit.x, hit.y + 1)) || activeHits.contains(new Point(hit.x, hit.y - 1))) {
            isVertical = true;
            }
            if (activeHits.contains(new Point(hit.x + 1, hit.y)) || activeHits.contains(new Point(hit.x - 1, hit.y))) {
            isHorizontal = true;
            }

            if (!isVertical && !isHorizontal) {
                int[] dx = {0, 0, 1, -1};
                int[] dy = {1, -1, 0, 0};
                for (int i = 0; i < 4; i++) {
                    int adjX = hit.x + dx[i];
                    int adjY = hit.y + dy[i];
                    if (isValid(adjX, adjY) && !isAlreadyShot(adjX, adjY)) {
                        map[adjY][adjX] += 1000; // High priority "Hunt"
                    }
                }
            }
            if (isHorizontal) {
                int[] dx = {1, -1};
                for (int i = 0; i < 2; i++) {
                    int adjX = hit.x + dx[i];
                    int adjY = hit.y;
                    if (isValid(adjX, adjY) && !isAlreadyShot(adjX, adjY)) {
                        map[adjY][adjX] += 1000; // High priority "Target"
                    }
                }
            }
            if (isVertical) {
                int[] dy = {1, -1};
                for (int i = 0; i < 2; i++) {
                    int adjX = hit.x;
                    int adjY = hit.y + dy[i];
                    if (isValid(adjX, adjY) && !isAlreadyShot(adjX, adjY)) {
                        map[adjY][adjX] += 1000; // High priority "Target"
                    }
                }
            }
        }


        for (Point hit: playerBoard.hits) {
            map[hit.y][hit.x] = 0;
        }
        for (Point miss: playerBoard.misses) {
            map[miss.y][miss.x] = 0;
        }

        int maxProb = -1;
        ArrayList<Point> bestMoves = new ArrayList<>();

        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                if (map[y][x] > maxProb) {
                    maxProb = map[y][x];
                    bestMoves.clear();
                    bestMoves.add(new Point(x, y));
                } else if (map[y][x] == maxProb) {
                    bestMoves.add(new Point(x, y));
                }
            }
        }

        if (maxProb <= 0 || bestMoves.isEmpty()) {
            doRandomMove();
            return;
        }

        Point bestShot = bestMoves.get(aiRandom.nextInt(bestMoves.size()));
        int x = bestShot.x;
        int y = bestShot.y;

        if (playerBoard.checkIfShipAtLocation(x, y) != -1) { // HIT!
            playerBoard.hits.add(new Point(x, y));
            turn = "Opponent";
            statusLabel.setText("The enemy HIT! They shoot again. Press 'c'");
            // We stay in probability mode, so no state change needed
        } else { // MISS
            playerBoard.misses.add(new Point(x, y));
            turn = "Player";
            statusLabel.setText("The enemy missed. Your turn. Click a square and press 'c'");
        }
    }

    /**
     * A simple helper method for doBestMove which prints out the map.
     * @param map a 2D array of integers to be printed.
     */
    void printMap(int[][] map) {
        System.out.println("--- Probability Density Map ---");
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                System.out.print(String.format("%3d", map[i][j]));
            }   
            System.out.println();
        }
    }

    /**
     * A helper method for doBestMove, it sees wether an imaginery ship can exist 
     * at a certain location based on misses.
     * @param x the x location of the imaginery ship.
     * @param y the y location of the imaginery ship
     * @param length the lenght of the imaginery ship.
     * @param orientation the orientation of the imaginery ship.
     * @return wether a ship could theorethically be at the location. True if it can be there.
     */
    private boolean isHitOnSunkShip(Point hit) {
        int[] shotsTracker = new int[5]; // Assumes ships array always has 5 ships
        for (Point h : playerBoard.hits) {
            if (playerBoard.ships == null || playerBoard.ships.length != 5) {
                return true;
            }
            int index = playerBoard.checkIfShipAtLocation(h.x, h.y);
            if (index != -1 && index < shotsTracker.length) { // Check index bounds
                shotsTracker[index] += 1;
            }
        }

        int hitIndex = playerBoard.checkIfShipAtLocation(hit.x, hit.y);
        if (hitIndex == -1 || hitIndex >= playerBoard.ships.length || playerBoard.ships[hitIndex] == null) {
        }

        return shotsTracker[hitIndex] == playerBoard.ships[hitIndex].getLength();
    }
    boolean shipCanBeHere(int x, int y, int length, String orientation) {
        if (orientation.equals("Horizontal")) {
            for (int part = 0; part < length; part++) {
                Point p = new Point(x + part, y);
                if (playerBoard.misses.contains(p)) {
                     return false;
                }

                if (playerBoard.hits.contains(p)) {
                     return false;
                }
            }
        } else {
            for (int part = 0; part < length; part++) {
                Point p = new Point(x, y + part);
                if (playerBoard.misses.contains(p)) {
                    return false;
                }
                if (playerBoard.hits.contains(p)) {
                    return false;
            }
            }
        }
        return true;
    }

    // --- ADDED HELPER METHODS ---
    /**
     * Helper method to check if a square is on the board.
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return true if 0 <= x < 10 and 0 <= y < 10.
     */
 private boolean isValid(int x, int y) {
    return x >= 0 && x < 10 && y >= 0 && y < 10;
 }

 /**
     * Helper method to check if a square has already been shot.
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return true if the square is in the hits or misses list, false otherwise.
     */
 private boolean isAlreadyShot(int x, int y) {
        for (Point miss : playerBoard.misses) {
            if (miss.x == x && miss.y == y) {
                return true;
            }
    }
    for (Point hit : playerBoard.hits) {
            if (hit.x == x && hit.y == y) {
                return true;
            }
        }
        return false;
    }
}
