package io.github.pinont.singularitylib.api.ui;

/**
 * Interface for defining layouts in menus.
 * A layout maps a character key to a button for menu positioning.
 */
public interface Layout {
    /**
     * Gets the character key used to position this layout in a menu.
     *
     * @return the character key
     */
    char getKey();

    /**
     * Gets the button associated with this layout.
     *
     * @return the button for this layout position
     */
    Button getButton();
}
