package net.mirwaldt.bowling.scorecard;

public interface BowlingGame {
    int LAST_FRAME = 10;
    int MIN_PINS = 0;
    int MAX_PINS = 10;
    int STRIKE_PINS = MAX_PINS;

    void roll(int pins);
    boolean isLastRollStrike();
    boolean isLastFrameSpare();
    int currentFrame();
    int currentRollInFrame();
    int score(int frame);
    int score();
    boolean isOver();


    static boolean isFrame(int frame) {
        return 1 <= frame && frame <= LAST_FRAME;
    }
    static boolean isBeforeLastFrame(int frame) {
        return frame < LAST_FRAME;
    }
    static boolean isLastFrame(int frame) {
        return frame == LAST_FRAME;
    }

    static boolean isSecondRoll(int rollInFrame) {
        return rollInFrame == 2;
    }
    static boolean isBonusRoll(int rollInFrame) {
        return rollInFrame == 3;
    }

    static boolean isLastRollInGame(int rollInFrame) {
        return isBonusRoll(rollInFrame);
    }


    static boolean isTooFewPins(int pins) {
        return pins < MIN_PINS;
    }

    static boolean isTooManyPins(int pins) {
        return MAX_PINS < pins;
    }

    static void checkRange(int pins) {
        if (isTooFewPins(pins) || isTooManyPins(pins)) {
            throw new IllegalArgumentException("The number of pins must be at least 0 and at most 10 but not " + pins);
        }
    }
}
