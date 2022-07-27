package net.mirwaldt.bowling.scorecard.webapp.mvc;

public interface BowlingGameView {
    void setRoll(int frame, int rollInFrame, int pins);

    void setStrikeRoll(int frame, int rollInFrame);

    void setSpareRoll(int frame);

    void setScore(int frame, int score);

    void reset();

    void disableInput();

    void enableInput();
}
