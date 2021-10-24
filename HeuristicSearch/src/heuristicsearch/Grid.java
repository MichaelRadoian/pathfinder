package heuristicsearch;

import java.util.*;
import java.util.stream.Collectors;

import static heuristicsearch.Constants.*;
import java.io.File;
import java.io.FileNotFoundException;

public class Grid {

    private Cell[][] grid;
    int startColumn, startRow, endRow, endColumn;
    private Cell start;
    private Cell goal;
    private List<Coords> hardToTraverseCenters = new ArrayList<>();

    public Grid(Cell[][] grid) {
        this.grid = grid;
    }

    public Grid() {
    }

    public void displayGrid() {
        for (int i = 0; i < GRID_ROWS; i++) {
            for (int j = 0; j < GRID_COLUMNS; j++) {
                if (grid[i][j] != null) {
                    System.out.print(grid[i][j].getSymbol() + " ");
                } else {
                    System.out.print("O ");
                }
            }
            System.out.println("| " + i);
        }
    }

    public void generateGrid(int columns, int rows) {
        this.grid = new Cell[rows][columns];
        Random rand = new Random();
        for (int i = 0; i < GRID_ROWS; i++) {
            for (int j = 0; j < GRID_COLUMNS; j++) {
                //int n = rand.nextInt(10);
                grid[i][j] = new RegularUnblockedCell(new Coords(j, i), false);
            }
        }
        addSpecialCells();
        selectStartEnd();
    }

    public void addSpecialCells() {
        addHardToTraverseCells();
        addHighways();
        addBlockedCells();
    }

    //picks random points on the grid and the point isnt a highway or a blocked cell it becomes blocked
    private void addBlockedCells()  {
        int max = (GRID_COLUMNS * GRID_ROWS)/5;
        int i = 0;
        int randRow = 0;
        int randColumn = 0;
        while(i < max) {
            Random rand = new Random();
            randColumn = rand.nextInt(GRID_COLUMNS);
            randRow = rand.nextInt(GRID_ROWS);
            if( grid[randRow][randColumn].isHighway() || grid[randRow][randColumn].getCellType() == CellType.BLOCKED) {
                continue;
            }
            BlockedCell cell = new BlockedCell(new Coords(randColumn,randRow));
            grid[randRow][randColumn] = cell;
            i++;
        }
    }

    //picks a rand cord within 20 spaces away from the borders
    public void selectStartEnd() {
        Random rand = new Random();
        int sideOfMap;
        while (true) {
            sideOfMap = rand.nextInt(3);
            // 0 = left, 1=top, 2 = right, 3 = bottom
            if (sideOfMap == 0) {
                startColumn = rand.nextInt(20);
                startRow = rand.nextInt(120);
            } else if (sideOfMap == 1) {
                startColumn = rand.nextInt(160);
                startRow = rand.nextInt(20);
            } else if (sideOfMap == 2) {
                startColumn = rand.nextInt(20) + 140;
                startRow = rand.nextInt(120);
            } else {
                startColumn = rand.nextInt(160);
                startRow = rand.nextInt(20) + 100;
            }
            if (grid[startRow][startColumn].getCellType() == CellType.BLOCKED) {
                continue;
            }
            break;
        }

        //picks end that is at least 100 away
        while (true) {
            endColumn = rand.nextInt(160);
            endRow = rand.nextInt(120);
            if (grid[endRow][endColumn].getCellType() == CellType.BLOCKED || Math.sqrt(Math.pow(endColumn - startColumn, 2) + Math.pow(endRow - startRow, 2)) < 100) {
                continue;
            }
            break;
        }

        start = grid[startRow][startColumn];
        goal = grid[endRow][endColumn];
    }

