We love pull requests. Here is a quick guide:

1. You need to have Maven and a JDK (at least version 1.5) installed.
2. Fork the repo (see https://help.github.com/articles/fork-a-repo)
3. Create a new branch from master.
4. Ensure that you have a clean state by running `mvn verify`
5. Add your change together with a test (tests are not needed for refactorings and documentation changes).
6. Format your code: Import the JUnit project in Eclipse and use its formatter or apply the rules in the `CODING_STYLE` file manually. Only format the code you've changed; reformatting unrelated code makes it harder for us to review your changes.
6. Run `mvn verify` again and ensure all tests are passing.
8. Push to your fork and submit a pull request.

Now you are waiting on us. We review your pull request and at least leave some comments.


Note that if you are thinking of providing a fix for one of the bugs or feature requests, it's usually
a good idea to add a comment to the bug to make sure that there's agreement on how we should proceed.
