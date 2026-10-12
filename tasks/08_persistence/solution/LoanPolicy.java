/**
 * What every loan policy must answer. Library and Member only ever talk to
 * this interface, never to a concrete policy class, so a new policy needs
 * no change to either of them.
 */
public interface LoanPolicy {

    String name();

    int loanDays();

    /** Pence charged for each day late. */
    int finePerDay();

    int maxLoans();

    /** Shared behaviour in a default method: every policy inherits it. Never below 0. */
    default int fineFor(int daysLate) {
        return Math.max(0, daysLate) * finePerDay();
    }

    default String describe() {
        String fine = finePerDay() == 0 ? "no fines" : Text.money(finePerDay()) + " a day";
        return name() + ": " + loanDays() + " days, " + fine + ", up to " + Text.plural(maxLoans(), "loan", "loans");
    }
}
