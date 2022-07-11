package net.mirwaldt.bowling.scorecard;

public class BowlingGameByTDD implements BowlingGame {
    private int rolled;

    @Override
    public int score() {
        return rolled;
    }

    @Override
    public void roll(int pins) {
        rolled = pins;
    }
}
