# ListBox Implementation Tasks

This document breaks down the ListBox app into small, executable tasks. Each task should be completed before moving to the next. Tasks are grouped by feature area for logical flow.

---

## Phase 1: Project Setup & Core Infrastructure

### 1.1 Initialize Compose Multiplatform Project Structure [LIST-1]

- [x] Create base Gradle multiplatform project with Compose CMP dependencies
- [x] Set up iOS and Android target configurations
- [x] Configure Material 3 theming for both platforms
- [x] Test basic "Hello World" app on both platforms

### 1.2 Set Up SQLDelight Database Layer [LIST-2]

- [x] Create SQLDelight schema file with `listEntity` table
- [x] Create SQLDelight schema file with `itemEntity` table
- [x] Create necessary database indexes (list creation date, item list+order)
- [x] Generate SQLDelight database code
- [x] Test database initialization on both platforms

### 1.3 Set Up Dependency Injection (kotlin-inject) [LIST-3]

- [ ] Create `DataComponent` with `@Component` and `@Singleton` annotations
- [ ] Implement `provideDatabase()` provider for `ListBoxDatabase`
- [ ] Create `ListBoxRepository` abstract class and register in DI
- [ ] Test DI initialization and instance access

### 1.4 Set Up Navigation (Navigation 3) [LIST-4]

- [ ] Configure Navigation 3 for multiplatform
- [ ] Define navigation routes: Home, ListDetail, ItemDetail
- [ ] Create NavHost composable structure
- [ ] Test basic navigation between screens

---

## Phase 2: Home Screen (List Index)

### 2.1 Implement Database Queries for Lists (SQLDelight) [LIST-5]

- [ ] Write query to insert/update/delete `listEntity` records
- [ ] Write query to fetch all lists sorted by `createdAt DESC`
- [ ] Test queries with sample data

### 2.2 Implement ListBoxRepository - List Operations [LIST-6]

- [ ] Create `ListEntity` data class
- [ ] Implement `getAllLists(): Flow<List<ListEntity>>`
- [ ] Implement `createList(title: String): ListEntity`
- [ ] Implement `updateListTitle(listId: String, newTitle: String)`
- [ ] Implement `deleteList(listId: String)`
- [ ] Unit test list repository methods

### 2.3 Build Home Screen Basic Structure [LIST-7]

- [ ] Create `HomeScreen` composable
- [ ] Set up basic Material 3 top app bar with "Lists" title
- [ ] Create empty state layout (centered icon + text)
- [ ] Create list item composable for displaying a list
- [ ] Connect to `getAllLists()` repository flow and display in LazyColumn

### 2.4 Implement Empty State with Template Buttons [LIST-8]

- [ ] Add "Blank List" button to empty state
- [ ] Add "Gift Ideas" template button to empty state
- [ ] Add "Recipe Box" template button to empty state
- [ ] Add "Goal Tracker" template button to empty state
- [ ] Implement template data (pre-filled title + description labels)
- [ ] Test empty state renders correctly

### 2.5 Implement "Blank List" Creation [LIST-17]

- [ ] Create `CreateListDialog` composable with title input field
- [ ] Implement validation: required, max 100 chars
- [ ] Call repository `createList()` on confirm
- [ ] Navigate to `ListDetailScreen` after creation
- [ ] Test dialog dismissal (cancel behavior)

### 2.6 Implement List Item Tap Navigation [LIST-9]

- [ ] Add click handler to list items
- [ ] Navigate to `ListDetailScreen` with list ID parameter
- [ ] Test navigation works on both platforms

### 2.7 Implement Populated State (Hide Templates) [LIST-10]

- [ ] Show FAB when list count > 0
- [ ] Hide template buttons when list count > 0
- [ ] Show template buttons again when all lists are deleted
- [ ] Test state transitions

### 2.8 Implement FAB for "Add List" [LIST-11]

- [ ] Add floating action button to bottom-right of screen
- [ ] Trigger `CreateListDialog` on FAB tap
- [ ] Test FAB appears/disappears based on list count

---

## Phase 3: List Detail Screen - Basic Setup

### 3.1 Implement Database Queries for Items (SQLDelight) [LIST-11a]

- [ ] Write query to insert/update/delete `itemEntity` records
- [ ] Write query to fetch all items for a list sorted by `orderIndex`
- [ ] Test queries with sample data

### 3.2 Implement ListBoxRepository - Item Operations [LIST-12]

