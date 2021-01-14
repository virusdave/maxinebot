# MaxineBot

Reclaiming my time, for the masses!

### TL;DR

This is a slack bot with GCal integration to help identify recurring
meetings that provide little (or negative!) value for attendees.

## Background

Synchronous meetings can interrupt the productive time of attendees.
Often, interruptions are highly costly due to the disruption of [flow](
  https://heeris.id.au/trinkets/ProgrammerInterrupted.png).

If unchecked, the loss of productive flow time can absolutely cripple
the productivity of an individual or team.

This system will track recurring Gcal meetings with multiple attendees,
and poll those attendees in a lightweight fashion after each meeting
instance to determine how valuable, timely, focused, and useful the
meeting was.

When a meeting's value drops significantly, it's a good signal that the
meeting should be eliminated, freeing up the schedules of its attendees.

