package heuristicsearch.view;

import heuristicsearch.Cell;
import heuristicsearch.CellType;
import heuristicsearch.Constants;
import heuristicsearch.algorithms.Search;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.io.Serializable;

public class MainController implements Serializable {

    @FXML transient GridPane gridPane;

    @FXML transient Label pathCostLabel;
    @FXML transient Label executionTimeLabel;
    @FXML transient Label fValueLabel;
    @FXML transient Label hValueLabel;
    @FXML transient Label gValueLabel;
    @FXML transient Label cellTypeLabel;
    @FXML transient Label isHighwayLabel;
    @FXML transient Label columnLabel;
    @FXML transient Label rowLabel;
    @FXML transient Label parentColumnLabel;
    @FXML transient Label parentRowLabel;
    @FXML transient Label searchName;

    public void start(Cell[][] grid, Search search, String searchName) {
        this.searchName.setText(searchName);
        columnLabel.setText("column val");
        rowLabel.setText("row val");
        executionTimeLabel.setText(String.format("%d ms", search.getExecutionTime()));
        pathCostLabel.setText(String.format("%f", search.getPathCost()));

        for (int row = 0; row < Constants.GRID_ROWS; row++) {
            for (int col = 0; col < Constants.GRID_COLUMNS; col++) {
                Color color;
                Cell currCell = grid[row][col];
                if(currCell == search.getStart()) {
                    color = Color.ORANGE;
                } else if(currCell == search.getGoal()) {
                    color = Color.RED;
                } else if(currCell.getIsPath()) {
                    color = Color.LIGHTGREEN;
                } else if(currCell.getCellType() == CellType.REGULAR_UNBLOCKED && currCell.isHighway()) {
                    color = Color.DARKBLUE;
                } else if(currCell.getCellType() == CellType.HARD_TO_TRAVERSE && currCell.isHighway()) {
                    color = Color.AQUA;
                } else if(currCell.getCellType() == CellType.REGULAR_UNBLOCKED) {
                    color = Color.LIGHTGREY;
                } else if(currCell.getCellType() == CellType.BLOCKED) {
                    color = Color.BLACK;
                } else if(currCell.getCellType() == CellType.HARD_TO_TRAVERSE) {
                    color = Color.DARKGREY;
                } else {
                    color = Color.WHITE;
                }
                currCell.setFill(color);
                GridPane.setRowIndex(currCell, row);
                GridPane.setColumnIndex(grid[row][col], col);
                gridPane.getChildren().addAll(currCell);
            }
        }
    }

    @FXML public void getCellInfo(MouseEvent event) {
        Cell cell = (Cell) event.getPickResult().getIntersectedNode();
        rowLabel.setText(String.format("%d", cell.getCoordinates().getRow()));
        columnLabel.setText(String.format("%d", cell.getCoordinates().getColumn()));
        if(cell.getCellParent() != null) {
            parentRowLabel.setText(String.format("%d", cell.getCellParent().getCoordinates().getRow()));
            parentColumnLabel.setText(String.format("%d", cell.getCellParent().getCoordinates().getColumn()));
        }
        fValueLabel.setText(String.format("%f", cell.getFValue()));
        gValueLabel.setText(String.format("%f", cell.getGValue()));
        isHighwayLabel.setText(String.format("%s", cell.isHighway()));
        cellTypeLabel.setText(String.format("%s", cell.getCellType().name()));
        hValueLabel.setText(String.format("%f", cell.getHValue()));
    }
}
