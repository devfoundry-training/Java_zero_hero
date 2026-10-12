# Tasks: the Library Manager

One set of tasks for every Zero to Hero repo (Python, Java, C#, C++, JavaScript; the SQL repo uses the same schema
and data in its own order). Every language gets the same briefs, the same sample data and the same expected output.
Only the code differs.

There are 12 tasks and a bonus, one for the end of each session. Each task combines everything taught in that
session and builds on the task before, so by the end the learner has one working program, the **Library Manager**:
from a fixed array of ten books to a tested REST API over a SQLite database.

## Core, Stretch and Challenge

Every task has three levels:

- **Core** is for everyone. `expected.txt` is the exact Core output for the sample data, and it is what the checker
  compares.
- **Stretch** is for those who finish early. A solution prints its Stretch output only when asked (see the running
  section below), so its default run always matches `expected.txt`.
- **Challenge** is for the strongest learners or for homework. The solutions cover Core and Stretch only.

## Session to task map

The topic folders each task follows in each repo. "(no topic yet)" marks a session the repo does not teach today:
the task still works, but the repo needs a short topic file before the task is used on a course.

| # | Task | Session | Python | Java | C# | C++ | JavaScript |
|---|---|---|---|---|---|---|---|
| 01 | Bookshelf | types, operators, conditionals, switch, arrays | basics | basics, control_flow | datatypes, operators, control_flow | basics, control_flow | basics, control_flow |
| 02 | Search and report | loops, strings | iteration | iteration, basics (strings) | iteration, string_manipulation | iteration | iteration |
| 03 | Functions and a menu | functions, modules | functions, module_packages | functions | (no topic yet) | functions, compilation | functions |
| 04 | Dynamic catalogue | collections | collections | collections | collections, linq | collections | collections |
| 05 | Classes | classes, encapsulation | oop | oop | oop | oop | oop |
| 06 | Book types and loan policies | inheritance, interfaces, polymorphism | oop | oop | oop | oop, memory | oop |
| 07 | Errors done properly | exceptions | exceptions | exceptions | exceptions | exceptions | exceptions |
| 08 | Persistence | file I/O, CSV, JSON | io | io | io | io | (no topic yet) |
| 09 | Loans with due dates | dates | (no topic yet) | datetime | (no topic yet) | datetime | (no topic yet) |
| 10 | SQLite storage | databases | databases | databases | (no topic yet) | c_interop (C API) | (no topic yet) |
| 11 | Test the rules | testing, TDD | testing | testing | CS_zero_heroTests | testing | (no topic yet) |
| 12 | Expose the library | HTTP APIs | apis | apis | (no topic yet) | (no topic yet) | async |
| B | Busy library (bonus) | concurrency | (no topic yet) | concurrency | (no topic yet) | concurrency | async |

**SQL repo:** the same `schema.sql`, `seed.sql` and CSV data, with eight tasks (S1 to S8) in SQL's own order:
SELECT, filtering, joins, aggregation, subqueries and views, window functions, design (a `reservations` table, as in
task 10 Challenge 2) and transactions (a borrow that rolls back, as in task 10 Stretch 1).

## Choosing tasks: 3 days or 5 days

**5-day course:** all 12, one or two per half day in order, with B for groups that finish early or as homework.

| Day | Tasks |
|---|---|
| 1 | 01, 02, 03 |
| 2 | 04, 05, 06 |
| 3 | 07, 08, 09 |
| 4 | 10, 11 |
| 5 | 12, B (bonus) |

**3-day course:** these 8, which follow the usual fundamentals order and make one program end to end:

| Day | Tasks | Why |
|---|---|---|
| 1 | 01, 02, 03 | the language basics, ending with a working menu |
| 2 | 04, 05, 06 | collections and the object model the rest depends on |
| 3 | 07, 08 | errors and files: a program that survives bad input and keeps its data |

Swaps for a 3-day group with different needs (the starters carry the previous solution, so skipping is safe):

- **Testing focus:** replace 08 with 11 (start from the task 11 starter; the date tests use the model it provides).
- **Strong group:** fold 02 into 01 (Core only) and add 09 on day 3.
- **Web focus:** replace 06 with 12 on day 3 (start from the task 12 starter).

## What is in this folder

