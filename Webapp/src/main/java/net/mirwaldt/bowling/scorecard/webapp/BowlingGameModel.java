package net.mirwaldt.bowling.scorecard.webapp;

public interface BowlingGameModel {
    void roll(int pins);
    int frame();
    int rollOffset();
    boolean isSpare();
    boolean isStrike();
    int score(int frame);
    boolean isOver();
    void reset();
}
