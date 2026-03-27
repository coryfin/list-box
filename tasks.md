# ListBox Implementation Tasks

This document breaks down the ListBox app into small, executable tasks. Each task should be completed before moving to the next. Tasks are grouped by feature area for logical flow.

---

## Phase 1: Project Setup & Core Infrastructure

### 1.1 Initialize Compose Multiplatform Project Structure [LIST-1] ✅ COMPLETED

- [x] Create base Gradle multiplatform project with Compose CMP dependencies
- [x] Set up iOS and Android target configurations
- [x] Configure Material 3 theming for both platforms
- [x] Test basic "Hello World" app on both platforms

### 1.2 Set Up SQLDelight Database Layer [LIST-2] ✅ COMPLETED

- [x] Create SQLDelight schema file with `listEntity` table
- [x] Create SQLDelight schema file with `itemEntity` table
- [x] Create necessary database indexes (list creation date, item list+order)
- [x] Generate SQLDelight database code
- [x] Test database initialization on both platforms

### 1.3 Set Up Dependency Injection (kotlin-inject) [LIST-3] ✅ COMPLETED

- [x] Create `DataComponent` with `@Component` and `@Singleton` annotations
- [x] Implement `provideDatabase()` provider for `ListBoxDatabase`
- [x] Create `ListBoxRepository` abstract class and register in DI
- [x] Test DI initialization and instance access

### 1.4 Set Up Navigation (Navigation 3) [LIST-4] ✅ COMPLETED

- [x] Configure Navigation 3 for multiplatform
- [x] Define navigation routes: Home, ListDetail, ItemDetail
- [x] Create NavHost composable structure
- [x] Test basic navigation between screens

---

## Phase 2: Home Screen (List Index)

### 2.1 Implement Database Queries for Lists (SQLDelight) [LIST-5] ✅ COMPLETED

- [x] Write query to insert/update/delete `listEntity` records
- [x] Write query to fetch all lists sorted by `createdAt DESC`
- [x] Test queries with sample data (integration through Repository)

### 2.2 Implement ListBoxRepository - List Operations [LIST-6] ✅ COMPLETED

- [x] Create `ListEntity` data class (SQLDelight generated)
- [x] Implement `getAllLists(): Flow<List<ListEntity>>`
- [x] Implement `createList(title: String): ListEntity`
- [x] Implement `updateListTitle(listId: String, newTitle: String)`
- [x] Implement `deleteList(listId: String)`
- [x] Use SQLDelight coroutines-extensions library with `.asFlow()` and `.map()` transformations

### 2.3 Build Home Screen Basic Structure [LIST-7] ✅ COMPLETED

- [x] Create `HomeScreen` composable
- [x] Set up basic Material 3 top app bar with "Lists" title
- [x] Create empty state layout (centered icon + text)
- [x] Create list item composable for displaying a list
- [x] Connect to `getAllLists()` repository flow and display in LazyColumn

### 2.4 Implement Empty State with Template Buttons [LIST-8] ✅ COMPLETED

- [x] Add "Blank List" button to empty state  
- [x] Add "Gift Ideas" template button to empty state  
- [x] Add "Recipe Box" template button to empty state  
- [x] Add "Goal Tracker" template button to empty state  
- [x] Implement template data (pre-filled title + description labels)  
- [x] Test empty state renders correctly

### 2.5 Implement "Blank List" Creation [LIST-17] ✅ COMPLETED

- [x] Create `CreateListDialog` composable with title input field
- [x] Implement validation: required, max 100 chars
- [x] Call repository `createList()` on confirm
- [x] Navigate to `ListDetailScreen` after creation
- [x] Test dialog dismissal (cancel behavior)

### 2.6 Implement List Item Tap Navigation [LIST-11] ✅ COMPLETED

- [x] Add click handler to list items
- [x] Navigate to `ListDetailScreen` with list ID parameter
- [x] Test navigation works on both platforms

### 2.7 Implement Populated State (Hide Templates) [LIST-14] ✅ COMPLETED

- [x] Show FAB when list count > 0
- [x] Hide template buttons when list count > 0
- [x] Show template buttons again when all lists are deleted
- [x] Test state transitions

### 2.8 Implement FAB for "Add List" [LIST-10] ✅ COMPLETED

- [x] Add floating action button to bottom-right of screen
- [x] Trigger `CreateListDialog` on FAB tap
- [x] Test FAB appears/disappears based on list count

---

## Phase 3: List Detail Screen - Basic Setup

### 3.1 Implement Database Queries for Items (SQLDelight) [LIST-11a] ✅ COMPLETED

