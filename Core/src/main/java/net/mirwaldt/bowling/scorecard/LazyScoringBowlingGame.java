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

public class LazyScoringBowlingGame implements BowlingGame {
    private static final int MAX_ROLLS_WITHOUT_BONUS = 20;
    private static final int MAX_ROLLS_WITH_BONUS = MAX_ROLLS_WITHOUT_BONUS + 1;

    private static final int MIN_PINS = 0;

    private static final int MAX_PINS = 10;

    public static final int STRIKE_PINS = 10;

    private final static int LAST_FRAME = 10;
    public static final int BONUS_INDEX = 20;
    public static final int BONUS_ROLL = 20;

    private final int[] rolled = new int[MAX_ROLLS_WITH_BONUS];

    private int rolls;

    @Override
    public void roll(int pins) {
        checkRange(pins);
        checkTooManyPins(pins);
        checkGameOver();

        handleRoll(pins);
    }

    private void handleRoll(int pins) {
        setPins(pins);
        if (isStrike(pins) && isFirstRoll() && isBeforeLastFrame(nextFrame())) {
            turnToNextFrame();
        } else {
            turnToNextRoll();
        }
    }

    private boolean isFirstRoll() {
        return isFirstRoll(rolls);
    }

    private int nextFrame() {
        return currentFrame() + 1;
    }

    private void turnToNextRoll() {
        rolls++;
    }

    private void turnToNextFrame() {
        rolls += 2;
    }

    private void setPins(int pins) {
        rolled[rolls] = pins;
    }

    @Override
    public boolean isLastRollStrike() {
        return !isLastFrame(currentFrame()) && isStrikeFrame(currentFrame()) ||
                isLastFrame(currentFrame()) && !isLastFrameSpare() && isStrike(pinsOfLastRoll());
    }

    private int pinsOfLastRoll() {
        return rolled[rolls - 1];
    }

    @Override
    public boolean isLastFrameSpare() {
        return isSpareFrame(currentFrame());
    }

    @Override
    public int score() {
        return score(currentFrame());
    }

    @Override
    public int score(int frame) {
        int score = 0;
        for (int f = 1; f <= frame; f++) {
            score += sumRolls(f) + scoreStrikeAndSpare(f);
        }
        if (isLastFrame(frame)) {
            score += bonusRoll();
        }
        return score;
    }

    @Override
    public int currentFrame() {
        return frame(rolls);
    }

    @Override
    public int currentRollInFrame() {
        return (frame(rolls) < LAST_FRAME) ? (rolls - 1) % 2 : (rolls - 1) - 18;
    }

    @Override
    public boolean isOver() {
        return (rolls == BONUS_ROLL && sumRolls(LAST_FRAME) < 10) || rolls == MAX_ROLLS_WITH_BONUS;
    }

    private int scoreStrikeAndSpare(int frame) {
        int nextFrame = frame + 1;
        if (isFrame(nextFrame)) {
            if (isStrikeFrame(frame)) {
                return scoreStrike(nextFrame);
            } else if (isSpareFrame(frame)) {
                return scoreSpare(nextFrame);
            }
        }
        return 0;
    }

    private int scoreSpare(int nextFrame) {
        return firstRoll(nextFrame);
    }

    private int scoreStrike(int nextFrame) {
        if (isBeforeLastFrame(nextFrame) && isStrikeFrame(nextFrame)) {
            return firstRoll(nextFrame) + firstRoll(nextFrame + 1);
        } else {
            return sumRolls(nextFrame);
        }
    }

    private void checkGameOver() {
        if(isOver()) {
            throw new IllegalStateException("Game is over!");
        }
    }

    private boolean isStrikeFrame(int frame) {
        return isStrike(firstRoll(frame));
    }

    private boolean isSpareFrame(int frame) {
        return isSpare(firstRoll(frame), secondRoll(frame)) && rolls < 21;
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

    private boolean isLastFrame(int frame) {
        return frame == LAST_FRAME;
    }

    private boolean isFrame(int frame) {
        return 1 <= frame && frame <= LAST_FRAME;
    }

    private int firstRoll(int frame) {
        return rolled[index(frame)];
    }

    private int secondRoll(int frame) {
        return rolled[index(frame) + 1];
    }

    private int bonusRoll() {
        return rolled[BONUS_INDEX];
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
        if (isBeforeLastFrame(frame(rolls) + 1) && isSecondRoll(rolls) && isTooManyPins(firstRoll(currentFrame()) + pins)) {
            throw new IllegalArgumentException("The sum of pins within a frame must be at most 10 but not "
                    + firstRoll(currentFrame()) + " + " + pins + " = " + (firstRoll(currentFrame()) + pins));
        }
    }

    private boolean isTooFewPins(int pins) {
        return pins < MIN_PINS;
    }

    private boolean isTooManyPins(int pins) {
        return MAX_PINS < pins;
    }
}
