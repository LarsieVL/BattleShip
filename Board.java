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
        if (grabbedShipIndex == -1) {
            // Check if the player tries to grab a ship
            int indexOfShip = checkIfShipAtLocation(e.getX() / 50, e.getY() / 50);
            System.out.println(indexOfShip);
            if (indexOfShip != -1) {
                grabShip(indexOfShip);
            }
        } else {
            putDownShip(e.getX() / 50, e.getY() / 50);
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
        System.out.println("It should have rendered");
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
        System.out.println("It should have gridded");
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
            if (ships[index].orientation.equals("Horizontal")) {
                for (int i = 0; i < ships[index].getLength(); i++) {
                    if (ships[index].location.x + i == x && ships[index].location.y == y) {
                        return index;
                    }
                }
            } else {
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

    void putDownShip(int x, int y) {
        ships[grabbedShipIndex].setNewLocation(new Point(x, y));
        grabbedShipIndex = -1;
        System.out.println("The moving has occured");
        repaint();
    }
}
