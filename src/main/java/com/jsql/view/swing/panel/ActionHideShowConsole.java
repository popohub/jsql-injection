package com.jsql.view.swing.panel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import com.jsql.view.swing.MediatorGui;

/**
 * MouseAdapter to show/hide bottom panel.
 */
public class ActionHideShowConsole implements ActionListener {
    /**
     * Save the divider location when bottom panel is not visible.
     */
    private int loc = 0;

    /**
     * Ersatz panel to display in place of tabbedpane.
     */
    private JPanel panel;

    /**
     * Create the hide/show bottom panel action.
     */
    public ActionHideShowConsole(JPanel panel) {
        super();
        this.panel = panel;
    }

    /**
     * Hide bottom panel if both main and bottom are visible, also
     * displays an ersatz bar replacing tabbedpane.  
     * Or else if only main panel is visible then displays bottom panel
     * and hide ersatz panel.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (
            MediatorGui.frame().splitPaneCenter.getTopComponent().isVisible() && 
            MediatorGui.frame().splitPaneCenter.getBottomComponent().isVisible()
        ) {
            MediatorGui.frame().splitPaneCenter.getBottomComponent().setVisible(false);
            this.loc = MediatorGui.frame().splitPaneCenter.getDividerLocation();
            this.panel.setVisible(true);
            MediatorGui.frame().splitPaneCenter.disableDragSize();
        } else {
            MediatorGui.frame().splitPaneCenter.getBottomComponent().setVisible(true);
            MediatorGui.frame().splitPaneCenter.setDividerLocation(this.loc);
            this.panel.setVisible(false);
            MediatorGui.frame().splitPaneCenter.enableDragSize();
        }
    }
}