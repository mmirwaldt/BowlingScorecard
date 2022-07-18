package net.mirwaldt.bowling.scorecard;

import org.junit.jupiter.api.BeforeEach;
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
        protected int startScore = 0;

        @DisplayName("when n pin(s) are hit, then the score is n")
        @ParameterizedTest(name = "when {0} pin(s) are hit, then the score is {0}")
        @ValueSource(ints = {0, 1, 2, 9})
        void whenNPinsAreHit_thenScoreIsN(int n) {
            bowlingGame.roll(n);
            assertFalse(bowlingGame.isStrike());
            assertFalse(bowlingGame.isSpare());
            assertEquals(startScore + n, bowlingGame.score());
        }

        @DisplayName("when 10 pins are hit, then it is a strike and the score is 10")
        @Test
        void when10PinsAreHit_thenItIsAStrikeAndScoreIs10() {
            bowlingGame.roll(10);
            assertTrue(bowlingGame.isStrike());
            assertFalse(bowlingGame.isSpare());
            assertEquals(startScore + 10, bowlingGame.score());
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
        protected int startScore = 0;
        protected int startFrame = 0;

        @DisplayName("when first m pins are hit and second n pins are hit, then the score is m + n")
        @ParameterizedTest(name = "when first {0} pins are hit and second {1} pins are hit, " +
                "then the score is {0} + {1}")
        @CsvSource({"0, 0", "1, 0", "0, 1", "1, 1"})
        void whenNoStrikeAndNoSpare_thenScoreIsNplusM(int m, int n) {
            assertNotEquals(10, m + n);

            bowlingGame.roll(m);
            assertFalse(bowlingGame.isStrike());
            assertFalse(bowlingGame.isSpare());
            assertEquals(startScore + m, bowlingGame.score());

            bowlingGame.roll(n);
            assertFalse(bowlingGame.isStrike());
            assertFalse(bowlingGame.isSpare());
            assertEquals(startScore + m + n, bowlingGame.score());
        }

        @DisplayName("when a spare is rolled by m and n so that m + n = 10, then the score is 10")
        @ParameterizedTest(name = "when a spare is rolled by {0} and {1} so that {0} + {1} = 10, then the score is 10")
        @CsvSource({"1, 9", "8, 2", "5, 5", "0, 10"})
        void whenSpareIsRolledByMandN_thenScoreIs10(int m, int n) {
            assertEquals(10, m + n);

            bowlingGame.roll(m);
            assertFalse(bowlingGame.isStrike());
            assertFalse(bowlingGame.isSpare());
            assertEquals(startScore + m, bowlingGame.score());

            bowlingGame.roll(n);
            assertFalse(bowlingGame.isStrike());
            assertTrue(bowlingGame.isSpare());
            assertEquals(startScore + 10, bowlingGame.score());
        }

        @DisplayName("when 10 pins are hit first and n second, " +
                "then it is one strike and the score is first 10 and second 10 + n")
        @ParameterizedTest(name = "when 10 pins are hit first and {0} second, " +
                "then score strike first 10 and second 10 + {0}")
        @ValueSource(ints = {0, 3, 4})
        void whenOneStrikeFirstAndNPinsSecond_thenScoreIs10plusTwotimesN(int n) {
            bowlingGame.roll(10);
            assertTrue(bowlingGame.isStrike());
            assertFalse(bowlingGame.isSpare());
            assertEquals(startScore + 10, bowlingGame.score());

            bowlingGame.roll(n);
            assertFalse(bowlingGame.isStrike());
            assertFalse(bowlingGame.isSpare());
            assertEquals(startScore + 10 + n, bowlingGame.score(startFrame + 1));
            assertEquals(bowlingGame.score(startFrame + 1) + n, bowlingGame.score());
        }

        @DisplayName("when 10 pins are hit twice, then those rolls are strikes and the score is first 10 and second 20")
        @Test
        void whenTwoStrikes_thenScoreIs30() {
            bowlingGame.roll(10);
            assertTrue(bowlingGame.isStrike());
            assertFalse(bowlingGame.isSpare());
            assertEquals(startScore + 10, bowlingGame.score());

            bowlingGame.roll(10);
            assertTrue(bowlingGame.isStrike());
            assertFalse(bowlingGame.isSpare());
            assertEquals(startScore + 20, bowlingGame.score(startFrame + 1));
            assertEquals(bowlingGame.score(startFrame + 1) + 10, bowlingGame.score());
        }

        @DisplayName("when m pins are hit first and n second so that 10 < m + n," +
                "then throw an IllegalArgumentException")
        @ParameterizedTest(name = "when {0} pins are hit first and {1} second so that 10 < {0} + {1}," +
                "then throw an IllegalArgumentException")
        @CsvSource({"3, 8", "7, 6", "1, 10"})
        void whenMoreThan10PinsAreHit_thenThrowAnIllegalArgumentException(int m, int n) {
            bowlingGame.roll(m);
            assertFalse(bowlingGame.isStrike());
            assertFalse(bowlingGame.isSpare());
            assertEquals(startScore + m, bowlingGame.score());

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
        @CsvSource({"0, 0, 0", "0, 1, 2", "4, 0, 7", "0, 0, 8", "3, 6, 0", "5, 3, 9"})
        void whenNoStrikeAndNoSpareIsRolledByMandNandPpins_thenScoreIsMplusNplusP(int m, int n, int p) {
            assertNotEquals(10, m + n);

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
            assertEquals(m + n, bowlingGame.score(1));
            assertEquals(bowlingGame.score(1) + p, bowlingGame.score());
        }

        @DisplayName("when 10 pins are hit three times, " +
                "then those rolls are strikes and the score is first 10 and second 20 and third 30")
        @Test
        void whenThreeStrikes_thenScoreIs60() {
            bowlingGame.roll(10);
            assertTrue(bowlingGame.isStrike());
            assertFalse(bowlingGame.isSpare());
            assertEquals(10, bowlingGame.score());

            bowlingGame.roll(10);
            assertTrue(bowlingGame.isStrike());
            assertFalse(bowlingGame.isSpare());
            assertEquals(20, bowlingGame.score(1));
            assertEquals(30, bowlingGame.score());

            bowlingGame.roll(10);
            assertTrue(bowlingGame.isStrike());
            assertFalse(bowlingGame.isSpare());
            assertEquals(30, bowlingGame.score(1));
            assertEquals(50, bowlingGame.score(2));
            assertEquals(60, bowlingGame.score());
        }

        @DisplayName("when two strikes are rolled first and third roll hits n pins, " +
                "then the score is first 10, second 20 + 10 and third 20 + 10 + 3 * n")
        @ParameterizedTest(name = "when two strikes are rolled first and third roll hits {0} pins, " +
                "then the score is first 10, second 20 + 10 and third 20 + 10 + 3 * {0}")
        @ValueSource(ints = {0, 2, 6, 9})
        void whenTwoStrikesAreRolledFirstAndThirdRollHitsNPins_thenScoreIs30plusThreeTimesN(int n) {
            bowlingGame.roll(10);
            assertTrue(bowlingGame.isStrike());
            assertFalse(bowlingGame.isSpare());
            assertEquals(10, bowlingGame.score());

            bowlingGame.roll(10);
            assertTrue(bowlingGame.isStrike());
            assertFalse(bowlingGame.isSpare());
            assertEquals(20, bowlingGame.score(1));
            assertEquals(30, bowlingGame.score());

            bowlingGame.roll(n);
            assertFalse(bowlingGame.isStrike());
            assertFalse(bowlingGame.isSpare());
            assertEquals(20 + n, bowlingGame.score(1));
            assertEquals(30 + 2 * n, bowlingGame.score(2));
            assertEquals(30 + 3 * n, bowlingGame.score());
        }

        @DisplayName("when one strikes is rolled first and second roll hits m pins and third roll hits n pins, " +
                "then the score is first 10, second 10 + m and third 10 + m + n")
        @ParameterizedTest(name = "when two strikes are rolled first and third roll hits {0} pins, " +
                "then the score is first 10, second 10 + {0} and third 10 + {0} + {1}")
        @CsvSource({"0, 0", "0, 4", "5, 0", "3, 3", "1, 2", "6, 3"})
        void whenOneStrikeFirstAndSecondAndThirdRollHitMandNPins_thenScoreIs10plusTwoTimesSumOfMplusN(int m, int n) {
            bowlingGame.roll(10);
            assertTrue(bowlingGame.isStrike());
            assertFalse(bowlingGame.isSpare());
            assertEquals(10, bowlingGame.score());

            bowlingGame.roll(m);
            assertFalse(bowlingGame.isStrike());
            assertFalse(bowlingGame.isSpare());
            assertEquals(10 + m, bowlingGame.score(1));
            assertEquals(10 + 2 * m, bowlingGame.score());

            bowlingGame.roll(n);
            assertFalse(bowlingGame.isStrike());
            assertFalse(bowlingGame.isSpare());
            assertEquals(10 + m + n, bowlingGame.score(1));
            assertEquals(10 + 2 * (m + n), bowlingGame.score());
        }

        @DisplayName("when one strike is rolled first and one spare is rolled second by m and n, " +
                "then the score is first 10, second 10 + m and third 10 + m + n")
        @ParameterizedTest(name = "when two strikes are rolled first and third roll hits {0} pins, " +
                "then the score is first 10, second 10 + {0} and third 10 + {0} + {1}")
        @CsvSource({"0, 10", "3, 7", "9, 1", "5, 5"})
        void whenOneStrikeFirstAndOneSpareSecondByMandN_thenScoreIs30(int m, int n) {
            assertEquals(10, m + n);

            bowlingGame.roll(10);
            assertTrue(bowlingGame.isStrike());
            assertFalse(bowlingGame.isSpare());
            assertEquals(10, bowlingGame.score());

            bowlingGame.roll(m);
            assertFalse(bowlingGame.isStrike());
            assertFalse(bowlingGame.isSpare());
            assertEquals(10 + m, bowlingGame.score(1));
            assertEquals(bowlingGame.score(1) + m, bowlingGame.score());

            bowlingGame.roll(n);
            assertFalse(bowlingGame.isStrike());
            assertTrue(bowlingGame.isSpare());
            assertEquals(20, bowlingGame.score(1));
            assertEquals(30, bowlingGame.score());
        }

        @DisplayName("when one spare is rolled first by m and n and third roll hits p pins, " +
                "then the score is first m and second m + n = 10 and third m + n + 2 * p")
        @ParameterizedTest(name = "when one spare is rolled first by {0} and {1} and third roll hits {2} pins, " +
                "then the score is first {0}, second {0} + {1} and third {0} + {1} + 2 * {2}")
        @CsvSource({"0, 10, 0", "0, 10, 1", "5, 5, 0", "8, 2, 1", "1, 9, 3"})
        void whenOneSpareFirstByMandNandThirdRollHitsPpins_thenScoreIs10plusP(int m, int n, int p) {
            assertEquals(10, m + n);

            bowlingGame.roll(m);
            assertFalse(bowlingGame.isStrike());
            assertFalse(bowlingGame.isSpare());
            assertEquals(m, bowlingGame.score());

            bowlingGame.roll(n);
            assertFalse(bowlingGame.isStrike());
            assertTrue(bowlingGame.isSpare());
            assertEquals(m + n, bowlingGame.score());

            bowlingGame.roll(p);
            assertFalse(bowlingGame.isStrike());
            assertFalse(bowlingGame.isSpare());
            assertEquals(m + n + p, bowlingGame.score(1));
            assertEquals(bowlingGame.score(1) + p, bowlingGame.score());
        }

        @DisplayName("when one spare is rolled first by m and n and third roll is strike, " +
                "then the score is first m and second m + n = 10 and third m + n + 2 * p")
        @ParameterizedTest(name = "when one spare is rolled first by {0} and {1} and third roll is strike, " +
                "then the score is first {0}, second {0} + {1} and third {0} + {1} + 2 * {2}")
        @CsvSource({"0, 10", "4, 6", "7, 3"})
        void whenOneSpareFirstByMandNandThirdRollIsStrike_thenScoreIs30(int m, int n) {
            assertEquals(10, m + n);

            bowlingGame.roll(m);
            assertFalse(bowlingGame.isStrike());
            assertFalse(bowlingGame.isSpare());
            assertEquals(m, bowlingGame.score());

            bowlingGame.roll(n);
            assertFalse(bowlingGame.isStrike());
            assertTrue(bowlingGame.isSpare());
            assertEquals(10, bowlingGame.score());

            bowlingGame.roll(10);
            assertTrue(bowlingGame.isStrike());
            assertFalse(bowlingGame.isSpare());
            assertEquals(20, bowlingGame.score(1));
            assertEquals(30, bowlingGame.score());
        }
    }

    @DisplayName("Given four balls are rolled")
    @Nested
    class GivenFourBallsAreRolled {
        @DisplayName("when no strikes and no spares by m pins, n pins and p pins and q pins are rolled, " +
                "then the score is m + n + p + q")
        @ParameterizedTest(name = "when no strikes and no spares by {0} pins, {1} pins and {2} pins are rolled, " +
                "then the score is {0} + {1} + {2} + {3}")
        @CsvSource({"0, 0, 0, 0", "0, 1, 2, 3", "0, 0, 5, 1", "3, 4, 0, 0", "7, 1, 2, 0", "8, 1, 5, 3"})
        void whenNoStrikeAndNoSpareIsRolledByMandNandPandQ_thenScoreIsMplusNplusPplusQ(int m, int n, int p, int q) {
            assertNotEquals(10, m + n);
            assertNotEquals(10, p + q);

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
            assertEquals(m + n, bowlingGame.score(1));
            assertEquals(bowlingGame.score(1) + p, bowlingGame.score());

            bowlingGame.roll(q);
            assertFalse(bowlingGame.isStrike());
            assertFalse(bowlingGame.isSpare());
            assertEquals(m + n, bowlingGame.score(1));
            assertEquals(bowlingGame.score(1) + p + q, bowlingGame.score());
        }

        @DisplayName("when 10 pins are hit four times, then those rolls are 4 strikes and the score is 90")
        @Test
        void whenFourStrikes_thenItIsAStrikeAndScoreIs10() {
            bowlingGame.roll(10);
            assertTrue(bowlingGame.isStrike());
            assertFalse(bowlingGame.isSpare());
            assertEquals(10, bowlingGame.score());

            bowlingGame.roll(10);
            assertTrue(bowlingGame.isStrike());
            assertFalse(bowlingGame.isSpare());
            assertEquals(20, bowlingGame.score(1));
            assertEquals(bowlingGame.score(1) + 10, bowlingGame.score());

            bowlingGame.roll(10);
            assertTrue(bowlingGame.isStrike());
            assertFalse(bowlingGame.isSpare());
            assertEquals(30, bowlingGame.score(1));
            assertEquals(50, bowlingGame.score(2));
            assertEquals(60, bowlingGame.score());

            bowlingGame.roll(10);
            assertTrue(bowlingGame.isStrike());
            assertFalse(bowlingGame.isSpare());
            assertEquals(30, bowlingGame.score(1));
            assertEquals(60, bowlingGame.score(2));
            assertEquals(80, bowlingGame.score(3));
            assertEquals(90, bowlingGame.score());
        }

        @DisplayName("when three strikes are rolled first and fourth roll hits n pins, " +
                "then the score is first 10, second 20 + 10 and third 30 + 20 + 10 and fourth 30 + 20 + 10 + 3 * n")
        @ParameterizedTest(name = "when three strikes are rolled first and fourth roll hits {0} pins, " +
                "then the score is first 10, second 20 + 10 and third 30 + 20 + 10 and fourth 30 + 20 + 10 + 3 * {0}")
        @ValueSource(ints = {0, 1, 5, 9})
        void whenThreeStrikesAreRolledFirstAndFourthRollHitsNPins_thenScoreIs60plusThreeTimesN(int n) {
            bowlingGame.roll(10);
            assertTrue(bowlingGame.isStrike());
            assertFalse(bowlingGame.isSpare());
            assertEquals(10, bowlingGame.score());

            bowlingGame.roll(10);
            assertTrue(bowlingGame.isStrike());
            assertFalse(bowlingGame.isSpare());
            assertEquals(20, bowlingGame.score(1));
            assertEquals(bowlingGame.score(1) + 10, bowlingGame.score());

            bowlingGame.roll(10);
            assertTrue(bowlingGame.isStrike());
            assertFalse(bowlingGame.isSpare());
            assertEquals(30, bowlingGame.score(1));
            assertEquals(bowlingGame.score(1) + 20, bowlingGame.score(2));
            assertEquals(bowlingGame.score(2) + 10, bowlingGame.score());

            bowlingGame.roll(n);
            assertFalse(bowlingGame.isStrike());
            assertFalse(bowlingGame.isSpare());
            assertEquals(30, bowlingGame.score(1));
            assertEquals(bowlingGame.score(1) + 20 + n, bowlingGame.score(2));
            assertEquals(bowlingGame.score(2) + 10 + n, bowlingGame.score(3));
            assertEquals(bowlingGame.score(3) + n, bowlingGame.score());
        }

        @DisplayName("when two spares by m pins and n pins and p pins and q pins are rolled, " +
                "then the score is m + n + 2 * p + q")
        @ParameterizedTest(name = "when no strikes and no spares by {0} pins and {1} pins " +
                "and {2} pins and {3} pins are rolled, then the score is {0} + {1} + 2 * {2} + {3}")
        @CsvSource({"0, 10, 0, 10", "0, 10, 1, 9", "3, 7, 0, 10", "8, 2, 5, 5"})
        void whenTwoSparesAreRolledByMandNandPandQ_thenScoreIsMplusNplusTwoTimesPplusQ(
                int m, int n, int p, int q) {
            assertEquals(10, m + n);
            assertEquals(10, p + q);

            bowlingGame.roll(m);
            assertFalse(bowlingGame.isStrike());
            assertFalse(bowlingGame.isSpare());
            assertEquals(m, bowlingGame.score());

            bowlingGame.roll(n);
            assertFalse(bowlingGame.isStrike());
            assertTrue(bowlingGame.isSpare());
            assertEquals(10, bowlingGame.score());

            bowlingGame.roll(p);
            assertFalse(bowlingGame.isStrike());
            assertFalse(bowlingGame.isSpare());
            assertEquals(10 + p, bowlingGame.score(1));
            assertEquals(10 + 2 * p, bowlingGame.score());

            bowlingGame.roll(q);
            assertFalse(bowlingGame.isStrike());
            assertTrue(bowlingGame.isSpare());
            assertEquals(10 + p, bowlingGame.score(1));
            assertEquals(10 + 2 * p + q, bowlingGame.score());
        }
    }

    @DisplayName("Given five balls are rolled")
    @Nested
    class GivenFiveBallsAreRolled {
        @DisplayName("when two spares by m pins, n pins, p pins, q pins and then r pins are rolled, " +
                "then the score is m + n + 2 * p + q + 2 * r")
        @ParameterizedTest(name = "when no strikes and no spares by {0} pins, {1} pins and {2} pins are rolled, " +
                "then the score is {0} + {1} + 2 * {2} + {3} + 2 * {4}")
        @CsvSource({"0, 10, 0, 10, 0", "0, 10, 0, 10, 9", "3, 7, 8, 2, 1", "5, 5, 0, 10, 5"})
        void whenTwoSparesAreRolled_thenScoreIsMplusNplus2PplusQplus2R(int m, int n, int p, int q, int r) {
            assertEquals(10, m + n);
            assertEquals(10, p + q);

            bowlingGame.roll(m);
            assertFalse(bowlingGame.isStrike());
            assertFalse(bowlingGame.isSpare());
            assertEquals(m, bowlingGame.score());

            bowlingGame.roll(n);
            assertFalse(bowlingGame.isStrike());
            assertTrue(bowlingGame.isSpare());
            assertEquals(10, bowlingGame.score());

            bowlingGame.roll(p);
            assertFalse(bowlingGame.isStrike());
            assertFalse(bowlingGame.isSpare());
            assertEquals(10 + p, bowlingGame.score(1));
            assertEquals(10 + 2 * p, bowlingGame.score());

            bowlingGame.roll(q);
            assertFalse(bowlingGame.isStrike());
            assertTrue(bowlingGame.isSpare());
            assertEquals(10 + p, bowlingGame.score(1));
            assertEquals(10 + 2 * p + q, bowlingGame.score());

            bowlingGame.roll(r);
            assertFalse(bowlingGame.isStrike());
            assertFalse(bowlingGame.isSpare());
            assertEquals(10 + p, bowlingGame.score(1));
            assertEquals(bowlingGame.score(1) + p + q + r, bowlingGame.score(2));
            assertEquals(bowlingGame.score(2) + r, bowlingGame.score());
        }
    }

    @DisplayName("Given all balls are rolled")
    @Nested
    class GivenAllBallsAreRolled {
        @DisplayName("when always no pins, then the score is 0")
        @Test
        void whenAlwaysNoPins_thenScoreIs0() {
            for (int i = 0; i < 11; i++) {
                bowlingGame.roll(0);
            }
            assertEquals(0, bowlingGame.score());
        }

        @DisplayName("when always strike, then the score is 300")
        @Test
        void whenAlwaysStrike_thenScoreIs300() {
            for (int i = 0; i < 12; i++) {
                bowlingGame.roll(10);
            }
            assertEquals(300, bowlingGame.score());
        }

        @DisplayName("when 9 strikes first and neither a strike nor a spare in last frame 10, then no bonus")
        @Test
        void when9StrikesAndNeitherStrikeNorSpareInLastFrame10_thenNoBonus() {
            for (int i = 0; i < 9; i++) {
                bowlingGame.roll(10);
            }
            bowlingGame.roll(0);
            bowlingGame.roll(0);
            assertThrows(IllegalStateException.class, () -> bowlingGame.roll(0));
        }

        @DisplayName("when 9 strikes and one spare by m pins and n pins and third p pins, " +
                "then the score 7 * 30 + 20 + 10 + 3 * m + 2 * n + p")
        @ParameterizedTest(name = "when 9 strikes and one spare by {0} pins and {1} pins and third {2} pins, " +
                "then the score 7 * 30 + 20 + 10 + 3 * {0} + 2 * {1} + {2}")
        @CsvSource({"2, 8, 0", "5, 5, 3"})
        void when9StrikesAndOneSpareByMandNandLastRollIsP_thenScoreIs262(int m, int n, int p) {
            assertEquals(10, m + n);
            for (int i = 0; i < 9; i++) {
                bowlingGame.roll(10);
            }
            assertEquals(7 * 30 + 20 + 10, bowlingGame.score());

            bowlingGame.roll(m);
            assertEquals(7 * 30 + 20 + 10 + 2 * m, bowlingGame.score());

            bowlingGame.roll(n);
            assertEquals(7 * 30 + 20 + 10 + 2 * m + n, bowlingGame.score());

            bowlingGame.roll(p);

            assertEquals(7 * 30 + 20 + 10 + 2 * m + n, bowlingGame.score(9));
            assertEquals(bowlingGame.score(9) + m + n + p, bowlingGame.score());
        }
    }

    @DisplayName("Given one ball is rolled after one frame")
    @Nested
    class GivenOneBallIsRolledAfterOneFrame extends GivenOneBallIsRolled {
        @BeforeEach
        void startPlaying() {
            bowlingGame.roll(2);
            bowlingGame.roll(5);
            startScore = 2 + 5;
        }
    }

    @DisplayName("Given one ball is rolled after two frames")
    @Nested
    class GivenOneRollAfterTwoFrames extends GivenOneBallIsRolled {
        @BeforeEach
        void startPlaying() {
            bowlingGame.roll(4);
            bowlingGame.roll(1);
            bowlingGame.roll(0);
            bowlingGame.roll(8);
            startScore = 4 + 1 + 8;
        }
    }

    @DisplayName("Given two balls are rolled after one frame")
    @Nested
    class GivenTwoBallAreRolledAfterOneFrame extends GivenTwoBallsAreRolled {
        @BeforeEach
        void startPlaying() {
            bowlingGame.roll(2);
            bowlingGame.roll(5);
            startScore = 2 + 5;
            startFrame = 1;
        }
    }
}