```
tasks/
  README.md              this file
  data/                  sample data, identical in every repo: books, members, loans, schema.sql, seed.sql
  check/check.py         the checker (Python 3.10 or later, standard library only)
  check/api_script.json  the requests the checker sends in task 12
  01_bookshelf/
    README.md            the brief: Core, Stretch, Challenge, data contract, output format, language notes
    expected.txt         the exact Core output for the sample data
    starter/             builds and runs as it is; unfinished parts say "not implemented"
    solution/            Core and Stretch, passes the check
  ...
  12_library_api/        also expected_stretch.txt (the Open Library summary)
  B_busy_library/        bonus: concurrency
```

Each `starter/` contains the previous task's solution (or the parts of it the task builds on) plus stubs for the
new work, so a group can start any task without having finished the one before. Task B stands alone, so it can
follow any task from 07 on.

## The checker

`check/check.py TASK [--expected FILE] [--timeout SECS] -- COMMAND ...` runs the command and prints `PASS`, or
`FAIL` with the first line that differs. Exit code 0 is a pass. It needs Python 3.10 or later and nothing else.

- Line endings, trailing spaces and trailing blank lines do not count; everything else does.
- Tasks 03 and 07 read commands from standard input: the checker feeds in `data/input_03.txt` and
  `data/input_07.txt` itself.
- Task 11 runs the tests and passes when the run succeeds and every test name in `expected.txt` appears in the
  output.
- Task 12 starts the server on a free port (`{port}` in the command is replaced with it), sends the requests in
  `check/api_script.json`, compares the answers with `expected.txt`, then stops the server.

## Conventions every solution follows

| Rule | Detail |
|---|---|
| Output | Exactly `expected.txt`: no extra lines, no prompts, no colours. Debug output goes to stderr. |
| Data folder | Tasks 08 to 12 take it as the first argument, default `tasks/data`. Tasks 01 to 07 carry their data in the code. |
| Today | Tasks 09 and 10 take today's date as the second argument, default `2026-10-12`; task 12 uses `2026-10-12`. Nothing reads the real clock. |
| Port | Task 12 takes the port as the second argument, default `8080`. |
| Written files | Tasks 08 and 10 write into `out/` in the current directory (git-ignored). |
| Money | Integer pence everywhere, shown as `£1.25`. |
| Sorting text | A-Z means lower-case, character by character (ordinal). Never a locale-aware compare. |

## Running and checking: Java

You need JDK 21, Maven 3.8 or later, and Python 3.10 or later for the checker (`py -3` on Windows if `python` is not
on the path). Run every command from the repo root.

Every `starter` and `solution` folder is a small program of its own with a class called `Main`. `mvn compile`
compiles all of them (into `target/tasks/...`) along with the rest of the repo, and `exec:java@task` runs the folder
named by `-Dtask`. Keep the `-D` arguments in double quotes: PowerShell splits them otherwise.

```bash
mvn -q compile exec:java@task "-Dtask=01_bookshelf/starter"
mvn -q compile exec:java@task "-Dtask=01_bookshelf/solution" "-Dexec.args=--stretch"
mvn -q compile exec:java@task "-Dtask=08_persistence/solution" "-Dexec.args=tasks/data"
mvn -q compile exec:java@tests "-Dtask=11_testing/solution"
```

Tasks 03 and 07 read from standard input: add `< tasks/data/input_03.txt` in bash or cmd, or put
`Get-Content tasks/data/input_03.txt |` in front of the command in PowerShell. The checker feeds the file in for you.
Tasks 08 and 10 write an `out/` folder in the current folder (it is git-ignored).

To check a task, put the run command after `--`. Use `starter` while you work and `solution` to see the target:

