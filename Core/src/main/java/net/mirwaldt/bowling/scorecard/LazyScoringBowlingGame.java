package net.mirwaldt.bowling.scorecard;

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
    private final BowlingGameRollRecorder recorder = new ArrayBowlingGameRecorder();

    @Override
    public void roll(int pins) {
        checkGameOver();

        recorder.roll(pins);
    }

    @Override
    public boolean isPreviousRollStrike() {
        return !isLastFrame() && isStrikeFrame() || !isPreviousFrameSpare() && isStrikeInLastFrame();
    }

    @Override
    public boolean isPreviousFrameSpare() {
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
            score += recorder.sumRolls(f) + scoreStrikeAndSpare(f);
        }
        if (BowlingGame.isLastFrame(frame)) {
            score += recorder.bonusRoll();
        }
        return score;
    }

    @Override
    public int currentFrame() {
        return recorder.currentFrame();
    }

    @Override
    public int currentRollInFrame() {
        return recorder.currentRollInFrame();
    }

    @Override
    public boolean isOver() {
        return (isLastFrame() && isSecondRoll() && neitherStrikeNorSpare(LAST_FRAME)) || isLastRollInGame();
    }

    private int scoreStrikeAndSpare(int frame) {
        int nextFrame = frame + 1;
        if (BowlingGame.isFrame(nextFrame)) {
            return scoreFrame(frame);
        } else {
            return 0;
        }
    }

    private int scoreFrame(int frame) {
        int nextFrame = frame + 1;
        if (BowlingGame.isStrike(recorder.firstRoll(frame))) {
            return scoreStrike(nextFrame);
        } else if (isSpareFrame(frame)) {
            return scoreSpare(nextFrame);
        } else {
            return 0;
        }
    }

    private int scoreSpare(int nextFrame) {
        return recorder.firstRoll(nextFrame);
    }

    private int scoreStrike(int nextFrame) {
        if (BowlingGame.isBeforeLastFrame(nextFrame) && BowlingGame.isStrike(recorder.firstRoll(nextFrame))) {
            return recorder.firstRoll(nextFrame) + recorder.firstRoll(nextFrame + 1);
        } else {
            return recorder.sumRolls(nextFrame);
        }
    }

    private boolean isLastRollInGame() {
        return BowlingGame.isLastRollInGame(currentRollInFrame());
    }

    private boolean isSecondRoll() {
        return BowlingGame.isSecondRoll(currentRollInFrame());
    }

    private boolean isLastFrame() {
        return BowlingGame.isLastFrame(currentFrame());
    }

    @SuppressWarnings("SameParameterValue")
    private boolean neitherStrikeNorSpare(int frame) {
        return recorder.sumRolls(frame) < 10;
    }

    private boolean isSpareFrame(int frame) {
        return isSpare(recorder.firstRoll(frame), recorder.secondRoll(frame))
                && (BowlingGame.isBeforeLastFrame(frame) || !isLastRollInGame());
    }

    private boolean isSpare(int firstRoll, int secondRoll) {
        return firstRoll < 10 && firstRoll + secondRoll == 10;
    }

    private boolean isStrikeInLastFrame() {
        return isLastFrame() && BowlingGame.isStrike(recorder.pinsOfLastRoll());
    }

    private boolean isStrikeFrame() {
        return BowlingGame.isStrike(recorder.firstRoll(currentFrame()));
    }

    private void checkGameOver() {
        if (isOver()) {
            throw new IllegalStateException("Game is over!");
        }
    }
}
