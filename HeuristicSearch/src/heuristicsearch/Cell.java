package heuristicsearch;

import javafx.scene.shape.Rectangle;

import static heuristicsearch.Constants.*;

public abstract class Cell extends Rectangle {

    protected char symbol;
    protected Coords coords;
    protected CellType type;
    protected boolean isHighway;
    private double gValue;
    private Cell parent;
    private double fValue;
    private double hValue;
    private boolean isPath;
    private double key;
    private Cell[] parents;
    private double[] gValues;
    private double[] fValues;

    public Cell(Coords coords, boolean isHighway) {
        super(CELL_SIZE, CELL_SIZE);
        this.coords = coords;
        this.isHighway = isHighway;
        this.parents = new Cell[5];
        this.gValues = new double[5];
        this.fValues = new double[5];
    }

    public abstract void setIsHighway(boolean isHighway);

    public abstract Cell getCopy();

    public double getFValue() {
        return this.fValue;
    }

    public double getHValue() {
        return this.hValue;
    }

    public double getGValue() {
        return this.gValue;
    }

    public double getGValueForHeuristic(int i) { return this.gValues[i]; }

    public double getFValueForHeuristic(int i) { return this.fValues[i]; }

    public Cell getCellParent() {
        return this.parent;
    }

    public Cell getParentForHeuristic(int j) {
        return this.parents[j];
    }

    public boolean isHighway() {
        return this.isHighway;
    }

    public boolean getIsPath() {
        return this.isPath;
    }

    public char getSymbol() {
        return this.symbol;
    }

    public CellType getCellType() {
        return this.type;
    }

    public Coords getCoordinates() {
        return this.coords;
    }

    public void setFValue(double fVal) {
        this.fValue = fVal;
    }

    public void setHValue(double hVal) {
        this.hValue = hVal;
    }

    public void setGValue(double dist) {
        this.gValue = dist;
    }

    public void setParent(Cell parent) {
        this.parent = parent;
    }

    public void setParentForHeuristic(Cell parent, int i) {
        parents[i] = parent;
    }

    public void setGValueForHeuristic(double g, int i) {
        gValues[i] = g;
    }

    public void setFValueForHeuristic(double f, int i) {
        fValues[i] = f;
    }

    public void setIsPath(boolean isPath) {
        this.isPath = isPath;
    }

    public void setSymbol(char symbol) {
        this.symbol = symbol;
    }

    public Boolean isDiagonal(Cell s0) {
        int x = getCoordinates().getColumn();
        int y = getCoordinates().getRow();
        int x0 = s0.getCoordinates().getColumn();
        int y0 = s0.getCoordinates().getRow();
        return x == x0 + 1 && y == y0 + 1
                || x == x0 + 1 && y == y0 - 1
                || x == x0 - 1 && y == y0 + 1
                || x == x0 - 1 && y == y0 - 1;
    }

    public void printCell() {
        Coords coords = getCoordinates();
        System.out.println("My Coords: x = " + coords.getColumn() + "  y = " + coords.getRow());
    }

    @Override
    public int hashCode() {
        return String.format("x%dy%d", getCoordinates().getColumn(), getCoordinates().getRow()).hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Cell)) {
            return false;
        }

        Cell s0 = (Cell) o;
        int x = getCoordinates().getColumn();
        int y = getCoordinates().getRow();
        int x0 = s0.getCoordinates().getColumn();
        int y0 = s0.getCoordinates().getRow();

        return x == x0 && y == y0;
    }
}