| Task | Check command |
|---|---|
| 01 | `python tasks/check/check.py 01 -- mvn -q compile exec:java@task "-Dtask=01_bookshelf/starter"` |
| 02 | `python tasks/check/check.py 02 -- mvn -q compile exec:java@task "-Dtask=02_search_and_report/starter"` |
| 03 | `python tasks/check/check.py 03 -- mvn -q compile exec:java@task "-Dtask=03_functions/starter"` |
| 04 | `python tasks/check/check.py 04 -- mvn -q compile exec:java@task "-Dtask=04_dynamic_catalogue/starter"` |
| 05 | `python tasks/check/check.py 05 -- mvn -q compile exec:java@task "-Dtask=05_classes/starter"` |
| 06 | `python tasks/check/check.py 06 -- mvn -q compile exec:java@task "-Dtask=06_book_types/starter"` |
| 07 | `python tasks/check/check.py 07 -- mvn -q compile exec:java@task "-Dtask=07_errors/starter"` |
| 08 | `python tasks/check/check.py 08 -- mvn -q compile exec:java@task "-Dtask=08_persistence/starter" "-Dexec.args=tasks/data"` |
| 09 | `python tasks/check/check.py 09 -- mvn -q compile exec:java@task "-Dtask=09_due_dates/starter" "-Dexec.args=tasks/data"` |
| 10 | `python tasks/check/check.py 10 -- mvn -q compile exec:java@task "-Dtask=10_sqlite/starter" "-Dexec.args=tasks/data"` |
| 11 | `python tasks/check/check.py 11 -- mvn -q compile exec:java@tests "-Dtask=11_testing/starter"` |
| 12 | `python tasks/check/check.py 12 -- mvn -q compile exec:java@task "-Dtask=12_library_api/starter" "-Dexec.args=tasks/data {port}"` |
| 12 Stretch | `python tasks/check/check.py 12_stretch -- mvn -q compile exec:java@task "-Dtask=12_library_api/starter" "-Dexec.args=--summarise tasks/data/openlibrary_dune.json"` |
| B | `python tasks/check/check.py B -- mvn -q compile exec:java@task "-Dtask=B_busy_library/starter"` |

Notes for Java:

- **Task 10** uses the `org.xerial:sqlite-jdbc` driver, already in `pom.xml`. Maven downloads it on the first build.
- **Task 11** runs with the JUnit console launcher (`exec:java@tests`), which prints every test name; the checker
  needs those names. `mvn test` also runs the task 11 solution tests, but its report only lists failures.
- **Task 12** starts a server. The checker picks a free port, puts it where `{port}` is, and stops the server
  afterwards. To try it by hand, run it with `"-Dexec.args=tasks/data 8080"` and stop it with Ctrl+C.
- **Task B** prints the same summary every run, but run the check several times: a race that is only sometimes wrong
  is still wrong.
- **Money** is printed with the pound sign. The checker asks Java for UTF-8 output itself; if the pound sign looks
  wrong in your own terminal, set `JAVA_TOOL_OPTIONS=-Dstdout.encoding=UTF-8`.

### In IntelliJ IDEA

Nothing to set up by hand: IntelliJ makes every `starter` and `solution` folder a module of its own when it reads
`pom.xml`, and the repo brings a ready-made run configuration for each one. Do not use Mark Directory as.

1. **File > Open**, pick the repo folder (the one with `pom.xml`) and choose **Trust Project**.
2. Wait for the Maven import to finish (the progress bar at the bottom right). If IntelliJ asks for a JDK, pick
   JDK 21 (**File > Project Structure > Project > SDK**).
3. Open your task's file, for example `tasks/05_classes/starter/Main.java`. Code completion works in every task
   folder, and JUnit works in task 11.
4. To run it, pick the task in the run list at the top of the window (**Task starters** or **Task solutions**,
   for example `05_classes starter`) and click the green **Run** button. It runs the same Maven command as the
   table above. Task 11's configurations run its tests.
5. Tasks 03 and 07 wait for commands: type them in the Run window. Task 12 keeps running until you click the red
   **Stop** button.
6. For Stretch output or other arguments: **Run > Edit Configurations**, pick the task and add to the end of the
   **Run** (command line) field, for example `-Dexec.args=--stretch`. Put double quotes round anything with a
   space: `"-Dexec.args=tasks/data 2026-10-12 --stretch"`.
7. To check a task, open the **Terminal** tab (Alt+F12) and use the check command from the table.

Good to know:

- The green arrow beside `main` also runs a task, but with IntelliJ's own build, which puts every task folder into
  the same `target/classes`. If it ever shows another task's output, use the run configuration instead (it always
  builds with Maven, and Maven ignores those IDE-built classes).
- In the Maven tool window the `intellij-task-modules` profile is ticked automatically. Leave it on: it is what
  lets the task modules see JUnit in the IDE. It never runs outside IntelliJ.
- Tested with IntelliJ IDEA 2026.2. If your IntelliJ shows the task folders as plain folders rather than blue
  source folders, it is too old to make the task modules: update it, or use the run configurations and the
  commands above.
