package heuristicsearch;

import heuristicsearch.algorithms.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.function.BiFunction;

import heuristicsearch.view.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import static heuristicsearch.algorithms.HeuristicAlgorithms.Heuristic;
import java.util.ArrayList;
import java.util.List;

public class HeuristicSearchMain extends Application {

    private static Grid grid;
    private static int launch;

    @Override
    public void start(Stage primaryStage) throws IOException {
        if(launch == 0) {
            run(HeuristicAlgorithms::manhattanHeuristic, Heuristic.MANHATTAN);
        } else if(launch == 1) {
            generateAndRunBenchmarks();
        } else {
            runSequential();
        }
        System.out.println("Finished - back to main.");
    }

    public void generateAndRunBenchmarks() {
        // Number of grids
        for (int i = 0; i < 5; i++) {
            grid = new Grid();
            grid.generateGrid(Constants.GRID_COLUMNS, Constants.GRID_ROWS);
            // Number of start/goal generations for each grid
            for (int j = 0; j < 10; j++) {
                grid.selectStartEnd();
                runHeuristic(grid, HeuristicAlgorithms::diagonalHeuristic, Heuristic.DIAGONAL);
                runHeuristic(grid, HeuristicAlgorithms::euclideanHeuristic, Heuristic.EUCLIDEAN);
                runHeuristic(grid, HeuristicAlgorithms::manhattanHeuristic, Heuristic.MANHATTAN);
                runHeuristic(grid, HeuristicAlgorithms::euclideanSquaredHeuristic, Heuristic.EUCLIDEAN_SQUARED);
                runHeuristic(grid, HeuristicAlgorithms::chebyshevHeuristic, Heuristic.CHEBYSHEV);
            }
        }

        System.out.println("Uniform Search Results");
        System.out.println("==========================");
        UniformCostSearch.heuristicResults.printUniformResults();

        System.out.println("A* Search Results");
        System.out.println("==========================");
        AStarSearch.heuristicResultsForAStar.printResults();

        System.out.printf("Weighted A* Search Results With Weight %.2f%n", WeightedAStarSearch.WEIGHT_1);
        System.out.println("==========================");
        WeightedAStarSearch.heuristicResultsWithFirstWeight.printResults();

        System.out.printf("Weighted A* Search Results With Weight %.2f%n", WeightedAStarSearch.WEIGHT_2);
        System.out.println("==========================");
        WeightedAStarSearch.heuristicResultsWithSecondWeight.printResults();

        System.out.printf("Sequential A* Search Results With W1=%.2f and W2=%.2f%n", Search.w1, Search.w2);
        System.out.println("==========================");
        AStarSearch.heuristicResultsForSequentialAStar.printResults();
    }

