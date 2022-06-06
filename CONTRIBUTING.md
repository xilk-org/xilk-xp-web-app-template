# Xilk Contributing Guidelines

First of all, thank you so much! Seriously! Without your help on bug reports,
enhancement/feature requests, and code/doc changes, this project—staffed by
only one, very part-time maintainer—could never go anywhere. Your help really
means a lot!

Please read everything below before starting on your contribution. This will
make the process smooth. And smooth is fast. Thanks again!

## Rules

* Be kind.
  - Remember that we are all human here. We're all different, with our own
    strengths and weaknesses, likes and dislikes. And we all have times when
    we're not at our best. But *we're all on the same team*. So let's support
    each other.
  - If you get upset or otherwise have trouble being kind for a bit, please
    step away from this project until your kindness returns.
  - If you feel that this rule is unjustified or infringes your
    rights/freedoms to abuse, demean, discriminate, disrespect, harass, or be
    passive-aggressive toward others, please do not contribute to this project.
    These behaviors are unacceptable here, and will lead to your being blocked.

* Be considerate.
  - Use [Stack Overflow](https://stackoverflow.com/) if you have a question
    about the software, not GitHub issues. GitHub issues are for bug reports
    and enhancement/feature requests only.
  - Prevent extra work by searching for existing
    [issues](https://github.com/xilk-org/xilk-xp-web-app-template/issues)
    before creating a new one.
  - Write text in clear, simple English. Many readers may not be native English
    speakers.
  - Comment only when it adds actionable information to the issue or pull
    request. When appropriate, use
    [reactions](https://github.blog/2016-03-10-add-reactions-to-pull-requests-issues-and-comments/)
    instead of comments.

* Be consistent.
  - Use issue and pull request templates unless there is strong reason not to.
  - Write documentation in the same style as the rest of the docs here.
  - Write code in the same style as the rest of the code here, following
    [The Clojure Style Guide](https://github.com/bbatsov/clojure-style-guide).
  - Write commit messages in the same style as the rest here, following
    [The seven rules of a great Git commit message](https://cbea.ms/git-commit/#seven-rules).
    and [Conventional Commits](https://www.conventionalcommits.org/).
  - Keep your change, if any, focused to only the corresponding issue.

## How to Submit an Issue

1. Search [issues](https://github.com/xilk-org/xilk-xp-web-app-template/issues)
   to see if anyone else has already submitted something similar.
2. If so, add a comment ONLY if you have additional relevant details to
   contribute; otherwise, feel free to add a
   [reaction](https://github.blog/2016-03-10-add-reactions-to-pull-requests-issues-and-comments/)
   to any comments to show your interest/support. Your maintainer thanks you for
   not waking him up with redundant comment/issue notifications. 🤗

   If not, then please submit a
   [new issue](https://github.com/xilk-org/xilk-xp-web-app-template/issues/new/choose)
   using the appropriate template.
3. Wait up to one week for the maintainer to respond, although a response within
   a couple of business days is common. If you haven't received a response after
   one week, feel free to post a comment to check status. Thank you for your
   help and patience!

## How to Report a Bug

Follow the steps in [How to Submit an Issue](#how-to-submit-an-issue), using
the Bug Report template if the bug hasn't yet been reported.

## How to Request an Enhancement or Feature

Follow the steps in [How to Submit an Issue](#how-to-submit-an-issue), using
the Enhancement/Feature Request template if something similar hasn't been
requested.

## How to Request an Improvement to Documentation

Follow the steps in [How to Submit an Issue](#how-to-submit-an-issue), using
the Documentation Request template if something similar hasn't been requested.

## How to Contribute a Change (code, docs, etc.)

1. Before you start work on a change, please ensure there is a matching
   [bug report](#how-to-report-a-bug) or an
   [enhancement/feature request](#how-to-request-an-enhancement-or-feature) for
   reference. All changes must begin with a corresponding
   [issue](https://github.com/xilk-org/xilk-xp-web-app-template/issues).

2. Post a comment to the issue.
   1. Indicate your desire to contribute a change.
   2. Describe your approach.
   3. Wait up to one week for the maintainer to respond, although a response
      within a couple of business days is common. If you haven't received a
      response after one week, feel free to post another comment to check
      status. The maintainer may approve, collaborate with you on modifications,
      or reject your approach.

3. Once an approach is agreed upon, set up your environment so you can make and
   submit your change. We use the standard GitHub pull request workflow; see
   [Contributing to projects - GitHub Docs](https://docs.github.com/en/get-started/quickstart/contributing-to-projects).
   1. [Fork](https://docs.github.com/en/get-started/quickstart/fork-a-repo#forking-a-repository)
      this repository to your GitHub account.
   2. [Clone](https://docs.github.com/en/get-started/quickstart/fork-a-repo#cloning-your-forked-repository)
      the forked repository to your local machine.
      ```sh
      git clone <your-forked-repo-url>
      ```
   3. [Sync](https://docs.github.com/en/get-started/quickstart/fork-a-repo#configuring-git-to-sync-your-fork-with-the-original-repository)
      your fork, with the original repository as the `upstream` remote.
      ```sh
      git remote add upstream https://github.com/xilk-org/xilk-xp-web-app-template.git
      ```
   4. Create and checkout a new branch so your changes are easier to manage.
      ```sh
      git checkout -b <branch-name>
      ```
   5. If your change affects the projects created from this template, see
      [Getting Started](../README.md#getting-started) for information on
      how to create and run a new project.

4. Make your change.
   * Familiarize yourself with similar
     [code](../resources/leiningen/new/xp_web_app/),
     [documentation](../doc/), and
     [commit messages](https://github.com/xilk-org/xilk-xp-web-app-template/commits/)
     in the project.
   * Write consistently with the rest of the project, as if it were all written
     by one author.
   * For code, follow the
     [The Clojure Style Guide](https://github.com/bbatsov/clojure-style-guide).
   * For commit messages, follow the
     [The seven rules of a great Git commit message](https://cbea.ms/git-commit/#seven-rules)
     and [Conventional Commits](https://www.conventionalcommits.org/).
   * Keep the scope of your change focused to only the one corresponding issue.
   * Contribute out-of-scope changes separately, starting again at
     [step 1](#how-to-contribute-a-change-code-docs-etc).
   * Add/edit tests and documentation for any code changes.

5. Push your commit(s) to your fork.
   ```sh
   git push origin <branch-name>
   ```

6. When your change is ready for review,
   [create a Pull Request](https://docs.github.com/en/get-started/quickstart/contributing-to-projects#making-a-pull-request).
   1. Include any information that will help the maintainer review your change
      more effectively.
   2. Wait up to one week for the maintainer to respond, although a response
      within a couple of business days is common. If you haven't received a
      response after one week, feel free to post a comment to check status.
   3. If changes are requested, continue pushing new commits to the same branch;
      they will automatically update the pull request.

7. Once the review and requested changes are complete, the maintainer may
   request that you `squash` some commits.
   ```sh
   git rebase -i <hash-of-first-commit-on-this-change>
   ```

8. Your maintainer will gratefully merge your change! Thank you for all your
   help!
