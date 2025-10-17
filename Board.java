import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class Board extends JPanel implements MouseListener {
    
    Ship[] ships;
    ArrayList<Point> hits = new ArrayList<>();
    ArrayList<Point> misses = new ArrayList<>();
    int grabbedShipIndex = -1;
    
    public Board() {
        setSize(500, 500);
        addMouseListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
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

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
    
    Graphics graphicsVar;

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

    void paintShips(Graphics g) {
        g.setColor(new Color(100, 100, 100));
        for (Ship s: ships) {
            if (s.getOrientation().equals("Horizontal")){
                g.fillOval(s.location.x * 50, s.location.y * 50, s.length * 50, 50);
            } else {
                g.fillOval(s.location.x * 50, s.location.y * 50, 50, s.length * 50);
            }
        }
    }

    int checkIfShipAtLocation(int x, int y) { // the ints are coördinates
        for (int index = 0; index < ships.length; index++) {
            if (ships[index].orientation.equals("Horizontal") && index != grabbedShipIndex) {
                for (int i = 0; i < ships[index].getLength(); i++) {
                    if (ships[index].location.x + i == x && ships[index].location.y == y) {
                        return index;
                    }
                }
            } else if (ships[index].orientation.equals("Vertical") && index != grabbedShipIndex){
                for (int i = 0; i < ships[index].getLength(); i++) {
                    if (ships[index].location.x == x && ships[index].location.y + i == y) {
                        return index;
                    }
                }
            }
        }
        return -1;
    }

    void grabShip(int index) {
        grabbedShipIndex = index;
    }

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

        if (canPutDownShip){
            ships[grabbedShipIndex].setNewLocation(new Point(x, y));
            grabbedShipIndex = -1;
            repaint();
        }
    }

    void tryToRotateShip(int index) {
        grabbedShipIndex = index;
        boolean canRotateShip = true;
        if (ships[index].orientation.equals("Horizontal")) {
            for (int i = 0; i < ships[index].getLength(); i++) {
                if (checkIfShipAtLocation(ships[index].location.x, ships[index].location.y + i) != -1) {
                    canRotateShip = false;
                    break;
                }
            }

            if (ships[index].location.y + ships[index].getLength() - 1 > 9 || ships[index].location.x > 9) {
                canRotateShip = false;
            }
        } else {
            for (int i = 0; i < ships[index].getLength(); i++) {
                if (checkIfShipAtLocation(ships[index].location.x + i, ships[index].location.y) != -1) {
                    canRotateShip = false;
                    break;
                }
            }

            if (ships[index].location.y > 9 || ships[index].location.x + ships[index].getLength() - 1 > 9) {
                canRotateShip = false;
            }
        }
        grabbedShipIndex = -1;

        if (canRotateShip) {
            if (ships[index].orientation.equals("Horizontal")){
                ships[index].setOrientation("Vertical");
            } else {
                ships[index].setOrientation("Horizontal");
            }
            repaint();
        }
    }
}
