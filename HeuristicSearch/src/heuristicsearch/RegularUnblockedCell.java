package heuristicsearch;

public class RegularUnblockedCell extends Cell {

    public RegularUnblockedCell(Coords coords, boolean isHighway) {
        super(coords, isHighway);
        this.type = CellType.REGULAR_UNBLOCKED;
        this.symbol = isHighway ? 'a' : '.';
    }

    @Override
    public void setIsHighway(boolean isHighway) {
        this.symbol = isHighway ? 'a' : '.';
        this.isHighway = isHighway;
    }
    
    @Override
    public Cell getCopy() {
        Coords coords = getCoordinates();
        return new RegularUnblockedCell(new Coords(coords.getColumn(), coords.getRow()), isHighway());
    }
}
