# Java Zero to Hero

[![Tasks](https://github.com/devfoundry-training/Java_zero_hero/actions/workflows/tasks.yml/badge.svg)](https://github.com/devfoundry-training/Java_zero_hero/actions/workflows/tasks.yml)

A structured, single-project teaching repository covering Java 21 from the ground up: no frameworks, no fluff. Domain-based packages, one runnable demo per domain, all fixes and anti-patterns documented in comments.

---

## Who Is This For?

- Developers learning Java for the first time or transitioning from another language
- Delegates on instructor-led Java fundamentals courses
- Self-learners who want a clear progression from basics to applied topics

Basic programming awareness is helpful but not required. No Java experience is assumed.

---

## Prerequisites

| Tool  | Minimum Version |
|-------|----------------|
| JDK   | 21             |
| Maven | 3.8+           |

```bash
java -version
mvn -version
```

---

## Repository Structure

```
src/
  main/
    java/com/teaching/
      basics/               # Primitives, arrays, strings, enums
      control_flow/         # Conditionals, switch
      iteration/            # Loops, enhanced-for, labelled break
      functions/            # Methods, scope, constructors
      oop/                  # Inheritance, polymorphism
      collections/          # List, Set, Map, Queue, generics
      exceptions/           # Checked/unchecked, custom hierarchy
      io/                   # NIO2 file handling
      datetime/             # Modern date/time API
      apis/                 # TCP sockets, HttpClient
      concurrency/          # Threads, executors, race conditions
      databases/            # JDBC with H2 and MySQL
      testing/              # JUnit 5
    resources/
      databases/schema.sql
  test/
    java/com/teaching/testing/
      CalculatorTest.java
tasks/                      # Practical tasks: the Library Manager (see tasks/README.md)
  data/                     # Shared sample data
  check/                    # The checker
  01_bookshelf/ ... 12_library_api/, B_busy_library/
                            # Brief, expected output, starter/ and solution/
```

---

## How to Use

**Build** (the demos and every task folder under `tasks/`):
```bash
mvn clean compile
```

**Run a demo:**
```bash
mvn exec:java -Dexec.mainClass="com.teaching.<domain>.<RunnerClass>"
```

**Run all tests** (the JUnit demo tests and the task 11 solution tests):
```bash
mvn test
```

**Run a practical task** (see [tasks/README.md](tasks/README.md)):
```bash
mvn -q compile exec:java@task "-Dtask=01_bookshelf/starter"
```

---

## Topics Covered

### Core Java Fundamentals

- **basics**: Primitives and the 8 wrapper types, overflow behaviour, arrays (1D + 2D), String immutability and API, `StringBuilder`, `Scanner` input, enums with fields, pass-by-value.
  ```bash
  mvn exec:java -Dexec.mainClass="com.teaching.basics.BasicsRunner"
  mvn exec:java -Dexec.mainClass="com.teaching.basics.DatatypeRunner"
  mvn exec:java -Dexec.mainClass="com.teaching.basics.ArrayRunner"
  mvn exec:java -Dexec.mainClass="com.teaching.basics.StringRunner"
  mvn exec:java -Dexec.mainClass="com.teaching.basics.EnumRunner"
  ```

- **control_flow** and **iteration**: `if`/`else-if`, traditional switch (fall-through risk), modern switch expressions (Java 14+, arrow syntax, multi-label cases), `for`/`while`/`do-while`, enhanced for, 2D iteration, labelled `break`.
  ```bash
  mvn exec:java -Dexec.mainClass="com.teaching.control_flow.ConditionalsRunner"
  mvn exec:java -Dexec.mainClass="com.teaching.control_flow.SwitchRunner"
  mvn exec:java -Dexec.mainClass="com.teaching.iteration.LoopsRunner"
  mvn exec:java -Dexec.mainClass="com.teaching.control_flow.GradingRunner"
  ```

- **functions**: Return types, method overloading, pass-by-value for primitives vs arrays, variable scope, constructor chaining with `this(...)`.
  ```bash
  mvn exec:java -Dexec.mainClass="com.teaching.functions.MethodRunner"
  ```

### Object-Oriented Programming

- **oop**: Inheritance, `super`, method overriding, polymorphism via `List<Shape>`, `instanceof` pattern matching (Java 16+).
  ```bash
  mvn exec:java -Dexec.mainClass="com.teaching.oop.OopRunner"
  ```

- **collections**: `List` (ArrayList, `List.of()` immutability, Iterator), `Set` (Hash/Linked/Tree, set operations), `Map` (Hash/Linked/Tree, `getOrDefault`, `putIfAbsent`, entrySet), `Queue`/`Deque` (ArrayDeque as FIFO and LIFO), `Collections` utilities.
  `Box<T>` and `GenericsRunner` demonstrate generics safety vs raw types and the `ClassCastException` that raw types can cause at runtime.
  ```bash
  mvn exec:java -Dexec.mainClass="com.teaching.collections.CollectionsRunner"
  mvn exec:java -Dexec.mainClass="com.teaching.collections.GenericsRunner"
  ```

### Error Handling

- **exceptions**: Checked vs unchecked, `try/catch/finally`, multi-catch (`|`), 3-level custom hierarchy (`ShapeException > InvalidDimensionException > InvalidSquareException`), `try-with-resources` with `AutoCloseable`, rethrowing with cause wrapping.
  File I/O exception patterns (EAFP vs LBYL) live in the `io` domain.
  ```bash
  mvn exec:java -Dexec.mainClass="com.teaching.exceptions.ExceptionRunner"
  ```

### File and Date/Time

- **io**: `Path` and `Files` (NIO2), `Files.writeString`/`readString` (Java 11+), `BufferedWriter`/`BufferedReader` with try-with-resources, EAFP vs LBYL with TOCTOU race condition note. Uses `Files.createTempFile()`, so no external files are required.
  ```bash
  mvn exec:java -Dexec.mainClass="com.teaching.io.IoRunner"
  ```

- **datetime**: `LocalDate`, `LocalDateTime`, `ZonedDateTime` (`withZoneSameInstant` vs `withZoneSameLocal`), `DateTimeFormatter` (parse + format + `DateTimeParseException`), `Period`/`Duration`, `Instant` for wall-clock timing.
  ```bash
  mvn exec:java -Dexec.mainClass="com.teaching.datetime.DateTimeRunner"
  ```

### APIs and Networking

- **apis**: TCP `ServerSocket`/`Socket` with `PrintWriter(autoFlush=true)`. `HttpClient` (Java 11+): synchronous GET, response metadata, reusing the client across multiple requests. No extra dependencies: `java.net.http` is part of the JDK.

  ```bash
  # HTTP (no setup required)
  mvn exec:java -Dexec.mainClass="com.teaching.apis.HttpClientRunner"

  # TCP sockets: two separate terminals
  # Terminal 1:
  mvn exec:java -Dexec.mainClass="com.teaching.apis.SocketServerRunner"
  # Terminal 2 (while server is running):
  mvn exec:java -Dexec.mainClass="com.teaching.apis.SocketClientRunner"
  ```

### Concurrency

- **concurrency**: `Thread` (3 creation styles), `start()` vs `run()` (most common beginner mistake), `join()`, daemon threads. `ExecutorService` with fixed thread pool, `Callable<T>`, `Future<T>`, `invokeAll()`. Race condition demo: `count++` non-atomicity, `synchronized`, `AtomicInteger`, `volatile`.
  ```bash
  mvn exec:java -Dexec.mainClass="com.teaching.concurrency.ThreadRunner"
  mvn exec:java -Dexec.mainClass="com.teaching.concurrency.ExecutorRunner"
  mvn exec:java -Dexec.mainClass="com.teaching.concurrency.RaceConditionRunner"
  ```

### Databases

- **databases**: JDBC with H2 embedded (zero setup, no running server). `DbConnection` initialises an in-memory schema from `schema.sql`. `StudentDao` interface + `StudentDaoImpl` with `PreparedStatement`, `RETURN_GENERATED_KEYS`, `ResultSet` mapping. `JdbcRunner` covers full CRUD, SQL injection prevention, and manual `setAutoCommit`/`commit`/`rollback` transactions.
  ```bash
  mvn exec:java -Dexec.mainClass="com.teaching.databases.JdbcRunner"
  ```

  **Connecting to a real MySQL instance** (after completing the H2 demos):
  Only two lines need to change in `DbConnection.java`:
  ```java
  private static final String URL      = "jdbc:mysql://localhost:3306/teaching";
  private static final String USER     = "your_user";
  private static final String PASSWORD = "your_password";
  ```
  The MySQL driver is already in `pom.xml`. No DAO code changes required.

### Testing

- **testing**: JUnit 5 with `@Test`, `@DisplayName`, `@BeforeEach`, `@AfterEach`, `@ParameterizedTest` with `@ValueSource` and `@CsvSource`, `assertEquals`, `assertTrue`, `assertThrows`. `Calculator` is the subject under test; `CalculatorTest` covers happy paths, edge cases, and exception paths.
  ```bash
  mvn test
  # 27 tests, 0 failures (plus the 32 task 11 solution tests)
  ```

---

## Tasks

The [`tasks/`](tasks/README.md) folder holds the **Library Manager**, one practical project shared by every Zero to
Hero repo: 12 tasks and a bonus in Python, Java, C#, C++ and JavaScript, and eight tasks (S1 to S8) in SQL's own
order on the same database. Every repo uses the same briefs, the same sample data and the same expected output; only
the code differs.

- **One task per session.** Each task is the end-of-session check and builds on the one before, so by the end the
  learner has one working program.
- **Three levels:** Core (everyone), Stretch (for those who finish early) and Challenge (the strongest learners, or
  homework).
- **Every task folder** has the brief (`README.md`), the exact expected output (`expected.txt`), a `starter/` that
  runs as it is (unfinished parts say "not implemented") and a `solution/` with Core and Stretch.
- **One checker:** `tasks/check/check.py` (Python 3.10 or later, standard library only) runs a program and prints
  `PASS`, or `FAIL` with the first line that differs from `expected.txt`. On Windows, `py` works in place of
  `python`.

[tasks/README.md](tasks/README.md) has the session to task map, the 3-day and 5-day selections, and the run and
check command for every task. To check the first task's solution, from the repo root:

```bash
python tasks/check/check.py 01 -- mvn -q compile exec:java@task "-Dtask=01_bookshelf/solution"
```

---

## Teaching Approach

- Single Maven project, no multi-module complexity
- Each domain has a `*Runner` class with a `main()` method delegates can execute directly
- Anti-patterns and common mistakes are shown alongside correct approaches, documented in comments
- Topics progress from fundamentals to applied concepts
- No frameworks: the focus is on the language, the JDK, and core patterns

---

## Tech Stack

| Technology    | Purpose                                  |
|---------------|------------------------------------------|
| Java 21       | Language (switch expressions, pattern matching, `var`, `toList()`) |
| Maven 3.8+    | Build system                             |
| JUnit 5.11    | Testing                                  |
| H2 2.3        | Embedded in-memory database for JDBC demos |
| MySQL 9.1     | JDBC driver included: connect to a live server after H2 demos |
| SQLite JDBC 3.50 | Task 10 (tasks/10_sqlite): the library in a SQLite file |

No Spring. No Hibernate. No Lombok. No mocking frameworks.

---

## What's Next?

This course covers core Java and the standard library. For the same structured approach in other languages, see the companion repositories:

- [Python Zero to Hero](https://github.com/Edrzapi/Python_zero_hero)
- [C# Zero to Hero](https://github.com/Edrzapi/CS_zero_hero)
