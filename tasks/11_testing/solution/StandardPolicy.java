/** Standard members: 14 days, 25p a day, up to 3 loans. */
public class StandardPolicy implements LoanPolicy {

    @Override
    public String name() {
        return "standard";
    }

    @Override
    public int loanDays() {
        return 14;
    }

    @Override
    public int finePerDay() {
        return 25;
    }

    @Override
    public int maxLoans() {
        return 3;
    }
}
