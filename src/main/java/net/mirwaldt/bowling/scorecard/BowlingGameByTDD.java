package net.mirwaldt.bowling.scorecard;

public class BowlingGameByTDD implements BowlingGame {
    private final int[] rolled = new int[2];

    private int rolls;

    @Override
    public int score() {
        return rolled[0] + rolled[1];
    }

    @Override
    public void roll(int pins) {
        if(pins < 0 || 10 < pins) {
            throw new IllegalArgumentException("The number of pins must be at least 0 and at most 10 but not " + pins);
        }
        rolled[rolls++] = pins;
    }

    @Override
    public boolean isStrike() {
        return rolled[0] == 10;
    }
}
