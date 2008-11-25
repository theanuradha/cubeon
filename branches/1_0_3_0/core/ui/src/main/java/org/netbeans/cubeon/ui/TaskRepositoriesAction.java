/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.cubeon.ui;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.actions.Presenter.Popup;
import org.openide.windows.TopComponent;

/**
 * Action which shows TaskRepositorys component.
 */
public class TaskRepositoriesAction extends AbstractAction implements Popup {

    public TaskRepositoriesAction(String name) {
        super(name);
        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(TaskRepositoriesTopComponent.ICON_PATH, true)));
    }

    public TaskRepositoriesAction() {
        this(NbBundle.getMessage(TaskRepositoriesAction.class, "CTL_TaskRepositoriesAction"));
    }

    public void actionPerformed(ActionEvent evt) {
        TopComponent win = TaskRepositoriesTopComponent.findInstance();
        win.open();
        win.requestActive();
    }

    public JMenuItem getPopupPresenter() {
        return new JMenuItem(this);
    }
}
