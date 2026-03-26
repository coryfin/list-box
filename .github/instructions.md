# ListBox Project Instructions

## Committing Changes

When committing changes to the project, follow these steps:

### 1. Mark Task Complete in tasks.md

Find the task in [tasks.md](/Users/cory/Projects/list-box/tasks.md) that corresponds to the Linear issue you are working on (identified by the issue number like [LIST-2]). Change the checkbox from incomplete to complete:

**Before:**

```markdown
### 1.2 Set Up SQLDelight Database Layer [LIST-2]

- [ ] Create SQLDelight schema file with `listEntity` table
```

**After:**

```markdown
### 1.2 Set Up SQLDelight Database Layer [LIST-2]

- [x] Create SQLDelight schema file with `listEntity` table
```

### 2. Format Commit Message

Commit changes using the following format:

```
{LINEAR_ISSUE_NUMBER} {Short Description}

{Long Description, if necessary}

Resolves {LINEAR_ISSUE_NUMBER}
```

**Example:**

```
LIST-2 Set up SQLDelight database layer

- AddedSQLDelight 2.0.1 plugin and dependencies
- Created database schema files:
  - listEntity.sq with list table and queries
  - itemEntity.sq with item table and queries
- Added platform-specific database drivers:
  - Android: AndroidSqliteDriver
  - iOS: NativeSqliteDriver
- Implemented DatabaseProvider singleton for database access
- Integrated database initialization in MainActivity (Android) and iOSApp (iOS)
- Created database initialization tests
- All database code generation working without errors

Resolves LIST-2
```

**Format breakdown:**

- **First line:** Linear issue identifier followed by a brief description (e.g., `LIST-2 Set up SQLDelight database layer`)
- **Body (optional):** Detailed description of changes, improvements, or notes
- **Last line:** `Resolves {LINEAR_ISSUE_NUMBER}` to link the commit to the Linear issue

### 3. Merge branch

I working on a feature branch, merge the feature branch into main locally, then switch to the main branch and delete the feature branch.

## Connecting to Linear

### 1. Calling save_issue

When calling Linear MCP save_issue, ALWAYS include all arguments in the arguments object:  
{"server": "linear", "toolName": "save_issue", "arguments": {"title": "...", "teamId": "...", ...}}  
NEVER call save_issue without the arguments field.