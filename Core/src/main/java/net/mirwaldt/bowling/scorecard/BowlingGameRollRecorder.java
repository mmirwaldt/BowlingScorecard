package net.mirwaldt.bowling.scorecard;

public interface BowlingGameRollRecorder {
    void roll(int pins);
    int currentFrame();
    int currentRollInFrame();
    int pinsOfLastRoll();

    int firstRoll(int frame);
    int secondRoll(int frame);
    int bonusRoll();
    int sumRolls(int frame);
}
