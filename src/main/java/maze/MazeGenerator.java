package main.java.maze;

import java.util.*;

/**
 * @author jesse.maeder
 * @since 6/17/2015
 */
public class MazeGenerator
{

    private int width;
    private int height;

    private List<Wall> walls;
    private Set<Set<Cell>> cells;

    public Cell[][] getMaze() {
        return maze;
    }

    private Cell[][] maze;

    public MazeGenerator(int width, int height)
    {
        this.width = width;
        this.height = height;

        maze = new Cell[height][width];

        walls = new ArrayList<>();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                maze[i][j] = new Cell(i, j);
            }
        }
        generateWalls();
    }

    private void generateWalls() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Cell c = maze[i][j];

                if (i < height - 1) {
                    walls.add(new Wall(c, maze[i + 1][j]));
                }

                if (j < width - 1) {
                    walls.add(new Wall(c, maze[i][j + 1]));
                }
            }
        }
    }

    /**
     * MAZE GENERATION ALGORITHM - Randomized Kruskal's
     *
     *  1. Create a list of all walls, and create a set for each cell
     *  2. For each wall, in some random order:
     *      1. If the cells divided by this wall belong to distinct sets:
     *          1. Remove the current wall
     *          2. Join the sets of the formerly divided cells
     */
    public List<Wall> generateMaze() {
        cells = new HashSet<>();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Set<Cell> cellSet = new HashSet<>();
                cellSet.add(maze[i][j]);
                cells.add(cellSet);
            }
        }
        Collections.shuffle(walls);
        List<Wall> mazeWalls = new ArrayList<>();
        mazeWalls.addAll(walls);
        for (Wall w : walls) {
            Cell[] separated = w.getCells();
            Set<Cell> set1 = getSet(separated[0]);
            Set<Cell> set2 = getSet(separated[1]);
            if (set1 != null && set2 != null) {
                if (!set1.equals(set2)) {
                    mazeWalls.remove(w);
                    cells.remove(set1);
                    cells.remove(set2);
                    set1.addAll(set2);
                    cells.add(set1);
                }
            }
            if (cells.size() == 1) {
                break;
            }
        }
        return mazeWalls;
    }

    private Set<Cell> getSet(Cell c) {
        for (Set<Cell> set : cells) {
            if (set.contains(c)) return set;
        }
        return null;
    }
}
