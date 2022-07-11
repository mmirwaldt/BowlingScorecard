package net.mirwaldt.bowling.scorecard;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BowlingGameTest {
    @Test
    void givenNoBallWasRolled_whenNoPinsWereHit_thenScoreIs0() {
        BowlingGame bowlingGame = new BowlingGameByTDD();
        assertEquals(0, bowlingGame.score());
    }
}