    private void addHardToTraverseCells() {
        int counter = 0;
        for (int i = 0; i < NUM_OF_HTT_AREAS; i++) {
            Random rand = new Random();
            int randColumn = rand.nextInt(129) + 16;
            int randRow = rand.nextInt(89) + 16;
            //System.out.println("randX: " + randColumn + "  randY: " + randRow);
            hardToTraverseCenters.add(new Coords(randRow, randColumn));
            for (int j = randRow - 16; j < randRow + 15; j++) {
                for (int n = randColumn - 16; n < randColumn + 15; n++) {
                    //System.out.println(j + "  " + n);
                    int chance = rand.nextInt(101);

                    if (chance <= 50) {
                        if (grid[j][n].getCellType() != CellType.HARD_TO_TRAVERSE) {
                            HardToTraverseCell cell = new HardToTraverseCell(new Coords(n, j), false);
                            grid[j][n] = cell;
                            counter++;
                        }
                    }
                }
            }
        }
        System.out.println("changed to HTTR: " + counter + " cells");
    }

    private void addHighways() {
        Random rand = new Random();
        int builtHighways = 0;
        while (builtHighways < NUM_OF_HIGHWAYS) {
            Stack<Cell> highway = new Stack<>();
            boolean hasCollision = false;
            int randStartPointRow = rand.nextInt(GRID_ROWS);
            int randStartPointColumn = rand.nextInt(GRID_COLUMNS);
            int direction = rand.nextInt(4);

            if (direction == Direction.UP.getValue()) {
                for (int y = 0; y < HIGHWAY_CHUNK_SIZE; y++) {
                    Cell currCell = grid[y][randStartPointColumn];
                    if (!currCell.isHighway()) {
                        currCell.setIsHighway(true);
                        highway.push(currCell);
                    } else {
                        restartHighway(highway);
                        hasCollision = true;
                        break;
                    }
                }
                if (!hasCollision && extendHighway(new Coords(randStartPointColumn, HIGHWAY_CHUNK_SIZE - 1), Direction.UP, highway)) {
                    builtHighways++;
                }
            } else if (direction == Direction.RIGHT.getValue()) {
                for (int x = GRID_COLUMNS - 1; x > GRID_COLUMNS - HIGHWAY_CHUNK_SIZE - 1; x--) {
                    Cell currCell = grid[randStartPointRow][x];
                    if (!currCell.isHighway()) {
                        currCell.setIsHighway(true);
                        highway.push(currCell);
                    } else {
                        restartHighway(highway);
                        hasCollision = true;
                        break;
                    }
                }
                if (!hasCollision && extendHighway(new Coords(GRID_COLUMNS - HIGHWAY_CHUNK_SIZE, randStartPointRow), Direction.RIGHT, highway)) {
                    builtHighways++;
                }
            } else if (direction == Direction.DOWN.getValue()) {
                for (int y = GRID_ROWS - 1; y > GRID_ROWS - HIGHWAY_CHUNK_SIZE - 1; y--) {
                    Cell currCell = grid[y][randStartPointColumn];
                    if (!currCell.isHighway()) {
                        currCell.setIsHighway(true);
                        highway.push(currCell);
                    } else {
                        restartHighway(highway);
                        hasCollision = true;
                        break;
                    }
                }
                if (!hasCollision && extendHighway(new Coords(randStartPointColumn, GRID_ROWS - HIGHWAY_CHUNK_SIZE), Direction.DOWN, highway)) {
                    builtHighways++;
                }
            } else if (direction == Direction.LEFT.getValue()) {
                for (int x = 0; x < HIGHWAY_CHUNK_SIZE; x++) {
                    Cell currCell = grid[randStartPointRow][x];
                    if (!currCell.isHighway()) {
                        currCell.setIsHighway(true);
                        highway.push(currCell);
                    } else {
                        restartHighway(highway);
                        hasCollision = true;
                        break;
                    }
                }
                if (!hasCollision && extendHighway(new Coords(HIGHWAY_CHUNK_SIZE - 1, randStartPointRow), Direction.LEFT, highway)) {
                    builtHighways++;
                }
            }
        }
    }

