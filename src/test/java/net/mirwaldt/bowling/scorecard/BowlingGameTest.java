package net.mirwaldt.bowling.scorecard;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        @DisplayName("when n pin(s) are hit, then the score is n")
        @ParameterizedTest(name = "when {0} pin(s) are hit, then the score is {0}")
        @ValueSource(ints = {0, 1, 2, 9})
        void whenNPinsAreHit_thenScoreIsN(int n) {
            bowlingGame.roll(n);
            assertEquals(n, bowlingGame.score());
        }

        @Test
        @DisplayName("when a negative number of pins is rolled, then throw an IllegalArgumentException")
        void whenANegativeNumberOfPinsIsRolled_thenThrowAnIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class, () -> bowlingGame.roll(-1));
        }

        @Test
        @DisplayName("when a too high number of pins is rolled, then throw an IllegalArgumentException")
        void whenATooHighNumberOfPinsIsRolled_thenThrowAnIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class, () -> bowlingGame.roll(11));
        }
    }
}
