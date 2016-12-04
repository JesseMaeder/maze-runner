package main.java.maze;


import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

/**
 * @author jesse.maeder
 * @since 6/17/2015
 */
public class Maze extends JFrame {

	private static final long serialVersionUID = 1740370805048698320L;
	
	private List<Wall> walls;
    private int mazeWidth;
    private int mazeHeight;
    private MazeGenerator generator;
    private Runner runner;
    public BufferedImage mazeImage;

    public final static int WINDOW_SIZE = 1000; //px
    private int cellSize;

    public static final Color BG = Color.white;
    public static final Color FG = Color.darkGray;

    public Maze(int size) {
        this(size, size);
    }

    public Maze(int mazeWidth, int mazeHeight) {
        super(String.format("Maze (%dx%d)", mazeWidth, mazeHeight));
        //setUndecorated(true);
        //setLocation(100, 100);
        this.mazeWidth = mazeWidth;
        this.mazeHeight = mazeHeight;
        runner = new Runner(this);
        int largerDim = Math.max(mazeHeight, mazeWidth);
        cellSize = WINDOW_SIZE / (largerDim * 2 + 1);
        if (cellSize <= 0) cellSize = 1;
        generator = new MazeGenerator(mazeWidth, mazeHeight);
        walls = generator.generateMaze();
        mazeImage = runner.draw(drawMaze());
        add(new JLabel(new ImageIcon(mazeImage)));
        addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {
                runner.move(e);
            }
            public void keyReleased(KeyEvent e) {}
            public void keyTyped(KeyEvent e) {}
        });
        pack();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
        toFront();
    }

    public static void main(String[] args) {
        Maze m;
        long start, end;
        double time;
        /*double sumCreationTime, sumSolveTime, avgCreationTime, avgSolveTime;
        int sizes[] = {5, 10, 25, 50, 100, 250, 500};
        int iterations = 1;
        for (int size : sizes) {
            sumCreationTime = 0;
            sumSolveTime = 0;
            System.out.println(String.format("***** SIZE %3d *****", size));
            for (int i = 0; i < iterations; i++) {
                start = System.nanoTime();
                m = new Maze(size);
                end = System.nanoTime();
                time = (end - start) / 1000000000.0;
                sumCreationTime += time;

                start = System.nanoTime();
                m.drawPath(m.solve());
                end = System.nanoTime();
                time = (end - start) / 1000000000.0;
                sumSolveTime += time;
            }
            avgCreationTime = sumCreationTime / iterations;
            avgSolveTime = sumSolveTime / iterations;
            System.out.println(
                    String.format(
                            "Creation - %.5f seconds (%.5f minutes)\nSolving - %.5f seconds (%.5f minutes)\n",
                            avgCreationTime, avgCreationTime / 60.0,
                            avgSolveTime, avgSolveTime / 60.0
                    )
            );
        }*/
        Scanner s = new Scanner(System.in);
        String str = s.nextLine();
        int size = 50;
        m = null;
        while (true) {
            if (str.equals("new")) {
                size = s.nextInt();
                if (m != null) m.dispatchEvent(new WindowEvent(m, WindowEvent.WINDOW_CLOSING));
                start = System.nanoTime();
                m = new Maze(size);
                end = System.nanoTime();
                time = (end - start) / 1000000000.0;
                System.out.println(String.format("Created maze in %.5f seconds", time));
            } else if (str.equals("solve")) {
            	if (m == null) m = new Maze(size);
                start = System.nanoTime();
                List<Cell> path = m.solve();
                end = System.nanoTime();
                if (path == null) {
                    System.out.println("No solution found.");
                } else {
                    m.drawPath(path);
	                time = (end - start) / 1000000000.0;
	                System.out.println(String.format("Found solution in %.5f seconds", time));
                }
            } else if (str.equals("quit") || str.equals("q")) {
                m.dispatchEvent(new WindowEvent(m, WindowEvent.WINDOW_CLOSING));
                s.close();
                System.exit(0);
            }
            if (s.hasNextLine()) {
                str = s.nextLine();
            }
        }
    }

    public void update() {
        getContentPane().removeAll();
        if (runner.getRow() == mazeHeight - 1 && runner.getCol() == mazeWidth - 1) {
            runner = new Runner(this);
            walls = generator.generateMaze();
            mazeImage = drawMaze();
        }
        add(new JLabel(new ImageIcon(mazeImage)));
        repaint();
        revalidate();
        pack();
    }

    private void drawPath(List<Cell> path) {
        Graphics g = mazeImage.getGraphics();
        g.setColor(Color.RED);
        int i = 0;
        Cell c1;
        Cell c2;
        for (; i < path.size() - 1; i++) {
            c1 = path.get(i);
            c2 = path.get(i + 1);
            g.fillRect(
                    cellSize + (2 * cellSize * c2.col),
                    cellSize + (2 * cellSize * c2.row),
                    cellSize,
                    cellSize
            );
            int avgX = cellSize * (1 + c1.col + c2.col);
            int avgY = cellSize * (1 + c1.row + c2.row);
            g.fillRect(
                    avgX,
                    avgY,
                    cellSize,
                    cellSize
            );
        }
        update();
    }

    private List<Cell> solve() {
        Stack<Cell> s = new Stack<>();
        Cell[][] maze = generator.getMaze();
        Map<Cell, Cell> visited = new HashMap<>();
        s.push(maze[0][0]);
        visited.put(s.peek(), null);
        Cell cur;
        while (!s.isEmpty()) {
            cur = s.pop();
            if (cur.equals(maze[mazeHeight - 1][mazeWidth - 1])) {
                break;
            }
            for (Cell c : getNeighbors(cur)) {
                if (c != null && !visited.containsKey(c)) {
                    visited.put(c, cur);
                    s.push(c);
                }
            }
        }
        List<Cell> path = new ArrayList<>();
        cur = generator.getMaze()[mazeHeight - 1][mazeWidth - 1];
        if (!visited.containsKey(cur)) { return null; }
        Cell next = visited.get(cur);
        while (cur != null) {
            path.add(cur);
            cur = next;
            next = visited.get(cur);
        }
        return path;
    }

    private Cell[] getNeighbors(Cell c) {
        Cell[] neighbors = new Cell[4];
        int i = 0;
        Cell n;
        if (c.row > 0) {
            n = generator.getMaze()[c.row - 1][c.col];
            if (!wallExists(n, c)) {
                neighbors[i++] = n;
            }
        }
        if (c.col > 0) {
            n = generator.getMaze()[c.row][c.col - 1];
            if (!wallExists(n, c)) {
                neighbors[i++] = n;
            }
        }
        if (c.row < mazeHeight - 1) {
            n = generator.getMaze()[c.row + 1][c.col];
            if (!wallExists(c, n)) {
                neighbors[i++] = n;
            }
        }
        if (c.col < mazeWidth - 1) {
            n = generator.getMaze()[c.row][c.col + 1];
            if (!wallExists(c, n)) {
                neighbors[i] = n;
            }
        }
        return neighbors;
    }

    public BufferedImage drawMaze() {
        BufferedImage image = new BufferedImage(
                (mazeWidth * 2 + 1) * cellSize,
                (mazeHeight * 2 + 1) * cellSize,
                BufferedImage.TYPE_INT_RGB
        );

        Graphics g = image.getGraphics();

        g.setColor(FG);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());

        g.setColor(BG);
        g.fillRect(cellSize, cellSize, image.getWidth() - 2 * cellSize, image.getHeight() - 2 * cellSize);

        g.setColor(FG);

        for (int i = 0; i < mazeHeight; i++) {
            for (int j = 0; j < mazeWidth; j++) {
                Cell c1 = generator.getMaze()[i][j];
                if (j < mazeWidth - 1) {
                    Cell c2 = generator.getMaze()[i][j + 1];
                    if (wallExists(c1, c2)) {
                        g.fillRect(
                                2 * cellSize * (j + 1),
                                cellSize * (2 * i + 1),
                                cellSize,
                                cellSize
                        );
                    }
                }
                if (i < mazeHeight - 1) {
                    Cell c2 = generator.getMaze()[i + 1][j];
                    if (wallExists(c1, c2)) {
                        g.fillRect(
                                cellSize * (2 * j + 1),
                                2 * cellSize * (i + 1),
                                cellSize,
                                cellSize
                        );
                    }
                }

                if (i < mazeHeight - 1 && j < mazeWidth - 1) {
                    g.fillRect(
                            (j + 1) * 2 * cellSize,
                            (i + 1) * 2 * cellSize,
                            cellSize,
                            cellSize
                    );
                }
            }
        }

        g.setColor(Color.green);
        g.fillRect(
                image.getWidth() - 2 * cellSize,
                image.getHeight() - 2 * cellSize,
                cellSize,
                cellSize
        );
        return image;
    }

    public boolean wallExists(Cell c1, Cell c2) {
        return walls.contains(new Wall(c1, c2));
    }

    public boolean wallExists(float row, float col) {
        if (row % 1 != 0 && col % 1 != 0) return true;
        if (row < 0 || col < 0) return false;
        if (row % 1 != 0) {
            Cell c1 = new Cell((int) (row - 0.5), (int) col);
            Cell c2 = new Cell((int) (row + 0.5), (int) col);
            return wallExists(c1, c2);
        } else if (col % 1 != 0) {
            Cell c1 = new Cell((int) row, (int) (col - 0.5));
            Cell c2 = new Cell((int) row, (int) (col + 0.5));
            return wallExists(c1, c2);
        }
        return false;
    }

    public int getMazeWidth() { return mazeWidth; }
    public int getMazeHeight() { return mazeHeight; }
    public int getCellSize() { return cellSize; }
}
