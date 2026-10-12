/** The member already has as many loans as their policy allows. */
public class MemberLimitReachedException extends LibraryException {

    public MemberLimitReachedException(String name, int limit) {
        super(name + " already has " + Text.plural(limit, "loan", "loans") + " (limit " + limit + ")");
    }
}