    private boolean extendHighway(Coords coords, Direction prevDirection, Stack<Cell> highway) {
        int x = coords.getColumn();
        int y = coords.getRow();
        if (!highway.isEmpty()) {
            Cell currentCell = highway.pop();
            currentCell.setIsHighway(false);
        }
        Direction d = Direction.randomDirectionExcluding(prevDirection);
        if (d == Direction.UP) {
            for (y = coords.getRow(); y > coords.getRow() - HIGHWAY_CHUNK_SIZE; y--) {
                if (y >= 0 && grid[y][x].isHighway()) {
                    restartHighway(highway);
                    return false;
                } else if (y >= 0) {
                    grid[y][x].setIsHighway(true);
                    highway.push(grid[y][x]);
                } else {
                    if (highway.size() < 100) {
                        restartHighway(highway);
                        return false;
                    } else {
                        return true;
                    }
                }
            }
            if (y >= 0 && grid[y][x].isHighway()) {
                restartHighway(highway);
                return false;
            }
            y++;
            return extendHighway(new Coords(x, y), Direction.DOWN, highway);
        } else if (d == Direction.RIGHT) {
            for (x = coords.getColumn(); x < coords.getColumn() + HIGHWAY_CHUNK_SIZE; x++) {
                if (x < GRID_COLUMNS && grid[y][x].isHighway()) {
                    restartHighway(highway);
                    return false;
                } else if (x < GRID_COLUMNS) {
                    grid[y][x].setIsHighway(true);
                    highway.push(grid[y][x]);
                } else {
                    if (highway.size() < 100) {
                        restartHighway(highway);
                        return false;
                    } else {
                        return true;
                    }
                }
            }
            if (x < GRID_COLUMNS && grid[y][x].isHighway()) {
                restartHighway(highway);
                return false;
            }
            x--;
            return extendHighway(new Coords(x, y), Direction.LEFT, highway);
        } else if (d == Direction.DOWN) {
            for (y = coords.getRow(); y < coords.getRow() + HIGHWAY_CHUNK_SIZE; y++) {
                if (y < GRID_ROWS && grid[y][x].isHighway()) {
                    restartHighway(highway);
                    return false;
                } else if (y < GRID_ROWS) {
                    grid[y][x].setIsHighway(true);
                    highway.push(grid[y][x]);
                } else {
                    if (highway.size() < 100) {
                        restartHighway(highway);
                        return false;
                    } else {
                        return true;
                    }
                }
            }
            if (y < GRID_ROWS && grid[y][x].isHighway()) {
                restartHighway(highway);
                return false;
            }
            y--;
            return extendHighway(new Coords(x, y), Direction.UP, highway);
        } else if (d == Direction.LEFT) {
            for (x = coords.getColumn(); x > coords.getColumn() - HIGHWAY_CHUNK_SIZE; x--) {
                if (x >= 0 && grid[y][x].isHighway()) {
                    restartHighway(highway);
                    return false;
                } else if (x >= 0) {
                    grid[y][x].setIsHighway(true);
                    highway.push(grid[y][x]);
                } else {
                    if (highway.size() < 100) {
                        restartHighway(highway);
                        return false;
                    } else {
                        return true;
                    }
                }
            }
            if (x >= 0 && grid[y][x].isHighway()) {
                restartHighway(highway);
                return false;
            }
            x++;
            return extendHighway(new Coords(x, y), Direction.RIGHT, highway);
        }
        return true;
    }

    private void restartHighway(Stack<Cell> highway) {
        while (!highway.empty()) {
            Cell cell = highway.pop();
            cell.setIsHighway(false);
        }
    }

    void generateGridDescriptor(String name) {
        GridDescriptorBuilder gdb = new GridDescriptorBuilder(grid, hardToTraverseCenters, startColumn, startRow, endRow, endColumn);
        gdb.generateFile(name);
    }

