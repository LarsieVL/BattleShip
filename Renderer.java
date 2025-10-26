import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

/**
 * The most important class, it holds the window and also some of the logic
 * which controls the game.
 * 
 * @author Lars van Luipen
 * @author Ece Camurlu
 */
public class Renderer implements KeyListener, ComputerAlgorithm {
    // All of the objects we are going to use.
    JFrame frame;
    JPanel panel;
    PlayerBoard playerBoard;
    EnemyBoard enemyBoard;
    JLabel statusLabel;
    String turn = "";

    // Initialize random.
    private Random aiRandom = new Random();

    /**
     * The constructor in which we setup the game.
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
                        + "Shift-click to rotate. Press 'c' to continue");
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
     * A method which starts the game loop, the turn goes to "Player" as opposed to
     * "".
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
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

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

    private void doProbabilityMove() {
        // Create the base probability map ("Search" logic)
        int[][] map = createBaseProbabilityMap();

        // Apply the "Hunt" bonus to squares near active hits
        applyHuntBonusToMap(map);

        // Select the best target based on the final map
        Point bestShot = selectBestTarget(map);

        // If no valid target found (map empty?), fall back to random
        if (bestShot == null) {
            doRandomMove();
            return;
        }

        // Execute the shot at the selected target
        int x = bestShot.x;
        int y = bestShot.y;

        if (playerBoard.checkIfShipAtLocation(x, y) != -1) { // HIT
            playerBoard.hits.add(new Point(x, y));
            turn = "Opponent";
            statusLabel.setText("The enemy HIT! They shoot again. Press 'c'");
        } else { // MISS
            playerBoard.misses.add(new Point(x, y));
            turn = "Player";
            statusLabel.setText("The enemy missed. Your turn. Click a square and press 'c'");
        }
    }

    /**
     * Creates the base probability map via checking all possible ship placements.
     */
    private int[][] createBaseProbabilityMap() {
        int[][] map = new int[10][10];
        ArrayList<Integer> lengthsToCheck = new ArrayList<>();
        int[] shipsDestroyed = playerBoard.whichShipsDestroyed();
        lengthsToCheck.add(2);
        lengthsToCheck.add(3);
        lengthsToCheck.add(3);
        lengthsToCheck.add(4);
        lengthsToCheck.add(5);
        for (int ship : shipsDestroyed) {
            lengthsToCheck.remove((Integer) ship);
        }

        // Check every single position and length.
        for (int length : lengthsToCheck) {
            // Horizontal placements
            for (int y = 0; y < 10; y++) {
                for (int x = 0; x < 11 - length; x++) {
                    if (shipCanBeHere(x, y, length, "Horizontal")) {
                        for (int part = 0; part < length; part++) {
                            map[y][x + part] += 1;
                        }
                    }
                }
            }
            // Vertical placements
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
        return map;
    }

    /**
     * Applies the Hunt bonus around active hits.
     */
    private void applyHuntBonusToMap(int[][] map) {
        ArrayList<Point> activeHits = new ArrayList<>();
        for (Point hit : playerBoard.hits) {
            if (!isHitOnSunkShip(hit)) {
                activeHits.add(hit);
            }
        }

        for (Point hit : activeHits) {
            boolean isVertical = false;
            boolean isHorizontal = false;

            // Checks neighbors to determine orientation
            if (activeHits.contains(new Point(hit.x, hit.y + 1)) 
                || activeHits.contains(new Point(hit.x, hit.y - 1))) {
                isVertical = true;
            }
            if (activeHits.contains(new Point(hit.x + 1, hit.y)) 
                || activeHits.contains(new Point(hit.x - 1, hit.y))) {
                isHorizontal = true;
            }

            // Apply bonus according to orientation
            if (!isVertical && !isHorizontal) {
                int[] dx = { 0, 0, 1, -1 };
                int[] dy = { 1, -1, 0, 0 };
                for (int i = 0; i < 4; i++) {
                    int adjX = hit.x + dx[i];
                    int adjY = hit.y + dy[i];
                    if (isValid(adjX, adjY) && !isAlreadyShot(adjX, adjY)) {
                        map[adjY][adjX] += 1000;
                    }
                }
            }
            if (isHorizontal) { // Horizontal orientation known
                int[] dx = { 1, -1 };
                for (int i = 0; i < 2; i++) {
                    int adjX = hit.x + dx[i];
                    int adjY = hit.y;
                    if (isValid(adjX, adjY) && !isAlreadyShot(adjX, adjY)) {
                        map[adjY][adjX] += 1000;
                    }
                }
            }
            if (isVertical) { // Vertical orientation known
                int[] dy = { 1, -1 };
                for (int i = 0; i < 2; i++) {
                    int adjX = hit.x;
                    int adjY = hit.y + dy[i];
                    if (isValid(adjX, adjY) && !isAlreadyShot(adjX, adjY)) {
                        map[adjY][adjX] += 1000;
                    }
                }
            }
        }
    }

    /**
     * Selects the best target square based on the probability map.
     */
    private Point selectBestTarget(int[][] map) {
        // Set already shot squares to 0 probability
        for (Point hit : playerBoard.hits) {
            map[hit.y][hit.x] = 0;
        }
        for (Point miss : playerBoard.misses) {
            map[miss.y][miss.x] = 0;
        }

        // Find the highest probability squares
        int maxProb = -1;
        ArrayList<Point> bestMoves = new ArrayList<>();
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                if (map[y][x] > maxProb) {
                    maxProb = map[y][x];
                    bestMoves.clear();
                    bestMoves.add(new Point(x, y));
                } else if (map[y][x] == maxProb && maxProb > 0) { // Only add if maxProb > 0
                    bestMoves.add(new Point(x, y));
                }
            }
        }

