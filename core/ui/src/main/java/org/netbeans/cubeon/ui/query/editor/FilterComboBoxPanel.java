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
import javax.swing.JComboBox;

/**
 *
 * @author g.hartmann
 */
public class FilterComboBoxPanel extends AbstractFilterValuePanel {

    JComboBox comboBox = new JComboBox();

    public FilterComboBoxPanel(Set<?> options) {
        for (Object option : options) {
            comboBox.addItem(option);
        }

        add(comboBox);
    }

    @Override
    public void setValues(Set<?> values) {
        Iterator iterator = values.iterator();
        Object value = iterator.next();
        iterator.remove();

        comboBox.setSelectedItem(value);
    }

    @Override
    public void getValues(Set<? super Object> values) {
        Object value = comboBox.getSelectedItem();
        if (value != null)
            values.add(value);
    }
}