    void generateGridFromFile(File file) {
        try {
            Scanner myReader = new Scanner(file);
            // scans END coordinates
            String[] endCoords = myReader.nextLine().split(" ");
            this.endColumn = Integer.parseInt(endCoords[0]);
            this.endRow = Integer.parseInt(endCoords[1]);

            // scans START coordinates
            String[] startCoords = myReader.nextLine().split(" ");
            this.startColumn = Integer.parseInt(startCoords[0]);
            this.startRow = Integer.parseInt(startCoords[1]);

            //for debug
            //System.out.println("sc: " + startColumn + "  sr: " + startRow);
            //System.out.println("ec: " + endColumn + "  er: " + endRow);
            // scans coordinates of centers of hard to traverse areas
            for (int i = 0; i < Constants.NUM_OF_HTT_AREAS; i++) {
                String[] httCellCoords = myReader.nextLine().split(" ");
                int column = Integer.parseInt(httCellCoords[0]);
                int row = Integer.parseInt(httCellCoords[1]);
                Coords coord = new Coords(column, row);
                this.hardToTraverseCenters.add(coord);

                //for debug
                //System.out.println("HTT added: " + coord.getColumn() + " " + coord.getRow());
            }

            // scans all the grid representation
            // generates grid
            this.grid = new Cell[GRID_ROWS][GRID_COLUMNS];
            for (int row = 0; row < GRID_ROWS; row++) {
                String[] gridRowLine = myReader.nextLine().split(" ");
                for (int column = 0; column < GRID_COLUMNS; column++) {
                    if (".".equals(gridRowLine[column])) {
                        grid[row][column] = new RegularUnblockedCell(new Coords(column, row), false);
                    } else if ("0".equals(gridRowLine[column])) {
                        grid[row][column] = new BlockedCell(new Coords(column, row));
                    } else if ("2".equals(gridRowLine[column])) {
                        grid[row][column] = new HardToTraverseCell(new Coords(column, row), false);
                    } else if ("a".equals(gridRowLine[column])) {
                        grid[row][column] = new RegularUnblockedCell(new Coords(column, row), true);
                    } else if ("b".equals(gridRowLine[column])) {
                        grid[row][column] = new HardToTraverseCell(new Coords(column, row), true);
                    }
                }
            }

            goal = grid[this.endRow][this.endColumn];
            start = grid[this.startRow][this.startColumn];

            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public List<Cell> getNeighbors(Cell s) {
        int x = s.getCoordinates().getColumn();
        int y = s.getCoordinates().getRow();
        List<Cell> neighbors = new ArrayList<>();
        if(y > 0 && x >= 0 && x < GRID_COLUMNS) {
            neighbors.add(grid[y-1][x]);
        }
        if(y > 0 && x > 0) {
            neighbors.add(grid[y-1][x-1]);
        }
        if(y > 0 && x < GRID_COLUMNS - 1) {
            neighbors.add(grid[y-1][x+1]);
        }
        if(y < GRID_ROWS - 1 && x >= 0 && x < GRID_COLUMNS) {
            neighbors.add(grid[y+1][x]);
        }
        if(y < GRID_ROWS - 1 && x > 0) {
            neighbors.add(grid[y+1][x-1]);
        }
        if(y < GRID_ROWS - 1 && x < GRID_COLUMNS - 1) {
            neighbors.add(grid[y+1][x+1]);
        }
        if(x > 0 && y > 0 && y < GRID_ROWS) {
            neighbors.add(grid[y][x-1]);
        }
        if(x < GRID_COLUMNS - 1 && y > 0 && y < GRID_ROWS) {
            neighbors.add(grid[y][x+1]);
        }
        
        neighbors.remove(s.getCellParent());
        return neighbors.stream()
                .filter(c -> c.getCellType() != CellType.BLOCKED)
                .collect(Collectors.toList());
    }

    public Cell[][] getGrid() {
        return this.grid;
    }

    public Cell getStartCell() {
        return this.start;
    }

    public Cell getGoalCell() {
        return this.goal;
    }

    public void setStartCell(Cell start){
        this.start = start;
    }

    public void setGoalCell(Cell goal){
        this.goal = goal;
    }

    public static Grid copyGrid(Grid oldGrid) {
        Grid newGrid = new Grid(new Cell[GRID_ROWS][GRID_COLUMNS]);
        Cell[][] gridFrom = oldGrid.getGrid();
        Cell[][] gridTo = newGrid.getGrid();

        for (int i = 0; i < GRID_ROWS; i++) {
            for (int j = 0; j < GRID_COLUMNS; j++) {
                gridTo[i][j] = gridFrom[i][j].getCopy();
            }
        }

        Coords oldStartCoords = oldGrid.getStartCell().getCoordinates();
        Coords oldGoalCoords = oldGrid.getGoalCell().getCoordinates();
        newGrid.setStartCell(gridTo[oldStartCoords.getRow()][oldStartCoords.getColumn()]);
        newGrid.setGoalCell(gridTo[oldGoalCoords.getRow()][oldGoalCoords.getColumn()]);
        return newGrid;
    }
}
