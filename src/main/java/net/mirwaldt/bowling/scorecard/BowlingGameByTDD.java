package net.mirwaldt.bowling.scorecard;

public class BowlingGameByTDD implements BowlingGame {
    private final int[] rolled = new int[2];

    private int rolls;

    @Override
    public int score() {
        return rolled[0] + rolled[1];
    }

    @Override
    public void roll(int pins) {
        if(pins < 0 || 10 < pins) {
            throw new IllegalArgumentException("The number of pins must be at least 0 and at most 10 but not " + pins);
        }
        if(0 < rolls && rolled[0] < 10 && 10 < rolled[0] + pins) {
            throw new IllegalArgumentException("The sum of pins within a frame must be at most 10 but not "
                    + rolled[0]  + " + " + pins + " == " + (rolled[0] + pins));
        }
        rolled[rolls++] = pins;
    }

    @Override
    public boolean isStrike() {
        return (rolls == 1 && rolled[0] == 10) || (rolled[0] != 0 && rolled[1] == 10);
    }

    @Override
    public boolean isSpare() {
        return rolled[0] < 10 && rolled[0] + rolled[1] == 10;
    }
}
