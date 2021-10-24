package heuristicsearch.algorithms;

import heuristicsearch.*;

import java.util.function.BiFunction;

import static heuristicsearch.algorithms.HeuristicAlgorithms.Heuristic;

/**
 * Weighted A* Search provides a constant that determines how much value the heuristic adds to the search. A higher
 * weight would indicate the heuristic should be valued more during the search while a lower weight would indicate
 * otherwise. A weight of one would make the search identical the original A* search.
 */
public class WeightedAStarSearch extends Search {

    public static final double WEIGHT_1 = 1.10;
    public static final double WEIGHT_2 = 2.40;

    public static HeuristicResults heuristicResultsWithFirstWeight = new HeuristicResults();
    public static HeuristicResults heuristicResultsWithSecondWeight = new HeuristicResults();

    public WeightedAStarSearch(Grid grid, double weight, BiFunction<Cell, Cell, Double> heuristicFunction, Heuristic heuristic) {
        super(grid, weight, heuristicFunction, heuristic);
    }

    public double calculateHValue(Cell s) {
        return (weight * heuristicFunction.apply(s, goal));
    }

    public void updateResult(HeuristicResult result) {
        if(weight == WEIGHT_1) {
            updateResultForFirstWeight(result);
        } else if(weight == WEIGHT_2) {
            updateResultForSecondWeight(result);
        } else {
            System.out.printf("Unable to store heuristic results for the current weight %.2f%n", weight);
        }
    }

    public void updateResultForFirstWeight(HeuristicResult result) {
        switch(heuristic) {
            case DIAGONAL:
                heuristicResultsWithFirstWeight.diagonalResult.addResult(result);
                break;
            case EUCLIDEAN:
                heuristicResultsWithFirstWeight.euclideanResult.addResult(result);
                break;
            case MANHATTAN:
                heuristicResultsWithFirstWeight.manhattanResult.addResult(result);
                break;
            case EUCLIDEAN_SQUARED:
                heuristicResultsWithFirstWeight.euclideanSquaredResult.addResult(result);
                break;
            case CHEBYSHEV:
                heuristicResultsWithFirstWeight.chebyshevResult.addResult(result);
                break;
        }
    }

    public void updateResultForSecondWeight(HeuristicResult result) {
        switch(heuristic) {
            case DIAGONAL:
                heuristicResultsWithSecondWeight.diagonalResult.addResult(result);
                break;
            case EUCLIDEAN:
                heuristicResultsWithSecondWeight.euclideanResult.addResult(result);
                break;
            case MANHATTAN:
                heuristicResultsWithSecondWeight.manhattanResult.addResult(result);
                break;
            case EUCLIDEAN_SQUARED:
                heuristicResultsWithSecondWeight.euclideanSquaredResult.addResult(result);
                break;
            case CHEBYSHEV:
                heuristicResultsWithSecondWeight.chebyshevResult.addResult(result);
                break;
        }
    }
}
