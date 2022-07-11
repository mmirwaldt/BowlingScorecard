package net.mirwaldt.bowling.scorecard;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BowlingGameTest {
    @Test
    @DisplayName("Given no ball is rolled, when no pins are hit, then the score is 0")
    void givenNoBallIsRolled_whenNoPinsAreHit_thenScoreIs0() {
        BowlingGame bowlingGame = new BowlingGameByTDD();
        assertEquals(0, bowlingGame.score());
    }

    @Test
    @DisplayName("Given one ball is rolled, when no pins are hit, then the score is 0")
    void givenOneBallIsRolled_whenNoPinsAreHit_thenScoreIs0() {
        BowlingGame bowlingGame = new BowlingGameByTDD();
        bowlingGame.roll(0);
        assertEquals(0, bowlingGame.score());
    }
}
