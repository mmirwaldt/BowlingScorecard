package net.mirwaldt.bowling.scorecard.webapp.mvc;

public interface BowlingGameController {
    void rolled(int pins);
    void reset();
}
