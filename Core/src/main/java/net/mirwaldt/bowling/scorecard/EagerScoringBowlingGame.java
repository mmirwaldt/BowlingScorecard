package net.mirwaldt.bowling.scorecard;

import java.util.stream.IntStream;

public class EagerScoringBowlingGame implements BowlingGame {
    private final int[] frames = new int[10];

    private int frame = 1;

    private int rollOffset = 0;

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
        checkRange(pins);
        checkTooManyPins(pins);
        checkGameOver();

        handlePreviousStrikesAndSpare(pins);
        handleRoll(pins);
    }

    @Override
    public boolean isStrike() {
        return isStrike;
    }

    @Override
    public boolean isSpare() {
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
    public int frame() {
        return frame;
    }

    @Override
    public int rollOffset() {
        return rollOffset;
    }

    private void handlePreviousStrikesAndSpare(int pins) {
        if(isDoubleStrike()) {
            handleDoubleStrike(pins);
        }

        if(isStrikeOrSpare()) {
            handleStrikeOrSpare(pins);
        }
    }

    private void handleRoll(int pins) {
        addPins(pins);
        if(isFirstRoll()) {
            handleFirstRoll(pins);
        } else if(isSecondRoll()) {
            handleSecondRoll(pins);
        } else {
            isSpare = false;
            setGameOver();
        }
    }

    private void handleFirstRoll(int pins) {
        backupStrikes();
        isStrike = isStrike(pins);
        isSpare = false;
        if (isStrike && isFrameBeforeLastFrame()) {
            turnToNextFrame();
        } else {
            wasStrikeTwoFramesAgo = false;
            rollOffset++;
        }
    }


    private void handleSecondRoll(int pins) {
        if(isFrameBeforeLastFrame()) {
            isStrike = false;
            isSpare = isSpareFrame();
            turnToNextFrame();
            rollOffset = 0;
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
        rollOffset++; // give bonus
    }

    private void setGameOver() {
        isOver = true;
    }

    private void addPins(int pins) {
        frames[frame - 1] += pins;
    }

    private void handleStrikeOrSpare(int pins) {
        frames[frame - 2] += pins;
    }

    private boolean isStrikeOrSpare() {
        return 1 < frame && (wasStrikeOneFrameAgo || rollOffset < 1 && (isStrike || isSpare));
    }

    private void handleDoubleStrike(int pins) {
        frames[frame - 3] += pins;
        wasStrikeTwoFramesAgo = false;
    }

    private boolean isDoubleStrike() {
        return 2 < frame && wasStrikeTwoFramesAgo && isStrike;
    }

    private boolean isSecondRoll() {
        return rollOffset == 1;
    }

    private boolean isFirstRoll() {
        return rollOffset == 0;
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

    private boolean isStrike(int pins) {
        return pins == 10;
    }

    private boolean isFrameBeforeLastFrame() {
        return frame < 10;
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
