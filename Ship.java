import java.awt.Point;

class Ship {
    Point location;
    int length;
    String orientation;

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
}