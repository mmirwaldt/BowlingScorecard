package net.mirwaldt.bowling.scorecard;

import org.junit.jupiter.api.Test;

public class BowlingGameTest {
    @Test
    void givenNoBallWasRolled_whenNoPinsWereHit_thenScoreIs0() {
        BowlingGame bowlingGame = new BowlingGameByTDD();
    }
}
