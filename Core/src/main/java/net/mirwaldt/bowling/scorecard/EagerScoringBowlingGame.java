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

    private boolean wasStrikeOneFrameAgo = false;

    private boolean wasStrikeTwoFramesAgo = false;

    private boolean isOver = false;

    @Override
    public int score() {
        return score(frame);
    }

    @Override
    public void roll(int pins) {
        if(isNextFrame) {
            turnToNextFrame();
            turnToFirstRoll();
            isNextFrame = false;
        }

        if(isNextRoll) {
            turnToSecondRoll();
            isNextRoll = false;
        }

        if(isBonusRoll) {
            turnToBonusRoll();
            isBonusRoll = false;
        }

        checkRange(pins);
        checkTooManyPins(pins);
        checkGameOver();

        scorePreviousStrikesAndSpare(pins);
        scoreRoll(pins);
    }

    @Override
    public boolean isLastRollStrike() {
        return isStrike;
    }

    @Override
    public boolean isLastFrameSpare() {
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
        return rollOffset;
    }

    private void scorePreviousStrikesAndSpare(int pins) {
        if(isDoubleStrike()) {
            scoreDoubleStrike(pins);
        }

        if(isStrikeOrSpare()) {
            scoreStrikeOrSpare(pins);
        }
    }

    private void scoreRoll(int pins) {
        addPins(pins);
        if(isFirstRoll()) {
            scoreFirstRoll(pins);
        } else if(isSecondRoll()) {
            scoreSecondRoll(pins);
        } else {
            isSpare = false;
            setGameOver();
        }
    }

    private void scoreFirstRoll(int pins) {
        backupStrikes();
        isStrike = isStrike(pins);
        isSpare = false;
        if (isStrike && isFrameBeforeLastFrame()) {
            isNextFrame = true;
        } else {
            wasStrikeTwoFramesAgo = false;
            isNextRoll = true;
        }
    }

    private void scoreSecondRoll(int pins) {
        if(isFrameBeforeLastFrame()) {
            isStrike = false;
            isSpare = isSpareFrame();
            isNextFrame = true;
        } else {
            if(isStrike || isSpareFrame()) {
                giveBonus(pins);
            } else {
                setGameOver();
            }
        }
    }

    private void giveBonus(int pins) {
        wasStrikeTwoFramesAgo = false;
        wasStrikeOneFrameAgo = false;
        isStrike = isStrike && isStrike(pins);
        isSpare = !isStrike && 0 < pins && isSpareFrame();
        isBonusRoll = true;
    }

    private void scoreStrikeOrSpare(int pins) {
        frames[frame - 2] += pins;
    }

    private boolean isStrike(int pins) {
        return pins == 10;
    }

    private boolean isStrikeOrSpare() {
        return isFrameAfterFirstFrame() && (wasStrikeOneFrameAgo || isFirstRoll() && (isStrike || isSpare));
    }

    private void addPins(int pins) {
        frames[frame - 1] += pins;
    }

    private void scoreDoubleStrike(int pins) {
        frames[frame - 3] += pins;
        wasStrikeTwoFramesAgo = false;
    }

    private boolean isDoubleStrike() {
        return 2 < frame && wasStrikeTwoFramesAgo && isStrike;
    }

    private boolean isSpareFrame() {
        return frames[frame - 1] == 10;
    }

    private void backupStrikes() {
        wasStrikeTwoFramesAgo = isStrike;
        wasStrikeOneFrameAgo = isStrike;
    }

    private void turnToNextFrame() {
        frame++;
    }

    private void turnToFirstRoll() {
        rollOffset = 0;
    }

    private void turnToSecondRoll() {
        rollOffset = 1;
    }

    private void turnToBonusRoll() {
        rollOffset = 2;
    }

    private boolean isFrameAfterFirstFrame() {
        return 1 < frame;
    }

    private boolean isFrameBeforeLastFrame() {
        return frame < 10;
    }

    private boolean isFirstRoll() {
        return rollOffset == 0;
    }

    private boolean isSecondRoll() {
        return rollOffset == 1;
    }

    private void setGameOver() {
        isOver = true;
    }

    private void checkGameOver() {
        if(isOver) {
            throw new IllegalStateException("Game is over!");
        }
    }

    private void checkRange(int pins) {
        if(pins < 0 || 10 < pins) {
            throw new IllegalArgumentException("Pins per roll must be at least 0 and at most 10 but not " + pins);
        }
    }

    private void checkTooManyPins(int pins) {
        if(isFrameBeforeLastFrame() && isSecondRoll() && 10 < frames[frame - 1] + pins) {
            throw new IllegalArgumentException("The sum of pins within a frame must be at most 10 but not "
                    + frames[frame - 1] + " + " + pins + " = " + (frames[frame - 1] + pins));
        }
    }
}
