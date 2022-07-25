package net.mirwaldt.bowling.scorecard.webapp.mvc;

import net.mirwaldt.bowling.scorecard.BowlingGame;
import net.mirwaldt.bowling.scorecard.LazyScoringBowlingGame;

public class LazyScoringBowlingGameModel implements BowlingGameModel {
    private BowlingGame bowlingGameModel = new LazyScoringBowlingGame();

    @Override
    public void roll(int pins) {
        bowlingGameModel.roll(pins);
    }

    @Override
    public int currentFrame() {
        return bowlingGameModel.currentFrame();
    }

    @Override
    public int currentRollInFrame() {
        return bowlingGameModel.currentRollInFrame();
    }

    @Override
    public boolean isLastFrameSpare() {
        return bowlingGameModel.isLastFrameSpare();
    }

    @Override
    public boolean isLastRollStrike() {
        return bowlingGameModel.isLastRollStrike();
    }

    @Override
    public int score(int frame) {
        return bowlingGameModel.score(frame);
    }

    @Override
    public boolean isOver() {
        return bowlingGameModel.isOver();
    }

    @Override
    public void reset() {
        bowlingGameModel = new LazyScoringBowlingGame();
    }
}
