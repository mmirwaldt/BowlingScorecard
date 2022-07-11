package net.mirwaldt.bowling.scorecard;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BowlingGameTest {
    private final BowlingGame bowlingGame = new BowlingGameByTDD();

    @Test
    @DisplayName("Given no ball is rolled, when no pins are hit, then the score is 0")
    void givenNoBallIsRolled_whenNoPinsAreHit_thenScoreIs0() {
        assertEquals(0, bowlingGame.score());
    }

    @Nested
    @DisplayName("Given one ball is rolled")
    class GivenOneBallIsRolled {
        @Test
        @DisplayName("when no pins are hit, then the score is 0")
        void whenNoPinsAreHit_thenScoreIs0() {
            bowlingGame.roll(0);
            assertEquals(0, bowlingGame.score());
        }

        @Test
        @DisplayName("when 1 pin is hit, then the score is 1")
        void when1PinIsHit_thenScoreIs1() {
            bowlingGame.roll(1);
            assertEquals(1, bowlingGame.score());
        }

        @Test
        @DisplayName("when 2 pins are hit, then the score is 2")
        void when2PinsAreHit_thenScoreIs2() {
            bowlingGame.roll(2);
            assertEquals(2, bowlingGame.score());
        }
    }
}
