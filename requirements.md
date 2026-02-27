# Product Requirements Document: ListBox

## 1. Product Overview

### The Problem

Spreadsheets (Google Sheets/Excel) are powerful for tabular data but are notoriously difficult to use on a mobile device. Note-taking apps (Keep/Notes) are too unstructured for data like "Gift Ideas," "Recipe Box," or "Personal Goals" where specific details (links, ingredients, milestones) are required. Users need a "spreadsheet UI alternative" that provides structure without the horizontal grid.

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

- **Home Screen:** A vertical index of all user-created lists.
- **Create List with Templates:** Users can create a blank list or choose a "Quick-Start" template. This populates the list with example items containing relevant **information** in the description:
  - **Gift Ideas:** Populates an example item with "Store," "Price," and "Recipient" info.
  - **Recipe Box:** Populates an example item with "Ingredients" and "Instructions" info.
  - **Goal Tracker:** Populates an example item with "Success Criteria" and "Target Date" info.
- **Delete List:** A way to remove an entire list and its items (includes a confirmation prompt).

### 3.2 List Detail Screen

- **Layout (Medium Top App Bar):** Displays the list name and a vertical stack of all item titles. It implements a **Material 3 Medium Top App Bar** where the List Title is positioned below the leading/trailing icon buttons to allow for **two-line wrapping**. 
- **Edit List Title:**
  - **Tap-to-Edit:** The List Title is editable via a **Tap-to-Edit** trigger. Tapping the title transitions it into an active `TextField`.
  - **Save Action:** While in Edit Mode, a **Save** button becomes visible to commit changes. To maintain one-handed utility, this button is located either as a trailing action in the **Medium Top App Bar** or within a **Toolbar** anchored to the top of the software keyboard.
  - **Scroll Behavior:** The App Bar uses `exitUntilCollapsed` behavior. When a title is tapped in a collapsed state, the UI must scroll to the top to expand the bar before focusing the input.
- **Add Item:** A streamlined input for the Item Title and Item Description. The Description input begins as a single row but expands vertically as the user enters more text; the bottom sheet itself expands to accommodate this growing content.
- **Multi-Select & Bulk Delete:**
  - **Trigger:** Long-pressing an item title in the List Detail Screen transitions the UI to a multi-select state.
  - **Selection:** The pressed item is selected; other items can be selected by tapping them.
  - **Action:** A delete action appears in the top app bar to delete all selected items (includes a confirmation prompt).
- **Reorder Items:** If a user starts dragging while the initially pressed item is selected (but no other items have been selected yet), the app transitions to a drag state to reorder items.

### 3.3 Item Detail View (Full-Screen)

- **Layout (Full-Screen):** Tapping an item opens a full-screen modal showing the full description. This view uses a **Material 3 Medium Top App Bar** with the Item Title positioned below the leading/trailing icon buttons to allow for **two-line wrapping**. 
- **Tap-to-Edit:** Tapping the Title or Description field directly transitions that specific field into an active `TextField`.
- **Save Action:** Similar to the List Detail Screen, an explicit **Save** button appears in Edit Mode (either in the Top App Bar or atop the keyboard) to commit the edit and return to a read-only state.
- **Delete Item:** The delete action is located within the **Item Detail View**. Tapping delete triggers a **confirmation prompt**.

---

## 4. Non-Functional Requirements

- **Speed:** The transition from the Home Screen to a List Detail screen must be near-instant.
- **One-Handed Utility:** Primary "Add" and "Save" buttons are located in the "bottom-third" of the screen for easy thumb access.
- **Reliability:** The app must function perfectly without an active internet connection.

---

## 5. Out of Scope (Future Roadmap)

- **Cloud Sync:** Syncing data across devices.
- **Collaboration:** Sharing lists with other users.
- **Media:** Adding photos or images to item descriptions.