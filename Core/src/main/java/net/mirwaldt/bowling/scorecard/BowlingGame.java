package net.mirwaldt.bowling.scorecard;

public interface BowlingGame {
    int score();

    void roll(int pins);

    boolean isLastRollStrike();

    boolean isLastRollSpare();

    int score(int frame);

    boolean isOver();

    int currentFrame();

    int currentRollInFrame();
}
