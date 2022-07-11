package net.mirwaldt.bowling.scorecard;

public class BowlingGameByTDD implements BowlingGame {
    private int rolled;

    @Override
    public int score() {
        return rolled;
    }

    @Override
    public void roll(int pins) {
        if(pins < 0) {
            throw new IllegalArgumentException("The number of pins must be at least 0 but not " + pins);
        }
        rolled = pins;
    }
}
