# Technical Design Document: ListBox

## 1. System Overview

**ListBox** is a mobile-first list manager built with **Compose Multiplatform (CMP)**. See [requirements.md](file:///Users/cory/Projects/list-box/requirements.md) for product details.

## 2. Technology Stack


| Layer                    | Technology                  | Rationale                                        |
| ------------------------ | --------------------------- | ------------------------------------------------ |
| **UI Framework**         | Compose Multiplatform (CMP) | Shared Material 3 UI for Android and iOS.        |
| **Navigation**           | Navigation 3                | Official state-driven cross-platform navigation. |
| **Dependency Injection** | kotlin-inject               | Compile-time safe DI for KMP.                    |
| **Database**             | SQLDelight                  | Type-safe SQLite persistence.                    |
| **Logic/State**          | ViewModel + StateFlow       | Standard UDF with multiplatform ViewModels.      |


---

## 3. Data Model (SQLDelight)

Uses **UUID v4** for primary keys and **Fractional Indexing** for item reordering.

### 3.1 Table: `listEntity`


| Field         | Type      | Constraint    | Description                                 |
| ------------- | --------- | ------------- | ------------------------------------------- |
| **id**        | `TEXT`    | `PRIMARY KEY` | Client-generated UUID v4.                   |
| **title**     | `TEXT`    | `NOT NULL`    | Max 100 chars.                              |
| **createdAt** | `INTEGER` | `NOT NULL`    | Epoch milliseconds.                         |
| **updatedAt** | `INTEGER` | `NOT NULL`    | Epoch milliseconds for eventual sync logic. |


### 3.2 Table: `itemEntity`


| Field           | Type      | Constraint    | Description                       |
| --------------- | --------- | ------------- | --------------------------------- |
| **id**          | `TEXT`    | `PRIMARY KEY` | Client-generated UUID v4.         |
| **listId**      | `TEXT`    | `NOT NULL`    | Foreign Key to `listEntity(id)`.  |
| **title**       | `TEXT`    | `NOT NULL`    | Primary text (Max 100 chars).     |
| **description** | `TEXT`    | `NULLABLE`    | Detailed notes (Max 5,000 chars). |
| **orderIndex**  | `INTEGER` | `NOT NULL`    | Sequential index. All items are updated on reorder. |
| **updatedAt**   | `INTEGER` | `NOT NULL`    | Last edit timestamp.              |


### 3.3 Database Optimization

```sql
CREATE INDEX idx_item_list_id ON itemEntity(listId);
CREATE INDEX idx_item_list_order ON itemEntity(listId, orderIndex);
CREATE INDEX idx_list_created_at ON listEntity(createdAt);
```

---

## 4. Technical Design & UI Patterns

### 4.1 Fractional Indexing Logic

`orderIndex` uses `INTEGER` (sequential) values. All items in the list are updated whenever a reorder occurs. **Drag-and-drop actions trigger an immediate write to the repository** to ensure state durability during movement. Reordering is not managed by the Draft State.

### 4.2 Selection & Drag State Machine

The List Detail screen manages four primary UI states (for functional requirements, see [Section 3.2 of requirements.md](./requirements.md#L55)):

1. **Idle:** Default state. Fields are implemented as a **Text** component matched exactly to **TextField** styling in read-only mode to ensure consistent aesthetics and layout.
2. **Edit Mode:** Swaps **Text** for and active **TextField** with identical dimensions to ensure zero layout shift. The ViewModel holds a **Draft State** separate from the repository until changes are committed. Tapping **Save** persists the edits from the Draft State to the database.
3. **Multi-Select:** Triggered by `onLongClick`. Managed via a `Set<UUID>` of seleted item IDs in the ViewModel.
4. **Reorder Mode:** Active for single-item movement using fractional indexing.

### 4.3 App Bar & Menu Actions

- **List Detail Overflow:** The Top App Bar contains an overflow menu (three-dot icon) with two actions: **Rename** and **Delete**. **Rename** opens a dialog pre-filled with the current title; confirming persists the change via `updateListTitle()`. **Delete** deletes the entire list and navigates to Home. The Delete action requires a confirmation dialog.
- **Item Detail:** In read-only state, the Top App Bar contains an overflow menu with a **Delete** action. Tapping **Delete** deletes the individual item and navigates back to List Detail. Requires a confirmation dialog. In Edit Mode, the overflow menu is replaced by a **Save** button trailing action.
### 4.4 Navigation & Screens

- **Home Screen States:**
  - **Empty vs Populated:** Managed via `Flow<List<ListEntity>>`. Template buttons are injected into the UI list when the database is empty.
- **"Add Item" View:** Implemented as a `ModalBottomSheetLayout`. The text field uses `onTextLayoutResult` to calculate dynamic height for expansion.
  - **Dismissal Handling:** Intercepting the dismissal signal tracks `isDirty` in the ViewModel to present a confirmation dialog.
- **"Item Detail" View:** Implemented as a **Full Screen** component. Uses **component swapping** (read-only `Text` → active `TextField` with identical layout dimensions) to achieve zero layout shift when Edit Mode is activated. Edit Mode is triggered by a **FAB** in the bottom-right corner rather than tap-to-edit on individual fields — the FAB activates both the title (in the top app bar) and the description (in the body) simultaneously. The FAB is hidden while in Edit Mode. The ViewModel holds a **Draft State** separate from the repository until the user taps **Save**.

### 4.5 Reactive Repository

The **Repository Layer** provides `Flow<List<Item>>` and `Flow<List<ListEntity>>` to the UI. Because SQLDelight is a reactive driver, any change to the database (adding an item via the bottom sheet or reordering via drag-and-drop) will automatically trigger a recomposition of the UI.

---

## 5. Technical Design Decisions

### 5.1 Local-First vs. Cloud-Ready

- **UUIDs:** Used to prevent ID collisions during future synchronization.
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