- [ ] Create `ItemEntity` data class
- [ ] Implement `getItemsForList(listId: String): Flow<List<ItemEntity>>`
- [ ] Implement `createItem(listId: String, title: String, description: String): ItemEntity`
- [ ] Implement `updateItem(itemId: String, title: String, description: String)`
- [ ] Implement `deleteItem(itemId: String)`
- [ ] Unit test item repository methods

### 3.3 Build List Detail Screen Basic Structure [LIST-14]

- [ ] Create `ListDetailScreen` composable
- [ ] Implement Material 3 Medium Top App Bar
- [ ] Display list title in expanded state
- [ ] Fetch and display list items in a LazyColumn
- [ ] Create item composable for list items

### 3.4 Implement Overflow Menu & Delete List [LIST-13]

- [ ] Add overflow menu icon (three-dot) to top app bar
- [ ] Create dropdown menu with "Delete" option
- [ ] Show confirmation dialog on "Delete" tap
- [ ] Call repository `deleteList()` on confirmation
- [ ] Navigate back to Home screen after deletion
- [ ] Test cancel dismisses dialog

### 3.5 Implement Empty State (No Items) [LIST-15]

- [ ] Create centered empty state view with icon + text
- [ ] Display: "Your list is empty." + "Add your first item to get started."
- [ ] Hide when items are added
- [ ] Test displays when list has 0 items

### 3.6 Implement Basic Item Tap Navigation [LIST-16]

- [ ] Add click handler to item tiles
- [ ] Navigate to `ItemDetailScreen` with item ID parameter
- [ ] Test navigation works

---

## Phase 4: List Detail Screen - Top App Bar Edit Mode

### 4.1 Implement Tap-to-Edit Title (Expanded State Only) [LIST-21]

- [ ] Track edit mode state in ViewModel
- [ ] Swap Text component for TextField on edit activation
- [ ] Disable editing when top app bar is collapsed
- [ ] Ensure zero layout shift during swap
- [ ] Test title field activates in expanded state

### 4.2 Implement Scroll Collapse/Expand Behavior [LIST-22]

- [ ] Add scroll behavior listener to LazyColumn
- [ ] Collapse top app bar on scroll down
- [ ] Expand top app bar on scroll up
- [ ] Test collapse/expand works smoothly

### 4.3 Implement Save Action in Edit Mode [LIST-23]

- [ ] Show "Save" button in trailing action position during edit
- [ ] Hide overflow menu during edit
- [ ] Call repository `updateListTitle()` on Save tap
- [ ] Exit edit mode and persist changes
- [ ] Test Save button only shows in edit mode

### 4.4 Implement Unsaved Changes Confirmation [LIST-25]

- [ ] Track draft state in ViewModel (separate from repository)
- [ ] Show confirmation dialog on back press if title is edited
- [ ] Show confirmation dialog on navigation if title is edited
- [ ] Discard draft if user chooses "Discard"
- [ ] Test confirmation appears/disappears correctly

### 4.5 Implement Validation for Title Edit [LIST-20]

- [ ] Enforce max 100 character limit
- [ ] Show character counter during edit
- [ ] Prevent Save if title is empty
- [ ] Test validation feedback

---

## Phase 5: Item Detail Screen

### 5.1 Build Item Detail Screen Basic Structure [LIST-24]

- [ ] Create `ItemDetailScreen` composable
- [ ] Implement Material 3 Medium Top App Bar with item title
- [ ] Display item description in body
- [ ] Fetch item from repository using item ID parameter

### 5.2 Implement Item Title Edit Mode (Top App Bar) [LIST-26]

- [ ] Track edit mode state in ViewModel
- [ ] Swap Text for TextField in expanded state only
- [ ] Implement scroll collapse/expand behavior
- [ ] Show Save button during edit (trailing action)
- [ ] Call repository `updateItem()` on Save
- [ ] Test edit mode works (expanded state only)

### 5.3 Implement Item Description Edit Mode (Body) [LIST-27]

- [ ] Swap Text for TextField in body on tap
- [ ] Track description draft state in ViewModel
- [ ] Allow editing anytime (no collapse restriction)
- [ ] Save description on focus loss or manual save
- [ ] Test description field expands/contracts with content

### 5.4 Implement Overflow Menu & Delete Item [LIST-34]

- [ ] Add overflow menu to top app bar
- [ ] Show "Delete" option in dropdown
- [ ] Show confirmation dialog on Delete tap
- [ ] Call repository `deleteItem()` on confirmation
- [ ] Navigate back to ListDetailScreen after deletion

