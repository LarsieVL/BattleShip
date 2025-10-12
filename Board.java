import java.awt.*;
import javax.swing.*;

public class Board extends JPanel {
    
    Ship[] ships;

    public Board() {
        setSize(500, 500);
    }

    Graphics g;
    
    void renderBoard() {
        paintGrid();
    }
    
    @Override
    public void paintComponent(Graphics g) {
        this.g = g;
        renderBoard();
    }

    void paintGrid() {
        super.paintComponent(g);
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

    void paintShips() {
        g.setColor(new Color(100, 100, 100));
        for (Ship s: ships) {
            if (s.getOrientation().equals("Horizontal")){
                g.fillOval(s.location.x * 50, s.location.y * 50, s.length * 50, 50);
            } else {
                g.fillOval(s.location.x * 50, s.location.y * 50, 50, s.length * 50);
            }
        }
    }

}
