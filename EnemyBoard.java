import java.awt.Point;
import java.awt.event.*;
import java.util.Random;

class EnemyBoard extends Board implements MouseListener{

    Point selectedLocation = new Point(0, 0);

    public EnemyBoard() {
        setSize(500, 500);
        ships = new Ship[5];
        ships[0] = new Ship(new Point(0, 0), 2, "Horizontal");
        ships[1] = new Ship(new Point(0, 0), 3, "Horizontal");
        ships[2] = new Ship(new Point(0, 0), 3, "Horizontal");
        ships[3] = new Ship(new Point(0, 0), 4, "Horizontal");
        ships[4] = new Ship(new Point(0, 0), 5, "Horizontal");
        placeShipsRandomly();
        addMouseListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        selectedLocation.x = e.getX() / 50;
        selectedLocation.y = e.getY() / 50;
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    boolean shootAtSelectedLocation() {
        boolean alreadyAPin = false;
        if (
                checkIfShipAtLocation(selectedLocation.x, selectedLocation.y) == -1
        ) { // If there is no ship.
            for (Point miss : misses) {
                if (miss.x == selectedLocation.x && miss.y == selectedLocation.y) {
                    alreadyAPin = true;
                    break;
                }
                if (!alreadyAPin) {
                    misses.add(new Point(selectedLocation.x, selectedLocation.y));
                }
            }
        } else {
            for (Point hit : hits) {
                if (hit.x == selectedLocation.x && hit.y == selectedLocation.y) {
                    alreadyAPin = true;
                    break;
                }
                if (!alreadyAPin) {
                    misses.add(new Point(selectedLocation.x, selectedLocation.y));
                }
            }
            hits.add(new Point(selectedLocation.x, selectedLocation.y));
        }
        return alreadyAPin;
    }

    private void placeShipsRandomly() {
        Random random = new Random();
        boolean[][] occupied = new boolean[10][10];

        for (Ship ship : ships) {
            boolean placed = false;
            while (!placed) {
                String orientation = random.nextBoolean() ? "Horizontal" : "Vertical";
                ship.orientation = orientation;
                int x = random.nextInt(10);
                int y = random.nextInt(10);

                if (orientation.equals("Horizontal")) {
                    if (x + ship.getLength() > 10) {
                        continue;
                    }
                } else { 
                    if (y + ship.getLength() > 10) {
                        continue;
                    }
                }

                boolean collision = false;
                for (int i = 0; i < ship.getLength(); i++) {
                    if (orientation.equals("Horizontal")) {
                        if (occupied[y][x + i]) {
                            collision = true;
                            break; 
                        }
                    } else { 
                        if (occupied[y + i][x]) {
                            collision = true;
                            break; 
                        }
                    }
                }

                if (!collision) {
                    ship.setNewLocation(new Point(x, y));
                    for (int i = 0; i < ship.getLength(); i++) {
                        if (orientation.equals("Horizontal")) {
                            occupied[y][x + i] = true;
                        } else { 
                            occupied[y + i][x] = true;
                        }
                    }
                    placed = true;
                }
            }
        }
    }
}