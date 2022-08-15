## Summary of changes in version 4.13.1

# Rules

### Security fix: `TemporaryFolder` now limits access to temporary folders on Java 1.7 or later

A local information disclosure vulnerability in `TemporaryFolder` has been fixed. See the published [security advisory](https://github.com/junit-team/junit4/security/advisories/GHSA-269g-pwp5-87pp) for details.

# Test Runners

### [Pull request #1669:](https://github.com/junit-team/junit/pull/1669) Make `FrameworkField` constructor public

Prior to this change, custom runners could make `FrameworkMethod` instances, but not `FrameworkField` instances. This small change allows for both now, because `FrameworkField`'s constructor has been promoted from package-private to public.
