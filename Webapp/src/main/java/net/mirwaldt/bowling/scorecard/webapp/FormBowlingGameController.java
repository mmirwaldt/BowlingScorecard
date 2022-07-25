package net.mirwaldt.bowling.scorecard.webapp;

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

        if (bowlingGameModel.isStrike()) {
            bowlingGameView.setStrikeRoll(bowlingGameModel.frame(), bowlingGameModel.rollOffset());
        } else if (bowlingGameModel.isSpare()) {
            bowlingGameView.setSpareRoll(bowlingGameModel.frame());
        } else {
            bowlingGameView.setRoll(bowlingGameModel.frame(), bowlingGameModel.rollOffset(), pins);
        }

        displayScores();

        if (bowlingGameModel.isOver()) {
            if(bowlingGameModel.rollOffset() == 1) {
                bowlingGameView.setRoll(10, 2, NO_BONUS_PINS);
            }
            bowlingGameView.disableRollButton();
        }
    }

    @Override
    public void reset() {
        bowlingGameModel.reset();
        bowlingGameView.reset();
        bowlingGameView.enableRollButton();
    }

    private void displayScores() {
        for (int f = 1; f <= bowlingGameModel.frame(); f++) {
            bowlingGameView.setScore(f, bowlingGameModel.score(f));
        }
    }
}
