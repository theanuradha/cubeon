/*
 *  Copyright 2008 g.hartmann.
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

package org.netbeans.cubeon.ui.query.editor;

import java.util.Iterator;
import java.util.Set;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import org.openide.util.NbBundle;

/**
 *
 * @author g.hartmann
 */
public class FilterCheckBoxPanel extends AbstractFilterValuePanel {

    private ButtonGroup group = new ButtonGroup();
    private JRadioButton yes = new JRadioButton(NbBundle.getMessage(FilterCheckBoxPanel.class, "LBL_Yes"));
    private JRadioButton no  = new JRadioButton(NbBundle.getMessage(FilterCheckBoxPanel.class, "LBL_No"));

    public FilterCheckBoxPanel() {
        // add to button group
        group.add(yes);
        group.add(no);

        // add to panel
        add(yes);
        add(no);
    }

    @Override
    public void setValues(Set<?> values) {
        // get value and remove from list
        Iterator iterator = values.iterator();
        String value = iterator.next().toString();
        iterator.remove();

        // set ui
        if ("1".equals(value)) // NOI18N
            yes.setSelected(true);
        else if ("0".equals(value)) // NOI18N
            no.setSelected(true);
        else{
            //issue - 97 : group.clearSelection() can use as it is 1.6 api
           yes.setSelected(false);
           no.setSelected(false);
        }
    }

    @Override
    public void getValues(Set<? super Object> values) {
        if (yes.isSelected())
            values.add("1"); // NOI18N
        else if (no.isSelected())
            values.add("0"); // NOI18N
    }
}
