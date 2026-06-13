# Sothirich CoffeeShopSystemManagement Reference Notes

Source inspected locally from `work/reference/CoffeeShopSystemManagement`.

## Useful UI Ideas

- JavaFX app uses FXML views and CSS, not Swing.
- User POS layout:
  - left selected drink card with large image, drink name, prices/options, and add-to-cart button
  - center scrollable drink grid using `item.fxml`
  - bottom cart table with total and purchase/reset/delete actions
- Admin layout:
  - left vertical navigation with icon + label actions
  - content pane swaps overview, accept order, pending order, update drink, history, user management
- Visual style:
  - large product images make the UI feel much more complete
  - dark coffee selected card gives focus to the current item
  - card grid is simpler than putting many controls into each card

## Asset Reuse

The reference repository does not include a license file, but the user confirmed the app owner allows reuse of the drink images for this coursework demo. Only the drink PNG assets are reused; business logic and UI code remain our own Swing implementation.

## Changes Applied To Our Project

- POS menu cards now select a drink instead of containing many action controls.
- Selected drink, topping choices, and add-to-cart action are separated into their own left panel.
- Drink images are now loaded from `/assets/drinks/<slug>.png` when present, with generated icon fallback.