- [x] Write query to insert/update/delete `itemEntity` records
- [x] Write query to fetch all items for a list sorted by `orderIndex`
- [x] Test queries with sample data

### 3.2 Implement ListBoxRepository - Item Operations [LIST-12]

- [x] Create `ItemEntity` data class
- [x] Implement `getItemsForList(listId: String): Flow<List<ItemEntity>>`
- [x] Implement `createItem(listId: String, title: String, description: String): ItemEntity`
- [x] Implement `updateItem(itemId: String, title: String, description: String)`
- [x] Implement `deleteItem(itemId: String)`
- [ ] Unit test item repository methods

### 3.3 Build List Detail Screen Basic Structure [LIST-14] ✅ COMPLETED

- [x] Create `ListDetailScreen` composable
- [x] Implement Material 3 Medium Top App Bar
- [x] Display list title in expanded state
- [x] Fetch and display list items in a LazyColumn
- [x] Create item composable for list items

### 3.4 Implement Overflow Menu & Delete List [LIST-13] ✅ COMPLETED

- [x] Add overflow menu icon (three-dot) to top app bar
- [x] Create dropdown menu with "Delete" option
- [x] Show confirmation dialog on "Delete" tap
- [x] Call repository `deleteList()` on confirmation
- [x] Navigate back to Home screen after deletion
- [x] Test cancel dismisses dialog

### 3.5 Implement Empty State (No Items) [LIST-15] ✅ COMPLETED

- [x] Create centered empty state view with icon + text
- [x] Display: "Your list is empty." + "Add your first item to get started."
- [x] Hide when items are added
- [x] Test displays when list has 0 items

### 3.6 Implement Basic Item Tap Navigation [LIST-16] ✅ COMPLETED

- [x] Add click handler to item tiles
- [x] Navigate to `ItemDetailScreen` with item ID parameter
- [x] Test navigation works

---

## Phase 4: List Detail Screen - Rename & Scroll

### 4.1 Implement Rename List Dialog [LIST-22] ✅ COMPLETED

- [x] Add "Rename" menu item to the overflow menu (above "Delete")
- [x] Create `RenameListDialog` composable with a pre-filled `TextField`
- [x] Disable Save button when title field is empty
- [x] Enforce max 100 character limit with character counter
- [x] Call repository `updateListTitle()` on Save confirmation
- [x] Test dialog pre-fills with current title
- [x] Test Cancel / tap-outside dismisses without saving

### 4.2 Implement Scroll Collapse/Expand Behavior [LIST-19] ✅ COMPLETED

- [x] Collapse top app bar on scroll down (using Material API)
- [x] Expand top app bar on scroll up (using Material API)
- [x] Test collapse/expand works smoothly on scroll

### 4.5 Implement Validation for Title Edit [LIST-20] ✅ COMPLETED

- [x] Enforce max 100 character limit
- [x] Show character counter during edit
- [x] Prevent Save if title is empty
- [x] Test validation feedback

---

## Phase 5: Item Detail Screen

### 5.1 Build Item Detail Screen Basic Structure [LIST-24] ✅ COMPLETED

- [x] Create `ItemDetailScreen` composable
- [x] Implement Material 3 Medium Top App Bar with item title
- [x] Display item description in body
- [x] Fetch item from repository using item ID parameter

### 5.2 Implement Edit Mode via FAB [LIST-26] ✅ COMPLETED

- [x] Add FAB to bottom-right corner of Item Detail screen (read-only state)
- [x] Track edit mode state in ViewModel
- [x] Tapping FAB activates Edit Mode: swap both title Text → TextField and description Text → TextField simultaneously
- [x] Hide FAB while in Edit Mode
- [x] Implement component swapping with matching layout dimensions to ensure zero layout shift
- [x] Show Save button as trailing action in top app bar during Edit Mode (replaces overflow menu)
- [x] Show Close icon button as navigation icon in top app bar during Edit Mode (replaces Back button)
- [x] Tapping Close discards edits and returns to read-only state (no confirmation needed)
- [x] Call repository `updateItem()` on Save
- [x] Return to read-only state (restore FAB, restore overflow menu, restore Back button) after Save
- [x] Test FAB activates both fields at once
- [x] Test zero layout shift on transition

### 5.3 Implement Overflow Menu & Delete Item [LIST-27] ✅ COMPLETED

- [x] Add overflow menu to top app bar
- [x] Show "Delete" option in dropdown
- [x] Show confirmation dialog on Delete tap
- [x] Call repository `deleteItem()` on confirmation
- [x] Navigate back to ListDetailScreen after deletion

