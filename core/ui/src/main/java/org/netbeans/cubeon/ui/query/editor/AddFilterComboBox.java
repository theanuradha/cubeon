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

import java.awt.Component;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import org.netbeans.cubeon.ui.query.QueryField;

/**
 *
 * @author g.hartmann
 */
public class AddFilterComboBox extends JComboBox {

    public AddFilterComboBox() {
        // set renderer
        setRenderer(new AddFilterRenderer());
    }

    public void refresh(List<QueryField> fields) {
        removeAllItems();
        // add empty
        addItem(null);
        // add all filter fields
        for (QueryField field : fields) {
            addItem(new AddFilterItem(field));
        }
        // select empty
        setSelectedItem(null);
    }

    public QueryField getSelectedField() {
        if (getSelectedItem() instanceof AddFilterItem) {
            AddFilterItem item = (AddFilterItem) getSelectedItem();
            if (item.isAddAllowed())
                return item.getQueryField();
        }
        return null;
    }

    public void setAddAllowed(QueryField field, boolean addAllowed) {
        for (int i = 0 ; i < getItemCount() ; i++)
            if (getItemAt(i) instanceof AddFilterItem) {
                AddFilterItem item = (AddFilterItem) getItemAt(i);
                if (item.getQueryField().equals(field))
                    item.setAddAllowed(addAllowed);
        }
    }

    private static class AddFilterItem {

        private final QueryField field;
        private boolean addAllowed = true;

        public AddFilterItem(QueryField field) {
            this.field = field;
        }

        public void setAddAllowed(boolean addAllowed) {
            this.addAllowed = addAllowed;
        }

        public QueryField getQueryField() {
            return field;
        }

        public String getText() {
            return field.getDisplay();
        }

        public boolean isAddAllowed() {
            return addAllowed;
        }
    }

    private static class AddFilterRenderer extends JLabel implements ListCellRenderer {

        public AddFilterRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {

            if (value instanceof AddFilterItem) {
                AddFilterItem item = (AddFilterItem) value;
                setText(item.getText());
                setBackground(UIManager.getDefaults().getColor("ComboBox.listBackground")); // NOI18N
                setForeground(item.isAddAllowed() ?
                    UIManager.getDefaults().getColor("ComboBox.listForeground") : // NOI18N
                    UIManager.getDefaults().getColor("ComboBox.disabledForeground")); // NOI18N
            } else
                setText(""); // NOI18N

            return this;
        }
    }

}
