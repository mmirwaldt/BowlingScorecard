package net.mirwaldt.bowling.scorecard.webapp.mvc;

public interface BowlingGameView {
    void setRoll(int frame, int rollOffset, int pins);

    void setStrikeRoll(int frame, int rollOffset);

    void setSpareRoll(int frame);

    void setScore(int frame, int score);

    void reset();

    void disableInput();

    void enableInput();
}
