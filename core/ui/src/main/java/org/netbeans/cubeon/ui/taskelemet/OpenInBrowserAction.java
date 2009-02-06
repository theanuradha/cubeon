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
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

/**
 *
 * @author Anuradha
 */
public class OpenInBrowserAction extends NodeAction {

   
    private OpenInBrowserAction() {
      
        putValue(NAME, NbBundle.getMessage(OpenInBrowserAction.class, "LBL_Open_with_Browser"));
    }

  

    @Override
    protected void performAction(Node[] activatedNodes) {
         for (Node node : activatedNodes) {
            TaskElement element = node.getLookup().lookup(TaskElement.class);
            if (element != null && element.getUrl() != null) {
                 URLDisplayer.getDefault().showURL(element.getUrl());
            }
        }
    }

    @Override
    protected boolean enable(Node[] activatedNodes) {
        for (Node node : activatedNodes) {
            TaskElement element = node.getLookup().lookup(TaskElement.class);
            if (element == null || element.getUrl() == null) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getName() {
        return (String) getValue(NAME);
    }

    @Override
    public HelpCtx getHelpCtx() {
        return new HelpCtx(OpenInBrowserAction.class);
    }
}
