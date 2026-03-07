---
name: start-next-task
description: Use when you want to pick the next Linear issue to work on. Fetches unstarted todo issues from Linear, displays them, lets you select one, and auto-updates your local task tracking. Provides implementation guidance.
---

# Start Next Task Skill

## Overview

This skill streamlines your issue selection and task startup workflow by:

1. **Fetching** unstarted/todo issues from Linear
2. **Displaying** them with key details (ID, title, status)
3. **Updating** `tasks.md` with your selected issue
4. **Reviewing** `requirements.md` and `plan.md` for project-level specs
5. **Providing** implementation guidance based on issue description

## Workflow Steps

### Step 1: Fetch Unstarted Issues

Use Linear MCP to list all issues with status "Todo" filtered by the current team.

**Pseudo-code:**

```
issues = linear.list_issues(
  status: ["Todo", "In Progress"],
  orderBy: "priority",
  limit: 10
)
```

### Step 2: Present Selection

Display issues in a numbered list with:

- Issue ID (e.g., `LIST-5`)
- Title
- Description preview (first 100 chars)
- Status

**Example Output:**

```
Available issues:

1. [LIST-5] Update list item UI component
   Status: Todo
   Improve visual hierarchy and spacing

2. [LIST-6] Add drag-and-drop reordering
   Status: Todo
   Implement reordering for items within a list
```

### Step 3: User Selection

Ask user to pick an issue number (1, 2, etc.).

### Step 4: Fetch Full Details

Once selected, fetch complete issue details:

- Full description
- Acceptance criteria (if in description)
- Dependencies
- Related tasks

### Step 5: Update tasks.md

Parse the selected issue and add/update entry in `tasks.md`:

- Add issue ID (e.g., `[LIST-5]`) to the section
- Mark relevant checkboxes as current work
- Link to Linear issue if not already present

### Step 6: Implementation Guidance

Extract implementation hints from issue description:

- List acceptance criteria
- Highlight dependencies or prerequisites
- Point to relevant source files in the codebase

---

## How to Use

In VS Code chat, type:

```
/select-next-issue
```

Or ask:

```
Pick the next issue I should work on from Linear.
```

---

## Integration Points

### Linear MCP

- Requires Linear workspace access
- Fetches issues by status and team
- Retrieves full issue details

### Local Task Tracking

- Reads from `tasks.md`
- Updates section headers with issue ID
- Marks related subtasks

### Git

- Creates feature branches following pattern: `{ISSUE_ID}-{kebab-case-title}`
- Provides branch creation guidance

---

## Example Session

```
User: /select-next-issue

Skill: Fetching your unstarted Linear issues...

[Lists 5 issues]

User: I'll pick #2

Skill: Selected: LIST-6 Add drag-and-drop reordering

[Updates tasks.md locally]
[Suggests git branch: LIST-6-add-drag-and-drop]

Skill: Ready to start! Here's what needs implementing:

Acceptance Criteria:
- [ ] Drag items within a list to reorder
- [ ] Position persists to database
- [ ] Animation smooth on both iOS and Android

Prerequisites:
- ✓ SQLDelight database layer (LIST-2 complete)
- ✓ Jetpack Compose layout basics

Next: Create a git branch and start coding.
```

---

## Assets & Tools

### Linear MCP Integration

This skill uses the Linear MCP server to:

- Query issues by status
- Retrieve issue details
- Access issue descriptions and metadata

### No External Scripts Required

All logic is handled by the AI agent with Linear MCP integration.