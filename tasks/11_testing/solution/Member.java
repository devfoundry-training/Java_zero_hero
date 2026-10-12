import java.util.ArrayList;
import java.util.List;

/**
 * A library member. The member type decides the loan policy, once, when the
 * member is created; after that the code asks the policy, never the type name.
 */
public class Member {

    private final int id;
    private final String name;
    private final String email;
    private final String type;
    private final String joined;
    private final LoanPolicy policy;
    private final List<Integer> loans = new ArrayList<>();

    public Member(int id, String name, String email, String type, String joined) {
        if (id <= 0) {
            throw new IllegalArgumentException("member id must be positive");
        }
        if (name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        this.policy = Policies.forType(type);       // throws for an unknown type
        this.id = id;
        this.name = name;
        this.email = email;
        this.type = type;
        this.joined = joined;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getType() {
        return type;
    }

    public String getJoined() {
        return joined;
    }

    public LoanPolicy getPolicy() {
        return policy;
    }

    public List<Integer> getLoans() {
        return List.copyOf(loans);
    }

    public boolean hasLoan(int bookId) {
        return loans.contains(bookId);
    }

    public boolean canBorrowMore() {
        return loans.size() < policy.maxLoans();
    }

    void addLoan(int bookId) {
        loans.add(bookId);
    }

    void removeLoan(int bookId) {
        loans.remove(Integer.valueOf(bookId));
    }

    @Override
    public String toString() {
        return "#" + id + " " + name + " (" + type + "), " + Text.plural(loans.size(), "loan", "loans");
    }
}
