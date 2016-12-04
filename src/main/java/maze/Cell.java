package main.java.maze;

/**
 * @author jesse.maeder
 * @since 6/17/2015
 */
public class Cell {

    public final int row;
    public final int col;

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
    }
    
    public boolean equals(Cell c) {
        return (row == c.row) && (col == c.col);
    }

    public String toString() {
        return String.format("{%d, %d}", row, col);
    }
}
