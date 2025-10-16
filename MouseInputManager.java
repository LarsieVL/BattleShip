import java.awt.event.*;

// Probably deprecated :(
class MouseInputManager implements MouseListener {
    
    @Override
    public void mouseClicked(MouseEvent e) {
        System.out.println("You clicked the mouse at X: " + e.getX() + " Y: " + e.getY());
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
}