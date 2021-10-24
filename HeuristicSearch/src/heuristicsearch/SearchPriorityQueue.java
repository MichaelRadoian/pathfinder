package heuristicsearch;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import static heuristicsearch.Constants.GRID_COLUMNS;
import static heuristicsearch.Constants.GRID_ROWS;

public class SearchPriorityQueue {

    private PriorityQueue<Cell> pq;
    private Set<Cell> set;

    public SearchPriorityQueue(int i) {
        // follows the enum values e.g. 0 = admissible, 1 is diagonal
        pq = new PriorityQueue<>(GRID_COLUMNS * GRID_ROWS, (c1, c2) -> {
            if (c1.getFValueForHeuristic(i) != c2.getFValueForHeuristic(i)) {
                return Double.compare(c1.getFValueForHeuristic(i), c2.getFValueForHeuristic(i));
            } else {
                // Come back to this. This would be weird to compare for gi(s)
                return Double.compare(c1.getGValueForHeuristic(i), c2.getGValueForHeuristic(i));
            }
        });

        set = new HashSet<>();
    }

    public void offer(Cell s) {
        pq.offer(s);
        set.add(s);
    }

    public Cell peek() {
        return pq.peek();
    }

    public double getMinKey(int i) {
        return peek().getFValueForHeuristic(i);
    }

    public Cell getTop() {
        Cell cell = pq.poll();
        set.remove(cell);
        return cell;
    }

    public boolean contains(Cell s) {
        return set.contains(s);
    }

    public boolean isEmpty() {
        return pq.isEmpty();
    }
}
