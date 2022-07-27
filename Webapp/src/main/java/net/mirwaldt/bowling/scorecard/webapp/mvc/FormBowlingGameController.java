package net.mirwaldt.bowling.scorecard.webapp.mvc;

public class FormBowlingGameController implements BowlingGameController {
    public static final int NO_BONUS_PINS = -1;
    private final BowlingGameModel bowlingGameModel;
    private final BowlingGameView bowlingGameView;

    public FormBowlingGameController(BowlingGameModel bowlingGameModel, BowlingGameView bowlingGameView) {
        this.bowlingGameModel = bowlingGameModel;
        this.bowlingGameView = bowlingGameView;
    }

    @Override
    public void rolled(int pins) {
        bowlingGameModel.roll(pins);

        if (bowlingGameModel.isLastRollStrike()) {
            bowlingGameView.setStrikeRoll(bowlingGameModel.currentFrame(), bowlingGameModel.currentRollInFrame());
        } else if (bowlingGameModel.isLastFrameSpare()) {
            bowlingGameView.setSpareRoll(bowlingGameModel.currentFrame());
        } else {
            bowlingGameView.setRoll(bowlingGameModel.currentFrame(), bowlingGameModel.currentRollInFrame(), pins);
        }

        displayScores();

        if (bowlingGameModel.isOver()) {
            if(isSecondRoll()) {
                bowlingGameView.setRoll(10, 3, NO_BONUS_PINS);
            }
            bowlingGameView.disableInput();
        }
    }

    @Override
    public void reset() {
        bowlingGameModel.reset();
        bowlingGameView.reset();
        bowlingGameView.enableInput();
    }

    private void displayScores() {
        for (int f = 1; f <= bowlingGameModel.currentFrame(); f++) {
            bowlingGameView.setScore(f, bowlingGameModel.score(f));
        }
    }

    private boolean isSecondRoll() {
        return bowlingGameModel.currentRollInFrame() == 2;
    }
}
