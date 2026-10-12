/** Staff (starter). TODO Core 4: 28 days, no fines (0p a day), up to 10 loans. */
public class StaffPolicy implements LoanPolicy {

    @Override
    public String name() {
        return "staff";
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
