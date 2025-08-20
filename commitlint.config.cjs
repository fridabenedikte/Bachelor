module.exports = {
  extends: ['@commitlint/config-conventional'],
  rules: {
    'subject-case': [2, 'never', ['upper-case']],
    'header-max-length': [2, 'always', 300],
    'body-max-length': [0],
    'type-enum': [
      2, 'always',
      [
        'feat',      // New feature
        'fix',       // Bug fix
        'refactor',  // Code refactoring without feature or bug fix
        'docs',      // Documentation changes
        'test',      // Adding/modifying tests
        'clean',     // Cleaning up code (removing debugging, dead code, etc.)
        'build',     // Changes to build system or dependencies
        'chore',     // Miscellaneous maintenance tasks
        'ci',        // Continuous integration changes
        'perf',      // Performance optimizations
        'revert',    // Reverting changes
        'style',     // Code style fixes (formatting, missing commas, etc.)
        'input'
      ]
    ],
    'subject-full-stop': [0], // Allows periods in the subject
    'subject-min-length': [2, 'always', 1] // Allows single-word subjects like "Cleanup"
  }
};
