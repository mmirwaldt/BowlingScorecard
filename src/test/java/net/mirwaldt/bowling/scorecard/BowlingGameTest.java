package net.mirwaldt.bowling.scorecard;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
            assertFalse(bowlingGame.isSpare());
            assertEquals(n, bowlingGame.score());
        }

        @DisplayName("when 10 pins are hit, then it is a strike and the score is 10")
        @Test
        void when10PinsAreHit_thenItIsAStrikeAndScoreIs10() {
            bowlingGame.roll(10);
            assertTrue(bowlingGame.isStrike());
            assertFalse(bowlingGame.isSpare());
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
        @DisplayName("when first m pins are hit and second n pins are hit, then the score is m + n")
        @ParameterizedTest(name = "when first {0} pins are hit and second {1} pins are hit, " +
                "then the score is {0} + {1}")
        @CsvSource({"0, 0", "1, 0", "0, 1", "1, 1"})
        void whenNoStrikeAndNoSpare_thenScoreIsNplusM(int m, int n) {
            assertNotEquals(10, m + n);

            bowlingGame.roll(m);
            assertFalse(bowlingGame.isStrike());
            assertFalse(bowlingGame.isSpare());
            assertEquals(m, bowlingGame.score());

            bowlingGame.roll(n);
            assertFalse(bowlingGame.isStrike());
            assertFalse(bowlingGame.isSpare());
            assertEquals(m + n, bowlingGame.score());
        }

        @DisplayName("when a spare is rolled by m and n so that m + n = 10, then the score is 10")
        @ParameterizedTest(name = "when a spare is rolled by {0} and {1} so that {0} + {1} = 10, then the score is 10")
        @CsvSource({"1, 9", "8, 2", "5, 5", "0, 10"})
        void whenSpareIsRolledByMandN_thenScoreIs10(int m, int n) {
            assertEquals(10, m + n);

            bowlingGame.roll(m);
            assertFalse(bowlingGame.isStrike());
            assertFalse(bowlingGame.isSpare());
            assertEquals(m, bowlingGame.score());

            bowlingGame.roll(n);
            assertFalse(bowlingGame.isStrike());
            assertTrue(bowlingGame.isSpare());
            assertEquals(10, bowlingGame.score());
        }

        @DisplayName("when 10 pins are hit first and n second, " +
                "then it is one strike and the score is first 10 and second 10 + n")
        @ParameterizedTest(name = "when 10 pins are hit first and {0} second, " +
                "then score strike first 10 and second 10 + {0}")
        @ValueSource(ints = {0, 3, 4})
        void whenOneStrikeFirstAndNPinsSecond_thenScoreStrikeFirst10AndSecond10plusN(int n) {
            bowlingGame.roll(10);
            assertTrue(bowlingGame.isStrike());
            assertFalse(bowlingGame.isSpare());
            assertEquals(10, bowlingGame.score());

            bowlingGame.roll(n);
            assertFalse(bowlingGame.isStrike());
            assertFalse(bowlingGame.isSpare());
            assertEquals(10 + n, bowlingGame.score());
        }

        @DisplayName("when 10 pins are hit twice, then those rolls are strikes and the score is first 10 and second 20")
        @Test
        void whenTwoStrikes_thenItIsAStrikeAndScoreIs10() {
            bowlingGame.roll(10);
            assertTrue(bowlingGame.isStrike());
            assertFalse(bowlingGame.isSpare());
            assertEquals(10, bowlingGame.score());

            bowlingGame.roll(10);
            assertTrue(bowlingGame.isStrike());
            assertFalse(bowlingGame.isSpare());
            assertEquals(10 + 10, bowlingGame.score());
        }

        @DisplayName("when m pins are hit first and n second so that 10 < m + n," +
                "then throw an IllegalArgumentException")
        @ParameterizedTest(name = "when {0} pins are hit first and {1} second so that 10 < {0} + {1}," +
                "then throw an IllegalArgumentException")
        @CsvSource({"3, 8", "7, 6", "1, 10"})
        void whenMPinsAreHitFirstAndNSecondSoThatMoreThan10PinsAreHit_thenThrowAnIllegalArgumentException(int m, int n) {
            bowlingGame.roll(m);
            assertFalse(bowlingGame.isStrike());
            assertFalse(bowlingGame.isSpare());
            assertEquals(m, bowlingGame.score());

            assertThrows(IllegalArgumentException.class, () -> bowlingGame.roll(n));
        }
    }

    @DisplayName("Given three balls are rolled")
    @Nested
    class GivenThreeBallsAreRolled {
        @DisplayName("when no strikes and no spares by m pins, n pins and p pins are rolled, " +
                "then the score is m + n + p")
        @ParameterizedTest(name = "when no strikes and no spares by {0} pins, {1} pins and {2} pins are rolled, " +
                "then the score is {0} + {1} + {2}")
        @CsvSource({"0, 1, 2", "5, 3, 9"})
        void whenNoStrikeAndNoSpareIsRolledByMandNandP_thenScoreIsFirstMSecondMplusNandThirdMplusNplusP(
                int m, int n, int p) {
            bowlingGame.roll(m);
            assertFalse(bowlingGame.isStrike());
            assertFalse(bowlingGame.isSpare());
            assertEquals(m, bowlingGame.score());

            bowlingGame.roll(n);
            assertFalse(bowlingGame.isStrike());
            assertFalse(bowlingGame.isSpare());
            assertEquals(m + n, bowlingGame.score());

            bowlingGame.roll(p);
            assertFalse(bowlingGame.isStrike());
            assertFalse(bowlingGame.isSpare());
            assertEquals(m + n + p, bowlingGame.score());
        }
    }
}
