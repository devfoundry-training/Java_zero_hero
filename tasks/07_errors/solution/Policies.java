import java.util.List;

/**
 * The one place that maps a member type name to its policy.
 * One shared object per policy: they hold no state, so every member can use the same one.
 */
public final class Policies {

    public static final LoanPolicy STANDARD = new StandardPolicy();
    public static final LoanPolicy STUDENT = new StudentPolicy();
    public static final LoanPolicy STAFF = new StaffPolicy();

    /** The member types in the data, in the order the reports list them. */
    public static final List<LoanPolicy> ALL = List.of(STANDARD, STUDENT, STAFF);

    private Policies() {
        // static members only: no objects of this class
    }

    public static LoanPolicy forType(String type) {
        return switch (type) {
            case "standard" -> STANDARD;
            case "student" -> STUDENT;
            case "staff" -> STAFF;
            default -> throw new IllegalArgumentException("member type must be standard, student or staff");
        };
    }
}
