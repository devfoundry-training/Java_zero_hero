/**
 * What every loan policy must answer (starter).
 *
 * TODO Core 4: StandardPolicy, StudentPolicy and StaffPolicy implement this
 * interface (see the table in README.md). fineFor is shared, so it is a
 * default method here: days late times the daily fine, never below 0.
 */
public interface LoanPolicy {

    String name();

    int loanDays();

    /** Pence charged for each day late. */
    int finePerDay();

    int maxLoans();

    default int fineFor(int daysLate) {
        throw new UnsupportedOperationException("not implemented");
    }
}
