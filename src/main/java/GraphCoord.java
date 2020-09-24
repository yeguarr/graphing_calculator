import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;

interface Function {
    double calculate(double x);
    default Color getColor() {
        return Color.getHSBColor((float) Math.abs(this.hashCode()) / Integer.MAX_VALUE, 1.f, 1.f);
    }
}

public class GraphCoord extends JComponent implements ActionListener {

    int WIDTH = 500;
    int HEIGHT = 500;
    double setX = (double) WIDTH / 2;
    double setY = (double) HEIGHT / 2;
    double dX = setX;
    double dY = setY;
    double scaleCount = 1.0;
    double scale = 2.0;
    int GRID_SIZE = 30;
    int GRID_NUMBER = GRID_SIZE / 5;
    Timer t;
    boolean isDark = false;
    java.util.List<Function> functions = new LinkedList<>();

    GraphCoord() {
        MyMouseListener listener = new MyMouseListener();
        addMouseListener(listener);
        addMouseMotionListener(listener);
        addMouseWheelListener(listener);
        addComponentListener(new ResizeListener());

        t = new Timer(1000, this);
        t.start();
    }

    public void addFunction(Function fun) {
        functions.add(fun);
    }

    private static String fmt(double d) {
        if (d == (long) d)
            return String.format("%d", (long) d);
        else
            return String.format("%s", d);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color background = isDark ? Color.BLACK : Color.WHITE;
        g2d.setPaint(background);
        Rectangle2D rect = new Rectangle2D.Float();
        rect.setFrame(0, 0, WIDTH, HEIGHT);
        g2d.fill(rect);

        BasicStroke b1 = new BasicStroke(1);
        BasicStroke b2 = new BasicStroke(2);
        Color gray = isDark ? new Color(60, 60, 60) : new Color(192, 192, 192);

        for (int i = -GRID_NUMBER * ((int) (dY / scale) / GRID_NUMBER); i * scale <= (HEIGHT - GRID_NUMBER * (dY / GRID_NUMBER)); i += GRID_NUMBER) {
            if (i % GRID_SIZE == 0) {
                g2d.setStroke(b2);
                g2d.setPaint(gray);
                Shape l = new Line2D.Double(0, i * scale + dY, WIDTH, i * scale + dY);
                g2d.draw(l);
            } else {
                g2d.setStroke(b1);
                g2d.setPaint(gray);
                Shape l = new Line2D.Double(0, i * scale + dY, WIDTH, i * scale + dY);
                g2d.draw(l);
            }
        }
        for (int i = -GRID_NUMBER * ((int) (dX / scale) / GRID_NUMBER); i * scale <= (WIDTH - GRID_NUMBER * (dX / GRID_NUMBER)); i += GRID_NUMBER) {
            if (i % GRID_SIZE == 0) {
                g2d.setStroke(b2);
                g2d.setPaint(gray);
                Shape l = new Line2D.Double(i * scale + dX, 0, i * scale + dX, HEIGHT);
                g2d.draw(l);
            } else {
                g2d.setStroke(b1);
                g2d.setPaint(gray);
                Shape l = new Line2D.Double(i * scale + dX, 0, i * scale + dX, HEIGHT);
                g2d.draw(l);
            }
        }

        g2d.setStroke(new BasicStroke(3));

        for (Function fun : functions) {
            g2d.setPaint(fun.getColor());
            for (double i = -dX; i <= WIDTH - dX; i+=0.5) {
                Line2D line2D = new Line2D.Double(i + dX,  -fun.calculate((i) / scale * scaleCount / GRID_SIZE) * GRID_SIZE / scaleCount * scale + dY,
                        i + 0.5 + dX, -fun.calculate((i + 0.5) / scale * scaleCount / GRID_SIZE) * GRID_SIZE / scaleCount * scale + dY);
                g2d.draw(line2D);
            }
        }

        g2d.setStroke(b2);
        g2d.setPaint(isDark ? Color.GRAY : Color.BLACK);
        Font f = new Font("Arial", Font.BOLD, 15);
        g2d.setFont(f);
        g2d.drawString("0", (float) (dX + 5), (float) (dY - 5));
        Shape axX = new Line2D.Double(Math.max(Math.min(dX, WIDTH - 5), 5), 0, Math.max(Math.min(dX, WIDTH - 5), 5), HEIGHT);
        Shape axY = new Line2D.Double(0, Math.max(Math.min(dY, HEIGHT - 5), 5), WIDTH, Math.max(Math.min(dY, HEIGHT - 5), 5));
        g2d.draw(axX);
        g2d.draw(axY);
        for (int i = -GRID_NUMBER * ((int) (dY / scale) / GRID_NUMBER); i * scale <= (HEIGHT - GRID_NUMBER * (dY / GRID_NUMBER)); i += GRID_NUMBER) {
            if (i % GRID_SIZE == 0 && i != 0) {
                String text = fmt(-i * scaleCount / GRID_SIZE);
                FontMetrics metrics = g2d.getFontMetrics(f);
                int strw = metrics.stringWidth(text);
                float rx = (float) Math.max(Math.min((dX + 5), WIDTH - strw - 10), 10);
                float ry = (float) (i * scale + dY) - (float) metrics.getHeight() / 2 + metrics.getAscent();
                g2d.setFont(f);
                g2d.drawString(text, rx, ry);
            }

        }
        for (int i = -GRID_NUMBER * ((int) (dX / scale) / GRID_NUMBER); i * scale <= (WIDTH - GRID_NUMBER * (dX / GRID_NUMBER)); i += GRID_NUMBER) {
            if (i % GRID_SIZE == 0 && i != 0) {
                drawCenteredString(g2d, fmt(i * scaleCount / GRID_SIZE), (float) (i * scale + dX), (float) Math.max(Math.min((dY - 10), HEIGHT - 15), 15), f);
            }
        }
    }

