package net.mirwaldt.bowling.scorecard;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BowlingGameTest {
    private final BowlingGame bowlingGame = new BowlingGameByTDD();

    @Test
    @DisplayName("Given no ball is rolled, when no pins are hit, then the score is 0")
    void givenNoBallIsRolled_whenNoPinsAreHit_thenScoreIs0() {
        assertEquals(0, bowlingGame.score());
    }

    @Test
    @DisplayName("Given one ball is rolled, when no pins are hit, then the score is 0")
    void givenOneBallIsRolled_whenNoPinsAreHit_thenScoreIs0() {
        bowlingGame.roll(0);
        assertEquals(0, bowlingGame.score());
    }
}
