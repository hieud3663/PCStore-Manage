package com.pcstore.components.menu;

/**
 *
 * @author Raven
 */
public class MenuAction {

    protected boolean isCancel() {
        return cancel;
    }

    public void cancel() {
        this.cancel = true;
    }

    private boolean cancel = false;
}
