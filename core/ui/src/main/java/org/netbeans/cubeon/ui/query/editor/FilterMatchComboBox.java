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
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import org.netbeans.cubeon.ui.query.QueryFilter;
import org.openide.util.NbBundle;

/**
 *
 * @author g.hartmann
 */
public class FilterMatchComboBox extends JComboBox {

    public FilterMatchComboBox(boolean text) {
        if (text) {
            addItem(QueryFilter.Match.CONTAINS);
            addItem(QueryFilter.Match.CONTAINS_NOT);
            addItem(QueryFilter.Match.STARTS_WITH);
            addItem(QueryFilter.Match.ENDS_WITH);
        }
        addItem(QueryFilter.Match.IS);
        addItem(QueryFilter.Match.IS_NOT);

        // set renderer
        setRenderer(new FilterMatchRenderer());
    }

    private static class FilterMatchRenderer extends JLabel implements ListCellRenderer {

        public FilterMatchRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {

            if (value instanceof QueryFilter.Match) {
                setText(NbBundle.getMessage(FilterMatchComboBox.class,
                        "LBL_Match_"+value.toString())); // NOI18N
            } else
                setText(""); // NOI18N

            return this;
        }
    }

}
