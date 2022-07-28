package net.mirwaldt.bowling.scorecard;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static net.mirwaldt.bowling.scorecard.BowlingGame.*;

public class ArrayBowlingGameRecorder implements BowlingGameRollRecorder {
    private static final int MAX_ROLLS_WITHOUT_BONUS = 20;
    private static final int MAX_ROLLS_WITH_BONUS = MAX_ROLLS_WITHOUT_BONUS + 1;
    public static final int BONUS_INDEX = 20;
    public static final int SECOND_ROLL_OF_NEXT_TO_LAST_FRAME = 18;

    private final int[] rolled = new int[MAX_ROLLS_WITH_BONUS];
    private int rolls;

    @Override
    public void roll(int pins) {
        checkRange(pins);
        checkTooManyPins(pins);

        setPins(pins);
        if (isBeforeLastFrame(nextFrame()) && isFirstRoll() && isStrike(pins)) {
            turnToNextFrame();
        } else {
            turnToNextRoll();
        }
    }

    @Override
    public int currentFrame() {
        return max(0, min(LAST_FRAME, (rolls + 1) / 2));
    }

    @Override
    public int currentRollInFrame() {
        if (rolls == 0) {
            return 0;
        } else if(rolls <= SECOND_ROLL_OF_NEXT_TO_LAST_FRAME) {
            if(isFirstRoll() && isStrike(firstRoll(currentFrame()))) {
                return 1;
            } else {
                return ((rolls + 1) % 2) + 1;
            }
        } else {
            return rolls - SECOND_ROLL_OF_NEXT_TO_LAST_FRAME;
        }
    }

    @Override
    public int firstRoll(int frame) {
        return rolled[index(frame)];
    }

    @Override
    public int secondRoll(int frame) {
        return rolled[index(frame) + 1];
    }

    @Override
    public int bonusRoll() {
        return rolled[BONUS_INDEX];
    }

    @Override
    public int sumRolls(int frame) {
        return rolled[index(frame)] + rolled[index(frame) + 1];
    }

    @Override
    public int pinsOfLastRoll() {
        return rolled[rolls - 1];
    }

    private int index(int frame) {
        return (frame - 1) * 2;
    }

    private int nextFrame() {
        return currentFrame() + 1;
    }

    private boolean isFirstRoll() {
        return rolls % 2 == 0;
    }

    private boolean isStrike(int pins) {
        return pins == 10;
    }

    private void setPins(int pins) {
        rolled[rolls] = pins;
    }

    private void turnToNextRoll() {
        rolls++;
    }

    private void turnToNextFrame() {
        rolls += 2;
    }

    private void checkTooManyPins(int pins) {
        if (isSecondRoll(nextRollInFrame())
                && !isStrike(firstRoll(currentFrame()))
                && isTooManyPins(firstRoll(currentFrame()) + pins)) {
            throw new IllegalArgumentException("The sum of pins within a frame must be at most 10 but not "
                    + firstRoll(currentFrame()) + " + " + pins + " = " + (firstRoll(currentFrame()) + pins));
        }
    }

    private int nextRollInFrame() {
        return currentRollInFrame() + 1;
    }
}
