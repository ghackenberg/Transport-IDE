# New Feature: JavaFX 2D Viewer Background Grid and Snap-to-Grid

**Date:** `2025-07-10`
**Author:** `Gemini`
**Ticket/Issue:** `N/A`
**Related Docs:** `N/A`

---

### 1. Feature Overview & Business Case (The "Why")

* **User Story / Epic:**
    * As a `Modeler`, I want to `see a grid in the 2D viewer and have intersections snap to it` so that `I can create more precise and aligned transportation network layouts`.

* **Business Value:**
    * This feature will improve the user experience by enabling the creation of clean, well-organized models. It will lead to faster model creation and fewer topological errors.

* **Success Metrics:**
    * Positive user feedback on the new grid and snapping functionality.
    * Reduction in the average time required to create and align network models.

---

### 2. Functional Requirements & User Experience (The "What")

* **Core Functionality:**
    * 1. The JavaFX 2D viewer shall display a grid of lines in the background, behind all model elements.
    * 2. The grid spacing must adapt dynamically to the current zoom level.
    * 3. The on-screen distance between adjacent grid lines must never be less than 10 pixels.
    * 4. When a user moves an intersection, its final position must be "snapped" to the nearest grid point upon releasing the mouse button.
    * 5. The grid should be visually unobtrusive, rendered with thin, light gray lines.

* **Acceptance Criteria:**
    * - `[ ]` Given the 2D viewer is open, a grid is visible in the background.
    * - `[ ]` When I zoom in, the grid becomes finer (more lines appear, representing smaller units).
    * - `[ ]` When I zoom out, the grid becomes coarser (fewer lines appear, representing larger units).
    * - `[ ]` The visual distance between any two adjacent grid lines on the screen is always 10 pixels or more.
    * - `[ ]` When an intersection is dragged and released, its center coordinates are set to the coordinates of the closest grid intersection point.
    * - `[ ]` The snapping behavior feels predictable and enhances precision.

* **UI/UX Design:**
    * The grid serves as a background layer. It should not interfere with the visibility of the primary model elements (intersections, segments, etc.).
    * **Styling:** Use `Color.LIGHTGRAY` or a similar unobtrusive color for the grid lines. Line thickness should be minimal (e.g., 1px).

* **Edge Cases & Error Handling:**
    * - The feature will initially be always-on. A future enhancement could be to make the grid and snapping toggleable.
    * - Snapping should remain consistent and accurate at all zoom levels, from extreme zoom-in to extreme zoom-out.

---

### 3. Technical Specification (The "How")

* **Affected Maven Modules:**
    * - `[ ]` `modules/fx`

* **Proposed Implementation Plan:**
    * 1. **Grid Layer:** Create a `GridLayer.java` class that extends `javafx.scene.layout.Pane`. This class will be responsible for drawing the grid lines.
    * 2. **Integration:** Add an instance of `GridLayer` to the main viewer pane, ensuring it is rendered behind the model elements layer.
    * 3. **Zoom Listener:** Implement a listener on the viewer's scale/transform properties. When the zoom level changes, the `GridLayer` must be notified to recalculate and redraw the grid.
    * 4. **Grid Logic:** The core logic will calculate the appropriate grid spacing in world units based on the current view scale, ensuring the on-screen distance is at least 10 pixels. The calculated spacing should be rounded to a "human-friendly" number (e.g., 1, 2, 5, 10, 50, 100).
        * `world_units_per_pixel = 1.0 / scale`
        * `min_world_spacing = 10 * world_units_per_pixel`
        * `grid_spacing = round_to_nice_number(min_world_spacing)`
    * 5. **Snapping Logic:** Modify the mouse drag handler for intersections. On the `MOUSE_RELEASED` event, get the current mouse position, transform it to world coordinates, calculate the nearest grid point based on the current `grid_spacing`, and update the intersection's model position.

* **New Files / Components:**
    * - **Class:** `io.github.ghackenberg.mbse.transport.fx.viewers.flat.GridLayer.java`

* **Modified Files:**
    * - `io.github.ghackenberg.mbse.transport.fx.viewers.flat.ModelViewerFlat.java` (to incorporate the `GridLayer` and handle zoom events)
    * - `io.github.ghackenberg.mbse.transport.fx.scenes.EditorScene.java` (to implement the snapping logic for intersections)

* **New Dependencies:**
    * None anticipated.

---

### 4. Data Model / Database

* **Database Schema Changes:**
    * None.

* **Data Migration:**
    * None.

---

### 5. Testing Strategy

* **Unit Tests:**
    * - Write unit tests for the grid spacing calculation logic to verify correct rounding and adherence to the minimum pixel distance across various scales.
    * - Write unit tests for the snapping calculation to verify that a given coordinate is correctly snapped to the nearest grid point.

* **Integration Tests:**
    * - N/A

* **End-to-End (E2E) / Manual QA Plan:**
    * 1. Launch the application and open a model in the JavaFX viewer.
    * 2. Confirm the background grid is visible.
    * 3. Zoom in and out continuously, verifying that the grid density updates correctly and line spacing is never too small.
    * 4. Drag an intersection and release it. Verify it snaps to the nearest grid line intersection.
    * 5. Repeat the drag-and-snap test at various zoom levels to ensure consistency.
    * 6. Pan the view and ensure the grid redraws correctly for the new viewport.

---

### 6. Definition of Done

* `[ ]` All acceptance criteria from section 2 are met.
* `[ ]` The feature is implemented according to the technical specification.
* `[ ]` The grid rendering is performant and does not introduce lag during panning or zooming.
* `[ ]` Code is documented (class and method Javadocs) and follows project coding standards.
* `[ ]` Unit tests are written and passing with sufficient coverage for the new logic.
* `[ ]` The feature has been successfully tested via the manual QA plan.
* `[ ]` The application builds successfully using `mvn clean install`.
