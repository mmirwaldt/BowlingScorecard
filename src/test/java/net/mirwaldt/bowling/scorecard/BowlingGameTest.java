package net.mirwaldt.bowling.scorecard;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class BowlingGameTest {
    private final BowlingGame bowlingGame = new BowlingGameByTDD();

    @DisplayName("Given no ball is rolled, when no pins are hit, then the score is 0")
    @Test
    void givenNoBallIsRolled_whenNoPinsAreHit_thenScoreIs0() {
        assertEquals(0, bowlingGame.score());
    }

    @DisplayName("Given one ball is rolled")
    @Nested
    class GivenOneBallIsRolled {
        @DisplayName("when n pin(s) are hit, then the score is n")
        @ParameterizedTest(name = "when {0} pin(s) are hit, then the score is {0}")
        @ValueSource(ints = {0, 1, 2, 9})
        void whenNPinsAreHit_thenScoreIsN(int n) {
            bowlingGame.roll(n);
            assertFalse(bowlingGame.isStrike());
            assertEquals(n, bowlingGame.score());
        }

        @DisplayName("when 10 pins are hit, then it is a strike and the score is 10")
        @Test
        void when10PinsAreHit_thenItIsAStrikeAndScoreIs10() {
            bowlingGame.roll(10);
            assertTrue(bowlingGame.isStrike());
            assertEquals(10, bowlingGame.score());
        }

        @DisplayName("when a invalid number of pins are hit, then throw an IllegalArgumentException")
        @ParameterizedTest(name = "when {0} pins are hit, then throw an IllegalArgumentException")
        @ValueSource(ints = {-1, 11})
        void whenAnInvalidNumberOfPinsAreHit_thenThrowAnIllegalArgumentException(int n) {
            assertThrows(IllegalArgumentException.class, () -> bowlingGame.roll(n));
        }
    }

    @DisplayName("Given two balls are rolled")
    @Nested
    class GivenTwoBallsAreRolled {
        @DisplayName("when no pins are hit, then the score is 0")
        @Test
        void whenNoPinsAreHit_thenScoreIs0() {
            bowlingGame.roll(0);
            bowlingGame.roll(0);
            assertEquals(0, bowlingGame.score());
        }

        @DisplayName("when 1 pin is hit first, then the score is 1")
        @Test
        void whenOnePinIsHitFirst_thenScoreIs1() {
            bowlingGame.roll(1);
            bowlingGame.roll(0);
            assertEquals(1, bowlingGame.score());
        }
    }
}
