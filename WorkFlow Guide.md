# ENG1 Workflow Guide

This guide outlines the standard workflow for contributing to the GitHub repository.
***

### 1. 🔄 Syncing with the Main Branch

Before you start working, always ensure your local `main` branch is up-to-date with the remote `main` branch to ensure you're working with the latest code.

```bash
# 1. Check out the main branch
git checkout main

# 2. Pull the latest changes from the remote repository
git pull origin main
```

### 2. 🌳 Creating a New Branch

Create a new branch for every task you work on (features, bug fixes, documentation updates). Never work directly on the main branch.

**Branch Naming Conventions:**
Use a prefix to describe the type of work being done, followed by a short, descriptive name.

- feat/: For new features.

  - Example: git checkout -b feat/player-movement

- fix/: For bug fixes.

  - Example: git checkout -b fix/collision-bug

- docs/: For documentation updates.

  - Example: git checkout -b docs/add-contributing-guide

- refactor/: For code refactoring without changing functionality.

  - Example: git checkout -b refactor/optimize-rendering


### 3. ✍️ Writing Code and Staging Changes
Once you're on your new branch, you can write your code. As you make changes, use git status to see which files you've modified.

```bash
# View the status of your working directory
git status
```

When you're ready to save a set of changes, stage the files. This adds them to the next commit.

```bash
# Stage all changes
git add .

# Or, stage specific files
git add path/to/your/file.js
```

### 4. 💾 Committing Your Changes
After staging, commit your changes with a clear and concise message that starts with the branch prefix, is under 50 chars, and uses imperative ('add' not 'added').

```bash
# Example commit message
git commit -m "feat: add player health bar"
```

### 5. 🚀 Pushing to the Remote Repository
Pushing your code to the remote repository ensures it is stored not just on your local device but also on GitHub. This also allows others to see your updated code.

```bash
# Push your branch to the remote repository
git push origin <branch-name>
```

### 6. 🤝 Creating a Pull Request (PR)
When your work is complete, create a Pull Request on GitHub. This is how you propose merging your changes into the **main** branch.

- Go to the repository page on GitHub.

- Click the "Compare & pull request" button.

- Write a clear description of your changes, including what problem you solved or what feature you added.

- Request reviews from other team members.
