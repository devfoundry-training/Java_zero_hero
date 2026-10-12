/** Standard members (starter). TODO Core 4: 14 days, 25p a day, up to 3 loans. */
public class StandardPolicy implements LoanPolicy {

    @Override
    public String name() {
        return "standard";
    }

    @Override
    public int loanDays() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public int finePerDay() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public int maxLoans() {
        throw new UnsupportedOperationException("not implemented");
    }
}
