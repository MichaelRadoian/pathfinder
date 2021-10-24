package heuristicsearch;

public class HardToTraverseCell extends Cell {

    public HardToTraverseCell(Coords coords, boolean isHighway) {
        super(coords, isHighway);
        setSymbol(isHighway ? 'b' : '2');
        this.type = CellType.HARD_TO_TRAVERSE;
    }

    @Override
    public void setIsHighway(boolean isHighway) {
        setSymbol(isHighway ? 'b' : '2');
        this.isHighway = isHighway;
    }
    
    @Override
    public Cell getCopy() {
        Coords coords = getCoordinates();
        return new HardToTraverseCell(new Coords(coords.getColumn(), coords.getRow()), isHighway());
    }
}
