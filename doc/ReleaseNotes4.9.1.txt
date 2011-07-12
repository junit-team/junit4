## Summary of Changes in version 4.9.1 [unreleased!] ##

### Theories ###

The `Theories` runner does not anticipate theory parameters that have generic
types, as reported by github#64.  Fixing this won't happen until `Theories` is
moved to junit-contrib. In anticipation of this, 4.9.1 adds some of the
necessary machinery to the runner classes, and deprecates a method that only
the `Theories` runner uses, `FrameworkMethod`#producesType(). 
The Common Public License that JUnit is released under is now included
in the source repository.

Thanks to `@pholser` for identifying a potential resolution for github#64
and initiating work on it.