### 5.4 Implement Unsaved Changes Confirmation [LIST-36] ✅ COMPLETED

- [x] Track draft state for title and description separately
- [x] Show confirmation on back press if either field is edited
- [x] Test confirmation logic

### 5.5 Implement Validation

- [ ] Enforce max 100 chars for title
- [ ] Enforce max 5000 chars for description
- [ ] Show character counters
- [ ] Prevent Save if title is empty
- [ ] Test validation feedback

---

## Phase 6: List Detail Screen - Add Item (FAB & Bottom Sheet)

### 6.1 Implement Add Item FAB [LIST-33] ✅ COMPLETED

- [x] Add floating action button to List Detail screen
- [x] Position in bottom-right corner
- [x] Open `AddItemBottomSheet` on tap
- [x] Test FAB appears on all states

### 6.2 Build Add Item Bottom Sheet Structure [LIST-28] ✅ COMPLETED

- [x] Create `AddItemBottomSheet` composable
- [x] Implement ModalBottomSheetLayout
- [x] Create title input field (required, max 100 chars)
- [x] Create description input field (optional, max 5000 chars)
- [x] Position Save button in bottom-right corner

### 6.3 Implement Dynamic Description Expansion [LIST-29] ✅ COMPLETED

- [x] Use `onTextLayoutResult` to measure description field height
- [x] Expand bottom sheet as description content grows
- [x] Ensure smooth, natural expansion behavior
- [x] Test with multi-line text

### 6.4 Implement Validation in Bottom Sheet [LIST-32] ✅ COMPLETED

- [x] Show "Title is required" error if title is empty
- [x] Enforce max 100 chars for title with counter
- [x] Enforce max 5000 chars for description with counter
- [x] Disable Save button if title is empty
- [x] Show validation errors as Material error messages

### 6.5 Implement Keyboard Navigation [LIST-34] ✅ COMPLETED

- [x] Pressing Enter in title field moves focus to description
- [x] Pressing Enter in description creates line breaks
- [x] Test keyboard navigation on both platforms

### 6.6 Implement Bottom Sheet Save & Dismissal [LIST-30] ✅ COMPLETED

- [x] Call repository `createItem()` on Save button tap
- [x] Clear form and close bottom sheet on success
- [x] Dismiss bottom sheet on back gesture
- [x] Show confirmation dialog if user dismisses with unsaved text
- [x] Test Save and Cancel flows

### 6.7 Implement Unsaved Changes in Bottom Sheet [LIST-35] ✅ COMPLETED

- [x] Track `isDirty` state for title and description
- [x] Show confirmation dialog on dismiss if text was entered
- [x] Allow user to "Discard" draft or "Keep editing"
- [x] Test confirmation appears only when needed

---

## Phase 7: List Detail Screen - Multi-Select & Deletion

### 7.1 Implement Long-Press Multi-Select Entry [LIST-42] ✅ COMPLETED

- [x] Add `onLongClick` handler to item tiles
- [x] Enter multi-select state with first item selected
- [x] Track selected items in a `Set<String>` in ViewModel
- [x] Test long-press activates multi-select

### 7.2 Implement Multi-Select Tap to Toggle [LIST-38] ✅ COMPLETED

- [x] While in multi-select mode, tapping items toggles selection
- [x] Update `Set<String>` in ViewModel
- [x] Visual feedback (highlight/checkbox) for selected items
- [x] Test toggling selection works

### 7.3 Implement Multi-Select Top App Bar [LIST-44] ✅ COMPLETED

- [x] Replace standard top app bar with multi-select variant in edit mode
- [x] Show close icon (X) to exit multi-select
- [x] Display count of selected items: "N items selected"
- [x] Add delete icon to trailing actions
- [x] Test app bar switches on multi-select entry/exit

### 7.4 Implement Multi-Select Delete [LIST-40] ✅ COMPLETED

- [x] Show confirmation dialog when delete icon is tapped
- [x] Display: "Delete N items?"
- [x] Call repository `deleteItem()` for each selected item
- [x] Exit multi-select mode after deletion
- [x] Update UI (remove deleted items from list)
- [x] Test deletion works for multiple items

### 7.5 Implement Multi-Select Exit [LIST-43]

- [ ] Tapping close icon (X) exits multi-select mode
- [ ] Clear selected items set
- [ ] Restore standard top app bar
- [ ] Test clean exit behavior

---

## Phase 8: List Detail Screen - Drag-and-Drop Reordering

### 8.1 Implement Reorder Database Query (SQLDelight) [LIST-45] ✅ COMPLETED

