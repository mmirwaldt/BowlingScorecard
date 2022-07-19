package net.mirwaldt.bowling.scorecard;

public interface BowlingGame {
    int score();

    void roll(int pins);

    boolean isStrike();

    boolean isSpare();

    int score(int frame);

    boolean isOver();
}
