package heuristicsearch.algorithms;

import heuristicsearch.Cell;
import heuristicsearch.Coords;

/**
 * These heuristics have been adjusted from their original algorithms to include the minimal possible cost to travel
 * given the current grid specifications.
 */
public class HeuristicAlgorithms {

    public enum Heuristic { DIAGONAL, EUCLIDEAN, MANHATTAN, EUCLIDEAN_SQUARED, CHEBYSHEV }

    /**
     * Diagonal Distance with adjusted D1 and D2 values
     */
    public static double diagonalHeuristic(Cell s, Cell goal) {
        Coords sCoords = s.getCoordinates();
        Coords goalCoords = goal.getCoordinates();
        double dx = Math.abs(sCoords.getColumn() - goalCoords.getColumn());
        double dy = Math.abs(sCoords.getRow() - goalCoords.getRow());
        return (.25 * (dx + dy) + ((.25 * Math.sqrt(2)) - 2 * .25) * Math.min(dx, dy));
    }

    /**
     * Adjusted Euclidean Distance
     */
     public static double euclideanHeuristic(Cell s, Cell goal) {
         Coords sCoords = s.getCoordinates();
         Coords goalCoords = goal.getCoordinates();
         double dx = Math.abs(sCoords.getColumn() - goalCoords.getColumn());
         double dy = Math.abs(sCoords.getRow() - goalCoords.getRow());
         return .25*Math.sqrt(dx*dx+dy*dy);
     }

    /**
     * Adjusted Manhattan Distance
     */
    public static double manhattanHeuristic(Cell s, Cell goal) {
        Coords sCoords = s.getCoordinates();
        Coords goalCoords = goal.getCoordinates();
        double dx = Math.abs(sCoords.getColumn() - goalCoords.getColumn());
        double dy = Math.abs(sCoords.getRow() - goalCoords.getRow());
        return .25 *(dx+dy);
    }

    /**
     * Adjusted Euclidean Distance Squared
     */
    public static double euclideanSquaredHeuristic(Cell s, Cell goal) {
        Coords sCoords = s.getCoordinates();
        Coords goalCoords = goal.getCoordinates();
        double dx = Math.abs(sCoords.getColumn() - goalCoords.getColumn());
        double dy = Math.abs(sCoords.getRow() - goalCoords.getRow());
        return .25*(dx*dx+dy*dy);
    }

    /**
     * Adjusted Chebyshev Distance
     */
    public static double chebyshevHeuristic(Cell s, Cell goal) {
        Coords sCoords = s.getCoordinates();
        Coords goalCoords = goal.getCoordinates();
        double dx = Math.abs(sCoords.getColumn() - goalCoords.getColumn());
        double dy = Math.abs(sCoords.getRow() - goalCoords.getRow());
        return (.25 * (dx + dy) + ((.25 ) - 2 * .25) * Math.min(dx, dy));
    }
}
