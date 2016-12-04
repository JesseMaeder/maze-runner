package main.java.maze;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

/**
 * @author jesse.maeder
 * @since 6/18/2015
 */
public class Runner {

    private float row;
    private float col;

    private Maze maze;

    private static boolean moving;

    private final Color COLOR = Color.BLUE;

    public Runner(Maze maze) {
        row = 0f;
        col = 0f;
        this.maze = maze;
        moving = false;
    }

    public void move(KeyEvent e) {
        if (!moving) {
            moving = true;
            maze.mazeImage = erase(maze.mazeImage);
            maze.update();
            float newRow = -1;
            float newCol = -1;
            float step = 0.5f;
            if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyChar() == 'w') {
                if (row > 0) {
                    newRow = row - step;
                }
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyChar() == 's') {
                if (row < maze.getMazeHeight() - 1) {
                    newRow = row + step;
                }
            } else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyChar() == 'a') {
                if (col > 0) {
                    newCol = col - step;
                }
            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyChar() == 'd') {
                if (col < maze.getMazeWidth() - 1) {
                    newCol = col + step;
                }
            }

            if (newCol != -1) {
                if (!maze.wallExists(row, newCol)) {
                    col = newCol;
                }
            } else if (newRow != -1) {
                if (!maze.wallExists(newRow, col)) {
                    row = newRow;
                }
            }
            maze.mazeImage = draw(maze.mazeImage);
            maze.update();
        }
        moving = false;
    }

    public BufferedImage erase(BufferedImage image) {
        Graphics g = image.getGraphics();
        g.setColor(Maze.BG);
        g.fillOval(getX(), getY(), maze.getCellSize(), maze.getCellSize());
        return image;
    }

    public BufferedImage draw(BufferedImage image) {
        Graphics g = image.getGraphics();
        g.setColor(COLOR);
        g.fillOval(getX(), getY(), maze.getCellSize(), maze.getCellSize());
        return image;
    }

    public int getX() { return (int) (maze.getCellSize() + 2 * col * maze.getCellSize()); }
    public int getY() { return (int) (maze.getCellSize() + 2 * row * maze.getCellSize()); }

    public float getRow() {
        return row;
    }

    public float getCol() {
        return col;
    }
}
