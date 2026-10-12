package com.teaching.datetime;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

/**
 * Demonstrates the java.time API (Java 8+):
 *   LocalDate, LocalDateTime, ZonedDateTime, DateTimeFormatter, Period, Duration, Instant.
 *
 * Key rule: all java.time objects are IMMUTABLE. Arithmetic methods (plusDays, minusMonths, etc.)
 * return a new object - the original is unchanged. This is the same principle as String.
 *
 * No external dependencies - java.time is part of the JDK.
 */
public class DateTimeRunner {

    public static void main(String[] args) {
        localDateDemo();
        localDateTimeDemo();
        zonedDateTimeDemo();
        formatterDemo();
        periodAndDurationDemo();
        instantDemo();
    }

    // -------------------------------------------------------------------------
    // 1. LocalDate - a date with no time and no timezone (year-month-day only)
    // -------------------------------------------------------------------------

    static void localDateDemo() {
        System.out.println("=== LocalDate ===");

        LocalDate today    = LocalDate.now();
        LocalDate birthday = LocalDate.of(1990, Month.MARCH, 15);  // Month enum is clearer than int 3
        LocalDate xmas     = LocalDate.of(today.getYear(), Month.DECEMBER, 25);

        System.out.println("today    : " + today);
        System.out.println("birthday : " + birthday);
        System.out.println("xmas     : " + xmas);

        // Arithmetic - returns a NEW date; original is unchanged
        LocalDate nextWeek   = today.plusWeeks(1);
        LocalDate lastMonth  = today.minusMonths(1);
        LocalDate inTwoYears = today.plusYears(2);
        System.out.println("next week   : " + nextWeek);
        System.out.println("last month  : " + lastMonth);
        System.out.println("in 2 years  : " + inTwoYears);

        // Extracting fields
        System.out.println("day-of-week  : " + today.getDayOfWeek());   // e.g. MONDAY
        System.out.println("day-of-month : " + today.getDayOfMonth());
        System.out.println("month        : " + today.getMonth());
        System.out.println("year         : " + today.getYear());
        System.out.println("leap year?   : " + today.isLeapYear());

        // Comparison
        System.out.println("xmas is after today  : " + xmas.isAfter(today));
        System.out.println("birthday before today: " + birthday.isBefore(today));
    }

    // -------------------------------------------------------------------------
    // 2. LocalDateTime - date + time, no timezone
    //    Use when you need both date and time but timezone is irrelevant
    //    (e.g., a meeting time stored without regard for the attendee's location).
    // -------------------------------------------------------------------------

    static void localDateTimeDemo() {
        System.out.println("\n=== LocalDateTime ===");

        LocalDateTime now     = LocalDateTime.now();
        LocalDateTime meeting = LocalDateTime.of(2025, Month.JUNE, 15, 14, 30); // 2025-06-15 14:30

        System.out.println("now     : " + now);
        System.out.println("meeting : " + meeting);

        // Extract back to date or time components
        System.out.println("date part : " + now.toLocalDate());
        System.out.println("time part : " + now.toLocalTime());

        // Combine a LocalDate + LocalTime
        LocalDate    date = LocalDate.of(2025, 12, 25);
        LocalTime    time = LocalTime.of(9, 0);
        LocalDateTime combined = LocalDateTime.of(date, time);
        System.out.println("combined  : " + combined);

        // truncatedTo: zero out sub-units - useful for grouping by hour, day, etc.
        LocalDateTime hourPrecision = now.truncatedTo(ChronoUnit.HOURS);
        System.out.println("truncated to hour : " + hourPrecision);

        // Arithmetic works the same as LocalDate
        System.out.println("meeting + 90 min  : " + meeting.plusMinutes(90));
    }

    // -------------------------------------------------------------------------
    // 3. ZonedDateTime - date + time + timezone offset
    //    Use whenever a precise moment in time must be unambiguous globally.
    // -------------------------------------------------------------------------

    static void zonedDateTimeDemo() {
        System.out.println("\n=== ZonedDateTime ===");

        ZoneId utc      = ZoneId.of("UTC");
        ZoneId london   = ZoneId.of("Europe/London");
        ZoneId newYork  = ZoneId.of("America/New_York");
        ZoneId tokyo    = ZoneId.of("Asia/Tokyo");

        ZonedDateTime utcNow = ZonedDateTime.now(utc);
        System.out.println("UTC    : " + utcNow);
        System.out.println("London : " + ZonedDateTime.now(london));
        System.out.println("NY     : " + ZonedDateTime.now(newYork));

        // withZoneSameInstant: same point in time, expressed in a different zone
        ZonedDateTime tokyoNow = utcNow.withZoneSameInstant(tokyo);
        System.out.println("Tokyo  : " + tokyoNow);

        // withZoneSameLocal: keeps the clock reading, changes the zone label -
        // this is a DIFFERENT point in time. Use with care.
        ZonedDateTime sameClockTokyo = utcNow.withZoneSameLocal(tokyo);
        System.out.println("Same clock in Tokyo (different instant): " + sameClockTokyo);

        // ZoneId.getAvailableZoneIds() returns ~600 zone strings - useful to know it exists
        System.out.println("Total available zones: " + ZoneId.getAvailableZoneIds().size());
    }

