package net.mirwaldt.bowling.scorecard;

/**
 * rolls | frame | index
 *     0 |     0 |     0
 *     1 |     1 |     0
 *     2 |     1 |     0
 *     3 |     2 |     2
 *     4 |     2 |     2
 *     5 |     3 |     4
 *     6 |     3 |     4
 *     7 |     4 |     6
 *     8 |     4 |     6
 *
 *  frame(rolls) = (rolls + 1) / 2
 *  index(frame) = (frame - 1) * 2
 */

public class BowlingGameByTDD implements BowlingGame {
    private final int[] rolled = new int[4];

    private int rolls;

    @Override
    public int score() {
        return score(frame(rolls));
    }

    @Override
    public void roll(int pins) {
        if (pins < 0 || 10 < pins) {
            throw new IllegalArgumentException("The number of pins must be at least 0 and at most 10 but not " + pins);
        }
        if (rolls == 1 && !isFirstRollStrike() && 10 < rolled[0] + pins) {
            throw new IllegalArgumentException("The sum of pins within a frame must be at most 10 but not "
                    + rolled[0] + " + " + pins + " == " + (rolled[0] + pins));
        }
        rolled[rolls] = pins;

        if (rolls % 2 == 0 && pins == 10) {
            rolls += 2;
        } else {
            rolls++;
        }
    }

    @Override
    public boolean isStrike() {
        int frame = frame(rolls);
        int index = index(frame);
        return 0 < rolls && rolled[index] == 10;
    }

    @Override
    public boolean isSpare() {
        int frame = frame(rolls);
        int index = index(frame);
        return 1 < rolls && rolled[index] != 10 && rolled[index] + rolled[index + 1] == 10;
    }

    @Override
    public int score(int frame) {
        int maxRolls = frame * 2;
        int score = 0;
        for (int roll = 0; roll < maxRolls; ) {
            int pins = rolled[roll];
            if(pins == 10) {
                score += 10;
                if(roll + 2 < rolls) {
                    score += rolled[roll + 2];
                    if (rolled[roll + 2] == 10 && roll + 4 < rolls) {
                        score += rolled[roll + 4];
                    } else if(rolled[roll + 2] != 10 && roll + 3 < rolls) {
                        score += rolled[roll + 3];
                    }
                }
            } else if(roll + 1 < rolls && rolled[roll] + rolled[roll + 1] == 10) {
                score += 10;
                if(roll + 2 < rolls) {
                    score += rolled[roll + 2];
                }
            } else {
                score += pins;
                if(roll + 1 < rolls) {
                    score += rolled[roll + 1];
                }
            }
            roll += 2;
        }
        return score;
    }

    private boolean areFirstTwoRollsSpare() {
        return rolled[0] + rolled[1] == 10;
    }

    private boolean isFirstRollStrike() {
        return rolled[0] == 10;
    }

    private int frame(int roll) {
        return (roll + 1) / 2;
    }

    private int index(int frame) {
        return (frame - 1) * 2;
    }
}
