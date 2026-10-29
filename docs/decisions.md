# Why it's built this way

These are the decisions I've made so far on Focus Dashboard and the thinking behind them. Some were obvious, some I changed my mind on, and a few only clicked once I saw what would break without them.

## Keeping the scope honest

The first version has no AI in it. It would be easy to bolt an LLM onto a study app, but I'd rather finish something solid first. Study-time suggestions in v1 come from simple rules, and AI features like a study assistant or feedback on graded work wait until v1 is built and deployed.

I also decided not to use Lombok. It's everywhere in Spring codebases because it writes getters, setters, and constructors for you, but this project is how I'm learning Java backend development, and I want to understand everything in it, including the boring parts Lombok would hide. It costs some extra lines per class, and I'm fine with that.

## How the code is organized

The backend uses the standard layered setup: controllers handle HTTP, services hold the business rules, and repositories talk to the database. It means a few more files per feature, but each one has a single job. I can test the rules without running a web server, and I can change how data is stored without touching the endpoints.

Every class gets its dependencies through its constructor, which turned out to matter more than I expected. Tests can hand a service fake repositories directly, with no Spring needed at all.

## The data model

IDs are UUIDs, and the database creates them when a row is saved. My entities deliberately have no way to set an ID, because Spring decides whether an object is new by checking if its ID is empty. Letting code fill one in early would quietly turn inserts into failed updates.

Tables use plural, lowercase names like `users`, while the Java classes stay singular like `User`, since one object is one row and a table holds many. It also sidesteps a trap I nearly walked into: `user` is a reserved word in PostgreSQL, so a table with that name won't even create.

The most important rule I follow is to store facts and calculate summaries. Each study session saves how many minutes it lasted, but nothing stores a "total hours this week" number. That gets calculated whenever someone asks. A stored total goes stale the moment a session is added or deleted, and a calculated one can't.

Weekly goals work the same way. When you change your Physics goal, the app doesn't overwrite the old one. It adds a new goal with the date it takes effect. That keeps a history I can compare against grades later, and it means nothing has to create a fresh goal every week, since a goal just stays in effect until a newer one replaces it.

A few smaller choices follow from thinking about what the data means:

- **Nothing can be created without its parent.** A class can't exist without a semester, so the constructor won't let you make one without it.
- **Empty values only mean "we don't know yet."** A session's end time and minutes are empty while it's running, a class's final letter grade is empty until the semester ends, and the onboarding completion time is empty until setup is done.
- **Choices like session mode are enums stored by name,** so a typo like `"pomodorro"` gets rejected, and adding new options later doesn't scramble old data.
- **Credits and hours use `BigDecimal`** instead of `double`, because some schools use half credits, and floating-point rounding errors have no place in GPA math.
- **Email is unique and can't be changed after signup,** because it's going to be how people log in. Display names don't need to be unique, since nothing looks people up by name. They're capped at 16 characters as a nickname limit.
- **Deleting a user deletes everything they own.** A semester with no owner doesn't mean anything.

## Getting time right

This is the part I spent the most time thinking about. Session start and end times are stored as exact moments in UTC, while dates like a semester's start are stored as plain calendar dates. "10 PM" means nothing without a timezone, but an exact moment can always be converted later.

The weekly progress report then calculates "this week" in the user's own timezone. If you study until 11:30 PM Sunday in New York, it's already Monday in UTC, and a server-time calculation would count that session toward next week.

To actually test this, the progress logic gets the current time from an injected clock instead of reading the system clock directly. In tests, I freeze it at exactly 11:30 PM Sunday in New York and check that the week boundaries land in the right place.

## The API

Endpoints never send or receive database entities directly. Each one has its own small request and response records. The main reason is that my entities point at each other, since a user has semesters and each semester points back to its user, so turning them straight into JSON loops forever. The response records include a parent's ID instead of the whole parent. They also stop clients from setting things they shouldn't, like an ID or a session's start time.

Creating and updating use separate request records because their rules differ. Everything is required when you create something, and everything is optional when you update it.

Updates are partial (PATCH), so changing your timezone doesn't mean resending your whole profile.

Study sessions aren't edited like normal records. You start one and you end one, and the server fills in the times, the status, and the minutes. You can only have one session running at a time.

Validation happens in two places. The request records check the shape of the input, like required fields, lengths, and positive numbers. The services check the actual business rules, like whether a semester's dates are in order, whether a timezone is real, and whether you already have a session going. Errors come back with codes that mean something: 400 for bad input, 404 for something that doesn't exist, and 409 for a conflict. A 500 should only ever mean I have a bug.

## Grades and GPA

Each class splits its grade into weighted categories, like Homework at 20% and Midterm at 30%. I cap the total at 100% per class and reject anything that would go over, because a class that adds up to 110% can't produce a meaningful grade.

