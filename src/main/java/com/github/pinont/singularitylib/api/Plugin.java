package com.github.pinont.singularitylib.api;

import com.github.pinont.singularitylib.plugin.CorePlugin;

public class Plugin extends CorePlugin {
    @Override
    public void onPluginStart() {
        sendConsoleMessage("SingularityLib Plugin ready for hook!");
    }

    @Override
    public void onPluginStop() {
        sendConsoleMessage("SingularityLib Plugin stopped!");
    }
}
