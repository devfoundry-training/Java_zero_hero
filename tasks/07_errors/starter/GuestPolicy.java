/**
 * Stretch 1: guests get 7 days, 50p a day, 1 loan.
 * Added without touching Library or Member: only Policies maps a name to it.
 */
public class GuestPolicy implements LoanPolicy {

    @Override
    public String name() {
        return "guest";
    }

    @Override
    public int loanDays() {
        return 7;
    }

    @Override
    public int finePerDay() {
        return 50;
    }

    @Override
    public int maxLoans() {
        return 1;
    }
}
