package com.frankondou.focus.service;

import com.frankondou.focus.dto.FreeTimeResponse;
import com.frankondou.focus.dto.FreeTimeWarning;
import com.frankondou.focus.dto.FreeTimeWarningCode;
import com.frankondou.focus.entity.Responsibility;
import com.frankondou.focus.entity.enums.ResponsibilityCategory;
import com.frankondou.focus.entity.enums.StrengthRating;
import com.frankondou.focus.entity.enums.SubjectCategory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

// Pure free-time math with no Spring or database, so it can be unit tested on its own
public final class FreeTimeCalculator {

    private static final BigDecimal HOURS_PER_WEEK = new BigDecimal("168");
    // Weekly sleep is compared to 7 hours x 7 nights directly, so there's no division
    private static final BigDecimal MIN_SLEEP_HOURS_PER_WEEK = new BigDecimal("49");
    // Meals, hygiene, and a social life come out of free time too, so under 30 is genuinely tight
    private static final BigDecimal PACKED_WEEK_FREE_HOURS = new BigDecimal("30");
    private static final BigDecimal UNUSED_FREE_HOURS = new BigDecimal("30");
    // The common rule of thumb of about 2 study hours per credit
    private static final BigDecimal STUDY_HOURS_PER_CREDIT = new BigDecimal("2");

    // Rough starting points, not science. The student's own strength rating adjusts them
    private static final BigDecimal LAW_MULTIPLIER = new BigDecimal("1.30");
    private static final BigDecimal STEM_MULTIPLIER = new BigDecimal("1.25");
    private static final BigDecimal HEALTH_MULTIPLIER = new BigDecimal("1.20");
    private static final BigDecimal BASELINE_MULTIPLIER = new BigDecimal("1.00");
    private static final BigDecimal BUSINESS_MULTIPLIER = new BigDecimal("0.90");
    // Much of the learning is hands-on and already counts as class time
    private static final BigDecimal OCCUPATIONAL_MULTIPLIER = new BigDecimal("0.80");

    private static final BigDecimal WEAK_MULTIPLIER = new BigDecimal("1.25");
    private static final BigDecimal AVERAGE_MULTIPLIER = new BigDecimal("1.00");
    private static final BigDecimal STRONG_MULTIPLIER = new BigDecimal("0.80");

    private static final int SCALE = 2;

    private FreeTimeCalculator() {}

    // Left unrounded so summing several classes rounds only once, in calculate
    public static BigDecimal recommendedHoursFor(BigDecimal credits, SubjectCategory category, StrengthRating strength) {
        return credits
                .multiply(STUDY_HOURS_PER_CREDIT)
                .multiply(categoryMultiplier(category))
                .multiply(strengthMultiplier(strength));
    }

    public static FreeTimeResponse calculate(List<Responsibility> responsibilities,
                                             BigDecimal classHours,
                                             BigDecimal plannedStudyHours,
                                             BigDecimal recommendedStudyHours) {
        BigDecimal sleepHours = hoursIn(responsibilities, ResponsibilityCategory.SLEEP);
        BigDecimal workHours = hoursIn(responsibilities, ResponsibilityCategory.WORK);
        BigDecimal customHours = hoursIn(responsibilities, ResponsibilityCategory.CUSTOM);

        BigDecimal committedHours = sleepHours.add(workHours).add(customHours).add(classHours);
        BigDecimal freeHours = HOURS_PER_WEEK.subtract(committedHours);
        BigDecimal leftoverHours = freeHours.subtract(plannedStudyHours);

        List<FreeTimeWarning> warnings = warnings(sleepHours, freeHours, plannedStudyHours,
                leftoverHours, recommendedStudyHours);

        return new FreeTimeResponse(
                scaled(sleepHours), scaled(workHours), scaled(customHours), scaled(classHours),
                scaled(committedHours), scaled(freeHours), scaled(plannedStudyHours),
                scaled(leftoverHours), scaled(recommendedStudyHours), warnings);
    }

