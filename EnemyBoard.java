import java.awt.Point;

class EnemyBoard extends Board {
    public EnemyBoard() {
        setSize(500, 500);
        ships = new Ship[5];
        ships[0] = new Ship(new Point(0, 0), 2, "Horizontal");
        ships[1] = new Ship(new Point(0, 1), 3, "Horizontal");
        ships[2] = new Ship(new Point(0, 2), 3, "Horizontal");
        ships[3] = new Ship(new Point(0, 3), 4, "Horizontal");
        ships[4] = new Ship(new Point(6, 0), 5, "Vertical");
    }
}