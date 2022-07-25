package net.mirwaldt.bowling.scorecard.webapp;

import net.mirwaldt.bowling.scorecard.BowlingGame;
import net.mirwaldt.bowling.scorecard.LazyScoringBowlingGame;

public class LazyScoringBowlingGameModel implements BowlingGameModel {
    private BowlingGame bowlingGameModel = new LazyScoringBowlingGame();

    @Override
    public void roll(int pins) {
        bowlingGameModel.roll(pins);
    }

    @Override
    public int frame() {
        return bowlingGameModel.frame();
    }

    @Override
    public int rollOffset() {
        return bowlingGameModel.rollOffset();
    }

    @Override
    public boolean isSpare() {
        return bowlingGameModel.isSpare();
    }

    @Override
    public boolean isStrike() {
        return bowlingGameModel.isStrike();
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
