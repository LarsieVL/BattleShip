import java.awt.Point;

class Ship {
    Point location;
    int length;
    String orientation;

    /**
     * The constructor for the ship which contains three variables.
     * @param location the left or top side of the ships.
     * @param length the length of the ship.
     * @param orientation the orientation, "Horizontal" or "Vertical".
     */
    public Ship(Point location, int length, String orientation) {
        this.location = location;
        this.length = length;
        this.orientation = orientation;
    }

    Point getLocation() {
        return location;
    }

    int getLength() {
        return length;
    }

    String getOrientation() {
        return orientation;
    }

    void setNewLocation(Point l) {
        location = l;
    }

    void setOrientation(String o) {
        orientation = o;
    }
}