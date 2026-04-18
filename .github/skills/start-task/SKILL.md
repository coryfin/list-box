---
name: start-task
description: Start work on a specific Linear issue. Fetches the issue details, updates local task tracking, and provides implementation guidance.
---

# Start Task Skill

## Overview

This skill streamlines task startup by:

1. **Accepting** a specific Linear issue ID from the user
2. **Fetching** complete issue details from Linear
3. **Setting** the Linear issue status to "In Progress"
4. **Reviewing** `requirements.md` and `plan.md` for project-level specs and update as needed.
5. **Providing** implementation guidance based on issue description

## Workflow Steps

### Step 1: Accept Issue ID

User provides a Linear issue ID (e.g., `LIST-5`).

**Pseudo-code:**

```
issueId = user_input  # e.g., "LIST-5"
```

### Step 2: Fetch Full Details

Fetch complete issue details from Linear:

- Full description
- Title and issue metadata
- Acceptance criteria (if in description)
- Dependencies
- Related tasks
- Assignee and status

**Pseudo-code:**

```
issue = linear.get_issue(id: issueId)
```

### Step 3: Update tasks.md

Add/update entry in `tasks.md` for the given issue:

- Add issue ID (e.g., `[LIST-5]`) to the relevant section
- Mark checkboxes as current work
- Link to Linear issue if not already present

### Step 4: Update Linear Issue Status

Update the Linear issue status to "In Progress":

- Use Linear MCP to update the issue state
- Sets the issue workflow state to "In Progress"
- Maintains all other issue properties unchanged

**Pseudo-code:**

```
linear.save_issue(
  id: issueId,
  state: "In Progress"
)
```

### Step 5: Implementation Guidance

Extract implementation hints from issue description:

- List acceptance criteria
- Highlight dependencies or prerequisites
- Point to relevant source files in the codebase

---

## How to Use

In VS Code chat, provide a specific Linear issue ID:

```
Start work on LIST-5
```

Or:

```
/start-task LIST-6
```

---

## Integration Points

### Linear MCP

- Requires Linear workspace access
- Fetches a specific issue by ID
- Retrieves full issue details and metadata

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
User: Start work on LIST-6

Skill: Fetching LIST-6 from Linear...

Skill: Selected: LIST-6 Add drag-and-drop reordering

[Updates tasks.md locally]

[Updates Linear issue status to In Progress]

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