import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * A library member. Two members can share a name: identity is the id.
 * The list of book ids on loan is private; Library changes it through
 * addLoan and removeLoan, and everyone else gets a read-only copy.
 */
public class Member {

    private static final Set<String> TYPES = Set.of("standard", "student", "staff");

    private final int id;
    private final String name;
    private final String email;
    private final String type;
    private final String joined;        // YYYY-MM-DD, kept as text in this task
    private final List<Integer> loans = new ArrayList<>();

    public Member(int id, String name, String email, String type, String joined) {
        if (id <= 0) {
            throw new IllegalArgumentException("member id must be positive");
        }
        if (name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        if (!TYPES.contains(type)) {
            throw new IllegalArgumentException("member type must be standard, student or staff");
        }
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

    /** The book ids on loan, in the order borrowed. A copy: changing it changes nothing here. */
    public List<Integer> getLoans() {
        return List.copyOf(loans);
    }

    public boolean hasLoan(int bookId) {
        return loans.contains(bookId);
    }

    void addLoan(int bookId) {
        loans.add(bookId);
    }

    void removeLoan(int bookId) {
        loans.remove(Integer.valueOf(bookId));     // remove the value, not the item at that index
    }

    @Override
    public String toString() {
        int count = loans.size();
        return "#" + id + " " + name + " (" + type + "), " + count + (count == 1 ? " loan" : " loans");
    }
}
