package net.mirwaldt.bowling.scorecard;

import java.util.stream.IntStream;

public class BowlingGameFrameArray implements BowlingGame {
    private final int[] frames = new int[10];

    private int frame = 1;

    private int rollOffset = 0;

    private boolean isStrike = false;

    private boolean isSpare = false;

    private boolean wasStrike = false;

    private boolean wasStrikeBefore = false;

    private boolean isOver = false;

    @Override
    public int score() {
        return score(frame);
    }

    @Override
    public void roll(int pins) {
        checkRange(pins);
        checkTooManyPins(pins);
        checkBonus();

        frames[frame - 1] += pins;
        if(rollOffset == 0) {
            isStrike = pins == 10;
            isSpare = false;
            if (isStrike) {
                frame++;
            } else {
                rollOffset++;
            }
        } else {
            isStrike = false;
            isSpare = frames[frame - 1] == 10;
            frame++;
            rollOffset = 0;
        }
    }

    private void checkRange(int pins) {
        if(pins < 0 || 10 < pins) {
            throw new IllegalArgumentException("Pins per roll must be at least 0 and at most 10 but not " + pins);
        }
    }

    private void checkTooManyPins(int pins) {
        if(frame < 10 && rollOffset == 1 && 10 < frames[frame - 1] + pins) {
            throw new IllegalArgumentException("Pins per frame must be at least 0 and at most 10 but not "
                    + frames[frame - 1] + pins);
        }
    }

    private void checkBonus() {
        if(frame == 10 && rollOffset == 2 && frames[9] < 10) {
            throw new IllegalStateException("No bonus allowed because the two rolls sum is " +
                    frames[9] + " which is smaller than 10!");
        }
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
}