    public void drawCenteredString(Graphics2D g, String text, float x, float y, Font font) {
        FontMetrics metrics = g.getFontMetrics(font);
        float rx = x - (float) metrics.stringWidth(text) / 2;
        float ry = y - (float) metrics.getHeight() / 2 + metrics.getAscent();
        g.setFont(font);
        g.drawString(text, rx, ry);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    class ResizeListener extends ComponentAdapter {
        public void componentResized(ComponentEvent e) {
            setX += (double) e.getComponent().getWidth() / 2 - (double) WIDTH / 2;
            setY += (double) e.getComponent().getHeight() / 2 - (double) HEIGHT / 2;
            HEIGHT = e.getComponent().getHeight();
            WIDTH = e.getComponent().getWidth();
            dX = setX;
            dY = setY;
            repaint();
        }
    }

    class MyMouseListener extends MouseAdapter implements MouseWheelListener {
        int oldX = 0;
        int oldY = 0;
        boolean lock = false;

        public void mousePressed(MouseEvent e) {
            oldX = e.getX();
            oldY = e.getY();
            lock = true;
        }

        public void mouseDragged(MouseEvent e) {
            dX = setX + e.getX() - oldX;
            dY = setY + e.getY() - oldY;
            repaint();
        }

        public void mouseReleased(MouseEvent e) {
            setX = dX;
            setY = dY;
            lock = false;
            repaint();
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            double oldX = (e.getX() - dX) / scale * scaleCount / GRID_SIZE;
            double oldY = (e.getY() - dY) / scale * scaleCount / GRID_SIZE;
            dX += e.getPreciseWheelRotation() * (e.getX() - dX) / 50;
            dY += e.getPreciseWheelRotation() * (e.getY() - dY) / 50;
            scale += -e.getPreciseWheelRotation() * scale / 50;
            if (scale <= 1) { // при увеличении
                scale = 10;
                scaleCount *= 10;
                dX = e.getX() - oldX * scale / scaleCount * GRID_SIZE;
                dY = e.getY() - oldY * scale / scaleCount * GRID_SIZE;
            } else if (scale > 10.0) { // при уменьшении
                scale = 1;
                scaleCount /= 10;
                dX = e.getX() - oldX * scale / scaleCount * GRID_SIZE;
                dY = e.getY() - oldY * scale / scaleCount * GRID_SIZE;
            }
            if(!lock) {
                setX = dX;
                setY = dY;
            }
            repaint();
        }
    }
}