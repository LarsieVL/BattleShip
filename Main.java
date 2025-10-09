/**
 * The main class for the battleship game, this is where everything starts.
 * 
 * @author Lars van Luipen
 * @author 
 * @date 9-10-2025
 */
public class Main {
    Renderer renderer;

    void run() {
        renderer = new Renderer();
    }
    
    public static void main(String[] args) {
        new Main().run();
    }
}