The current grade only counts categories that have at least one graded item, and divides by the weights actually used. If the final is worth 50% and hasn't happened yet, treating it as a zero would make every grade look terrible until the end of the semester, and ignoring its weight without renormalizing would cap the grade at 50%. Dividing by the weights in play answers the question I actually care about: how am I doing on what's been graded so far?

Extra credit is allowed, so a score of 55 out of 50 is fine. Plenty of classes work that way, and a rule against it would just make the app wrong for those classes.

The official final letter grade overrides the calculated one. At the end of a semester, a curve or a professor's judgment can make the real grade differ from the math, and the transcript is what counts for GPA. The calculated percent is still shown next to it, so I can see the difference.

Letter lookup uses the percent rounded to two decimals, since that's the number the student sees. 92.99 is an A- and 93.00 is an A, with no hidden digits deciding it behind the scenes. The grade scale is also per user, seeded with a standard one, because schools draw their cutoffs differently. Letters themselves can't change, only their cutoffs and grade points, and the lowest cutoff has to stay at 0% so every grade gets a letter.

Prior credits and GPA are a snapshot I enter once at signup, not something calculated from old classes. Cumulative GPA is then the prior quality points plus this app's quality points, divided by all the credits. Since the snapshot only covers what happened before the app, nothing gets counted twice.

All of this uses `BigDecimal`, and rounding happens once at the end of each calculation (half-up), not at every step, so small errors can't pile up. The math lives in one class with no Spring or database in it, so the tests can check exact numbers without any setup.

## Free time and check-ins

Free time is 168 hours minus sleep, work, custom commitments like the gym, and class hours. Only the current semester's classes count, since last semester's classes don't take up this week. Class hours live on the subject, next to its credits, because they're a fact about that class.

The app lets the total go over 168 hours and warns about it instead of blocking. If I rejected the input, the person would be stuck guessing which number to cut. If I show a negative free-time number with an "overbooked" warning, they can see the problem and fix it themselves. The same goes for the other warnings, like low sleep or a goal bigger than the free time.

The warning thresholds are named constants in one place, because they're reasonable guesses and not science. I used 7 hours of sleep a night, and treat under 30 free hours as a packed week, since meals and hygiene come out of free time too. Study time starts at about 2 hours per credit, then scales with the kind of class. Each class has a category (STEM, arts and humanities, social sciences, business, health, law, occupational, or other) that sets a starting multiplier, and the student's own strength rating for that class adjusts it: weak raises it and strong lowers it. I didn't want a list of subject names in the code, and I didn't want one fixed number for everybody. A student who is strong at STEM and weak at humanities ends up with more recommended time for the humanities class (2.5 hours per credit, against 2.0 for the STEM class), even though STEM starts higher. The category multipliers are rough guesses I accepted as starting points, not numbers from a study, and they're constants so they're easy to tune. Law gets its own category because of its heavy reading load, which doesn't fit with business or humanities, and the frontend will label it as graduate-level law since that's where most law classes happen.

Check-ins are a quick reflection after a completed session, with mood, difficulty, and confidence rated 1 to 5, plus optional notes. There's one per session, and they can't be edited. A check-in is a snapshot of how someone felt, so fixing a misclick means deleting it and submitting a new one. I'm only storing them for now, and they'll feed the suggestions in a later phase.

Onboarding is stored as a timestamp instead of a boolean. It answers "has this person finished setup?" the same way, but it also records when, which could be useful later. Calling it twice keeps the first timestamp.

One cost I accepted: a session and its check-in are a one-to-one link, and Hibernate can't load the check-in side lazily. That means one extra query for every session it loads. At this size it doesn't matter, and I'll revisit it if sessions ever get loaded in bulk.

Suggestions based on grades, like noticing that someone is struggling and has free time, are left for Phase 8, where hours, grades, and check-ins can be looked at together.

## The database workflow

Right now, Hibernate creates and updates the tables automatically, which is great while the design is still moving. It only ever adds things, though, and never removes or renames them, so before deploying I'll switch to Flyway migrations, where every schema change is a versioned script, and Hibernate will just double-check that everything matches.

## Testing

The service tests use Mockito to fake the repositories, so they run in milliseconds and test the business rules without needing a database. They cover the rules most likely to break quietly: backwards semester dates, double-started sessions, duplicate emails, fake timezones, the Sunday-night week boundary, the grade and GPA math, the free-time warnings including their exact boundaries, and the one-check-in-per-session rule.

## What's planned

I'm going to deploy the web app before building the desktop agent. A working link matters more right now, and the agent is its own program that only needs the API to exist.

The agent itself will be written in C++. It's a background process that talks directly to the operating system and should barely touch your CPU or memory, which is exactly what C++ is good at. I'm planning to put the OS-specific parts behind a single interface, with separate macOS and Windows versions, so the rest of the agent doesn't care which system it's running on.