- [x] Write query to update item `orderIndex` value
- [x] Test update query with sample data

### 8.2 Implement ListBoxRepository - Reorder Operations [LIST-45]

- [ ] Implement `reorderItem(itemId: String, newOrderIndex: Double)`
- [ ] Unit test reorder repository method

### 8.3 Implement Drag-and-Drop Entry (from Long-Press) [LIST-47]

- [ ] Detect drag gesture immediately after long-press
- [ ] Transition from multi-select to reorder drag mode
- [ ] Highlight the item being dragged
- [ ] Prepare drag drop targets
- [ ] Test drag entry behavior

### 8.4 Implement Visual Drag Feedback [LIST-48]

- [ ] Elevate dragged item (shadow/scale)
- [ ] Show drop zone indicators between items
- [ ] Highlight drop target on hover
- [ ] Test visual feedback is clear

### 8.5 Implement Fractional Index Calculation [LIST-49]

- [ ] Calculate midpoint value between two items for drop position
- [ ] Handle edge cases (drag to top, drag to bottom)
- [ ] Generate new `orderIndex` value (REAL type)
- [ ] Test calculation logic

### 8.6 Implement Immediate Persistence [LIST-50]

- [ ] Call repository `reorderItem()` on drop completion
- [ ] Persist new `orderIndex` immediately (not part of draft state)
- [ ] Update item position in list after save
- [ ] Test reordering persists across app restart

### 8.7 Implement Drag Exit [LIST-51]

- [ ] Release drag gesture exits reorder mode
- [ ] List returns to normal (Idle) state
- [ ] Items are sorted by new `orderIndex` values
- [ ] Test clean exit and proper sorting

### 8.8 Test Complex Drag Scenarios [LIST-46]

- [ ] Drag item to different positions multiple times
- [ ] Drag to top of list
- [ ] Drag to bottom of list
- [ ] Test order persists correctly

---

## Phase 9: Testing & Refinement

### 9.1 Unit Testing [LIST-55]

- [ ] Unit test Repository methods
- [ ] Unit test ViewModel state management
- [ ] Unit test fractional indexing logic
- [ ] Unit test validation logic

### 9.2 Integration Testing [LIST-58]

- [ ] Test full flow: Create list → Add items → Edit → Delete
- [ ] Test reordering with multiple operations
- [ ] Test multi-select across list changes
- [ ] Test back navigation with unsaved changes

### 9.3 UI/UX Testing [LIST-56]

- [ ] Test all screens on both iOS and Android
- [ ] Verify Material 3 styling consistency
- [ ] Test one-handed usability (FAB placement, button positions)
- [ ] Test keyboard navigation and IME behavior
- [ ] Test offline functionality (no network required)

### 9.4 Edge Cases [LIST-57]

- [ ] Create and delete lists rapidly
- [ ] Create items with max-length titles/descriptions
- [ ] Test empty list states
- [ ] Test navigation back from deep screens
- [ ] Test app backgrounding/resuming

### 9.5 Performance Optimization [LIST-59]

- [ ] Profile list rendering with 100+ items
- [ ] Optimize LazyColumn performance if needed
- [ ] Test database query performance
- [ ] Verify no memory leaks during navigation

---

## Phase 10: Polish & Finalization

### 10.1 Visual Polish [LIST-60]

- [ ] Refine Material 3 color schemes for light/dark mode
- [ ] Ensure consistent spacing and typography
- [ ] Add subtle animations for transitions
- [ ] Test visual hierarchy is clear

### 10.2 Accessibility [LIST-61]

- [ ] Add content descriptions for icons
- [ ] Ensure sufficient color contrast
- [ ] Test screen reader compatibility
- [ ] Verify keyboard navigation is complete

### 10.3 Documentation [LIST-62]

- [ ] Document Architecture decisions
- [ ] Document how to set up local development
- [ ] Document how to build and run on iOS/Android
- [ ] Create user-facing help/tutorial (optional)

### 10.4 Final Validation [LIST-63]

- [ ] Run full test suite
- [ ] Manual QA on both platforms
- [ ] Test all user flows from requirements.md
- [ ] Verify all requirements are met
- [ ] Get sign-off before release

---

## Task Execution Notes

- **Order Matters:** Complete tasks in the listed order; later phases depend on earlier ones
- **Testing:** After each phase, test the features before moving to the next
- **Git Commits:** Commit after completing each major phase (roughly every 10-15 tasks)
- **Documentation:** Keep technical notes during implementation for architecture documentation
- **Iterative:** If a task reveals issues in earlier work, backtrack and fix before continuing