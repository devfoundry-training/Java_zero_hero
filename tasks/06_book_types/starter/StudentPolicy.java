/** Students (starter). TODO Core 4: 21 days, 10p a day, up to 5 loans. */
public class StudentPolicy implements LoanPolicy {

    @Override
    public String name() {
        return "student";
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
