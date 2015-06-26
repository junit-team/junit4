### Project License:  Eclipse Public License v1.0

- You will only Submit Contributions where You have authored 100% of the content.
- You will only Submit Contributions to which You have the necessary rights. This means that if You are employed You have received the necessary permissions from Your employer to make the Contributions.
- Whatever content You Contribute will be provided under the Project License(s).

---

### How to submit a pull request

We love pull requests. Here is a quick guide:

1. You need to have Maven and a JDK (at least version 1.5) installed.
2. [Fork the repo](https://help.github.com/articles/fork-a-repo).
3. [Create a new branch](https://help.github.com/articles/creating-and-deleting-branches-within-your-repository/) from master.
4. Ensure that you have a clean state by running `mvn verify`.
5. Add your change together with a test (tests are not needed for refactorings and documentation changes).
6. Format your code: Import the JUnit project in Eclipse and use its formatter or apply the rules in the `CODING_STYLE` file manually. Only format the code you've changed; reformatting unrelated code makes it harder for us to review your changes.
7. Run `mvn verify` again and ensure all tests are passing.
8. Push to your fork and [submit a pull request](https://help.github.com/articles/creating-a-pull-request/).

Now you are waiting on us. We review your pull request and at least leave some comments.


Note that if you are thinking of providing a fix for one of the bugs or feature requests, it's usually
a good idea to add a comment to the bug to make sure that there's agreement on how we should proceed.