    // -------------------------------------------------------------------------
    // 4. DateTimeFormatter - convert between dates and strings
    // -------------------------------------------------------------------------

    static void formatterDemo() {
        System.out.println("\n=== DateTimeFormatter ===");

        LocalDate today = LocalDate.now();

        // Custom pattern: letters are format codes (d=day, M=month, y=year)
        // Two letters = zero-padded (01), four y's = full year (2025)
        DateTimeFormatter ukFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter verbose  = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy"); // "Monday, 3 March 2025"

        // Format: date -> String
        System.out.println("uk format : " + today.format(ukFormat));
        System.out.println("verbose   : " + today.format(verbose));

        // Built-in ISO formatters - no pattern string needed
        System.out.println("ISO date  : " + today.format(DateTimeFormatter.ISO_LOCAL_DATE));

        LocalDateTime now = LocalDateTime.now();
        System.out.println("ISO dt    : " + now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        // Parse: String -> date
        LocalDate parsed = LocalDate.parse("25/12/2025", ukFormat);
        System.out.println("parsed    : " + parsed);

        // DateTimeParseException - checked at runtime, not compile time
        try {
            LocalDate.parse("not-a-date", ukFormat);
        } catch (DateTimeParseException e) {
            System.out.println("parse error at index " + e.getErrorIndex()
                + ": " + e.getParsedString());
        }

        // ZonedDateTime formatting
        DateTimeFormatter zoned = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm z");
        System.out.println("zoned     : " + ZonedDateTime.now(ZoneId.of("UTC")).format(zoned));
    }

    // -------------------------------------------------------------------------
    // 5. Period (date gap) and Duration (time gap)
    //    Period uses calendar units (years/months/days).
    //    Duration uses time units (hours/minutes/seconds/nanos).
    // -------------------------------------------------------------------------

    static void periodAndDurationDemo() {
        System.out.println("\n=== Period and Duration ===");

        // Period: between two LocalDates
        LocalDate start  = LocalDate.of(2020, Month.JANUARY, 1);
        LocalDate end    = LocalDate.now();
        Period    period = Period.between(start, end);
        System.out.printf("Period: %d years, %d months, %d days%n",
            period.getYears(), period.getMonths(), period.getDays());

        // Period arithmetic
        LocalDate deadline = LocalDate.now().plus(Period.ofWeeks(6));
        System.out.println("Deadline in 6 weeks: " + deadline);

        // Duration: between two LocalTimes
        LocalTime  workStart = LocalTime.of(9, 0);
        LocalTime  workEnd   = LocalTime.of(17, 30);
        Duration   shift     = Duration.between(workStart, workEnd);
        // toMinutesPart() (Java 9+) gives only the minutes component, not total minutes
        System.out.printf("Shift: %d hours %d minutes%n",
            shift.toHours(), shift.toMinutesPart());

        // Duration between two LocalDateTimes
        LocalDateTime eventStart = LocalDateTime.of(2025, 1, 1, 0, 0);
        LocalDateTime eventEnd   = LocalDateTime.now();
        Duration      elapsed    = Duration.between(eventStart, eventEnd);
        System.out.printf("Elapsed since 2025-01-01: %d days, %d hours%n",
            elapsed.toDaysPart(), elapsed.toHoursPart());
    }

    // -------------------------------------------------------------------------
    // 6. Instant - a point on the UTC timeline (epoch-based)
    //    Use for logging, storing timestamps in databases, or measuring elapsed time.
    //    Not human-readable on its own - convert via ZonedDateTime for display.
    // -------------------------------------------------------------------------

    static void instantDemo() {
        System.out.println("\n=== Instant ===");

        Instant now        = Instant.now();
        Instant epochStart = Instant.EPOCH;   // 1970-01-01T00:00:00Z

        System.out.println("now        : " + now);
        System.out.println("epoch      : " + epochStart);
        System.out.println("epoch ms   : " + now.toEpochMilli());

        // Convert to human-readable ZonedDateTime for display
        ZonedDateTime readable = now.atZone(ZoneId.of("Europe/London"));
        System.out.println("readable   : " + readable.format(
            DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss z")));

        // Measuring elapsed time - use Instant, not System.currentTimeMillis(), in new code
        Instant before = Instant.now();
        long    sum    = 0;
        for (int i = 0; i < 1_000_000; i++) sum += i;
        Instant after  = Instant.now();
        System.out.println("sum        : " + sum);
        System.out.println("elapsed    : " + Duration.between(before, after).toMillis() + " ms");
    }
}
