import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A small JSON reader and the text escaping the writers need.
 *
 * The JDK has no JSON library. Writing JSON by hand is easy (see Storage.saveJson);
 * reading it back means walking the text one character at a time. This parser
 * turns JSON into plain Java values:
 *   object -> Map<String, Object> (keys in file order)
 *   array  -> List<Object>
 *   string -> String,  true / false -> Boolean,  null -> null
 *   number -> Long for a whole number, Double for anything with . or e
 * Broken JSON throws IllegalArgumentException saying where it went wrong.
 * In a real project you would add Jackson or Gson through Maven instead.
 */
public final class Json {

    private final String text;
    private int pos;

    private Json(String text) {
        this.text = text;
    }

    public static Object parse(String text) {
        Json reader = new Json(text);
        reader.skipSpaces();
        Object value = reader.readValue();
        reader.skipSpaces();
        if (reader.pos != text.length()) {
            throw reader.error("unexpected text after the value");
        }
        return value;
    }

    /** A string as a JSON string literal: quotes round it, and \ " and control characters escaped. */
    public static String quote(String value) {
        StringBuilder out = new StringBuilder("\"");
        for (char c : value.toCharArray()) {
            switch (c) {
                case '"' -> out.append("\\\"");
                case '\\' -> out.append("\\\\");
                case '\n' -> out.append("\\n");
                case '\r' -> out.append("\\r");
                case '\t' -> out.append("\\t");
                default -> {
                    if (c < 0x20) {
                        out.append(String.format("\\u%04x", (int) c));
                    } else {
                        out.append(c);
                    }
                }
            }
        }
        return out.append('"').toString();
    }

    // ---- The parser: one method per kind of value ----

    private Object readValue() {
        if (pos >= text.length()) {
            throw error("the text ends where a value should be");
        }
        char c = text.charAt(pos);
        return switch (c) {
            case '{' -> readObject();
            case '[' -> readArray();
            case '"' -> readString();
            case 't' -> readWord("true", Boolean.TRUE);
            case 'f' -> readWord("false", Boolean.FALSE);
            case 'n' -> readWord("null", null);
            default -> {
                if (c == '-' || Character.isDigit(c)) {
                    yield readNumber();
                }
                throw error("unexpected character '" + c + "'");
            }
        };
    }

    private Map<String, Object> readObject() {
        Map<String, Object> object = new LinkedHashMap<>();
        pos++;                          // the {
        skipSpaces();
        if (peek() == '}') {
            pos++;
            return object;
        }
        while (true) {
            skipSpaces();
            if (peek() != '"') {
                throw error("expected a key in double quotes");
            }
            String key = readString();
            skipSpaces();
            expect(':');
            skipSpaces();
            object.put(key, readValue());
            skipSpaces();
            if (peek() == ',') {
                pos++;
            } else {
                expect('}');
                return object;
            }
        }
    }

    private List<Object> readArray() {
        List<Object> array = new ArrayList<>();
        pos++;                          // the [
        skipSpaces();
        if (peek() == ']') {
            pos++;
            return array;
        }
        while (true) {
            skipSpaces();
            array.add(readValue());
            skipSpaces();
            if (peek() == ',') {
                pos++;
            } else {
                expect(']');
                return array;
            }
        }
    }

    private String readString() {
        StringBuilder out = new StringBuilder();
        pos++;                          // the opening "
        while (true) {
            if (pos >= text.length()) {
                throw error("a string is never closed");
            }
            char c = text.charAt(pos++);
            if (c == '"') {
                return out.toString();
            }
            if (c != '\\') {
                out.append(c);
                continue;
            }
            if (pos >= text.length()) {
                throw error("a string is never closed");
            }
            char escaped = text.charAt(pos++);
            switch (escaped) {
                case '"', '\\', '/' -> out.append(escaped);
                case 'b' -> out.append('\b');
                case 'f' -> out.append('\f');
                case 'n' -> out.append('\n');
                case 'r' -> out.append('\r');
                case 't' -> out.append('\t');
                case 'u' -> {
                    if (pos + 4 > text.length()) {
                        throw error("a \\u escape needs four hex digits");
                    }
                    try {
                        out.append((char) Integer.parseInt(text.substring(pos, pos + 4), 16));
                    } catch (NumberFormatException e) {
                        throw error("a \\u escape needs four hex digits");
                    }
                    pos += 4;
                }
                default -> throw error("unknown escape \\" + escaped);
            }
        }
    }

    private Object readNumber() {
        int start = pos;
        if (peek() == '-') {
            pos++;
        }
        boolean whole = true;
        while (pos < text.length()) {
            char c = text.charAt(pos);
            if (Character.isDigit(c)) {
                pos++;
            } else if (c == '.' || c == 'e' || c == 'E' || c == '+' || c == '-') {
                whole = false;
                pos++;
            } else {
                break;
            }
        }
        String number = text.substring(start, pos);
        try {
            return whole ? (Object) Long.parseLong(number) : (Object) Double.parseDouble(number);
        } catch (NumberFormatException e) {
            throw error("not a number: " + number);
        }
    }

    private Object readWord(String word, Object value) {
        if (!text.startsWith(word, pos)) {
            throw error("unexpected character '" + text.charAt(pos) + "'");
        }
        pos += word.length();
        return value;
    }

    // ---- Small helpers ----

    private char peek() {
        return pos < text.length() ? text.charAt(pos) : '\0';
    }

    private void expect(char wanted) {
        if (peek() != wanted) {
            throw error("expected '" + wanted + "'");
        }
        pos++;
    }

    private void skipSpaces() {
        while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) {
            pos++;
        }
    }

    private IllegalArgumentException error(String problem) {
        return new IllegalArgumentException("not valid JSON at character " + pos + ": " + problem);
    }
}
