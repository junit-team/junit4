## Summary of changes in version 4.13.2

# Rules

### [Pull request #1687:](https://github.com/junit-team/junit/pull/1687) Mark ThreadGroups created by FailOnTimeout as daemon groups

In JUnit 4.13 ([pull request #1517](https://github.com/junit-team/junit4/pull/1517)) an attempt was
made to fix leakage of the `ThreadGroup` instances created when a test is run with a timeout. That
change explicitly deleted the `ThreadGroup` that was created for the time-limited test. Numerous
people reported problems that were caused by explicitly deleting the `ThreadGroup`.

In this change, the code was updated to call  `ThreadGroup.setDaemon(true)` instead of deleting the
ThreadGroup.