    public void runHeuristic(Grid grid, BiFunction<Cell, Cell, Double> heuristicFunction, Heuristic heuristic) {
        // Uniform should be used as a baseline since it always returns the shortest path.
        UniformCostSearch uniformCostSearch = new UniformCostSearch(Grid.copyGrid(grid), heuristicFunction, heuristic);
        HeuristicResult uniformResult = uniformCostSearch.run();
        uniformCostSearch.updateResult(uniformResult);

        AStarSearch aStarSearch = new AStarSearch(Grid.copyGrid(grid), heuristicFunction, heuristic);
        HeuristicResult aStarResult = aStarSearch.run();
        // Measure cost here is the difference from the shortest/optimal path
        aStarResult.cost -= uniformResult.cost;
        aStarSearch.updateResult(aStarResult);

        WeightedAStarSearch weightedAStarSearch1 = new WeightedAStarSearch(Grid.copyGrid(grid), WeightedAStarSearch.WEIGHT_1, heuristicFunction, heuristic);
        HeuristicResult weightedAStarResult1 = weightedAStarSearch1.run();
        weightedAStarResult1.cost -= uniformResult.cost;
        weightedAStarSearch1.updateResult(weightedAStarResult1);

        WeightedAStarSearch weightedAStarSearch2 = new WeightedAStarSearch(Grid.copyGrid(grid), WeightedAStarSearch.WEIGHT_2, heuristicFunction, heuristic);
        HeuristicResult weightedAStarResult2 = weightedAStarSearch2.run();
        weightedAStarResult2.cost -= uniformResult.cost;
        weightedAStarSearch2.updateResult(weightedAStarResult2);

        // list of inadmissible heuristics
        List<BiFunction<Cell, Cell, Double>> inadmissibleHeuristicFunctions = new ArrayList();
        inadmissibleHeuristicFunctions.add(HeuristicAlgorithms::euclideanHeuristic);
        inadmissibleHeuristicFunctions.add(HeuristicAlgorithms::manhattanHeuristic);
        inadmissibleHeuristicFunctions.add(HeuristicAlgorithms::euclideanSquaredHeuristic);
        inadmissibleHeuristicFunctions.add(HeuristicAlgorithms::chebyshevHeuristic);

        // The heuristic is not actually diagonal. This Sequential A* search. This is temporary to quickly check values
        AStarSearch sequentialAStarSearch = new AStarSearch(Grid.copyGrid(grid), inadmissibleHeuristicFunctions,  HeuristicAlgorithms::diagonalHeuristic, Heuristic.DIAGONAL);
        HeuristicResult sequentialAStarResult = sequentialAStarSearch.runSeq();
        // Measure cost here is the difference from the shortest/optimal path
        sequentialAStarResult.cost -= uniformResult.cost;
        sequentialAStarSearch.updateResultForSequentialAStar(sequentialAStarResult);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            launch(args);
        } else if(args.length == 2 && args[0].equals("demo")) {
            launch = Integer.parseInt(args[1]);
            launch(args);
        } else if (args.length == 1) {
            File f = new File(args[0]);
            runWithSingleFile(f);
            launch(args);
        } else {
            displayMenu(args);
        }
    }

    private void runSequential() throws IOException {
        Grid grid = HeuristicSearchMain.grid;
        if (grid == null) {
            grid = new Grid();
            grid.generateGrid(Constants.GRID_COLUMNS, Constants.GRID_ROWS);
            grid.generateGridDescriptor("grid_descriptor"); //file name
        }

        // list of inadmissible heuristics
        List<BiFunction<Cell, Cell, Double>> inadmissibleHeuristicFunctions = new ArrayList();
        inadmissibleHeuristicFunctions.add(HeuristicAlgorithms::euclideanHeuristic);
        inadmissibleHeuristicFunctions.add(HeuristicAlgorithms::manhattanHeuristic);
        inadmissibleHeuristicFunctions.add(HeuristicAlgorithms::euclideanSquaredHeuristic);
        inadmissibleHeuristicFunctions.add(HeuristicAlgorithms::chebyshevHeuristic);

        //the admissible heuristic and it's name
        BiFunction<Cell, Cell, Double> admissibleFunction = HeuristicAlgorithms::manhattanHeuristic;
        Heuristic admissible = Heuristic.MANHATTAN;

        UniformCostSearch uniformCostSearch = new UniformCostSearch(Grid.copyGrid(grid), admissibleFunction, admissible);
        HeuristicResult uniformResult = uniformCostSearch.run();
        uniformCostSearch.updateResult(uniformResult);

        AStarSearch aStarSearch = new AStarSearch(Grid.copyGrid(grid), admissibleFunction, Heuristic.MANHATTAN);
        HeuristicResult aStarResult = aStarSearch.run();
        // Measure cost here is the difference from the shortest/optimal path
        aStarResult.cost -= uniformResult.cost;
        aStarSearch.updateResult(aStarResult);

        // The heuristic is not actually diagonal. This Sequential A* search. This is temporary to quickly check values
        AStarSearch sequentialAStarSearch = new AStarSearch(Grid.copyGrid(grid), inadmissibleHeuristicFunctions, admissibleFunction, Heuristic.MANHATTAN);
        HeuristicResult sequentialAStarResult = sequentialAStarSearch.runSeq();
        // Measure cost here is the difference from the shortest/optimal path
        sequentialAStarResult.cost -= uniformResult.cost;
        sequentialAStarSearch.updateResultForSequentialAStar(sequentialAStarResult);

        System.out.println("Uniform Search Results");
        System.out.println("==========================");
        UniformCostSearch.heuristicResults.printUniformResults();

        System.out.println("A* Search Results");
        System.out.println("==========================");
        AStarSearch.heuristicResultsForAStar.printResults();

        System.out.println("Sequential A* Search Results");
        System.out.println("==========================");
        AStarSearch.heuristicResultsForSequentialAStar.printResults();

        displaySearch(uniformCostSearch, "Uniform Search");
        displaySearch(aStarSearch, "A* search");
        displaySearch(sequentialAStarSearch, "Sequential A* search");
    }

    private void run(BiFunction<Cell, Cell, Double> heuristicFunction, Heuristic heuristic) throws IOException {
        Grid grid = HeuristicSearchMain.grid;
        if (grid == null) {
            grid = new Grid();
            grid.generateGrid(Constants.GRID_COLUMNS, Constants.GRID_ROWS);
            grid.generateGridDescriptor("grid_descriptor"); //file name
        }

        // Uniform should be used as a baseline since it always returns the shortest path.
        UniformCostSearch uniformCostSearch = new UniformCostSearch(Grid.copyGrid(grid), heuristicFunction, heuristic);
        HeuristicResult uniformResult = uniformCostSearch.run();
        uniformCostSearch.updateResult(uniformResult);

        AStarSearch aStarSearch = new AStarSearch(Grid.copyGrid(grid), heuristicFunction, heuristic);
        HeuristicResult aStarResult = aStarSearch.run();
        // Measure cost here is the difference from the shortest/optimal path
        aStarResult.cost -= uniformResult.cost;
        aStarSearch.updateResult(aStarResult);

        WeightedAStarSearch weightedAStarSearch1 = new WeightedAStarSearch(Grid.copyGrid(grid), WeightedAStarSearch.WEIGHT_1, heuristicFunction, heuristic);
        HeuristicResult weightedAStarResult1 = weightedAStarSearch1.run();
        // Measure cost here is the difference from the shortest/optimal path
        weightedAStarResult1.cost -= uniformResult.cost;
        weightedAStarSearch1.updateResult(weightedAStarResult1);

        WeightedAStarSearch weightedAStarSearch2 = new WeightedAStarSearch(Grid.copyGrid(grid), WeightedAStarSearch.WEIGHT_2, heuristicFunction, heuristic);
        HeuristicResult weightedAStarResult2 = weightedAStarSearch2.run();
        // Measure cost here is the difference from the shortest/optimal path
        weightedAStarResult2.cost -= uniformResult.cost;
        weightedAStarSearch2.updateResult(weightedAStarResult2);

        System.out.println("Uniform Search Results");
        System.out.println("==========================");
        UniformCostSearch.heuristicResults.printUniformResults();

        System.out.println("A* Search Results");
        System.out.println("==========================");
        AStarSearch.heuristicResultsForAStar.printResults();

        System.out.printf("Weighted A* Search Results With Weight %.2f%n", WeightedAStarSearch.WEIGHT_1);
        System.out.println("==========================");
        WeightedAStarSearch.heuristicResultsWithFirstWeight.printResults();

        System.out.printf("Weighted A* Search Results With Weight %.2f%n", WeightedAStarSearch.WEIGHT_2);
        System.out.println("==========================");
        WeightedAStarSearch.heuristicResultsWithSecondWeight.printResults();

        displaySearch(uniformCostSearch, "Uniform Search");
        displaySearch(aStarSearch, "A* search");
        displaySearch(weightedAStarSearch1, "Weighted A* search with Weight " + WeightedAStarSearch.WEIGHT_1);
        displaySearch(weightedAStarSearch2, "Weighted A* search with Weight " + WeightedAStarSearch.WEIGHT_2);
    }

    private void displaySearch(Search search, String name) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        Stage mainStage = new Stage();
        loader.setLocation(getClass().getResource("./view/main.fxml"));
        AnchorPane root = (AnchorPane) loader.load();
        MainController mainController = loader.getController();
        mainController.start(search.getGrid(), search, name);

        loader.setController(mainController);
        mainStage.setScene(new Scene(root));
        mainStage.setResizable(false);
        mainStage.setTitle(name);
        mainStage.show();
    }

    private static void runWithSingleFile(File f) {
        Grid grid = new Grid();
        if (f.canRead()) {
            try {
                int lines = 0;
                Scanner myReader = new Scanner(f);
                while (myReader.hasNextLine()) {
                    lines++;
                    myReader.nextLine();
                }
                myReader.close();
                System.out.println("number of lines in input file: " + lines);
                if (lines < Constants.EXPECTED_FILE_LENGTH) {
                    System.out.println("File too short");
                } else if (lines > Constants.EXPECTED_FILE_LENGTH) {
                    System.out.println("File too long");
                } else {
                    grid.generateGridFromFile(f);
                    HeuristicSearchMain.grid = grid;
                }

            } catch (FileNotFoundException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        } else {
            System.out.println("File is not readable, or doesn't exist!");
        }
    }

    private static void runWithArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            File f = new File(args[i]);
            System.out.println("\n\nGrid " + i + " generated from file: " + f.getName());
            runWithSingleFile(f);
            //Print stats instead of showing ui
            //launch(args);
        }
    }

    private static void displayMenu(String[] args) {
        System.out.println("received " + args.length + " files.\nRun program for each one of them?");
        Scanner scanner = new Scanner(System.in);
        String response = "";
        while (!"Y".equals(response) && !"N".equals(response)) {
            System.out.println("Y/N");
            response = scanner.nextLine().toUpperCase();
        }
        if ("N".equals(response)) {
            System.out.println("EXITING");
            System.exit(1);
        }

        if ("Y".equals(response)) {
            runWithArgs(args);
        }
    }
}
