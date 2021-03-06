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
package org.netbeans.cubeon.gcode.tasks.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import org.netbeans.cubeon.gcode.tasks.GCodeTask;
import org.openide.awt.HtmlBrowser.URLDisplayer;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 *
 * @author Anuradha
 */
public class OpenInBrowserTaskAction extends AbstractAction {
    private static final long serialVersionUID = -4258062164533591503L;

    private GCodeTask task;

    public OpenInBrowserTaskAction(GCodeTask task) {
        this.task = task;
        putValue(NAME, NbBundle.getMessage(OpenInBrowserTaskAction.class, "LBL_Open_Task_In_Browser"));
        putValue(SHORT_DESCRIPTION,  NbBundle.getMessage(OpenInBrowserTaskAction.class, "LBL_Open_Task_In_Browser"));
        putValue(SMALL_ICON, new ImageIcon(ImageUtilities.loadImage("org/netbeans/cubeon/gcode/web.png")));
    }


    public void actionPerformed(ActionEvent e) {
        URLDisplayer.getDefault().showURL(task.getUrl());
    }
}
