package com.github.pinont.singularitylib;

import com.github.pinont.singularitylib.plugin.CorePlugin;

// This is a simple test plugin extending CorePlugin for testing purposes.
public class TestPlugin extends CorePlugin {
    @Override
    public void onPluginStart() {
        // Plugin start logic for testing
        this.isTest = true;
    }

    @Override
    public void onPluginStop() {
        // Plugin stop logic for testing
    }
}
