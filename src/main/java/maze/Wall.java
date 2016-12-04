package main.java.maze;

/**
 * @author jesse.maeder
 * @since 6/17/2015
 */
public class Wall {

    public final float row;
    public final float col;
    private Cell c1;
    private Cell c2;

    public Wall(Cell c1, Cell c2) {
        row = (c1.row + c2.row) / 2f;
        col = (c1.col + c2.col) / 2f;
        this.c1 = c1;
        this.c2 = c2;
    }

    public Cell[] getCells() {
        return new Cell[] {c1, c2};
    }

    public boolean equals(Object o) {
        if (o.getClass() != Wall.class) return false;
        Wall w = (Wall) o;
        return (c1.equals(w.c1) && c2.equals(w.c2));
    }

    public String toString() {
        Cell[] cells = getCells();
        return String.format("{%4.1f, %4.1f}: Between %s and %s", row, col, cells[0], cells[1]);
    }
}
