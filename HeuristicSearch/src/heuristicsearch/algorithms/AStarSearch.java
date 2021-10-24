package heuristicsearch.algorithms;

import heuristicsearch.*;

import java.util.function.BiFunction;

import static heuristicsearch.algorithms.HeuristicAlgorithms.Heuristic;
import java.util.List;

public class AStarSearch extends Search {

    private static final double DEFAULT_WEIGHT = 1.0;
    public static HeuristicResults heuristicResultsForAStar = new HeuristicResults();
    public static HeuristicResults heuristicResultsForSequentialAStar = new HeuristicResults();

    public AStarSearch(Grid grid, BiFunction<Cell, Cell, Double> heuristicFunction, Heuristic heuristic) {
        super(grid, DEFAULT_WEIGHT, heuristicFunction, heuristic);
    }

    public AStarSearch(Grid grid, List<BiFunction<Cell, Cell, Double>> inadmissibleHeuristicFunctions, BiFunction<Cell, Cell, Double> admissibleFunction, Heuristic heuristic) {
        super(grid, DEFAULT_WEIGHT, inadmissibleHeuristicFunctions, admissibleFunction);
        // This is just so we can print out the results later.
        this.heuristic = heuristic;
    }

    public void updateResult(HeuristicResult result) {
        if(heuristicFunctions != null) {
            updateResultForSequentialAStar(result);
        } else {
            updateResultForAStar(result);
        }
    }

    /**
     * Diagonal with adjusted D1 and D2 values
     */
    public double calculateHValue(Cell s) {
        return (weight * heuristicFunction.apply(s, goal));
    }

    public void updateResultForAStar(HeuristicResult result) {
        switch (heuristic) {
            case DIAGONAL:
                heuristicResultsForAStar.diagonalResult.addResult(result);
                break;
            case EUCLIDEAN:
                heuristicResultsForAStar.euclideanResult.addResult(result);
                break;
            case MANHATTAN:
                heuristicResultsForAStar.manhattanResult.addResult(result);
                break;
            case EUCLIDEAN_SQUARED:
                heuristicResultsForAStar.euclideanSquaredResult.addResult(result);
                break;
            case CHEBYSHEV:
                heuristicResultsForAStar.chebyshevResult.addResult(result);
                break;
        }
    }

    public void updateResultForSequentialAStar(HeuristicResult result) {
        switch (heuristic) {
            case DIAGONAL:
                heuristicResultsForSequentialAStar.diagonalResult.addResult(result);
                break;
            case EUCLIDEAN:
                heuristicResultsForSequentialAStar.euclideanResult.addResult(result);
                break;
            case MANHATTAN:
                heuristicResultsForSequentialAStar.manhattanResult.addResult(result);
                break;
            case EUCLIDEAN_SQUARED:
                heuristicResultsForSequentialAStar.euclideanSquaredResult.addResult(result);
                break;
            case CHEBYSHEV:
                heuristicResultsForSequentialAStar.chebyshevResult.addResult(result);
                break;
        }
    }
}
