# Technical Design Document: ListBox

## 1. System Overview

**ListBox** is a mobile-first list manager built with **Compose Multiplatform (CMP)**. It utilizes a vertical, item-based architecture to replace traditional horizontal spreadsheets, optimized for one-handed use, high-performance reordering, and bulk management.

## 2. Technology Stack


| Layer                    | Technology                  | Rationale                                                                                              |
| ------------------------ | --------------------------- | ------------------------------------------------------------------------------------------------------ |
| **UI Framework**         | Compose Multiplatform (CMP) | Shared Material 3 UI for Android and iOS.                                                              |
| **Navigation**           | Navigation 3                | Official state-driven navigation for cross-platform apps.                                              |
| **Dependency Injection** | kotlin-inject               | Compile-time safe DI for Kotlin Multiplatform.                                                         |
| **Database**             | SQLDelight                  | Type-safe SQLite persistence with native drivers.                                                      |
| **Logic/State**          | ViewModel + StateFlow       | Standard UDF with multiplatform-capable ViewModels.Standard UDF with multiplatform-capable ViewModels. |


---

## 3. Data Model (SQLDelight)

The schema is "Sync-Ready," using **UUID v4** for primary keys and **Fractional Indexing** for item reordering.

### 3.1 Table: `listEntity`


| Field         | Type      | Constraint    | Description                        |
| ------------- | --------- | ------------- | ---------------------------------- |
| **id**        | `TEXT`    | `PRIMARY KEY` | Client-generated UUID v4.          |
| **title**     | `TEXT`    | `NOT NULL`    | The name of the list.              |
| **createdAt** | `INTEGER` | `NOT NULL`    | Epoch milliseconds.                |
| **updatedAt** | `INTEGER` | `NOT NULL`    | Epoch milliseconds for sync logic. |
| **isDeleted** | `INTEGER` | `DEFAULT 0`   | Soft-delete flag (0 or 1).         |


### 3.2 Table: `itemEntity`


| Field           | Type      | Constraint    | Description                           |
| --------------- | --------- | ------------- | ------------------------------------- |
| **id**          | `TEXT`    | `PRIMARY KEY` | Client-generated UUID v4.             |
| **listId**      | `TEXT`    | `NOT NULL`    | Foreign Key to `listEntity(id)`.      |
| **title**       | `TEXT`    | `NOT NULL`    | Primary text shown in the list index. |
| **description** | `TEXT`    | `NULLABLE`    | Detailed notes.                       |
| **orderIndex**  | `REAL`    | `NOT NULL`    | **Fractional Indexing** value.        |
| **updatedAt**   | `INTEGER` | `NOT NULL`    | Last edit timestamp.                  |
| **isDeleted**   | `INTEGER` | `DEFAULT 0`   | Soft-delete flag (0 or 1).            |


### 3.3 Database Optimization

An index is applied to the foreign key to ensure near-instant retrieval of items belonging to a specific list. Primary keys are indexed by default by the SQLite engine.

```sql
CREATE INDEX idx_item_list_id ON itemEntity(listId);
```

---

## 4. Technical Design & UI Patterns

### 4.1 Fractional Indexing Logic

To support manual reordering without mass-updating rows, `orderIndex` uses `REAL` (floating point) values. Moving an item between two others assigns it the midpoint value.

### 4.2 Selection & Drag State Machine

The List Detail screen manages three primary UI states:

1. **Idle:** Default viewing state.
2. **Multi-Select:** Triggered by `onLongClick`. Tracks a `Set<UUID>` of selected IDs.
  - *Transition:* If a drag gesture starts while `selectedIds.size == 1`, switch to **Reorder Mode.**
3. **Reorder Mode:** Drag-and-drop active using the fractional indexing logic.

### 4.3 Navigation & Modals

- **"Add Item" View:** Implemented as a **Bottom Half-Sheet Modal**. The description text field is configured for dynamic height, causing the modal to expand vertically as content is added.
- **"Item Detail" View:** Implemented as a **Full-Screen Modal** for focused reading and long-form editing.

### 4.4 Reactive Repository

The **Repository Layer** provides `Flow<List<Item>>` to the UI. Because SQLDelight is a reactive driver, any change to the database (adding an item via the bottom sheet or reordering via drag-and-drop) will automatically trigger a recomposition of the UI.

---

## 5. Technical Design Decisions

### 5.1 Local-First vs. Cloud-Ready

- **UUIDs:** Used to prevent ID collisions during future synchronization.
- **Soft Deletes:** Both `listEntity` and `itemEntity` use `isDeleted` to allow future sync engines to resolve deletions across clients.
- **Timestamps:** Rows track `updatedAt` to enable incremental "delta" syncing.

### 5.2 Dependency Injection

`kotlin-inject` provides singleton instances of the `Repository` and `Database` via an `@Component` structure.

```kotlin
@Component
@Singleton
abstract class DataComponent {
    abstract val repository: ListBoxRepository
    
    @Provides
    @Singleton
    fun provideDatabase(driver: SqlDriver): ListBoxDatabase = ListBoxDatabase(driver)
}
```