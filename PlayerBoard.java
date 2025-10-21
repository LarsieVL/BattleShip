import java.awt.Point;
import java.awt.event.*;

class PlayerBoard extends Board implements MouseListener {
    
    boolean gameStarted = false;

    /**
     * The constructor of the playerBoard which add ships to the playing field.
     */
    public PlayerBoard() {
        setSize(500, 500);
        ships = new Ship[5];
        ships[0] = new Ship(new Point(0, 0), 2, "Horizontal");
        ships[1] = new Ship(new Point(0, 1), 3, "Horizontal");
        ships[2] = new Ship(new Point(0, 2), 3, "Horizontal");
        ships[3] = new Ship(new Point(0, 3), 4, "Horizontal");
        ships[4] = new Ship(new Point(0, 4), 5, "Horizontal");
        addMouseListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (!gameStarted) {
            int indexOfShip = checkIfShipAtLocation(e.getX() / 50, e.getY() / 50);
            if (e.isShiftDown()) {
                if (indexOfShip != -1) {
                    tryToRotateShip(indexOfShip);
                }
            } else {
                if (grabbedShipIndex == -1) {
                    // Check if the player tries to grab a ship
                    if (indexOfShip != -1) {
                        grabShip(indexOfShip);
                    }
                } else {
                    tryToPutDownShip(e.getX() / 50, e.getY() / 50);
                }
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    void setGameStarted() {
        gameStarted = true;
    }
}