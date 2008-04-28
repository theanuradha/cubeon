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
 * Action which shows TaskRepositorys component.
 */
public class TaskRepositoriesAction extends AbstractAction {

    public TaskRepositoriesAction() {
        super(NbBundle.getMessage(TaskRepositoriesAction.class, "CTL_TaskRepositoriesAction"));
        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(TaskRepositoriesTopComponent.ICON_PATH, true)));
    }

    public void actionPerformed(ActionEvent evt) {
        TopComponent win = TaskRepositoriesTopComponent.findInstance();
        win.open();
        win.requestActive();
    }
}
