import javax.swing.*;
import java.awt.*;

public class MainFrame {
    JFrame frame;

    public static void main(String[] args) {
        MainFrame mainFrame = new MainFrame();
        mainFrame.display();
    }

    void display() {
        frame = new JFrame("Graph");
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        GraphCoord graphCoord = new GraphCoord();
        graphCoord.addFunction(new Function() {
            @Override
            public double calculate(double x) {
                return 3 * Math.sin(Math.pow(x, x)) / Math.pow(2, (Math.pow(x, x) - Math.PI / 2) / Math.PI);
            }

            public Color getColor() {
                return Color.GREEN;
            }
        });
        graphCoord.addFunction(x -> x * x);
        graphCoord.addFunction(x -> Math.sqrt(1 - x * x) + 4);
        graphCoord.addFunction(x -> -Math.sqrt(1 - x * x) + 4);

        frame.add(graphCoord);

        frame.setVisible(true);
    }
}
