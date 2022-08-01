package net.mirwaldt.bowling.scorecard;

import java.util.stream.IntStream;

public class EagerScoringBowlingGame implements BowlingGame {
    private final int[] frames = new int[10];

    private int frame = 0;

    private int rollOffset = 0;

    private boolean isNextFrame = true;

    private boolean isNextRoll = false;

    private boolean isBonusRoll = false;

    private boolean isStrike = false;

    private boolean isSpare = false;

    private boolean isSpareOneFrameBefore = false;

    private boolean isStrikeOneFrameBefore = false;

    private boolean isStrikeTwoFramesBefore = false;

    private boolean isOver = false;

    @Override
    public int score() {
        return score(frame);
    }

    @Override
    public void roll(int pins) {
        checkForNextFrame();
        checkForNextRoll();
        checkForBonusRoll();

        checkRoll(pins);

        scoreRoll(pins);

        scorePreviousFrames(pins);
    }

    @Override
    public boolean isPreviousRollStrike() {
        return isStrike;
    }

    @Override
    public boolean isPreviousFrameSpare() {
        return isSpare;
    }

    @Override
    public int score(int frame) {
        return IntStream.of(frames)
                .limit(frame)
                .sum();
    }

    @Override
    public boolean isOver() {
        return isOver;
    }

    @Override
    public int currentFrame() {
        return frame;
    }

    @Override
    public int currentRollInFrame() {
        return (frame == 0) ? 0 : rollOffset + 1;
    }

    private void checkRoll(int pins) {
        BowlingGame.checkRange(pins);
        checkTooManyPins(pins);
        checkGameOver();
    }

    private void scoreRoll(int pins) {
        addPins(pins);
        if (isFirstRoll()) {
            scoreFirstRoll(pins);
        } else if (isSecondRoll()) {
            scoreSecondRoll(pins);
        } else {
            checkForStrike(pins);
            setGameOver();
        }
    }

    private void scoreFirstRoll(int pins) {
        backupStrikes();
        backupSpare();
        checkForStrike(pins);
        if (isStrike && isFrameBeforeLastFrame()) {
            enableNextFrame();
        } else {
            enableNextRoll();
        }
    }

    private void scoreSecondRoll(int pins) {
        forgetSpareOneFrameBefore();
        if (isFrameBeforeLastFrame()) {
            checkForSpare();
            enableNextFrame();
        } else {
            if (isStrikeOrSpareInLastFrame()) {
                giveBonus(pins);
            } else {
                setGameOver();
            }
        }
    }

    private void scorePreviousFrames(int pins) {
        if (isDoubleStrike()) {
            scoreDoubleStrikeTwoFramesBefore(pins);
        }

        if (isStrikeOrSpareOneFrameBefore()) {
            scoreStrikeOrSpareOneFrameBefore(pins);
        }
    }

    private void giveBonus(int pins) {
        forgetStrikeTwoFramesBefore();
        checkForStrikeOrSpare(pins);
        enableBonus();
    }

    private void enableNextRoll() {
        isNextRoll = true;
    }

    private void checkForStrike(int pins) {
        isStrike = BowlingGame.isStrike(pins);
        isSpare = false;
    }

    private void enableNextFrame() {
        isNextFrame = true;
    }

    private boolean isStrikeOrSpareInLastFrame() {
        return isStrike || isSpareFrame();
    }

    private void checkForSpare() {
        isStrike = false;
        isSpare = isSpareFrame();
    }

    private boolean isStrikeOrSpareOneFrameBefore() {
        return isStrikeOneFrameBefore || isSpareOneFrameBefore;
    }

    private void scoreStrikeOrSpareOneFrameBefore(int pins) {
        frames[frame - 2] += pins;
    }

    private void scoreDoubleStrikeTwoFramesBefore(int pins) {
        frames[frame - 3] += pins;
    }

    private void enableBonus() {
        isBonusRoll = true;
    }

    private void checkForStrikeOrSpare(int pins) {
        isStrike = isStrike && BowlingGame.isStrike(pins);
        isSpare = !isStrike && 0 < pins && isSpareFrame();
    }

    private void checkForNextFrame() {
        if (isNextFrame) {
            turnToNextFrame();
            turnToFirstRoll();
            isNextFrame = false;
        }
    }

    private void checkForNextRoll() {
        if (isNextRoll) {
            turnToSecondRoll();
            isNextRoll = false;
        }
    }

    private void checkForBonusRoll() {
        if (isBonusRoll) {
            turnToBonusRoll();
            isStrikeOneFrameBefore = false;
            isBonusRoll = false;
        }
    }

    private void backupStrikes() {
        isStrikeTwoFramesBefore = isStrikeOneFrameBefore;
        isStrikeOneFrameBefore = isStrike;
    }

    private void backupSpare() {
        isSpareOneFrameBefore = isSpare;
    }

    private void forgetSpareOneFrameBefore() {
        isSpareOneFrameBefore = false;
    }

    private boolean isDoubleStrike() {
        return isStrikeTwoFramesBefore && isStrikeOneFrameBefore && isFirstRoll();
    }

    private void forgetStrikeTwoFramesBefore() {
        isStrikeTwoFramesBefore = false;
    }

    private void addPins(int pins) {
        frames[frame - 1] += pins;
    }

    private boolean isSpareFrame() {
        return frames[frame - 1] == 10;
    }

    private void turnToNextFrame() {
        frame++;
    }

    private void turnToFirstRoll() {
        rollOffset = FIRST_ROLL_IN_FRAME - 1;
    }

    private void turnToSecondRoll() {
        rollOffset = SECOND_ROLL_IN_FRAME - 1;
    }

    private void turnToBonusRoll() {
        rollOffset = BONUS_ROLL_IN_FRAME - 1;
    }

    private boolean isFrameBeforeLastFrame() {
        return frame < LAST_FRAME;
    }

    private boolean isFirstRoll() {
        return BowlingGame.isFirstRoll(currentRollInFrame());
    }

    private boolean isSecondRoll() {
        return BowlingGame.isSecondRoll(currentRollInFrame());
    }

    private void setGameOver() {
        isOver = true;
    }

    private void checkGameOver() {
        if (isOver) {
            throw new IllegalStateException("Game is over!");
        }
    }

    private void checkTooManyPins(int pins) {
        if (isFrameBeforeLastFrame() && isSecondRoll() && 10 < frames[frame - 1] + pins) {
            throw new IllegalArgumentException("The sum of pins within a frame must be at most 10 but not "
                    + frames[frame - 1] + " + " + pins + " = " + (frames[frame - 1] + pins));
        }
    }
}
