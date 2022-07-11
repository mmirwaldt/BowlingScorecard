package net.mirwaldt.bowling.scorecard;

public class BowlingGameByTDD implements BowlingGame {
    private final int[] rolled = new int[3];

    private int rolls;

    @Override
    public int score() {
        if(rolls == 0) {
            return score(0);
        } else if((rolls == 1 || rolls == 2) && rolled[0] < 10) {
            return score(1);
        } else {
            return score(2);
        }
    }

    @Override
    public void roll(int pins) {
        if(pins < 0 || 10 < pins) {
            throw new IllegalArgumentException("The number of pins must be at least 0 and at most 10 but not " + pins);
        }
        if(rolls == 1 && rolled[0] < 10 && 10 < rolled[0] + pins) {
            throw new IllegalArgumentException("The sum of pins within a frame must be at most 10 but not "
                    + rolled[0]  + " + " + pins + " == " + (rolled[0] + pins));
        }
        rolled[rolls++] = pins;
    }

    @Override
    public boolean isStrike() {
        return (rolls == 1 && isFirstRollStrike()) || (rolled[0] != 0 && rolled[1] == 10);
    }

    @Override
    public boolean isSpare() {
        return rolled[0] < 10 && areFirstTwoRollsSpare();
    }

    @Override
    public int score(int frame) {
        if(frame == 0) {
            return 0;
        } else if(frame == 1) {
            if(isFirstRollStrike() || areFirstTwoRollsSpare()) { // strike or spare
                return rolled[0] + rolled[1] + rolled[2];
            } else {
                return rolled[0] + rolled[1];
            }
        } else {
            if(isFirstRollStrike()) {
                return score(1) + rolled[1];
            } else {
                return score(1) + rolled[2];
            }
        }
    }

    private boolean areFirstTwoRollsSpare() {
        return rolled[0] + rolled[1] == 10;
    }

    private boolean isFirstRollStrike() {
        return rolled[0] == 10;
    }
}