### 5.5 Implement Unsaved Changes Confirmation (Both Fields) [LIST-36]

- [ ] Track draft state for title and description separately
- [ ] Show confirmation on back press if either field is edited
- [ ] Show confirmation on navigation if either field is edited
- [ ] Test confirmation logic

### 5.6 Implement Validation [LIST-37]

- [ ] Enforce max 100 chars for title
- [ ] Enforce max 5000 chars for description
- [ ] Show character counters
- [ ] Prevent Save if title is empty
- [ ] Test validation feedback

---

## Phase 6: List Detail Screen - Add Item (FAB & Bottom Sheet)

### 6.1 Implement Add Item FAB [LIST-27a]

- [ ] Add floating action button to List Detail screen
- [ ] Position in bottom-right corner
- [ ] Open `AddItemBottomSheet` on tap
- [ ] Test FAB appears on all states

### 6.2 Build Add Item Bottom Sheet Structure [LIST-28]

- [ ] Create `AddItemBottomSheet` composable
- [ ] Implement ModalBottomSheetLayout
- [ ] Create title input field (required, max 100 chars)
- [ ] Create description input field (optional, max 5000 chars)
- [ ] Position Save button in bottom-right corner

### 6.3 Implement Dynamic Description Expansion [LIST-29]

- [ ] Use `onTextLayoutResult` to measure description field height
- [ ] Expand bottom sheet as description content grows
- [ ] Ensure smooth, natural expansion behavior
- [ ] Test with multi-line text

### 6.4 Implement Validation in Bottom Sheet [LIST-29a]

- [ ] Show "Title is required" error if title is empty
- [ ] Enforce max 100 chars for title with counter
- [ ] Enforce max 5000 chars for description with counter
- [ ] Disable Save button if title is empty
- [ ] Show validation errors as Material error messages

### 6.5 Implement Keyboard Navigation [LIST-29b]

- [ ] Pressing Enter in title field moves focus to description
- [ ] Pressing Enter in description creates line breaks
- [ ] Test keyboard navigation on both platforms

### 6.6 Implement Bottom Sheet Save & Dismissal [LIST-30]

- [ ] Call repository `createItem()` on Save button tap
- [ ] Clear form and close bottom sheet on success
- [ ] Dismiss bottom sheet on back gesture
- [ ] Show confirmation dialog if user dismisses with unsaved text
- [ ] Test Save and Cancel flows

### 6.7 Implement Unsaved Changes in Bottom Sheet [LIST-31]

- [ ] Track `isDirty` state for title and description
- [ ] Show confirmation dialog on dismiss if text was entered
- [ ] Allow user to "Save" or "Discard" draft
- [ ] Test confirmation appears only when needed

---

## Phase 7: List Detail Screen - Multi-Select & Deletion

### 7.1 Implement Long-Press Multi-Select Entry [LIST-39]

- [ ] Add `onLongClick` handler to item tiles
- [ ] Enter multi-select state with first item selected
- [ ] Track selected items in a `Set<String>` in ViewModel
- [ ] Test long-press activates multi-select

### 7.2 Implement Multi-Select Tap to Toggle [LIST-40]

- [ ] While in multi-select mode, tapping items toggles selection
- [ ] Update `Set<String>` in ViewModel
- [ ] Visual feedback (highlight/checkbox) for selected items
- [ ] Test toggling selection works

### 7.3 Implement Multi-Select Top App Bar [LIST-41]

- [ ] Replace standard top app bar with multi-select variant in edit mode
- [ ] Show close icon (X) to exit multi-select
- [ ] Display count of selected items: "N items selected"
- [ ] Add delete icon to trailing actions
- [ ] Test app bar switches on multi-select entry/exit

### 7.4 Implement Multi-Select Delete [LIST-42]

- [ ] Show confirmation dialog when delete icon is tapped
- [ ] Display: "Delete N items?"
- [ ] Call repository `deleteItem()` for each selected item
- [ ] Exit multi-select mode after deletion
- [ ] Update UI (remove deleted items from list)
- [ ] Test deletion works for multiple items

### 7.5 Implement Multi-Select Exit [LIST-43]

- [ ] Tapping close icon (X) exits multi-select mode
- [ ] Clear selected items set
- [ ] Restore standard top app bar
- [ ] Test clean exit behavior

---

## Phase 8: List Detail Screen - Drag-and-Drop Reordering

### 8.1 Implement Reorder Database Query (SQLDelight) [LIST-44]

- [ ] Write query to update item `orderIndex` value
- [ ] Test update query with sample data

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