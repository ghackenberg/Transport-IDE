# Bug Fix: [Concise Title of the Bug]

**Date:** `YYYY-MM-DD`
**Author:** `[Your Name]`
**Ticket/Issue:** `[Link to Jira, Bugzilla, or other tracking tool]`
**Severity:** `[e.g., Blocker, Critical, Major, Minor, Trivial]`

---

### 1. Bug Summary & Impact (The "What & Why")

* **Problem Description:**
    * A clear, one-sentence summary of the problem.
    * (e.g., "The application crashes when a user tries to save a profile without a profile picture.", "Order totals are calculated incorrectly when a discount is applied.")

* **User/System Impact:**
    * Describe the consequences of this bug. Who is affected and how?
    * (e.g., "Prevents all users from updating their profile.", "Causes incorrect billing and potential revenue loss.", "Minor visual annoyance on the settings screen.")

---

### 2. Reproduction (The "How to See It")

* **Prerequisites:**
    * Any specific setup or state required to trigger the bug.
    * (e.g., "User must be logged in as an 'Editor'.", "The application must be running in offline mode.", "There must be zero items in the inventory for product XYZ.")

* **Steps to Reproduce:**
    * A precise, numbered list of actions to reliably trigger the bug.
    * 1. Navigate to the 'User Profile' page.
    * 2. Fill in all fields except the profile picture.
    * 3. Click the 'Save' button.
    * 4. Observe the application crash.

* **Expected Result:**
    * What *should* have happened if the bug didn't exist?
    * (e.g., "A validation message should appear asking the user to upload a picture.", "The profile should be saved successfully with a default placeholder image.")

* **Actual Result:**
    * What *actually* happened?
    * (e.g., "The application becomes unresponsive and then closes unexpectedly.", "The order total is $10.00 instead of the correct $9.00.")

* **Evidence:**
    * Link to or embed screenshots, videos, or logs that demonstrate the bug.
    * **Stack Trace / Error Log:**
      ```
      java.lang.NullPointerException
          at com.example.project.profile.ProfileService.save(ProfileService.java:42)
          ...
      ```
    * **Screenshot:** ![Bug Screenshot](link-to-image.png)

---

### 3. Root Cause Analysis (The "Where & Why")

* **Affected Maven Modules / Components:**
    * Pinpoint the location of the bug in the codebase.
    * - `com.example.project:core-module`
    * - `com.example.project.profile.ProfileService`

* **Technical Analysis (To be filled in by developer):**
    * A brief explanation of the technical cause of the bug.
    * (e.g., "A null pointer exception occurs in `ProfileService.java` on line 42 because the `profilePicture.getStream()` method is called without first checking if `profilePicture` is null.", "The discount logic incorrectly applies the percentage to the pre-tax total instead of the post-tax total.")

---

### 4. Proposed Fix (The "How to Fix It")

* **Solution Overview:**
    * A high-level description of the planned fix.
    * (e.g., "Add a null-check for the `profilePicture` object before attempting to process it.", "Refactor the calculation logic to apply discounts at the correct step in the sequence.")

* **Files to be Modified:**
    * - `com.example.project.core.service.ProfileService.java`
    * - `com.example.project.core.service.OrderCalculationService.java`

---

### 5. Testing & Verification Strategy

* **Fix Verification:**
    * A plan to confirm the bug is resolved.
    * `[ ]` Follow the original "Steps to Reproduce" and verify that the "Actual Result" now matches the "Expected Result".

* **Regression Testing:**
    * What related functionality needs to be checked to ensure the fix didn't introduce new problems?
    * `[ ]` Test saving a profile *with* a profile picture.
    * `[ ]` Test creating a new user account.
    * `[ ]` Test order calculation *without* a discount.
    * `[ ]` Test order calculation with a different type of discount.

* **New Automated Tests:**
    * What new tests will be added to prevent this bug from recurring?
    * - `[ ]` Add a unit test to `ProfileServiceTest` that attempts to save a profile with a null picture and asserts that no exception is thrown.

---

### 6. Definition of Done

* `[ ]` The original bug is no longer reproducible.
* `[ ]` The fix has been verified by following the original steps.
* `[ ]` Regression testing has been completed and passed.
* `[ ]` New automated tests covering the bug scenario have been added and are passing.
* `[ ]` The application builds successfully with `mvn clean install`.
* `[ ]` The changes have been peer-reviewed and approved.
* `[ ]` The fix has been merged into the target branch (e.g., `develop`, `hotfix/1.2.3`).
