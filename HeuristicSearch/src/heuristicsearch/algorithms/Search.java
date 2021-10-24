package heuristicsearch.algorithms;

import heuristicsearch.*;

import java.util.*;
import java.util.function.BiFunction;

import static heuristicsearch.Constants.GRID_COLUMNS;
import static heuristicsearch.Constants.GRID_ROWS;
import static heuristicsearch.algorithms.HeuristicAlgorithms.Heuristic;

public abstract class Search {

    protected Grid grid;
    protected Cell start;
    protected Cell goal;
    protected double weight;
    protected long executionTime;
    protected long usedMemory;
    protected double pathCost;
    protected double numberOfCellsExpanded;
    protected BiFunction<Cell, Cell, Double> heuristicFunction;
    protected Heuristic heuristic;
    protected List<BiFunction<Cell, Cell, Double>> heuristicFunctions;
    protected List<Cell> startCells;
    protected List<Cell> goalCells;
    public static double w1 = 1.0; // value heuristics over g(s)
    public static double w2 = 1.0; // value admissible heuristics over admissible

    public Search(Grid grid, double weight, BiFunction<Cell, Cell, Double> heuristicFunction, Heuristic heuristic) {
        this.grid = Grid.copyGrid(grid);
        this.weight = weight;
        this.heuristicFunction = heuristicFunction;
        this.heuristic = heuristic;

        Coords oldStartCoords = grid.getStartCell().getCoordinates();
        Coords oldGoalCoords = grid.getGoalCell().getCoordinates();
        start = this.grid.getGrid()[oldStartCoords.getRow()][oldStartCoords.getColumn()];
        goal = this.grid.getGrid()[oldGoalCoords.getRow()][oldGoalCoords.getColumn()];
    }

    public Search(
            Grid grid,
            double weight,
            List<BiFunction<Cell, Cell, Double>> heuristicFunctions,
            BiFunction<Cell, Cell, Double> heuristicFunction
    ) {
        this.grid = Grid.copyGrid(grid);
        this.weight = weight;
        this.heuristicFunctions = heuristicFunctions;
        this.heuristicFunction = heuristicFunction;

        Coords oldStartCoords = grid.getStartCell().getCoordinates();
        Coords oldGoalCoords = grid.getGoalCell().getCoordinates();
        start = this.grid.getGrid()[oldStartCoords.getRow()][oldStartCoords.getColumn()];
        goal = this.grid.getGrid()[oldGoalCoords.getRow()][oldGoalCoords.getColumn()];
    }

    public HeuristicResult run() {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        long usedMemoryBefore = runtime.totalMemory() - runtime.freeMemory();
        long startTime = System.currentTimeMillis();

        PriorityQueue<Cell> openList = new PriorityQueue<>(GRID_COLUMNS * GRID_ROWS, (c1, c2) -> {
            if (c1.getFValue() != c2.getFValue()) {
                return Double.compare(c1.getFValue(), c2.getFValue());
            } else {
                return Double.compare(c1.getGValue(), c2.getGValue());
            }
        });
        Set<Cell> visited = new HashSet<>();

        start.setGValue(0);
        start.setParent(start);
        start.setFValue(0);
        openList.offer(start);
        numberOfCellsExpanded = 0;
        while (!openList.isEmpty()) {
            Cell currCell = openList.poll();
            numberOfCellsExpanded++;
            if (currCell == goal) {
                executionTime = System.currentTimeMillis() - startTime;
                usedMemory = (runtime.totalMemory() - runtime.freeMemory()) - usedMemoryBefore;
                pathCost = 0;
                while (currCell != start) {
                    if (currCell != goal) {
                        currCell.setSymbol('X');
                    }
                    pathCost += calculateCValue(currCell, currCell.getCellParent());
                    currCell.setIsPath(true);
                    currCell = currCell.getCellParent();
                }
                return new HeuristicResult(executionTime, pathCost, numberOfCellsExpanded, usedMemory / 1000);
            }
            visited.add(currCell);
            for (Cell neighbor : grid.getNeighbors(currCell)) {
                if (!visited.contains(neighbor)) {
                    if (!openList.contains(neighbor)) {
                        neighbor.setGValue(Double.MAX_VALUE);
                        neighbor.setParent(null);
                    }
                    updateVertex(currCell, neighbor, openList);
                }
            }
        }
        return null;
    }

    public void updateVertex(Cell s, Cell s0, PriorityQueue<Cell> openList) {
        if (s.getGValue() + calculateCValue(s, s0) < s0.getGValue()) {
            s0.setGValue(s.getGValue() + calculateCValue(s, s0));
            s0.setParent(s);

            openList.remove(s0);
            s0.setHValue(calculateHValue(s0));
            s0.setFValue(s0.getGValue() + s0.getHValue());
            openList.add(s0);
        }
    }

