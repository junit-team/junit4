## Summary of changes in version 4.13.1

# Test Runners

### [Pull request #1669:](https://github.com/junit-team/junit/pull/1669) Make `FrameworkField` constructor public

Prior to this change, custom runners could make `FrameworkMethod` instances, but not `FrameworkField` instances. This small change allows for both now, because `FrameworkField`'s constructor has been promoted from package-private to public.
