/** Staff: 28 days, no fines, up to 10 loans. */
public class StaffPolicy implements LoanPolicy {

    @Override
    public String name() {
        return "staff";
    }

    @Override
    public int loanDays() {
        return 28;
    }

    @Override
    public int finePerDay() {
        return 0;
    }

    @Override
    public int maxLoans() {
        return 10;
    }
}
