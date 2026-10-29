package com.frankondou.focus.service;

import com.frankondou.focus.entity.GradeCutoff;
import com.frankondou.focus.entity.GradeEntry;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

// Pure grade math with no Spring or database, so it can be unit tested on its own
public final class GradeCalculator {

    public record WeightedPercent(BigDecimal weight, BigDecimal percent) {}

    public record CreditPoints(BigDecimal credits, BigDecimal gradePoints) {}

    private static final BigDecimal HUNDRED = new BigDecimal("100");
    private static final int RESULT_SCALE = 2;

    private GradeCalculator() {}

    public static BigDecimal componentPercent(List<GradeEntry> entries) {
        if (entries.isEmpty()) {
            return null;
        }

        BigDecimal earned = sum(entries.stream().map(GradeEntry::getScoreEarned));
        BigDecimal possible = sum(entries.stream().map(GradeEntry::getScorePossible));
        return divide(earned.multiply(HUNDRED), possible);
    }

    // Dividing by the weights actually used keeps the grade accurate before every component is graded
    public static BigDecimal weightedPercent(List<WeightedPercent> parts) {
        List<WeightedPercent> graded = parts.stream().filter(p -> p.percent() != null).toList();
        if (graded.isEmpty()) {
            return null;
        }

        BigDecimal weightedSum = sum(graded.stream().map(p -> p.weight().multiply(p.percent())));
        BigDecimal totalWeight = sum(graded.stream().map(WeightedPercent::weight));
        return divide(weightedSum, totalWeight);
    }

    public static Optional<GradeCutoff> letterFor(BigDecimal percent, List<GradeCutoff> cutoffsSortedDesc) {
        if (percent == null) {
            return Optional.empty();
        }

        return cutoffsSortedDesc.stream()
                .filter(c -> percent.compareTo(c.getMinPercent()) >= 0)
                .findFirst();
    }

    public static BigDecimal gpa(List<CreditPoints> graded) {
        BigDecimal totalCredits = sum(graded.stream().map(CreditPoints::credits));
        if (totalCredits.signum() == 0) {
            return null;
        }

        return divide(qualityPoints(graded), totalCredits);
    }

    public static BigDecimal cumulativeGpa(BigDecimal priorCredits, BigDecimal priorGpa, List<CreditPoints> graded) {
        BigDecimal totalCredits = priorCredits.add(sum(graded.stream().map(CreditPoints::credits)));
        if (totalCredits.signum() == 0) {
            return null;
        }

        BigDecimal totalQualityPoints = priorGpa.multiply(priorCredits).add(qualityPoints(graded));
        return divide(totalQualityPoints, totalCredits);
    }

    private static BigDecimal qualityPoints(List<CreditPoints> graded) {
        return sum(graded.stream().map(g -> g.credits().multiply(g.gradePoints())));
    }

    private static BigDecimal sum(Stream<BigDecimal> values) {
        return values.reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Round once at the end of each calculation, not at every step
    private static BigDecimal divide(BigDecimal numerator, BigDecimal denominator) {
        return numerator.divide(denominator, RESULT_SCALE, RoundingMode.HALF_UP);
    }
}
