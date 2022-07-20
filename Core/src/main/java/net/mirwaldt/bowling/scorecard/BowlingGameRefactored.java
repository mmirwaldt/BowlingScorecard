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
    private static final int MAX_ROLLS_WITHOUT_BONUS = 20;
    private static final int MAX_ROLLS_WITH_BONUS = MAX_ROLLS_WITHOUT_BONUS + 1;

    private static final int MIN_PINS = 0;

    private static final int MAX_PINS = 10;

    public static final int STRIKE_PINS = 10;

    private final static int LAST_FRAME = 10;
    public static final int BONUS_INDEX = 20;

    private final int[] rolled = new int[MAX_ROLLS_WITH_BONUS];

    private int rolls;

    @Override
    public void roll(int pins) {
        checkRange(pins);
        checkTooManyPins(pins);
        checkBonus();

        rolled[rolls] = pins;
        if (isStrike(pins) && isFirstRoll(rolls) && isBeforeLastFrame(frame() + 1)) {
            rolls += 2;
        } else {
            rolls++;
        }
    }

    @Override
    public boolean isStrike() {
        return isStrikeFrame(frame());
    }

    @Override
    public boolean isSpare() {
        return isSpareFrame(frame());
    }

    @Override
    public int score() {
        return score(frame(rolls));
    }

    @Override
    public int score(int frame) {
        int score = 0;
        for (int f = 1; f <= frame; f++) {
            score += sumRolls(f);
            int nextFrame = f + 1;
            if (nextFrame <= LAST_FRAME) {
                if (isStrikeFrame(f)) {
                    score += sumRolls(nextFrame);
                    if (nextFrame < LAST_FRAME && isStrikeFrame(nextFrame)) {
                        score += firstRoll(nextFrame + 1);
                    }
                } else if (isSpareFrame(f)) {
                    score += firstRoll(nextFrame);
                }
            }
        }
        if (frame == LAST_FRAME) {
            score += rolled[BONUS_INDEX];
        }
        return score;
    }

    @Override
    public int frame() {
        return frame(rolls);
    }

    @Override
    public int rollOffset() {
        return (frame(rolls) < LAST_FRAME) ? (rolls - 1) % 2 : (rolls - 1) - 18;
    }

    @Override
    public boolean isOver() {
        return (isBonusRoll() && sumRolls(LAST_FRAME) < 10) || rolls == MAX_ROLLS_WITH_BONUS;
    }

    private boolean isStrikeFrame(int frame) {
        return isStrike(rolled[index(frame)]);
    }

    private boolean isSpareFrame(int frame) {
        return isSpare(rolled[index(frame)], rolled[index(frame) + 1]);
    }

    private boolean isStrike(int pins) {
        return pins == STRIKE_PINS;
    }

    private boolean isSpare(int firstRoll, int secondRoll) {
        return !isStrike(firstRoll) && firstRoll + secondRoll == 10;
    }

    private int frame(int roll) {
        return max(1, min(LAST_FRAME, (roll + 1) / 2));
    }

    private int index(int frame) {
        return (frame - 1) * 2;
    }

    private boolean isFirstRoll(int roll) {
        return roll % 2 == 0;
    }

    private boolean isSecondRoll(int roll) {
        return roll % 2 == 1;
    }

    private boolean isBeforeLastFrame(int frame) {
        return frame < LAST_FRAME;
    }

    private boolean isBonusRoll() {
        return rolls == MAX_ROLLS_WITHOUT_BONUS;
    }

    private int firstRoll(int frame) {
        return rolled[index(frame)];
    }

    private int sumRolls(int frame) {
        return rolled[index(frame)] + rolled[index(frame) + 1];
    }

    private void checkRange(int pins) {
        if (isTooFewPins(pins) || isTooManyPins(pins)) {
            throw new IllegalArgumentException("The number of pins must be at least 0 and at most 10 but not " + pins);
        }
    }

    private void checkTooManyPins(int pins) {
        if (isBeforeLastFrame(frame(rolls) + 1) && isSecondRoll(rolls) && isTooManyPins(firstRoll(frame()) + pins)) {
            throw new IllegalArgumentException("The sum of pins within a frame must be at most 10 but not "
                    + firstRoll(frame()) + " + " + pins + " == " + (firstRoll(frame()) + pins));
        }
    }

    private void checkBonus() {
        if (isBonusRoll() && !isStrikeFrame(LAST_FRAME) && !isSpareFrame(LAST_FRAME)) {
            throw new IllegalStateException("No bonus allowed because the two rolls sum is " +
                    sumRolls(LAST_FRAME) + " which is smaller than 10!");
        }
    }

    private boolean isTooFewPins(int pins) {
        return pins < MIN_PINS;
    }

    private boolean isTooManyPins(int pins) {
        return MAX_PINS < pins;
    }
}
