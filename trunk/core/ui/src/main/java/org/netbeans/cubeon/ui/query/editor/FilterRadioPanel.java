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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.JCheckBox;

/**
 *
 * @author g.hartmann
 */
public class FilterRadioPanel extends AbstractFilterValuePanel {

    List<CheckBox> checkboxes = new ArrayList<CheckBox>();

    public FilterRadioPanel(Set<?> options) {
        // add to list
        for (Object option : options)
            checkboxes.add(new CheckBox(option));

        // add to panel
        for (CheckBox checkbox : checkboxes)
            add(checkbox.getCheckbox());
    }

    @Override
    public void setValues(Set<?> values) {
        for (CheckBox checkbox : checkboxes)
            checkbox.setSelected(values);
        // clear values: unused options will be ignored
        values.clear();
    }

    @Override
    public void getValues(Set<? super Object> values) {
        for (CheckBox checkbox : checkboxes)
            if (checkbox.isSelected())
                values.add(checkbox.getOption());
    }

    private static class CheckBox {
        private final Object    option;
        private final JCheckBox checkbox;

        public CheckBox(Object option) {
            this.option = option;
            checkbox = new JCheckBox(option.toString());
        }

        public Object getOption() {
            return option;
        }

        public JCheckBox getCheckbox() {
            return checkbox;
        }

        public boolean isSelected() {
            return checkbox.isSelected();
        }

        public void setSelected(Set<?> values) {
            if (values.contains(option)) {
                checkbox.setSelected(true);
            } else
                checkbox.setSelected(false);
        }
    }
}