    /**
     * TODO: Move runSeq() and expandState() to its own SequentialAStarSearch class
     */
    public HeuristicResult runSeq() {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        long usedMemoryBefore = runtime.totalMemory() - runtime.freeMemory();
        long startTime = System.currentTimeMillis();

        List<SearchPriorityQueue> openList = new ArrayList<>();
        List<Set<Cell>> visited = new ArrayList<>();
        int numberOfFunctions = heuristicFunctions.size();
        this.startCells = new ArrayList<>();
        this.goalCells = new ArrayList<>();

        // Inits openList and visited for each one of the inadmissible heuristics + one for the admissible hewuristic.
        // Admissible lists are at index 0.
        for (int i = 0; i <= numberOfFunctions; i++) {
            this.startCells.add(start);
            this.goalCells.add(goal);

            openList.add(new SearchPriorityQueue(i));
            visited.add(new HashSet<>());
            Cell startCell = startCells.get(i);
            Cell goalCell = goalCells.get(i);

            startCell.setGValueForHeuristic(0, i);
            goalCell.setGValueForHeuristic(Double.MAX_VALUE, i);
            startCell.setParentForHeuristic(null, i);
            goalCell.setParentForHeuristic(null, i);
            startCell.setFValueForHeuristic(getFValue(startCell, i), i);

            openList.get(i).offer(startCell);
        }
        numberOfCellsExpanded = 0;
        int numberOfHeuristicCellsExpanded = 0;
        while (openList.get(0).getMinKey(0) < Double.MAX_VALUE) {
            for (int i = 1; i <= numberOfFunctions; i++) {
                double minKey0 = openList.get(0).isEmpty() ? Double.MAX_VALUE : openList.get(0).getMinKey(0);
                double minKeyi = openList.get(i).isEmpty() ? Double.MAX_VALUE : openList.get(i).getMinKey(i);
                if (minKeyi <= w2 * minKey0) {
                    if (goal.getGValueForHeuristic(i) <= minKeyi) {
                        if (goal.getGValueForHeuristic(i) < Double.MAX_VALUE) {
                            return hCollectResults(startTime, runtime, usedMemoryBefore, i, goal);
                        }
                    } else {
                        numberOfHeuristicCellsExpanded++;
                        Cell currICell = openList.get(i).getTop();
                        expandState(currICell, i, openList, visited);
                        visited.get(i).add(currICell);
                    }
                } else {
                    if (goal.getGValueForHeuristic(0) <= minKey0) {
                        if (goal.getGValueForHeuristic(0) < Double.MAX_VALUE) {
                            return hCollectResults(startTime, runtime, usedMemoryBefore, 0, goal);
                        }
                    } else {
                        numberOfCellsExpanded++;
                        Cell curr0Cell = openList.get(0).getTop();
                        expandState(curr0Cell, 0, openList, visited);
                        visited.get(0).add(curr0Cell);
                    }
                }
            }
        }
        return null;
    }

    private HeuristicResult hCollectResults(long startTime, Runtime runtime, long usedMemoryBefore, int i, Cell goal) {
        executionTime = System.currentTimeMillis() - startTime;
        usedMemory = (runtime.totalMemory() - runtime.freeMemory()) - usedMemoryBefore;
        pathCost = 0;
        Cell currCell = goal;
        while (currCell != startCells.get(i)) {
            // Temporary symbol change to visualize path
            if (currCell != goal) {
                currCell.setSymbol('X');
            }
            pathCost += calculateCValue(currCell, currCell.getParentForHeuristic(i));
            currCell.setIsPath(true);
            currCell = currCell.getParentForHeuristic(i);
        }
        return new HeuristicResult(executionTime, pathCost, numberOfCellsExpanded, usedMemory / 1000);
    }

    private void expandState(Cell s, int i, List<SearchPriorityQueue> openList, List<Set<Cell>> visited) {
        for (Cell neighbor : grid.getNeighbors(s)) {
            if (!visited.get(i).contains(neighbor) && !openList.get(i).contains(neighbor)) {
                neighbor.setGValueForHeuristic(Double.MAX_VALUE, i);
                neighbor.setParentForHeuristic(null, i);
            }

            if (neighbor.getGValueForHeuristic(i) > s.getGValueForHeuristic(i) + calculateCValue(s, neighbor)) {
                neighbor.setGValueForHeuristic(s.getGValueForHeuristic(i) + calculateCValue(s, neighbor), i);
                neighbor.setParentForHeuristic(s, i);
                if (!visited.get(i).contains(neighbor)) {
                    neighbor.setFValueForHeuristic(getFValue(neighbor, i), i);
                    openList.get(i).offer(neighbor);
                }
            }
        }
    }

    private double getFValue(Cell s, int i) {
        // If i == 0, apply yjr admissible heuristic. Otherwise, apply an inadmissible heuristic from the list of BiFunctions.
        if(i == 0) {
            return s.getGValueForHeuristic(i) + w1 * heuristicFunction.apply(s, goal);
        } else {
            return s.getGValueForHeuristic(i) + w1 * heuristicFunctions.get(i - 1).apply(s, goal);
        }
    }

    /**
     * Update results for the current heuristic in the static field of the current search e.g. A*, Weighted A*, Uniform
     */
    public abstract void updateResult(HeuristicResult result);

    /**
     * The heuristic used to calculate h(s) is set in {@link #heuristicFunction}
     */
    public abstract double calculateHValue(Cell s);

    /**
     * Calculates the cost between traveling between two cells.
     */
    public double calculateCValue(Cell s, Cell s0) {
        double cost = 1.0;
        if (s.isDiagonal(s0)) {
            cost = Math.sqrt(2);
        }
        if (s.getCellType() == CellType.HARD_TO_TRAVERSE && s0.getCellType() == CellType.HARD_TO_TRAVERSE) {
            cost *= 2;
        } else if (s.getCellType() == CellType.REGULAR_UNBLOCKED && s0.getCellType() == CellType.HARD_TO_TRAVERSE
                || s.getCellType() == CellType.HARD_TO_TRAVERSE && s0.getCellType() == CellType.REGULAR_UNBLOCKED) {
            cost *= 1.5;
        }
        if (s.isHighway() && s0.isHighway()) {
            cost *= .25;
        }
        return cost;
    }

    public Cell[][] getGrid() {
        return this.grid.getGrid();
    }

    public Cell getStart() {
        return this.start;
    }

    public Cell getGoal() {
        return this.goal;
    }

    public long getExecutionTime() {
        return this.executionTime;
    }

    public double getPathCost() {
        return this.pathCost;
    }
}
