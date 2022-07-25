package net.mirwaldt.bowling.scorecard.webapp.mvc;

public interface BowlingGameModel {
    void roll(int pins);
    int currentFrame();
    int currentRollInFrame();
    boolean isLastFrameSpare();
    boolean isLastRollStrike();
    int score(int frame);
    boolean isOver();
    void reset();
}
