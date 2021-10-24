package heuristicsearch;

public class HeuristicResult {

    public double runTime;
    public double cost;
    public double nodesExpanded;
    public long memoryReq;
    public double numOfBenchmarks;

    public HeuristicResult() {
        this.runTime = 0;
        this.cost = 0;
        this.nodesExpanded = 0;
        this.memoryReq = 0;
        this.numOfBenchmarks = 0;
    }

    public HeuristicResult(double runtime, double cost, double nodesExpanded, long memoryReq) {
        this.runTime = runtime;
        this.cost = cost;
        this.nodesExpanded = nodesExpanded;
        this.memoryReq = memoryReq;
        this.numOfBenchmarks = 0;
    }

    public void addResult(HeuristicResult result) {
        this.runTime += result.runTime;
        this.cost += result.cost;
        this.nodesExpanded += result.nodesExpanded;
        this.memoryReq += result.memoryReq;
        this.numOfBenchmarks++;
    }

    public void printResult() {
        if(numOfBenchmarks != 0) {
            System.out.printf("    Average runtime: %.2f ms%n", runTime / numOfBenchmarks);
            System.out.printf("    Average cost difference from optimal: %.2f%n", cost / numOfBenchmarks);
            System.out.printf("    Average nodes expanded: %.2f%n", nodesExpanded / numOfBenchmarks);
            System.out.printf("    Average memory requirements: %.2f kb %n", memoryReq / numOfBenchmarks);
            System.out.printf("    Number of benchmarks: %.2f%n", numOfBenchmarks);
        }
    }
}
