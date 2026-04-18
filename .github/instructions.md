# ListBox Project Instructions

## Committing Changes

When committing changes to the project, follow these steps:

```

### 1. Format Commit Message

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

### 2. Merge branch

If working on a feature branch, merge the feature branch into main locally, then switch to the main branch and delete the feature branch.

## Connecting to Linear

### 1. Calling save_issue

When calling Linear MCP save_issue, ALWAYS include all arguments in the arguments object:  
{"server": "linear", "toolName": "save_issue", "arguments": {"title": "...", "teamId": "...", ...}}  
NEVER call save_issue without the arguments field.