package com.frankondou.focus.service;

import com.frankondou.focus.entity.GradeCutoff;
import com.frankondou.focus.entity.GradeEntry;
import com.frankondou.focus.service.GradeCalculator.CreditPoints;
import com.frankondou.focus.service.GradeCalculator.WeightedPercent;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GradeCalculatorTest {

    private GradeEntry entry(String earned, String possible) {
        GradeEntry entry = new GradeEntry();
        entry.setScoreEarned(new BigDecimal(earned));
        entry.setScorePossible(new BigDecimal(possible));
        return entry;
    }

    private List<GradeCutoff> defaultCutoffs() {
        String[][] scale = {
                {"A", "93"}, {"A-", "90"}, {"B+", "87"}, {"B", "83"}, {"B-", "80"}, {"C+", "77"},
                {"C", "73"}, {"C-", "70"}, {"D+", "67"}, {"D", "63"}, {"D-", "60"}, {"F", "0"}
        };
        List<GradeCutoff> cutoffs = new ArrayList<>();
        for (String[] row : scale) {
            GradeCutoff cutoff = new GradeCutoff();
            cutoff.setLetter(row[0]);
            cutoff.setMinPercent(new BigDecimal(row[1]));
            cutoffs.add(cutoff);
        }
        return cutoffs;
    }

    private String letter(String percent) {
        return GradeCalculator.letterFor(new BigDecimal(percent), defaultCutoffs())
                .map(GradeCutoff::getLetter).orElse(null);
    }

    @Test
    void componentPercent_singleEntry() {
        assertEquals(new BigDecimal("90.00"), GradeCalculator.componentPercent(List.of(entry("45", "50"))));
    }

    @Test
    void componentPercent_sumsEarnedAndPossibleAcrossEntries() {
        assertEquals(new BigDecimal("75.00"),
                GradeCalculator.componentPercent(List.of(entry("45", "50"), entry("30", "50"))));
    }

    @Test
    void componentPercent_withNoEntries_isNull() {
        assertNull(GradeCalculator.componentPercent(List.of()));
    }

    @Test
    void componentPercent_allowsExtraCredit() {
        assertEquals(new BigDecimal("110.00"), GradeCalculator.componentPercent(List.of(entry("55", "50"))));
    }

    @Test
    void weightedPercent_ignoresComponentsWithoutGrades() {
        List<WeightedPercent> parts = List.of(
                new WeightedPercent(new BigDecimal("20"), new BigDecimal("90.00")),
                new WeightedPercent(new BigDecimal("30"), new BigDecimal("80.00")),
                new WeightedPercent(new BigDecimal("50"), null));

        assertEquals(new BigDecimal("84.00"), GradeCalculator.weightedPercent(parts));
    }

    @Test
    void weightedPercent_whenNothingIsGraded_isNull() {
        List<WeightedPercent> parts = List.of(
                new WeightedPercent(new BigDecimal("50"), null),
                new WeightedPercent(new BigDecimal("50"), null));

        assertNull(GradeCalculator.weightedPercent(parts));
    }

    @Test
    void letterFor_usesTheHighestCutoffAtOrBelowThePercent() {
        assertEquals("A", letter("93.00"));
        assertEquals("A-", letter("92.99"));
        assertEquals("F", letter("0.00"));
        assertTrue(GradeCalculator.letterFor(null, defaultCutoffs()).isEmpty());
    }

    @Test
    void gpa_weightsGradePointsByCredits() {
        List<CreditPoints> graded = List.of(
                new CreditPoints(new BigDecimal("4"), new BigDecimal("4.0")),
                new CreditPoints(new BigDecimal("3"), new BigDecimal("3.0")));

        assertEquals(new BigDecimal("3.57"), GradeCalculator.gpa(graded));
    }

    @Test
    void gpa_withNoGradedSubjects_isNull() {
        assertNull(GradeCalculator.gpa(List.of()));
    }

    @Test
    void cumulativeGpa_includesPriorHistory() {
        List<CreditPoints> graded = List.of(
                new CreditPoints(new BigDecimal("4"), new BigDecimal("4.0")),
                new CreditPoints(new BigDecimal("3"), new BigDecimal("3.0")));

        assertEquals(new BigDecimal("3.42"),
                GradeCalculator.cumulativeGpa(new BigDecimal("45"), new BigDecimal("3.4"), graded));
    }

    @Test
    void cumulativeGpa_withNoCreditsAtAll_isNull() {
        assertNull(GradeCalculator.cumulativeGpa(BigDecimal.ZERO, BigDecimal.ZERO, List.of()));
    }
}
