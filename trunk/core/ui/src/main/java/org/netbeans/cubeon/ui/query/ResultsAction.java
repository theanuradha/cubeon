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
package org.netbeans.cubeon.ui.query;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import org.netbeans.cubeon.tasks.spi.query.TaskQuery;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 * Action which shows Resuts component.
 */
public class ResultsAction extends AbstractAction {

    private TaskQuery query;

    public ResultsAction() {
        super(NbBundle.getMessage(ResultsAction.class, "CTL_ResultsAction"));
        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(ResultsTopComponent.ICON_PATH, true)));
    }

    public ResultsAction(TaskQuery query) {
        super(NbBundle.getMessage(ResultsAction.class, "LBL_ShowResults"));
        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(ResultsTopComponent.ICON_PATH, true)));
        this.query = query;

    }

    public void actionPerformed(ActionEvent evt) {

        ResultsTopComponent component = ResultsTopComponent.findInstance();
        component.open();
        component.requestActive();
        if (query != null) {
            component.showResults(query);
        }
    }
}
