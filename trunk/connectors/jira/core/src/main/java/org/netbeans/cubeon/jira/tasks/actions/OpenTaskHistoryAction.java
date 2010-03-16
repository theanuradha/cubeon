/*
 *  Copyright 2008 Anuradha.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package org.netbeans.cubeon.jira.tasks.actions;

import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import org.netbeans.cubeon.jira.tasks.JiraTask;
import org.openide.awt.HtmlBrowser.URLDisplayer;
import org.openide.util.NbBundle;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Anuradha
 */
public class OpenTaskHistoryAction extends AbstractAction {
    private static final long serialVersionUID = -3533110265103846290L;

    private JiraTask task;

    public OpenTaskHistoryAction(JiraTask task) {
        this.task = task;
        putValue(NAME, NbBundle.getMessage(OpenTaskHistoryAction.class, "LBL_Open_Change_History"));
        putValue(SHORT_DESCRIPTION, NbBundle.getMessage(OpenTaskHistoryAction.class, "LBL_Open_Change_History"));
        putValue(SMALL_ICON, new ImageIcon(ImageUtilities.loadImage("org/netbeans/cubeon/jira/history.png")));
    }

    public void actionPerformed(ActionEvent e) {
        try {
            URLDisplayer.getDefault().showURL(new URL(task.getUrlString() + "?page=com.atlassian.jira.plugin.system.issuetabpanels:changehistory-tabpanel"));//NOI18N
        } catch (MalformedURLException ex) {
            Logger.getLogger(OpenTaskHistoryAction.class.getName()).warning(ex.getMessage());
        }
    }
}
