package com.tyron.code.editor.shortcuts;

import java.util.List;

public class ShortcutItem {

    public ShortcutItem() {

    }

    public ShortcutItem(List<ShortcutAction> actions, String label, String kind) {
        this.actions = actions;
        this.label = label;
        this.kind = kind;
    }

    public List<ShortcutAction> actions;

    public String kind;

    public String label;
}
