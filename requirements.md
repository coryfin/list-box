# Product Requirements Document: ListBox

## 1. Product Overview

### The Problem

Spreadsheets are powerful for tabular data but are notoriously difficult to use on a mobile device. Note-taking apps are too unstructured for lists like "Gift Ideas," "Recipe Box," or "Personal Goals" where specific details (links, ingredients, milestones) are required. Users need a structured "spreadsheet UI alternative" designed for mobile screens.

### The Solution

A mobile-first list manager where every entry is an "item." Each item consists of a **Title** (visible in the list view) and a **Description** (visible in a full-screen detail view). This separation allows for a high-density vertical list that provides the organization of a database with the simplicity of a mobile-native app.

---

## 2. Target Audience & Top 3 Use-Cases

### 2.1 The Gift Curator (Gift Ideas & Wishlists)

- **The Item Use:** **Title:** "Leather Journal for Sarah." **Detail View:** Link to the Etsy shop, price, and a note about the color she likes.

### 2.2 The Home Chef (Personal Recipe Box)

- **The Pain:** Navigating ad-heavy recipe sites or cluttered notes when hands are messy in the kitchen.
- **The Item Use:** **Title:** "Quick 15-Min Pesto Pasta." **Detail View:** A clean, distraction-free list of ingredients and simplified cooking steps.

### 2.3 The Personal Achiever (Life Goals)

- **The Item Use:** **Title:** "Learn 10 Piano Songs." **Detail View:** A list of specific songs, the "why" behind the goal, and a record of the start date.

---

## 3. Functional Requirements

### 3.1 Home Screen (List Index)

- **Layout:** Vertical index of lists, sorted by **Creation Date (Descending)**. Reordering is not supported; deletion is handled in the **List Detail Screen**.
- **Empty State:** Displays a "Blank List" button alongside "Quick-Start" template buttons (Gift Ideas, Recipe Box, Goal Tracker). These templates serve strictly as an onboarding tool to demonstrate the app's capability and structure.
  - **Create from Template:** Populates a new list with example items where the **Title** and **Description** fields are pre-filled. The **Description** field is pre-filled with plain text labels for relevant information:
    - **Gift Ideas:** Populates the description with "Store:", "Price:", and "Recipient:" labels.
    - **Recipe Box:** Populates the description with "Ingredients:" and "Instructions:" labels.
    - **Goal Tracker:** Populates the description with "Success Criteria:" and "Target Date:" labels.
- **Populated State:** Contains a FAB that creates "Blank List" when tapped. Template buttons are hidden once the user has created at least one list. If all lists are subsequently deleted, the Home Screen returns to the **Empty State**, and the template buttons reappear to assist the user.
- **List Selection:** Tapping a list opens the **List Detail Screen**.
- **List Creation:** Upon choosing "Blank List" (from the empty state or populated state), the user is prompted for a required **List Title** (Max 100 chars, duplicates allowed). Once confirmed, the list is created and the user is navigated directly to the **List Detail Screen**.

### 3.2 List Detail Screen

- **Layout:** Displays a top app bar with the list name and a vertical stack of all item titles.
- **Top App Bar:**
  - Uses a **Material 3 Medium Top App Bar** where the List Title is positioned below the leading/trailing icon buttons to allow for **two-line wrapping**. 
  - **Actions:** Contains an overflow menu with a **Delete** menu item. Tapping **Delete** triggers a confirmation prompt to delete the entire list and its items. After deletion, the user is taken back to the Home Screen.
  - **Tap-to-Edit:** The List Title is editable via a **Tap-to-Edit** trigger. This trigger is only active when the **Medium Top App Bar** is in its expanded state; editing is disabled in the collapsed state. Tapping the title activates it for editing without causing layout shifts.
  - **Save Action:** While in Edit Mode, a **Save** button appears as a trailing action in the **Medium Top App Bar**, replacing the overflow menu. Tapping **Save** persists the edits and returns the UI to a read-only state. If the user attempts to navigate away (including via the **system Back gesture**) without saving, they are prompted to save or discard their draft. This draft state applies only to text edits.
  - **Scroll Behavior:** The App Bar should collapse as the user scrolls down and re-expand when scrolling to the top. Tapping the title in a collapsed state does nothing; the user must scroll to expand the bar before editing.
