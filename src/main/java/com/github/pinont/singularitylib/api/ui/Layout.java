package com.github.pinont.singularitylib.api.ui;

public class Layout {

    private final char key;
    private final Button button;
    public Layout(char c, Button button) {
        this.key = c;
        this.button = button;

    }

    /**
     * Gets the character key used to position this layout in a menu.
     *
     * @return the character key
     */
    public char getKey() {
        return key;
    }

    /**
     * Gets the button associated with this layout.
     *
     * @return the button for this layout position
     */
    public Button getButton() {
        return button;
    }
}
