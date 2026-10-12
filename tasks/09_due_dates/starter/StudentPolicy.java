/** Students: 21 days, 10p a day, up to 5 loans. */
public class StudentPolicy implements LoanPolicy {

    @Override
    public String name() {
        return "student";
    }

    @Override
    public int loanDays() {
        return 21;
    }

    @Override
    public int finePerDay() {
        return 10;
    }

    @Override
    public int maxLoans() {
        return 5;
    }
}
