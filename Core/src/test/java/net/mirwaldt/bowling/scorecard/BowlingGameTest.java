package net.mirwaldt.bowling.scorecard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public abstract class BowlingGameTest {
    protected BowlingGame game;

    @DisplayName("Given no roll, when no pins are hit, then the score is 0")
    @Test
    void givenNoRoll_whenNoPinsAreHit_thenScoreIs0() {
        assertEquals(0, game.score());
    }

    @DisplayName("Given one roll")
    @Nested
    class GivenOneRoll {
        protected int startFrame = 0;

        @DisplayName("when n pin(s) are hit, then the score is n more")
        @ParameterizedTest(name = "when {0} pin(s) are hit, then the score is {0} more")
        @ValueSource(ints = {0, 1, 2, 9})
        void whenNPinsAreHit_thenScoreIsNmore(int n) {
            game.roll(n);
            assertFalse(game.isStrike());
            assertFalse(game.isSpare());
            assertEquals(game.score(startFrame) + n, game.score());

            assertFalse(game.isOver());
        }

        @DisplayName("when 10 pins are hit, then it is a strike and the score is 10 more")
        @Test
        void when10PinsAreHit_thenItIsAStrikeAndScoreIs10more() {
            game.roll(10);
            assertTrue(game.isStrike());
            assertFalse(game.isSpare());
            assertEquals(game.score(startFrame) + 10, game.score());

            assertFalse(game.isOver());
        }

        @DisplayName("when a invalid number of pins are hit, then throw an IllegalArgumentException")
        @ParameterizedTest(name = "when {0} pins are hit, then throw an IllegalArgumentException")
        @ValueSource(ints = {-1, 11})
        void whenAnInvalidNumberOfPinsAreHit_thenThrowAnIllegalArgumentException(int n) {
            assertThrows(IllegalArgumentException.class, () -> game.roll(n));
        }
    }

    @DisplayName("Given two rolls")
    @Nested
    class GivenTwoRolls {
        protected int startFrame = 0;

        @DisplayName("when first m pins are hit and second n pins are hit, then the score is m + n more")
        @ParameterizedTest(name = "when first {0} pins are hit and second {1} pins are hit, " +
                "then the score is {0} + {1} more")
        @CsvSource({"0, 0", "1, 0", "0, 1", "1, 1"})
        void whenNoStrikeAndNoSpare_thenScoreIsNplusMmore(int m, int n) {
            assertNotEquals(10, m + n);

            game.roll(m);
            assertFalse(game.isStrike());
            assertFalse(game.isSpare());
            assertEquals(game.score(startFrame) + m, game.score());

            game.roll(n);
            assertFalse(game.isStrike());
            assertFalse(game.isSpare());
            assertEquals(game.score(startFrame) + m + n, game.score());

            assertFalse(game.isOver());
        }

        @DisplayName("when a spare is rolled by m and n so that m + n = 10, then the score is 10 more")
        @ParameterizedTest(name = "when a spare is rolled by {0} and {1} so that {0} + {1} = 10, " +
                "then the score is 10 more")
        @CsvSource({"1, 9", "8, 2", "5, 5", "0, 10"})
        void whenSpare_thenScoreIs10more(int m, int n) {
            assertEquals(10, m + n);

            game.roll(m);
            assertFalse(game.isStrike());
            assertFalse(game.isSpare());
            assertEquals(game.score(startFrame) + m, game.score());

            game.roll(n);
            assertFalse(game.isStrike());
            assertTrue(game.isSpare());
            assertEquals(game.score(startFrame) + 10, game.score());

            assertFalse(game.isOver());
        }

        @DisplayName("when 10 pins are hit first and n second, " +
                "then it is one strike and the score is first 10 and second 10 + n more")
        @ParameterizedTest(name = "when 10 pins are hit first and {0} second, " +
                "then score strike first 10 and second 10 + {0} more")
        @ValueSource(ints = {0, 3, 4})
        void whenOneStrike_thenScoreIs10plusTwoTimesNmore(int n) {
            game.roll(10);
            assertTrue(game.isStrike());
            assertFalse(game.isSpare());
            assertEquals(game.score(startFrame) + 10, game.score());

            game.roll(n);
            assertFalse(game.isStrike());
            assertFalse(game.isSpare());
            assertEquals(game.score(startFrame) + 10 + n, game.score(startFrame + 1));
            assertEquals(game.score(startFrame + 1) + n, game.score());

            assertFalse(game.isOver());
        }

        @DisplayName("when two strikes, then score is 30 more")
        @Test
        void whenTwoStrikes_thenScoreIs30more() {
            game.roll(10);
            assertTrue(game.isStrike());
            assertFalse(game.isSpare());
            assertEquals(game.score(startFrame) + 10, game.score());

            game.roll(10);
            assertTrue(game.isStrike());
            assertFalse(game.isSpare());
            assertEquals(game.score(startFrame) + 20, game.score(startFrame + 1));
            assertEquals(game.score(startFrame + 1) + 10, game.score());

            assertFalse(game.isOver());
        }

        @DisplayName("when m pins are hit first and n second so that 10 < m + n," +
                "then throw an IllegalArgumentException")
        @ParameterizedTest(name = "when {0} pins are hit first and {1} second so that 10 < {0} + {1}," +
                "then throw an IllegalArgumentException")
        @CsvSource({"3, 8", "7, 6", "1, 10"})
        void whenMoreThan10PinsAreHit_thenThrowAnIllegalArgumentException(int m, int n) {
            game.roll(m);
            assertFalse(game.isStrike());
            assertFalse(game.isSpare());
            assertEquals(game.score(startFrame) + m, game.score());

            assertFalse(game.isOver());

            assertThrows(IllegalArgumentException.class, () -> game.roll(n));
        }
    }

    @DisplayName("Given three rolls")
    @Nested
    class GivenThreeRolls {
        protected int startFrame = 0;

        @DisplayName("when no strikes and no spares by m pins, n pins and p pins are rolled, " +
                "then the score is m + n + p more")
        @ParameterizedTest(name = "when no strikes and no spares by {0} pins, {1} pins and {2} pins are rolled, " +
                "then the score is {0} + {1} + {2} more")
        @CsvSource({"0, 0, 0", "0, 1, 2", "4, 0, 7", "0, 0, 8", "3, 6, 0", "5, 3, 9"})
        void whenNoStrikeAndNoSpare_thenScoreIsMplusNplusPmore(int m, int n, int p) {
            assertNotEquals(10, m + n);

            game.roll(m);
            assertFalse(game.isStrike());
            assertFalse(game.isSpare());
            assertEquals(game.score(startFrame) + m, game.score());

            game.roll(n);
            assertFalse(game.isStrike());
            assertFalse(game.isSpare());
            assertEquals(game.score(startFrame) + m + n, game.score());

            game.roll(p);
            assertFalse(game.isStrike());
            assertFalse(game.isSpare());
            assertEquals(game.score(startFrame) + m + n, game.score(startFrame + 1));
            assertEquals(game.score(startFrame + 1) + p, game.score());

            assertFalse(game.isOver());
        }

        @DisplayName("when three strikes, then score is 60 more")
        @Test
        void whenThreeStrikes_thenScoreIs60more() {
            game.roll(10);
            assertTrue(game.isStrike());
            assertFalse(game.isSpare());
            assertEquals(game.score(startFrame) + 10, game.score());

            game.roll(10);
            assertTrue(game.isStrike());
            assertFalse(game.isSpare());
            assertEquals(game.score(startFrame) + 20, game.score(startFrame + 1));
            assertEquals(game.score(startFrame + 1) + 10, game.score());

            game.roll(10);
            assertTrue(game.isStrike());
            assertFalse(game.isSpare());
            assertEquals(game.score(startFrame) + 30, game.score(startFrame + 1));
            assertEquals(game.score(startFrame + 1) + 20, game.score(startFrame + 2));
            assertEquals(game.score(startFrame + 2) + 10, game.score());

            assertFalse(game.isOver());
        }

        @DisplayName("when two strikes, then the score is 20 + 10 + 3 * n more")
        @ParameterizedTest(name = "when two strikes, then score is 20 + 10 + 3 * {0} more")
        @ValueSource(ints = {0, 2, 6, 9})
        void whenTwoStrikes_thenScoreIs30plusThreeTimesNmore(int n) {
            game.roll(10);
            assertTrue(game.isStrike());
            assertFalse(game.isSpare());
            assertEquals(game.score(startFrame) + 10, game.score());

            game.roll(10);
            assertTrue(game.isStrike());
            assertFalse(game.isSpare());
            assertEquals(game.score(startFrame) + 20, game.score(startFrame + 1));
            assertEquals(game.score(startFrame + 1) + 10, game.score());

            game.roll(n);
            assertFalse(game.isStrike());
            assertFalse(game.isSpare());
            assertEquals(game.score(startFrame) + 20 + n, game.score(startFrame + 1));
            assertEquals(game.score(startFrame + 1) + 10 + n, game.score(startFrame + 2));
            assertEquals(game.score(startFrame + 2) + n, game.score());

            assertFalse(game.isOver());
        }

        @DisplayName("when one strike, then the score is 10 + m + n more")
        @ParameterizedTest(name = "when two strikes, then the score is 10 + {0} + {1} more")
        @CsvSource({"0, 0", "0, 4", "5, 0", "3, 3", "1, 2", "6, 3"})
        void whenOneStrike_thenScoreIs10plusTwoTimesSumOfMplusNMore(int m, int n) {
            game.roll(10);
            assertTrue(game.isStrike());
            assertFalse(game.isSpare());
            assertEquals(game.score(startFrame) + 10, game.score());

            game.roll(m);
            assertFalse(game.isStrike());
            assertFalse(game.isSpare());
            assertEquals(game.score(startFrame) + 10 + m, game.score(startFrame + 1));
            assertEquals(game.score(startFrame + 1) +  m, game.score());

            game.roll(n);
            assertFalse(game.isStrike());
            assertFalse(game.isSpare());
            assertEquals(game.score(startFrame) + 10 + m + n, game.score(startFrame + 1));
            assertEquals(game.score(startFrame + 1) + m + n, game.score());

            assertFalse(game.isOver());
        }

        @DisplayName("when one strike and one spare, then the score is 10 + m + n more")
        @ParameterizedTest(name = "when one strikes and one spare, then the score is 10 + {0} + {1} more")
        @CsvSource({"0, 10", "3, 7", "9, 1", "5, 5"})
        void whenOneStrikeFirstAndOneSpareSecondByMandN_thenScoreIs10plusMplusNmore(int m, int n) {
            assertEquals(10, m + n);

            game.roll(10);
            assertTrue(game.isStrike());
            assertFalse(game.isSpare());
            assertEquals(game.score(startFrame) + 10, game.score());

            game.roll(m);
            assertFalse(game.isStrike());
            assertFalse(game.isSpare());
            assertEquals(game.score(startFrame) + 10 + m, game.score(startFrame + 1));
            assertEquals(game.score(startFrame + 1) + m, game.score());

            game.roll(n);
            assertFalse(game.isStrike());
            assertTrue(game.isSpare());
            assertEquals(game.score(startFrame) + 20, game.score(startFrame + 1));
            assertEquals(game.score(startFrame + 1) + 10, game.score());

            assertFalse(game.isOver());
        }

        @DisplayName("when one spare, then the score m + n + 2 * p more")
        @ParameterizedTest(name = "when one spare , then the score is {0} + {1} + 2 * {2} more")
        @CsvSource({"0, 10, 0", "0, 10, 1", "5, 5, 0", "8, 2, 1", "1, 9, 3"})
        void whenOneSpare_thenScoreIsMplusNplus2TimesPmore(int m, int n, int p) {
            assertEquals(10, m + n);

            game.roll(m);
            assertFalse(game.isStrike());
            assertFalse(game.isSpare());
            assertEquals(game.score(startFrame) + m, game.score());

            game.roll(n);
            assertFalse(game.isStrike());
            assertTrue(game.isSpare());
            assertEquals(game.score(startFrame) + m + n, game.score());

            game.roll(p);
            assertFalse(game.isStrike());
            assertFalse(game.isSpare());
            assertEquals(game.score(startFrame) + m + n + p, game.score(startFrame + 1));
            assertEquals(game.score(startFrame + 1) + p, game.score());

            assertFalse(game.isOver());
        }

        @DisplayName("when one spare and one strike, then the score 30 more")
        @ParameterizedTest(name = "when one spare and one strike, then the score is 30 more")
        @CsvSource({"0, 10", "4, 6", "7, 3"})
        void whenOneSpareAndOneStrike_thenScoreIs30more(int m, int n) {
            assertEquals(10, m + n);

            game.roll(m);
            assertFalse(game.isStrike());
            assertFalse(game.isSpare());
            assertEquals(game.score(startFrame) + m, game.score());

            game.roll(n);
            assertFalse(game.isStrike());
            assertTrue(game.isSpare());
            assertEquals(game.score(startFrame) + 10, game.score());

            game.roll(10);
            assertTrue(game.isStrike());
            assertFalse(game.isSpare());
            assertEquals(game.score(startFrame) + 20, game.score(startFrame + 1));
            assertEquals(game.score(startFrame + 1) + 10, game.score());

            assertFalse(game.isOver());
        }
    }

    @DisplayName("Given four rolls")
    @Nested
    class GivenFourRolls {
        protected int startFrame = 0;

        @DisplayName("when no strikes and no spares, then the score is m + n + p + q more")
        @ParameterizedTest(name = "when no strikes and no spares, then the score is {0} + {1} + {2} + {3} more")
        @CsvSource({"0, 0, 0, 0", "0, 1, 2, 3", "0, 0, 5, 1", "3, 4, 0, 0", "7, 1, 2, 0", "8, 1, 5, 3"})
        void whenNoStrikeAndNoSpare_thenScoreIsMplusNplusPplusQmore(int m, int n, int p, int q) {
            assertNotEquals(10, m + n);
            assertNotEquals(10, p + q);

            game.roll(m);
            assertFalse(game.isStrike());
            assertFalse(game.isSpare());
            assertEquals(game.score(startFrame) + m, game.score());

            game.roll(n);
            assertFalse(game.isStrike());
            assertFalse(game.isSpare());
            assertEquals(game.score(startFrame) + m + n, game.score());

            game.roll(p);
            assertFalse(game.isStrike());
            assertFalse(game.isSpare());
            assertEquals(game.score(startFrame) + m + n, game.score(startFrame + 1));
            assertEquals(game.score(startFrame + 1) + p, game.score());

            game.roll(q);
            assertFalse(game.isStrike());
            assertFalse(game.isSpare());
            assertEquals(game.score(startFrame) + m + n, game.score(startFrame + 1));
            assertEquals(game.score(startFrame + 1) + p + q, game.score());

            assertFalse(game.isOver());
        }

        @DisplayName("when four strikes, then the score is 90 more")
        @Test
        void whenFourStrikes_thenScoreIs90more() {
            game.roll(10);
            assertTrue(game.isStrike());
            assertFalse(game.isSpare());
            assertEquals(game.score(startFrame) + 10, game.score());

            game.roll(10);
            assertTrue(game.isStrike());
            assertFalse(game.isSpare());
            assertEquals(game.score(startFrame) + 20, game.score(startFrame + 1));
            assertEquals(game.score(startFrame + 1) + 10, game.score());

            game.roll(10);
            assertTrue(game.isStrike());
            assertFalse(game.isSpare());
            assertEquals(game.score(startFrame) + 30, game.score(startFrame + 1));
            assertEquals(game.score(startFrame + 1) + 20, game.score(startFrame + 2));
            assertEquals(game.score(startFrame + 2) + 10, game.score());

            game.roll(10);
            assertTrue(game.isStrike());
            assertFalse(game.isSpare());
            assertEquals(game.score(startFrame) + 30, game.score(startFrame + 1));
            assertEquals(game.score(startFrame + 1) + 30, game.score(startFrame + 2));
            assertEquals(game.score(startFrame + 2) + 20, game.score(startFrame + 3));
            assertEquals(game.score(startFrame + 3) + 10, game.score());

            assertFalse(game.isOver());
        }

        @DisplayName("when three strikes, then the score 60 + 3 * n")
        @ParameterizedTest(name = "when three strikes, then the score is 60 + 3 * {0}")
        @ValueSource(ints = {0, 1, 5, 9})
        void whenThreeStrikes_thenScoreIs60plusThreeTimesNmore(int n) {
            game.roll(10);
            assertTrue(game.isStrike());
            assertFalse(game.isSpare());
            assertEquals(game.score(startFrame) + 10, game.score());

            game.roll(10);
            assertTrue(game.isStrike());
            assertFalse(game.isSpare());
            assertEquals(game.score(startFrame) + 20, game.score(startFrame + 1));
            assertEquals(game.score(startFrame + 1) + 10, game.score());

            game.roll(10);
            assertTrue(game.isStrike());
            assertFalse(game.isSpare());
            assertEquals(game.score(startFrame) + 30, game.score(startFrame + 1));
            assertEquals(game.score(startFrame + 1) + 20, game.score(startFrame + 2));
            assertEquals(game.score(startFrame + 2) + 10, game.score());

            game.roll(n);
            assertFalse(game.isStrike());
            assertFalse(game.isSpare());
            assertEquals(game.score(startFrame) + 30, game.score(startFrame + 1));
            assertEquals(game.score(startFrame + 1) + 20 + n, game.score(startFrame + 2));
            assertEquals(game.score(startFrame + 2) + 10 + n, game.score(startFrame + 3));
            assertEquals(game.score(startFrame + 3) + n, game.score());

            assertFalse(game.isOver());
        }

        @DisplayName("when two spares, then the score is m + n + 2 * p + q")
        @ParameterizedTest(name = "when two spares, then the score is {0} + {1} + 2 * {2} + {3}")
        @CsvSource({"0, 10, 0, 10", "0, 10, 1, 9", "3, 7, 0, 10", "8, 2, 5, 5"})
        void whenTwoSpares_thenScoreIsMplusNplusTwoTimesPplusQmore(int m, int n, int p, int q) {
            assertEquals(10, m + n);
            assertEquals(10, p + q);

            game.roll(m);
            assertFalse(game.isStrike());
            assertFalse(game.isSpare());
            assertEquals(game.score(startFrame) + m, game.score());

            game.roll(n);
            assertFalse(game.isStrike());
            assertTrue(game.isSpare());
            assertEquals(game.score(startFrame) + 10, game.score());

            game.roll(p);
            assertFalse(game.isStrike());
            assertFalse(game.isSpare());
            assertEquals(game.score(startFrame) + 10 + p, game.score(startFrame + 1));
            assertEquals(game.score(startFrame + 1) + p, game.score());

            game.roll(q);
            assertFalse(game.isStrike());
            assertTrue(game.isSpare());
            assertEquals(game.score(startFrame) + 10 + p, game.score(startFrame + 1));
            assertEquals(game.score(startFrame + 1) + p + q, game.score());

            assertFalse(game.isOver());
        }
    }

    @DisplayName("Given five rolls")
    @Nested
    class GivenFiveRolls {
        protected int startFrame = 0;

        @DisplayName("when two spares, then the score is m + n + 2 * p + q + 2 * r")
        @ParameterizedTest(name = "when two spares, then the score is {0} + {1} + 2 * {2} + {3} + 2 * {4}")
        @CsvSource({"0, 10, 0, 10, 0", "0, 10, 0, 10, 9", "3, 7, 8, 2, 1", "5, 5, 0, 10, 5"})
        void whenTwoSpares_thenScoreIsMplusNplus2PplusQplus2Rmore(int m, int n, int p, int q, int r) {
            assertEquals(10, m + n);
            assertEquals(10, p + q);

            game.roll(m);
            assertFalse(game.isStrike());
            assertFalse(game.isSpare());
            assertEquals(game.score(startFrame) + m, game.score());

            game.roll(n);
            assertFalse(game.isStrike());
            assertTrue(game.isSpare());
            assertEquals(game.score(startFrame) + 10, game.score());

            game.roll(p);
            assertFalse(game.isStrike());
            assertFalse(game.isSpare());
            assertEquals(game.score(startFrame) + 10 + p, game.score(startFrame + 1));
            assertEquals(game.score(startFrame + 1) + p, game.score());

            game.roll(q);
            assertFalse(game.isStrike());
            assertTrue(game.isSpare());
            assertEquals(game.score(startFrame) + 10 + p, game.score(startFrame + 1));
            assertEquals(game.score(startFrame + 1) + p + q, game.score());

            game.roll(r);
            assertFalse(game.isStrike());
            assertFalse(game.isSpare());
            assertEquals(game.score(startFrame) + 10 + p, game.score(startFrame + 1));
            assertEquals(game.score(startFrame + 1) + p + q + r, game.score(startFrame + 2));
            assertEquals(game.score(startFrame + 2) + r, game.score());

            assertFalse(game.isOver());
        }
    }

    @DisplayName("Given all rolls")
    @Nested
    class GivenAllRolls {
        @DisplayName("when always no pins, then the score is 0")
        @Test
        void whenAlwaysNoPins_thenScoreIs0() {
            for (int i = 0; i < 10; i++) {
                game.roll(0);
                game.roll(0);
            }
            assertEquals(0, game.score());

            assertTrue(game.isOver());
        }

        @DisplayName("when always strike, then the score is 300")
        @Test
        void whenAlwaysStrike_thenScoreIs300() {
            for (int i = 0; i < 12; i++) {
                game.roll(10);
            }
            assertEquals(300, game.score());

            assertTrue(game.isOver());
        }

        @DisplayName("when 9 strikes first and neither a strike nor a spare in last frame 10, then no bonus")
        @Test
        void when9StrikesAndNeitherStrikeNorSpareInLastFrame10_thenNoBonus() {
            for (int i = 0; i < 9; i++) {
                game.roll(10);
            }
            game.roll(0);
            game.roll(0);
            assertEquals(7 * 30 + 20 + 10, game.score());

            assertTrue(game.isOver());

            assertThrows(IllegalStateException.class, () -> game.roll(0));
        }

        @DisplayName("when 9 strikes and one spare, then the score 240 + 3 * m + 2 * n + p")
        @ParameterizedTest(name = "when 9 strikes and one spare, then the score 240 + 3 * {0} + 2 * {1} + {2}")
        @CsvSource({"2, 8, 0", "5, 5, 3"})
        void when9StrikesAndOneSpare_thenScoreIs240plus3timesMplus2timesNplusP(int m, int n, int p) {
            assertEquals(10, m + n);
            for (int i = 0; i < 9; i++) {
                game.roll(10);
            }
            assertEquals(7 * 30 + 20 + 10, game.score(9));

            game.roll(m);
            assertEquals(7 * 30 + 20 + 10 + 2 * m, game.score(9));
            assertEquals(game.score(9) + m, game.score());

            game.roll(n);
            assertEquals(7 * 30 + 20 + 10 + 2 * m + n, game.score(9));
            assertEquals(game.score(9) + 10, game.score());

            game.roll(p);
            assertEquals(7 * 30 + 20 + 10 + 2 * m + n, game.score(9));
            assertEquals(game.score(9) + 10 + p, game.score());

            assertTrue(game.isOver());
        }

        @DisplayName("when no strikes and no spares, then score is sum")
        @Test
        void whenNoStrikesAndNoSpares_thenScoreIsSum() {
            for (int i = 0; i < 9; i++) {
                game.roll(0);
                game.roll(i + 1);
            }
            game.roll(2);
            game.roll(3);
            int gaussSum = (9 * 10) / 2;
            assertEquals(gaussSum + 2 + 3, game.score());

            assertTrue(game.isOver());
        }
    }

    @DisplayName("Given one roll after one frame")
    @Nested
    class GivenOneRollAfterOneFrame extends GivenOneRoll {
        @BeforeEach
        void startPlaying() {
            game.roll(2);
            game.roll(5);
            startFrame = 1;
        }
    }

    @DisplayName("Given one roll after two frames")
    @Nested
    class GivenOneRollAfterTwoFrames extends GivenOneRoll {
        @BeforeEach
        void startPlaying() {
            game.roll(4);
            game.roll(1);
            game.roll(0);
            game.roll(8);
            startFrame = 2;
        }
    }

    @DisplayName("Given two rolls after one frame")
    @Nested
    class GivenTwoRollsAfterOneFrame extends GivenTwoRolls {
        @BeforeEach
        void startPlaying() {
            game.roll(3);
            game.roll(2);
            startFrame = 1;
        }
    }

    @DisplayName("Given two rolls after two frames")
    @Nested
    class GivenTwoRollsAfterTwoFrames extends GivenTwoRolls {
        @BeforeEach
        void startPlaying() {
            game.roll(7);
            game.roll(2);
            game.roll(5);
            game.roll(4);
            startFrame = 2;
        }
    }

    @DisplayName("Given three rolls after one frame")
    @Nested
    class GivenThreeRollsAfterOneFrame extends GivenThreeRolls {
        @BeforeEach
        void startPlaying() {
            game.roll(0);
            game.roll(6);
            startFrame = 1;
        }
    }

    @DisplayName("Given three rolls after two frames")
    @Nested
    class GivenThreeRollsAfterTwoFrames extends GivenThreeRolls {
        @BeforeEach
        void startPlaying() {
            game.roll(8);
            game.roll(0);
            game.roll(0);
            game.roll(2);
            startFrame = 2;
        }
    }

    @DisplayName("Given four rolls after one frame")
    @Nested
    class GivenFourRollsAfterOneFrame extends GivenFourRolls {
        @BeforeEach
        void startPlaying() {
            game.roll(4);
            game.roll(0);
            startFrame = 1;
        }
    }

    @DisplayName("Given four rolls after two frames")
    @Nested
    class GivenFourRollsAfterTwoFrames extends GivenFourRolls {
        @BeforeEach
        void startPlaying() {
            game.roll(1);
            game.roll(3);
            game.roll(0);
            game.roll(0);
            startFrame = 2;
        }
    }

    @DisplayName("Given five rolls after one frame")
    @Nested
    class GivenFiveRollsAfterOneFrame extends GivenFiveRolls {
        @BeforeEach
        void startPlaying() {
            game.roll(7);
            game.roll(2);
            startFrame = 1;
        }
    }

    @DisplayName("Given five rolls after two frames")
    @Nested
    class GivenFiveRollsAfterTwoFrames extends GivenFiveRolls {
        @BeforeEach
        void startPlaying() {
            game.roll(3);
            game.roll(4);
            game.roll(3);
            game.roll(3);
            startFrame = 2;
        }
    }
}
