# Why it's built this way

These are the decisions I've made so far on Focus Dashboard and the thinking behind them. Some were obvious, some I changed my mind on, and a few only clicked once I saw what would break without them.

## Keeping the scope honest

The first version has no AI in it. It would be easy to bolt an LLM onto a study app, but I'd rather finish something solid first. Study-time suggestions in v1 come from simple rules, and AI features like a study assistant or feedback on graded work wait until v1 is built and deployed.

I also decided not to use Lombok. It's everywhere in Spring codebases because it writes getters, setters, and constructors for you, but this project is how I'm learning Java backend development, and I want to understand everything in it, including the boring parts Lombok would hide. It costs some extra lines per class, and I'm fine with that.

## How the code is organized

The backend uses the standard layered setup: controllers handle HTTP, services hold the business rules, and repositories talk to the database. It means a few more files per feature, but each one has a single job. I can test the rules without running a web server, and I can change how data is stored without touching the endpoints.

Every class gets its dependencies through its constructor, which turned out to matter more than I expected. When I wrote my first tests, I could hand a service fake repositories directly, with no Spring needed at all.

## The data model

IDs are UUIDs, and the database creates them when a row is saved. My entities deliberately have no way to set an ID, because Spring decides whether an object is new by checking if its ID is empty. Letting code fill one in early would quietly turn inserts into failed updates.

Tables use plural, lowercase names like `users`, while the Java classes stay singular like `User`, since one object is one row and a table holds many. It also sidesteps a trap I nearly walked into: `user` is a reserved word in PostgreSQL, so a table with that name won't even create.

The most important rule I follow is to store facts and calculate summaries. Each study session saves how many minutes it lasted, but nothing stores a "total hours this week" number. That gets calculated whenever someone asks. A stored total goes stale the moment a session is added or deleted, and a calculated one can't.

Weekly goals work the same way. When you change your Physics goal, the app doesn't overwrite the old one. It adds a new goal with the date it takes effect. That keeps a history I can compare against grades later, and it means nothing has to create a fresh goal every week, since a goal just stays in effect until a newer one replaces it.

A few smaller choices follow from thinking about what the data means:

- **Nothing can be created without its parent.** A class can't exist without a semester, so the constructor won't let you make one without it.
- **Empty values only mean "we don't know yet."** The only fields allowed to be empty are a session's end time and minutes, because a running session genuinely doesn't have them yet.
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

## The database workflow

Right now, Hibernate creates and updates the tables automatically, which is great while the design is still moving. It only ever adds things, though, and never removes or renames them, so before deploying I'll switch to Flyway migrations, where every schema change is a versioned script, and Hibernate will just double-check that everything matches.

## Testing

The service tests use Mockito to fake the repositories, so they run in milliseconds and test the business rules without needing a database. They cover the rules most likely to break quietly: backwards semester dates, double-started sessions, duplicate emails, fake timezones, and the Sunday-night week boundary.

## What's planned

I'm going to deploy the web app before building the desktop agent. A working link matters more right now, and the agent is its own program that only needs the API to exist.

The agent itself will be written in C++. It's a background process that talks directly to the operating system and should barely touch your CPU or memory, which is exactly what C++ is good at. I'm planning to put the OS-specific parts behind a single interface, with separate macOS and Windows versions, so the rest of the agent doesn't care which system it's running on.
