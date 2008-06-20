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
package org.netbeans.cubeon.ui.taskelemet;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.openide.awt.HtmlBrowser.URLDisplayer;

/**
 *
 * @author Anuradha
 */
public class OpenInBrowserAction extends AbstractAction {

    private TaskElement element;

    public OpenInBrowserAction(TaskElement element) {
        this.element = element;
        putValue(NAME, "Open with Browser");
    }

    public void actionPerformed(ActionEvent e) {
        URLDisplayer.getDefault().showURL(element.getUrl());
    }

    @Override
    public boolean isEnabled() {
        return element.getUrl() != null;
    }
}
