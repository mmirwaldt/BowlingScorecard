package net.mirwaldt.bowling.scorecard;

/**
 * rolls | frame | index
 * 0 |     0 |     0
 * 1 |     1 |     0
 * 2 |     1 |     0
 * 3 |     2 |     2
 * 4 |     2 |     2
 * 5 |     3 |     4
 * 6 |     3 |     4
 * 7 |     4 |     6
 * 8 |     4 |     6
 * <p>
 * frame(rolls) = (rolls + 1) / 2
 * index(frame) = (frame - 1) * 2
 * indexFromRolls(rolls) = index(frame(rolls)) = ((rolls + 1) / 2 - 1) * 2
 */

public class BowlingGameByTDD implements BowlingGame {
    private final int[] rolled = new int[6];

    private int rolls;

    @Override
    public int score() {
        return score(frame(rolls));
    }

    @Override
    public void roll(int pins) {
        if (pins < 0 || 10 < pins) {
            throw new IllegalArgumentException("The number of pins must be at least 0 and at most 10 but not " + pins);
        }
        if (rolls % 2 == 1 && 10 < rolled[0] + pins) {
            throw new IllegalArgumentException("The sum of pins within a frame must be at most 10 but not "
                    + rolled[0] + " + " + pins + " == " + (rolled[0] + pins));
        }
        rolled[rolls] = pins;

        if (rolls % 2 == 0 && isStrike(pins)) {
            rolls += 2;
        } else {
            rolls++;
        }
    }

    @Override
    public boolean isStrike() {
        int frame = frame(rolls);
        int index = index(frame);
        return 0 < rolls && isStrike(rolled[index]);
    }

    @Override
    public boolean isSpare() {
        int frame = frame(rolls);
        int index = index(frame);
        return 1 < rolls && !isStrike() && rolled[index] + rolled[index + 1] == 10;
    }

    @Override
    public int score(int frame) {
        int maxRolls = frame * 2;
        int score = 0;
        for (int roll = 0; roll < maxRolls; roll += 2) {
            int pins = rolled[roll];
            if (isStrike(pins)) {
                score += pins;
                for (int rollOfFrame = roll + 2; exists(rollOfFrame) && rollOfFrame <= roll + 4; ) {
                    int nextPins = rolled[rollOfFrame];
                    score += nextPins;
                    rollOfFrame += isStrike(nextPins) ? 2 : 1;
                }
            } else if (isSpareRoll(roll)) {
                score += 10 + ifExists(roll + 2);
            } else {
                score += pins + ifExists(roll + 1);
            }
        }
        return score;
    }

    private boolean isSpareRoll(int roll) {
        return rolled[roll] + ifExists(roll + 1) == 10;
    }

    private boolean exists(int roll) {
        return roll < rolls;
    }

    private int ifExists(int roll) {
        return exists(roll) ? rolled[roll] : 0;
    }

    private boolean isStrike(int pins) {
        return pins == 10;
    }

    private int frame(int roll) {
        return (roll + 1) / 2;
    }

    private int index(int frame) {
        return (frame - 1) * 2;
    }
}