    // Order matters: the response lists warnings in this order
    private static List<FreeTimeWarning> warnings(BigDecimal sleepHours, BigDecimal freeHours,
                                                  BigDecimal plannedStudyHours, BigDecimal leftoverHours,
                                                  BigDecimal recommendedStudyHours) {
        List<FreeTimeWarning> warnings = new ArrayList<>();

        if (sleepHours.signum() == 0) {
            warnings.add(new FreeTimeWarning(FreeTimeWarningCode.MISSING_SLEEP,
                    "No sleep is recorded, so your free time is overstated. Add it for an accurate number."));
        }
        if (sleepHours.signum() > 0 && sleepHours.compareTo(MIN_SLEEP_HOURS_PER_WEEK) < 0) {
            BigDecimal perNight = sleepHours.divide(new BigDecimal("7"), 1, RoundingMode.HALF_UP);
            warnings.add(new FreeTimeWarning(FreeTimeWarningCode.LOW_SLEEP,
                    "You're planning about " + perNight + " hours of sleep a night. Most people do best with 7-9."));
        }
        if (freeHours.signum() < 0) {
            warnings.add(new FreeTimeWarning(FreeTimeWarningCode.OVER_BOOKED,
                    "Your commitments add up to more hours than a week has."));
        }
        if (freeHours.signum() >= 0 && freeHours.compareTo(PACKED_WEEK_FREE_HOURS) < 0) {
            warnings.add(new FreeTimeWarning(FreeTimeWarningCode.PACKED_WEEK,
                    "Your week is packed, with only " + scaled(freeHours) + " free hours left. "
                            + "It's hard to stay consistent on a schedule this tight, so consider trimming something."));
        }
        if (freeHours.signum() >= 0 && plannedStudyHours.compareTo(freeHours) > 0) {
            warnings.add(new FreeTimeWarning(FreeTimeWarningCode.GOALS_EXCEED_FREE_TIME,
                    "Your weekly study goals (" + scaled(plannedStudyHours) + " h) are more than your free time ("
                            + scaled(freeHours) + " h)."));
        }
        if (leftoverHours.compareTo(UNUSED_FREE_HOURS) >= 0 && plannedStudyHours.compareTo(recommendedStudyHours) < 0) {
            warnings.add(new FreeTimeWarning(FreeTimeWarningCode.UNUSED_FREE_TIME,
                    "You have about " + scaled(leftoverHours) + " free hours a week that no goal is using, and your "
                            + "study goals are below the recommended amount for your classes. There's room to do more."));
        }

        return warnings;
    }

    private static BigDecimal hoursIn(List<Responsibility> responsibilities, ResponsibilityCategory category) {
        return responsibilities.stream()
                .filter(r -> r.getCategory() == category)
                .map(Responsibility::getHoursPerWeek)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static BigDecimal categoryMultiplier(SubjectCategory category) {
        return switch (category) {
            case LAW -> LAW_MULTIPLIER;
            case STEM -> STEM_MULTIPLIER;
            case HEALTH -> HEALTH_MULTIPLIER;
            case BUSINESS -> BUSINESS_MULTIPLIER;
            case OCCUPATIONAL -> OCCUPATIONAL_MULTIPLIER;
            case ARTS_HUMANITIES, SOCIAL_SCIENCES, OTHER -> BASELINE_MULTIPLIER;
        };
    }

    private static BigDecimal strengthMultiplier(StrengthRating strength) {
        return switch (strength) {
            case WEAK -> WEAK_MULTIPLIER;
            case AVERAGE -> AVERAGE_MULTIPLIER;
            case STRONG -> STRONG_MULTIPLIER;
        };
    }

    private static BigDecimal scaled(BigDecimal hours) {
        return hours.setScale(SCALE, RoundingMode.HALF_UP);
    }
}
