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

    private boolean isSpareOneFrameAgo = false;

    private boolean isStrikeOneFrameAgo = false;

    private boolean isStrikeTwoFramesAgo = false;

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

        rescorePreviousFrames(pins);
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
        checkRange(pins);
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
            isStrike = isStrike(pins);
            isSpare = false;
            setGameOver();
        }
    }

    private void scoreFirstRoll(int pins) {
        backupStrikes();
        backupSpare();
        isStrike = isStrike(pins);
        isSpare = false;
        if (isStrike && isFrameBeforeLastFrame()) {
            isNextFrame = true;
        } else {
            isNextRoll = true;
        }
    }

    private void scoreSecondRoll(int pins) {
        forgetSpareOneFrameAgo();
        if (isFrameBeforeLastFrame()) {
            isStrike = false;
            isSpare = isSpareFrame();
            isNextFrame = true;
        } else {
            if (isStrike || isSpareFrame()) {
                giveBonus(pins);
            } else {
                setGameOver();
            }
        }
    }

    private void rescorePreviousFrames(int pins) {
        if (isDoubleStrike()) {
            frames[frame - 3] += pins;
        }

        if (isStrikeOneFrameAgo || isSpareOneFrameAgo) {
            frames[frame - 2] += pins;
        }
    }

    private void giveBonus(int pins) {
        forgetStrikeTwoFramesAgo();
        isStrike = isStrike && isStrike(pins);
        isSpare = !isStrike && 0 < pins && isSpareFrame();
        isBonusRoll = true;
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
            isStrikeOneFrameAgo = false;
            isBonusRoll = false;
        }
    }

    private void backupStrikes() {
        isStrikeTwoFramesAgo = isStrikeOneFrameAgo;
        isStrikeOneFrameAgo = isStrike;
    }

    private void backupSpare() {
        isSpareOneFrameAgo = isSpare;
    }

    private void forgetSpareOneFrameAgo() {
        isSpareOneFrameAgo = false;
    }

    private boolean isDoubleStrike() {
        return isStrikeTwoFramesAgo && isStrikeOneFrameAgo && isFirstRoll();
    }

    private void forgetStrikeTwoFramesAgo() {
        isStrikeTwoFramesAgo = false;
    }

    private boolean isStrike(int pins) {
        return pins == 10;
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
        rollOffset = 0;
    }

    private void turnToSecondRoll() {
        rollOffset = 1;
    }

    private void turnToBonusRoll() {
        rollOffset = 2;
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
        if (isOver) {
            throw new IllegalStateException("Game is over!");
        }
    }

    private void checkRange(int pins) {
        if (pins < 0 || 10 < pins) {
            throw new IllegalArgumentException("Pins per roll must be at least 0 and at most 10 but not " + pins);
        }
    }

    private void checkTooManyPins(int pins) {
        if (isFrameBeforeLastFrame() && isSecondRoll() && 10 < frames[frame - 1] + pins) {
            throw new IllegalArgumentException("The sum of pins within a frame must be at most 10 but not "
                    + frames[frame - 1] + " + " + pins + " = " + (frames[frame - 1] + pins));
        }
    }
}