- **Item Interactions:**
  - **Tap:** Tapping an item opens the **Item Detail Screen**.
  - **Long-press:** Long-pressing an item transitions the UI to a multi-select state where:
    - The pressed item is selected initially.
    - Other items can be added to the selection by tapping them.
    - The top app bar should transition to a new top app bar that contains:
      - A close icon that exits the multi-select state.
      - The number of selected items.
      - A delete action. Tapping the delete action prompts the user for confirmation and then deletes all selected items.
  - **Drag:** If the user **drags** the item immediately after the initial long-press (without lifting their finger), the app transitions from the multi-select state to a **reorder drag-and-drop** state. The selected item can then be dragged to reorder it in the list. Reordering actions are saved immediately upon the completion of the drag gesture and are not part of the "Save/Discard" draft flow.
- **Add Item:** A FAB opens a bottom sheet to enter the Item Title and Item Description. The Save button is located in the **bottom-right** of the sheet to ensure easy one-handed access. The Description input begins as a single row but expands vertically as the user enters more text; the bottom sheet itself expands to accommodate this growing content.
  - **Dismissal:** If the user attempts to dismiss the sheet (swiping down, tapping outside, or using the **system Back gesture**) after entering text, they are prompted to save or discard their draft.
  - **Validation:**
    - **Item Title:** Required, max 100 characters. **Keyboard Action:** Moves focus to Description.
    - **Item Description:** Optional, max 5,000 characters. **Keyboard Action:** Standard Enter key for line breaks.
    - **Feedback:** Character counters and standard Material error messages. Validation acts as a "soft block" (disables Save button).
- **Empty State:** Display a centered minimalist icon and the text:
  - *"Your list is empty."* (Heading)
  - *"Add your first item to get started."* (Body)

### 3.3 Item Detail Screen

- **Layout:** Displays a top app bar with the item title and a body with the description
- **Top App Bar:**
  - Uses a **Material 3 Medium Top App Bar** where the List Title is positioned below the leading/trailing icon buttons to allow for **two-line wrapping**.
  - **Actions:** Contains an overflow menu with a **Delete** menu item. Tapping **Delete** triggers a confirmation prompt to delete the item. After deletion, the user is taken back to the List Detail Screen.
  - **Tap-to-Edit:** The Item Title is editable via a **Tap-to-Edit** trigger. This trigger is only active when the **Medium Top App Bar** is in its expanded state; editing is disabled in the collapsed state. Tapping the title activates it for editing without causing layout shifts.
  - **Save Action:** While in Edit Mode, a **Save** button appears as a trailing action in the **Medium Top App Bar**, replacing the overflow menu. Tapping **Save** persists the edits and returns the UI to a read-only state. If the user attempts to navigate away (including via the **system Back gesture**) without saving, they are prompted to save or discard their draft.
  - **Scroll Behavior:** The App Bar should collapse as the user scrolls down and re-expand when scrolling to the top. Tapping the title in a collapsed state does nothing; the user must scroll to expand the bar before editing.

- **Body:**
  - Displays the Item Description
  - **Tap-to-Edit:** The Item Description is editable via a **Tap-to-Edit** trigger. Tapping the description activates it for editing without causing layout shifts.

---

## 4. Non-Functional Requirements

- **Speed:** Instant transitions between Home and Detail screens.
- **One-Handed Utility:** Interactive actions (Add FAB) should be in the bottom-third. Primary Save/Menu actions should be in the Top App Bar for consistency with Material 3 patterns.
- **Reliability:** Fully functional offline.

---

## 5. Out of Scope (Future Roadmap)

- **Cloud Sync:** Syncing data across devices.
- **Collaboration:** Sharing lists with other users.
- **Media:** Adding photos or images to item descriptions.