import Solver.Square;
import Solver.WordleSolver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import static java.awt.Color.white;

public class GUI extends JPanel implements ActionListener {

    private WordleSolver solver;
    private ArrayList<Square> squares = new ArrayList<>();
    private JPanel panel;
    private Point mousePointer;
    private Timer timer;
    private int initialX = 400;
    private int initialY = 50;
    public boolean started = false;

    public GUI(JPanel panel){
        this.panel = panel;
        for (int i = 0; i < 30; i++) {
            squares.add(new Square(true));
        }
        try {
            solver = new WordleSolver(squares);
            solver.start();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        timer = new Timer(50, this);
    }

    private void buildUi() {

    }

    private void start() {
        timer.start();
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {


        repaint();
    }

    public void clickedSquare(Point mousePoint){
        int x = ((mousePoint.x - initialX) /100);  // 1,40
        int y = ((mousePoint.y - initialY) /100);

        int index = y*5 + x;
        Square square = squares.get(index);
        Square.Value value = square.getValue();
        if(value == null) // RETIRAR NO FINAL
            square.setValue(Square.Value.GRAY);
        if(value == Square.Value.GRAY)
            square.setValue(Square.Value.YELLOW);
        if(value == Square.Value.YELLOW)
            square.setValue(Square.Value.GREEN);
        if(value == Square.Value.GREEN)
            square.setValue(Square.Value.GRAY);
    }


    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        int spacing = 10;

        Graphics2D g2d = (Graphics2D) g;
        g.setFont(new Font("Arial", Font.BOLD, 80));
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 5; j++) {

                Square square = squares.get(i*5 + j);
                    if(square.getValue() != null){
                        switch(square.getValue()) {
                            case GRAY:
                                g2d.setColor(Color.GRAY);
                                break;
                            case YELLOW:
                                g2d.setColor(Color.orange);
                                break;
                            case GREEN:
                                g2d.setColor(Color.GREEN);
                                break;
                            default:
                                break;
                        }
                        g.fillRect(initialX + 100*j + spacing, initialY + 100 * i + spacing, 80, 80);
                        g.setColor(white);
                        g.drawString(square.getLetter().toUpperCase(), initialX + 12 + 100 * j + spacing, initialY + 70 + 100 * i + spacing);
                    }
                    else {
                        g.setColor(Color.black);
                        g.drawRect(initialX + 100 * j + spacing, initialY + 100 * i + spacing, 80, 80);
                        if(square.getLetter() != null)
                            g.drawString(square.getLetter().toUpperCase(), initialX + 12 + 100 * j + spacing, initialY + 70 + 100 * i + spacing);
                    }
            }
        }
    }

    public static void main(String[] args) {
        // Frame
        JFrame frame = new JFrame("Physics Simulator");
        frame.setLayout(new BorderLayout());

        // Panel
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(1300, 700));
        panel.setLayout(new BorderLayout());

        // Button
        JButton calculate = new JButton("Calculate");
        boolean started = false;

        // Add stuff
        frame.add(calculate, BorderLayout.SOUTH);
        frame.add(panel, BorderLayout.CENTER);  // Add the panel to the frame

        // Adjust sizes
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Create the GUI instance before the listener, so we can access its engine
        GUI gui = new GUI(panel);

        // MouseListener added directly to panel
        panel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                gui.clickedSquare(e.getPoint());
                gui.repaint();}

            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
        });

        // Listener for the start button (So the GUI can start with proper sizes)
        calculate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!gui.started) {
                    panel.add(gui, BorderLayout.CENTER);
                    panel.revalidate();
                    panel.repaint();
                    gui.repaint();
                    gui.start();
                    gui.started = true;
                }else{
                    gui.solver.incrementCurrentAttempt();
                    gui.solver.notifyInput();
                }

            }
        });
    }



}
