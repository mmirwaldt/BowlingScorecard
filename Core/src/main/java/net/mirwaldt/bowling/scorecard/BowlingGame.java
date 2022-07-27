package net.mirwaldt.bowling.scorecard;

public interface BowlingGame {
    void roll(int pins);
    boolean isLastRollStrike();
    boolean isLastFrameSpare();
    int currentFrame();
    int currentRollInFrame();
    int score(int frame);
    int score();
    boolean isOver();
}
