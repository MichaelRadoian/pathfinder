package heuristicsearch;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GridDescriptorBuilder {

    private Cell[][] grid;
    private int startColumn, startRow, endRow, endColumn;
    private List<Coords> hardToTraverseCenters = new ArrayList<>();

    GridDescriptorBuilder(Cell[][] grid, List<Coords> hardToTraverseCenters, int startColumn, int startRow, int endRow, int endColumn) {
        this.grid = grid;
        this.startColumn = startColumn;
        this.startRow = startRow;
        this.endColumn = endColumn;
        this.endRow = endRow;
        this.hardToTraverseCenters = hardToTraverseCenters;
    }

    void generateFile(String fileName) {
        int fileNum = 1;
        File file = new File(fileName + "_" + fileNum + ".txt");

        while (file.exists()) {
            fileNum++;
            file = new File(fileName + "_" + fileNum + ".txt");
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
                System.out.println("File created: " + file.getName());

                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                String content = generateStringFromData();
                //System.out.println(content);
                bw.write(content);
                bw.close();

            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }
    }

    private String generateStringFromData() {
        String s = "";
        s += this.endColumn + " " + this.endRow + "\n";
        s += this.startColumn + " " + this.startRow + "\n";
        
        for (int i = 0; i < hardToTraverseCenters.size(); i++) {
            s += hardToTraverseCenters.get(i).getColumn() + " " + hardToTraverseCenters.get(i).getRow() + "\n";
        }

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                s += grid[i][j].getSymbol() + " ";
            }
            s += "\n";
        }
        
        return s;
    }
}
