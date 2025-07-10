# New Feature: JavaFX 3D Viewer Controls

**Date:** `2025-07-10`
**Author:** `Gemini`
**Ticket/Issue:** `N/A`
**Related Docs:** `N/A`

---

### 1. Feature Overview & Business Case (The "Why")

* **User Story / Epic:**
    * As a `User`, I want to `use my mouse to control the 3D viewer` so that `I can easily inspect the 3D model from different perspectives`.

* **Business Value:**
    * This feature improves the usability of the 3D viewer, which is a core component of the application. A more intuitive and powerful viewer will lead to a better user experience and allow users to work more efficiently.

* **Success Metrics:**
    * - User feedback on the new controls.
    * - Reduction in time spent by users to position the 3D camera.

---

### 2. Functional Requirements & User Experience (The "What")

* **Core Functionality:**
    * 1. The user opens a 3D model in the JavaFX viewer.
    * 2. The user can zoom in and out by scrolling the mouse wheel.
    * 3. The user can rotate the camera around the model by dragging the mouse with the primary button pressed.
    * 4. The user can pan the camera (translate) by dragging the mouse with the secondary button pressed.

* **Acceptance Criteria:**
    * - `[ ]` Given the 3D viewer is open, scrolling the mouse wheel up zooms in.
    * - `[ ]` Given the 3D viewer is open, scrolling the mouse wheel down zooms out.
    * - `[ ]` Given the 3D viewer is open, dragging the mouse with the primary button rotates the camera.
    * - `[ ]` Given the 3D viewer is open, dragging the mouse with the secondary button pans the camera.
    * - `[ ]` The controls should feel smooth and responsive.

* **UI/UX Design:**
    * The feature is purely about interaction design. No new UI elements are required.

* **Edge Cases & Error Handling:**
    * - What if the user has a mouse without a scroll wheel? (No zoom functionality in that case).
    * - What if the user has a single-button mouse? (No panning functionality).

---

### 3. Technical Specification (The "How")

* **Affected Maven Modules:**
    * - `[ ]` `mbse.transport:fx`

* **Proposed Implementation Plan:**
    * 1. Create a new `CameraManager` class to handle all camera manipulations.
    * 2. Add event handlers to the 3D scene for mouse scroll, drag, and move events.
    * 3. The event handlers will call methods on the `CameraManager` to update the camera's position and orientation.

* **New Files / Components:**
    * - **Class:** `io.github.ghackenberg.mbse.transport.fx.viewers.deep.input.CameraManager.java`
    * - **Test:** `io.github.ghackenberg.mbse.transport.fx.viewers.deep.input.CameraManagerTest.java`

* **Modified Files:**
    * - `io.github.ghackenberg.mbse.transport.fx.viewers.deep.ModelViewerDeep.java` (To integrate the `CameraManager`)

* **API Changes (if applicable):**
    * N/A

* **New Dependencies:**
    * N/A

---

### 4. Data Model / Database

* **Database Schema Changes:**
    * N/A

* **Data Migration:**
    * N/A

---

### 5. Testing Strategy

* **Unit Tests:**
    * - Test the `CameraManager` logic for zoom, rotation, and translation.

* **Integration Tests:**
    * - Test the interaction between the `ModelViewerDeep` and the `CameraManager`.

* **End-to-End (E2E) / Manual QA Plan:**
    * 1. Open a 3D model.
    * 2. Verify that zooming with the scroll wheel works as expected.
    * 3. Verify that rotating with the primary mouse button works as expected.
    * 4. Verify that panning with the secondary mouse button works as expected.

---

### 6. Definition of Done

* `[ ]` All acceptance criteria from section 2 are met.
* `[ ]` The feature is implemented according to the technical specification.
* `[ ]` Code is well-documented and follows project coding standards.
* `[ ]` Unit and integration tests are written and passing with sufficient coverage.
* `[ ]` The feature has been successfully tested via the manual QA plan.
* `[ ]` The application builds successfully with `mvn clean install`.
* `[ ]` The changes have been peer-reviewed and approved.
* `[ ]` The feature branch has been merged into the target branch.
