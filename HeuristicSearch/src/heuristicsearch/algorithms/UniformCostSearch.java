package heuristicsearch.algorithms;

import heuristicsearch.Cell;
import heuristicsearch.Grid;
import heuristicsearch.HeuristicResult;
import heuristicsearch.HeuristicResults;

import java.util.function.BiFunction;

import static heuristicsearch.algorithms.HeuristicAlgorithms.Heuristic;

/**
 * Uniform-cost search is A* search where h(s) always evaluates to 0. Essentially Dijkstra's algorithm.
 */
public class UniformCostSearch extends Search {

    public static HeuristicResults heuristicResults = new HeuristicResults();

    private static final double DEFAULT_WEIGHT = 1.0;

    public UniformCostSearch(Grid grid, BiFunction<Cell, Cell, Double> heuristicFunction, Heuristic heuristic) {
        super(grid, DEFAULT_WEIGHT, heuristicFunction, heuristic);
    }

    public double calculateHValue(Cell s) {
        return 0;
    }

    public void updateResult(HeuristicResult result) {
        switch(heuristic) {
            case DIAGONAL:
                heuristicResults.diagonalResult.addResult(result);
                break;
            case EUCLIDEAN:
                heuristicResults.euclideanResult.addResult(result);
                break;
            case MANHATTAN:
                heuristicResults.manhattanResult.addResult(result);
                break;
            case EUCLIDEAN_SQUARED:
                heuristicResults.euclideanSquaredResult.addResult(result);
                break;
            case CHEBYSHEV:
                heuristicResults.chebyshevResult.addResult(result);
                break;
        }
    }
}
