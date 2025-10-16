import java.awt.Point;
import java.util.Random;

class EnemyBoard extends Board {
    public EnemyBoard() {
        setSize(500, 500);
        ships = new Ship[5];
        ships[0] = new Ship(new Point(0, 0), 2, "Horizontal");
        ships[1] = new Ship(new Point(0, 0), 3, "Horizontal");
        ships[2] = new Ship(new Point(0, 0), 3, "Horizontal");
        ships[3] = new Ship(new Point(0, 0), 4, "Horizontal");
        ships[4] = new Ship(new Point(0, 0), 5, "Horizontal");
        placeShipsRandomly();
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