        // If no valid target found
        if (maxProb <= 0 || bestMoves.isEmpty()) {
            return null;
        }

        // Pick one of the best moves randomly
        return bestMoves.get(aiRandom.nextInt(bestMoves.size()));
    }

    /**
     * A simple helper method for doBestMove which prints out the map.
     * 
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
     * Helper for AI: Checks if an imaginary ship placement conflicts with existing shots.
     * Used for building the probability map.
     *
     * @param x           Starting x-coordinate.
     * @param y           Starting y-coordinate.
     * @param length      Length of ship.
     * @param orientation Ship orientation ("Horizontal" or "Vertical").
     */
    boolean shipCanBeHere(int x, int y, int length, String orientation) {
        if (orientation.equals("Horizontal")) {
             // Check parts of horizontal ship
            for (int part = 0; part < length; part++) {
                Point p = new Point(x + part, y);
                if (playerBoard.misses.contains(p)) {
                    return false;
                }

                if (playerBoard.hits.contains(p)) {
                    return false;
                }
            }
        } else { // Vertical
            // Check parts of vertical ship
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

    private boolean isHitOnSunkShip(Point hit) {
        // Track hits by ship index
        int[] shotsTracker = new int[5]; // Assume ships array has 5 ships
        for (Point h : playerBoard.hits) {
            if (playerBoard.ships == null || playerBoard.ships.length != 5) {
                return true;
            }
            int index = playerBoard.checkIfShipAtLocation(h.x, h.y);
            if (index != -1 && index < shotsTracker.length) { // Checks bounds
                shotsTracker[index] += 1;
            }
        }

        int hitIndex = playerBoard.checkIfShipAtLocation(hit.x, hit.y);
        if (hitIndex == -1 || hitIndex >= playerBoard.ships.length 
            || playerBoard.ships[hitIndex] == null) {
            return true;
        }

        return shotsTracker[hitIndex] == playerBoard.ships[hitIndex].getLength();
    }

    private boolean isValid(int x, int y) {
        // Check if x and y are between 0-10
        return x >= 0 && x < 10 && y >= 0 && y < 10;
    }

    private boolean isAlreadyShot(int x, int y) {
        // Check if the point exists in the misses list
        for (Point miss : playerBoard.misses) {
            if (miss.x == x && miss.y == y) {
                return true;
            }
        }
        // Check if the point exists in the hits list
        for (Point hit : playerBoard.hits) {
            if (hit.x == x && hit.y == y) {
                return true;
            }
        }
        // Return false if not found in either list
        return false;
    }
}
