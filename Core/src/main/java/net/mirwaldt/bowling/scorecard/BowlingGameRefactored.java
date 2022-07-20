package net.mirwaldt.bowling.scorecard;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * rolls | frame | index
 * 0 |     0 |     0
 * 1 |     1 |     0
 * 2 |     1 |     0
 * 3 |     2 |     2
 * 4 |     2 |     2
 * 5 |     3 |     4
 * 6 |     3 |     4
 * 7 |     4 |     6
 * 8 |     4 |     6
 * <p>
 * frame(rolls) = (rolls + 1) / 2
 * index(frame) = (frame - 1) * 2
 * indexFromRolls(rolls) = index(frame(rolls)) = ((rolls + 1) / 2 - 1) * 2
 */

public class BowlingGameRefactored implements BowlingGame {
    private final int[] rolled = new int[21];

    private int rolls;

    @Override
    public void roll(int pins) {
        checkRange(pins);
        checkTooManyPins(pins);
        checkBonus();

        rolled[rolls] = pins;
        if (rolls % 2 == 0 && isStrike(pins) && isOneFrameBeforeTheLastFrame()) {
            rolls += 2;
        } else {
            rolls++;
        }
    }

    @Override
    public boolean isStrike() {
        return minRolls(1) && isStrike(firstRollOfCurrentFrame());
    }

    @Override
    public boolean isSpare() {
        return minRolls(2) && !isStrike() && firstRollOfCurrentFrame() + secondRollOfCurrentFrame() == 10;
    }

    @Override
    public int score() {
        return score(frame(rolls));
    }

    @Override
    public int score(int frame) {
        int score = 0;
        for (int f = 1; f <= frame; f++) {
            if (isStrikeFrame(f)) {
                score += rolled[index(f)];
                int nextRoll = index(f + 1);
                for (int count = 0; count < 2 && nextRoll < 21; count++) {
                    score += rolled[nextRoll];
                    if (f < 10 && isStrike(rolled[nextRoll])) {
                        nextRoll += 2;
                    } else {
                        nextRoll++;
                    }
                }
                if (f == 10) {
                    score += rolled[20];
                }
            } else {
                int firstRoll = index(f);
                int secondRoll = firstRoll + 1;
                score += rolled[firstRoll] + rolled[secondRoll];
                if (isSpareFrame(f)) {
                    int nextRoll = secondRoll + 1;
                    score += rolled[nextRoll];
                }
            }
        }
        return score;
    }

    private boolean isSpareFrame(int frame) {
        return !isStrikeFrame(frame) && rolled[index(frame)] + rolled[index(frame) + 1] == 10;
    }

    private boolean isStrikeFrame(int frame) {
        return isStrike(rolled[index(frame)]);
    }

    @Override
    public boolean isOver() {
        return (rolls == 20 && rolled[18] + rolled[19] < 10) || rolls == 21;
    }

    private boolean minRolls(int minRolls) {
        return minRolls <= rolls;
    }

    private boolean isOneFrameBeforeTheLastFrame() {
        return frame(rolls) < 9;
    }

    private void checkBonus() {
        if (19 < rolls && rolled[18] + rolled[19] < 10) {
            throw new IllegalStateException("No bonus allowed because the two rolls sum is " +
                    (rolled[18] + rolled[19]) + " which is smaller than 10!");
        }
    }

    private int index() {
        return index(frame(rolls));
    }

    @Override
    public int frame() {
        return frame(rolls);
    }

    @Override
    public int rollOffsetOfFrame() {
        return (frame() < 10) ? 1 - (rolls % 2) : rolls - 19;
    }

    private boolean isSpareRoll(int roll) {
        return roll < 10 * 2 && rolled[roll] + ifExists(roll + 1) == 10;
    }

    private boolean exists(int roll) {
        return roll < rolls;
    }

    private int ifExists(int roll) {
        return exists(roll) ? rolled[roll] : 0;
    }

    private boolean isStrike(int pins) {
        return pins == 10;
    }

    private int frame(int roll) {
        return max(1, min(10, (roll + 1) / 2));
    }

    private int index(int frame) {
        return (frame - 1) * 2;
    }

    private int firstRollOfCurrentFrame() {
        return rolled[firstRollIndexOfCurrentFrame()];
    }

    private int secondRollOfCurrentFrame() {
        return rolled[firstRollIndexOfCurrentFrame() + 1];
    }

    private int firstRollIndexOfCurrentFrame() {
        int frame = frame(rolls);
        return index(frame);
    }

    private void checkRange(int pins) {
        if (pins < 0 || isTooManyPins(pins)) {
            throw new IllegalArgumentException("The number of pins must be at least 0 and at most 10 but not " + pins);
        }
    }

    private void checkTooManyPins(int pins) {
        if (isSecondRollOfFrame() && isTooManyPins(currentRoll() + pins) && isOneFrameBeforeTheLastFrame()) {
            throw new IllegalArgumentException("The sum of pins within a frame must be at most 10 but not "
                    + currentRoll() + " + " + pins + " == " + (currentRoll() + pins));
        }
    }

    private boolean isTooManyPins(int pins) {
        return 10 < pins;
    }

    private int currentRoll() {
        return rolled[index()];
    }

    private boolean isSecondRollOfFrame() {
        return rolls % 2 == 1;
    }
}
