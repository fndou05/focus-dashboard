package com.frankondou.focus.service;

import com.frankondou.focus.dto.FreeTimeResponse;
import com.frankondou.focus.dto.FreeTimeWarning;
import com.frankondou.focus.dto.FreeTimeWarningCode;
import com.frankondou.focus.entity.Responsibility;
import com.frankondou.focus.entity.enums.ResponsibilityCategory;
import com.frankondou.focus.entity.enums.StrengthRating;
import com.frankondou.focus.entity.enums.SubjectCategory;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FreeTimeCalculatorTest {

    private Responsibility responsibility(ResponsibilityCategory category, String hours) {
        Responsibility responsibility = new Responsibility();
        responsibility.setCategory(category);
        responsibility.setHoursPerWeek(new BigDecimal(hours));
        return responsibility;
    }

    private Responsibility sleep(String hours) { return responsibility(ResponsibilityCategory.SLEEP, hours); }
    private Responsibility work(String hours) { return responsibility(ResponsibilityCategory.WORK, hours); }
    private Responsibility custom(String hours) { return responsibility(ResponsibilityCategory.CUSTOM, hours); }

    private FreeTimeResponse calculate(List<Responsibility> responsibilities, String classHours,
                                       String planned, String recommended) {
        return FreeTimeCalculator.calculate(responsibilities, new BigDecimal(classHours),
                new BigDecimal(planned), new BigDecimal(recommended));
    }

    private List<FreeTimeWarningCode> codes(FreeTimeResponse response) {
        return response.warnings().stream().map(FreeTimeWarning::code).toList();
    }

    // recommendedHoursFor is left unrounded, so compare by value instead of by scale
    private void assertHours(String expected, BigDecimal actual) {
        assertTrue(new BigDecimal(expected).compareTo(actual) == 0, "expected " + expected + " but was " + actual);
    }

    @Test
    void calculate_sumsEachCategoryAndHasNoWarningsForABalancedWeek() {
        FreeTimeResponse r = calculate(List.of(sleep("56"), work("20"), custom("5"), custom("3")), "15", "25", "24");

        assertEquals(new BigDecimal("56.00"), r.sleepHours());
        assertEquals(new BigDecimal("20.00"), r.workHours());
        assertEquals(new BigDecimal("8.00"), r.customHours());
        assertEquals(new BigDecimal("15.00"), r.classHours());
        assertEquals(new BigDecimal("99.00"), r.committedHours());
        assertEquals(new BigDecimal("69.00"), r.freeHours());
        assertEquals(new BigDecimal("25.00"), r.plannedStudyHours());
        assertEquals(new BigDecimal("44.00"), r.leftoverHours());
        assertEquals(new BigDecimal("24.00"), r.recommendedStudyHours());
        assertEquals(List.of(), codes(r));
    }

    @Test
    void calculate_withNoSleep_warnsMissingSleep() {
        FreeTimeResponse r = calculate(List.of(work("10")), "0", "0", "0");

        assertEquals(new BigDecimal("158.00"), r.freeHours());
        assertEquals(List.of(FreeTimeWarningCode.MISSING_SLEEP), codes(r));
    }

    @Test
    void calculate_withUnderSevenHoursASleepNight_warnsLowSleep() {
        FreeTimeResponse r = calculate(List.of(sleep("42")), "0", "0", "0");

        assertEquals(new BigDecimal("126.00"), r.freeHours());
        assertEquals(List.of(FreeTimeWarningCode.LOW_SLEEP), codes(r));
    }

    @Test
    void calculate_withExactlySevenHoursASleepNight_doesNotWarn() {
        assertEquals(List.of(), codes(calculate(List.of(sleep("49")), "0", "0", "0")));
    }

    @Test
    void calculate_whenCommitmentsExceedTheWeek_warnsOverBooked() {
        FreeTimeResponse r = calculate(List.of(sleep("56"), work("60")), "60", "0", "0");

        assertEquals(new BigDecimal("-8.00"), r.freeHours());
        assertEquals(List.of(FreeTimeWarningCode.OVER_BOOKED), codes(r));
    }

    @Test
    void calculate_withUnder30FreeHours_warnsPackedWeek() {
        FreeTimeResponse r = calculate(List.of(sleep("56"), work("50")), "35", "0", "0");

        assertEquals(new BigDecimal("27.00"), r.freeHours());
        assertEquals(List.of(FreeTimeWarningCode.PACKED_WEEK), codes(r));
    }

    @Test
    void calculate_withExactly30FreeHours_doesNotWarn() {
        FreeTimeResponse r = calculate(List.of(sleep("56"), work("50")), "32", "0", "0");

        assertEquals(new BigDecimal("30.00"), r.freeHours());
        assertEquals(List.of(), codes(r));
    }

    @Test
    void calculate_whenGoalsExceedFreeTime_warns() {
        FreeTimeResponse r = calculate(List.of(sleep("56"), work("20")), "15", "80", "24");

        assertEquals(new BigDecimal("77.00"), r.freeHours());
        assertEquals(List.of(FreeTimeWarningCode.GOALS_EXCEED_FREE_TIME), codes(r));
    }

    @Test
    void calculate_withLotsOfFreeTimeAndLowGoals_warnsUnusedFreeTime() {
        FreeTimeResponse r = calculate(List.of(sleep("56")), "12", "10", "24");

        assertEquals(new BigDecimal("100.00"), r.freeHours());
        assertEquals(new BigDecimal("90.00"), r.leftoverHours());
        assertEquals(new BigDecimal("24.00"), r.recommendedStudyHours());
        assertEquals(List.of(FreeTimeWarningCode.UNUSED_FREE_TIME), codes(r));
    }

    @Test
    void calculate_whenGoalsMeetTheRecommendation_doesNotWarnUnused() {
        assertEquals(List.of(), codes(calculate(List.of(sleep("56")), "12", "24", "24")));
    }

    @Test
    void calculate_withSeveralWarnings_listsThemInOrder() {
        FreeTimeResponse r = calculate(List.of(sleep("42"), work("60")), "40", "0", "0");

        assertEquals(new BigDecimal("26.00"), r.freeHours());
        assertEquals(List.of(FreeTimeWarningCode.LOW_SLEEP, FreeTimeWarningCode.PACKED_WEEK), codes(r));
    }

    @Test
    void recommendedHoursFor_startsAtTwoHoursPerCreditForABaselineAverageClass() {
        assertHours("8", FreeTimeCalculator.recommendedHoursFor(
                new BigDecimal("4"), SubjectCategory.ARTS_HUMANITIES, StrengthRating.AVERAGE));
    }

    @Test
    void recommendedHoursFor_giveStemMoreTimeThanHumanitiesAtTheSameStrength() {
        assertHours("10", FreeTimeCalculator.recommendedHoursFor(
                new BigDecimal("4"), SubjectCategory.STEM, StrengthRating.AVERAGE));
    }

    @Test
    void recommendedHoursFor_lawAndOccupationalSitOnOppositeSidesOfTheBaseline() {
        assertHours("2.6", FreeTimeCalculator.recommendedHoursFor(
                BigDecimal.ONE, SubjectCategory.LAW, StrengthRating.AVERAGE));
        assertHours("1.6", FreeTimeCalculator.recommendedHoursFor(
                BigDecimal.ONE, SubjectCategory.OCCUPATIONAL, StrengthRating.AVERAGE));
    }

    @Test
    void recommendedHoursFor_strengthCanOutweighTheCategory() {
        // Strong in STEM lands on the plain 2 hours; weak in humanities needs more than average STEM
        assertHours("2", FreeTimeCalculator.recommendedHoursFor(
                BigDecimal.ONE, SubjectCategory.STEM, StrengthRating.STRONG));
        assertHours("2.5", FreeTimeCalculator.recommendedHoursFor(
                BigDecimal.ONE, SubjectCategory.ARTS_HUMANITIES, StrengthRating.WEAK));
    }

    @Test
    void calculate_roundsTheRecommendedTotalOnceAtTheEnd() {
        // 3 credits, weak STEM = 9.375, which rounds half-up to 9.38
        BigDecimal exact = FreeTimeCalculator.recommendedHoursFor(
                new BigDecimal("3"), SubjectCategory.STEM, StrengthRating.WEAK);

        FreeTimeResponse r = FreeTimeCalculator.calculate(List.of(), BigDecimal.ZERO, BigDecimal.ZERO, exact);

        assertEquals(new BigDecimal("9.38"), r.recommendedStudyHours());
    }
}
