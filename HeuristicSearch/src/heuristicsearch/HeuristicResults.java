package heuristicsearch;

public class HeuristicResults {

    public HeuristicResult diagonalResult;
    public HeuristicResult euclideanResult;
    public HeuristicResult manhattanResult;
    public HeuristicResult euclideanSquaredResult;
    public HeuristicResult chebyshevResult;

    public HeuristicResults() {
        diagonalResult = new HeuristicResult();
        euclideanResult = new HeuristicResult();
        manhattanResult = new HeuristicResult();
        euclideanSquaredResult = new HeuristicResult();
        chebyshevResult = new HeuristicResult();
    }

    public void printResults() {
        System.out.println("  Manhattan Result: ");
        manhattanResult.printResult();
        System.out.println("  Diagonal Result: ");
        diagonalResult.printResult();
        System.out.println("  Euclidean Result: ");
        euclideanResult.printResult();
        System.out.println("  Euclidean Squared Result: ");
        euclideanSquaredResult.printResult();
        System.out.println("  Chebyshev Result: ");
        chebyshevResult.printResult();
    }

    /**
     * Uniform does not use a heuristic and h(s) always returns 0. Therefore only one result should be printed.
     */
    public void printUniformResults() {
        System.out.println("  No Heuristic Result: ");
        // Bad coding design, but the 5 heuristics applied to Uniform search are all equivalent because the heuristic function
        // is ignored and h(s) defaults to 0, so printing the result of any one of the heuristic results will show Uniform's
        // performance.
        manhattanResult.printResult();
    }
}
