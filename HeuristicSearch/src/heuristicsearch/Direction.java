package heuristicsearch;

import java.util.List;
import java.util.Random;

enum Direction {
    UP(0), DOWN(1), LEFT(2), RIGHT(3);

    private int val;
    private static final List<Direction> VALUES = List.of(values());
    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();

    public static Direction randomDirection()  {
        return VALUES.get(RANDOM.nextInt(SIZE));
    }

    /**
     * The excluding direction is the previous direction where the current node came from.
     * This method is to prevent going backwards and generate a random direction from the
     * remaining 3 directions given the probability distribution of 60% continuing forward
     * and 40% making a turn left or right.
     *
     * @param direction The direction to exclude in calculation
     * @return The direction to move in
     */
    public static Direction randomDirectionExcluding(Direction direction)  {
        Direction randomDirection;
        // 60% chance to continue moving forward
        if(RANDOM.nextInt(10) < 6) {
            return getOpposite(direction);
        } else {
            do {
                randomDirection = randomDirection();
            } while (randomDirection == direction && randomDirection == getOpposite(direction));
        }
        return randomDirection;
    }

    public static Direction getOpposite(Direction direction) {
        if(direction == Direction.UP) {
            return Direction.DOWN;
        } else if (direction == Direction.RIGHT) {
            return Direction.LEFT;
        } else if (direction == Direction.DOWN) {
            return Direction.UP;
        } else {
            return Direction.RIGHT;
        }
    }

    Direction(int value){
        val = value;
    }

    public int getValue(){
        return val;
    }
}
