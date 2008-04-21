/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.cubeon.ui;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

/**
 * Action which shows TaskExplorer component.
 */
public class TaskExplorerAction extends AbstractAction {

    public TaskExplorerAction() {
        super(NbBundle.getMessage(TaskExplorerAction.class, "CTL_TaskExplorerAction"));
        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(TaskExplorerTopComponent.ICON_PATH, true)));
    }

    public void actionPerformed(ActionEvent evt) {
        TopComponent win = TaskExplorerTopComponent.findInstance();
        win.open();
        win.requestActive();
    }
}
