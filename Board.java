import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

/**
 * The big Board class, this class is used as a base by PlayerBoard and EnemyBoard,
 * It contains many methods which are usefull for both of the subclasses.
 * 
 * @author Lars van Luipen
 * @author 
 */
public class Board extends JPanel {
    
    Ship[] ships;
    ArrayList<Point> hits = new ArrayList<>();
    ArrayList<Point> misses = new ArrayList<>();
    int grabbedShipIndex = -1;
    
    public Board() {
        setSize(500, 500);
    }
    
    Graphics graphicsVar;

    /**
     * Renders the board, it paints the grid, paints the ships, and paints the shots.
     */
    void renderBoard() {
        paintGrid(graphicsVar);
        paintShips(graphicsVar);
        paintShots(graphicsVar);
    }
    
    @Override
    public void paintComponent(Graphics g) {
        graphicsVar = g;
        renderBoard();
    }

    /**
     * Paints a grid of 10X10 blocks with two different blue colors in a 
     * checkerboard pattern. 
     * @param g the graphics object which can draw the grid.
     */
    void paintGrid(Graphics g) {
        g.setColor(new Color(255, 0, 0));
        g.fillRect(0, 0, 500, 500);
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if ((i + j) % 2 == 0) {
                    g.setColor(new Color(0, 11, 88));
                } else {
                    g.setColor(new Color(0, 49, 97));
                }
                g.fillRect(j * 50, i * 50, 50, 50);
            }   
        }
    }

    /**
     * Paints the shots which have been shot as white and red dots.
     * @param g the graphics object which should draw the dots.
     */
    void paintShots(Graphics g) {
        g.setColor(Color.WHITE);
        for (Point p : misses) {
            g.fillOval(p.x * 50 + 15, p.y * 50 + 15, 20, 20);
        }
        g.setColor(Color.RED);
        for (Point p : hits) {
            g.fillOval(p.x * 50 + 15, p.y * 50 + 15, 20, 20);
        }
    }

    /**
     * Paints the ships as simple gray ovals.
     * @param g the graphics object which draws the ships.
     */
    void paintShips(Graphics g) {
        g.setColor(new Color(100, 100, 100));
        for (Ship s: ships) {
            if (s.getOrientation().equals("Horizontal")) {
                g.fillOval(s.location.x * 50, s.location.y * 50, s.length * 50, 50);
            } else {
                g.fillOval(s.location.x * 50, s.location.y * 50, 50, s.length * 50);
            }
        }
    }

    /**
     * A very usefull method which gives wether there is a ship at a given location.
     * @param x the x-coordinate to check.
     * @param y the y-coordinate to check.
     * @return the index of the ship which has been hit. It returns -1 if there is no ship.
     */
    int checkIfShipAtLocation(int x, int y) {
        for (int index = 0; index < ships.length; index++) {
            if (ships[index].orientation.equals("Horizontal") && index != grabbedShipIndex) {
                for (int i = 0; i < ships[index].getLength(); i++) {
                    if (ships[index].location.x + i == x && ships[index].location.y == y) {
                        return index;
                    }
                }
            } else if (ships[index].orientation.equals("Vertical") && index != grabbedShipIndex) {
                for (int i = 0; i < ships[index].getLength(); i++) {
                    if (ships[index].location.x == x && ships[index].location.y + i == y) {
                        return index;
                    }
                }
            }
        }
        return -1;
    }

    /**
     * Puts an index into the grabbedShipIndex.
     * @param index The index of the ship to be grabbed.
     */
    void grabShip(int index) {
        grabbedShipIndex = index;
    }

    /**
     * The player tries to move a ship to a new location, this method checks wether it can
     * do that and if it can it moves it.
     * @param x the x-coordinate which the ship gets moved to.
     * @param y the y-coordinate which the ship gets moved to.
     */
    void tryToPutDownShip(int x, int y) {
        boolean canPutDownShip = true;
        if (ships[grabbedShipIndex].orientation.equals("Horizontal")) {
            for (int i = 0; i < ships[grabbedShipIndex].getLength(); i++) {
                if (checkIfShipAtLocation(x + i, y) != -1) {
                    canPutDownShip = false;
                    break;
                }
            }

            if (y > 9 || x + ships[grabbedShipIndex].getLength() - 1 > 9) {
                canPutDownShip = false;
            }
        } else {
            for (int i = 0; i < ships[grabbedShipIndex].getLength(); i++) {
                if (checkIfShipAtLocation(x, y + i) != -1) {
                    canPutDownShip = false;
                    break;
                }
            }

            if (y + ships[grabbedShipIndex].getLength() - 1 > 9 || x > 9) {
                canPutDownShip = false;
            }
        }

        if (canPutDownShip) {
            ships[grabbedShipIndex].setNewLocation(new Point(x, y));
            grabbedShipIndex = -1;
            repaint();
        }
    }

    /**
     * Tries to rotate a ship, if the ship can rotate it will rotate the ship.
     * @param index the index of the ship to be rotated.
     */
    void tryToRotateShip(int index) {
        grabbedShipIndex = index;
        boolean canRotateShip = true;
        if (ships[index].orientation.equals("Horizontal")) {
            for (int i = 0; i < ships[index].getLength(); i++) {
                if (checkIfShipAtLocation(
                        ships[index].location.x, ships[index].location.y + i) != -1
                ) {
                    canRotateShip = false;
                    break;
                }
            }

            if (ships[index].location.y + ships[index].getLength() - 1 > 9 
                    || ships[index].location.x > 9) {
                canRotateShip = false;
            }
        } else {
            for (int i = 0; i < ships[index].getLength(); i++) {
                if (checkIfShipAtLocation(
                        ships[index].location.x + i, ships[index].location.y) != -1
                ) {
                    canRotateShip = false;
                    break;
                }
            }

            if (ships[index].location.y > 9 
                    || ships[index].location.x + ships[index].getLength() - 1 > 9) {
                canRotateShip = false;
            }
        }
        grabbedShipIndex = -1;

        if (canRotateShip) {
            if (ships[index].orientation.equals("Horizontal")) {
                ships[index].setOrientation("Vertical");
            } else {
                ships[index].setOrientation("Horizontal");
            }
            repaint();
        }
    }

    boolean haveAllShipsBeenDestroyed() {
        return hits.size() == 17;
    }

    /**
     * A method which gives which lengths of ship have been destroyed.
     * @return An array of ints which contains all of the lengths of the ships 
     *      which have already been destroyed.
     */
    int[] whichShipsDestroyed() {
        int[] shotsTracker = new int[5];
        for (Point hit: hits) {
            shotsTracker[checkIfShipAtLocation(hit.x, hit.y)] += 1;
        }
        ArrayList<Integer> lengths = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            if (shotsTracker[i] == ships[i].getLength()) {
                lengths.add(ships[i].getLength());
            }
        }
        // Convert arraylist into int[]
        int[] result = new int[lengths.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = lengths.get(i);
        }
        return result;
    }
}
