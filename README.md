# Focus Dashboard

**Author**: Franko Ndou

**Status**: In development

## Overview

This project began as a response to a problem I kept running into as a computer science student: I would sit down to study, lose focus within a few minutes, and end the session with no real sense of how much time I had actually spent on the subject that mattered. Existing productivity apps either track time without connecting it to a goal, or they block distractions without any sense of whether the blocking is working. I wanted something that ties the three together in one place: a goal for each subject, a way to log real focused time against it, and a record of whether that time is translating into better grades.

The result is a full stack productivity system built around three components. A backend service handles user accounts, study goals, session logs, and grade records. A web dashboard lets a user set weekly goals per subject, run a timer, and see progress toward those goals. A local agent, written in C++, monitors which application is active on the user's machine during a study session and reports that usage back to the backend, so the system has an honest picture of whether a session was actually spent focused or not.

## Motivation

Most time tracking tools separate the act of logging time from any judgment about whether the time was well spent. A student can log two hours toward a subject and still have spent much of that time on unrelated tabs or applications. This project treats that gap as the actual problem to solve. Rather than trusting a self reported timer, the system pairs the timer with a lightweight local process that can confirm what was actually in focus while the clock was running, and it uses that data to give feedback on where a student's time is really going.

## Planned Architecture

**Backend**: Java, Spring Boot, PostgreSQL. Exposes a REST API for authentication, subjects, goals, study sessions, and grades.

**Frontend**: Next.js, TypeScript, Tailwind CSS. A dashboard for setting goals, running timers, and reviewing progress and grade trends by subject.

**Local agent**: C++. Runs on the user's machine during a study session, tracks the active application and idle time using native OS APIs, and sends structured usage events to the backend.

**AI layer**: A suggestion engine that reviews a student's logged hours against their grade trends and recommends adjusting weekly goals, along with an image analysis feature that lets a student upload a photo of a graded assignment and receive feedback on which topics need more attention.

## Current Progress

This project is in the early stages of development. The data model and backend API are being built first, followed by the web dashboard, the local agent, and finally the AI features. A detailed breakdown of the build order is tracked in the project's milestones.

## Repository Structure

```
├── backend
│   └── (Spring Boot API)
├── frontend
│   └── (Next.js dashboard)
├── agent
│   └── (C++ local usage tracker)
├── docs
├── README.md
└── .gitignore
```

## Next Steps

* Finish the core backend API for subjects, goals, and sessions
* Add authentication
* Build the initial web dashboard and connect it to the API
* Deploy a working version for early testing
* Build the C++ agent and integrate it with the backend
* Add the AI suggestion and homework analysis features

## Contact

**Franko Ndou** | frankondou@gmail.com
