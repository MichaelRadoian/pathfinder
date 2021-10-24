package heuristicsearch;

public class BlockedCell extends Cell {

    public BlockedCell(Coords coords) {
        super(coords, false);
        this.type = CellType.BLOCKED;
        setSymbol('0');
    }

    @Override
    public void setIsHighway(boolean isHighway) {
    }
    
    @Override
    public Cell getCopy() {
        Coords coords = getCoordinates();
        return new BlockedCell(new Coords(coords.getColumn(), coords.getRow()));
    }
}
