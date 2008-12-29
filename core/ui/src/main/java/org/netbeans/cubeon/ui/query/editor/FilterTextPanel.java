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
import javax.swing.JTextField;

/**
 *
 * @author g.hartmann
 */
public class FilterTextPanel extends AbstractFilterValuePanel {

    private JTextField text = new JTextField();

    public FilterTextPanel() {
        text.setColumns(20);
        add(text);
    }


    @Override
    public void setValues(Set<?> values) {
        Iterator iterator = values.iterator();
        String value = iterator.next().toString();
        iterator.remove();

        text.setText(value.toString());
    }

    @Override
    public void getValues(Set<? super Object> values) {
        String value = text.getText();
        if (value != null && value.trim().length() > 0)
            values.add(value);
    }
}
