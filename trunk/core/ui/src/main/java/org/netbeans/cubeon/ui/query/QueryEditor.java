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

/*
 * QueryEditor.java
 *
 * Created on 20.12.2008, 15:16:39
 */
package org.netbeans.cubeon.ui.query;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;
import org.netbeans.cubeon.tasks.spi.query.TaskQuery;
import org.netbeans.cubeon.tasks.spi.query.TaskQuerySupportProvider;
import org.netbeans.cubeon.ui.query.editor.AbstractFilterValuePanel;
import org.netbeans.cubeon.ui.query.editor.AddFilterComboBox;
import org.netbeans.cubeon.ui.query.editor.FilterCheckBoxPanel;
import org.netbeans.cubeon.ui.query.editor.FilterComboBoxPanel;
import org.netbeans.cubeon.ui.query.editor.FilterMatchComboBox;
import org.netbeans.cubeon.ui.query.editor.FilterRadioPanel;
import org.netbeans.cubeon.ui.query.editor.FilterTextPanel;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 *
 * @author g.hartmann
 */
public class QueryEditor extends javax.swing.JPanel implements TaskQuerySupportProvider.ConfigurationHandler {

    private final QuerySupport querySupport;
    private TaskQuery query;
    private AddFilterComboBox addCombo;
    private List<FilterLine> lines = new ArrayList<FilterLine>();
    private final Set<ChangeListener> listeners = new HashSet<ChangeListener>(1);
    private static Logger LOG = Logger.getLogger(QueryEditor.class.getName());

    /** Creates new form QueryEditor */
    public QueryEditor(QuerySupport querySupport) {
        initComponents();
        this.querySupport = querySupport;

        // can't add AddFilterComboBox using painter
        // so i cast it here
        addCombo = (AddFilterComboBox) cbxAddFilter;
        addCombo.refresh(querySupport.getQueryFields());

        // add filler panel to position filter line on top
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 4;
        constraints.weighty = 1.0;
        pnlFilters.add(fillerPanel, constraints);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblName = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        lblFilters = new javax.swing.JLabel();
        scrFilters = new javax.swing.JScrollPane();
        pnlFilters = new javax.swing.JPanel();
        lblAddFilter = new javax.swing.JLabel();
        cbxAddFilter = new AddFilterComboBox();

        lblName.setText(org.openide.util.NbBundle.getMessage(QueryEditor.class, "LBL_Name")); // NOI18N

        lblFilters.setText(org.openide.util.NbBundle.getMessage(QueryEditor.class, "LBL_Filters")); // NOI18N

        scrFilters.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        pnlFilters.setLayout(new java.awt.GridBagLayout());
        scrFilters.setViewportView(pnlFilters);

        lblAddFilter.setText(org.openide.util.NbBundle.getMessage(QueryEditor.class, "LBL_Add_Filter")); // NOI18N

        cbxAddFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxAddFilterActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, scrFilters, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, txtName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, lblName)
                    .add(layout.createSequentialGroup()
                        .add(lblAddFilter)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cbxAddFilter, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, lblFilters))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(lblName)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lblFilters)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(scrFilters, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cbxAddFilter, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lblAddFilter))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cbxAddFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxAddFilterActionPerformed
        QueryField field = addCombo.getSelectedField();
        if (field != null) {
            Set<String> defaultValue = new HashSet<String>();
            defaultValue.add(""); // NOI18N
            QueryFilter filter = new QueryFilter(field,
                    QueryFilter.getDefaultMatchForType(field.getType()),
                    defaultValue);
            addFilter(filter);
            addCombo.setSelectedItem(null);
        }
    }//GEN-LAST:event_cbxAddFilterActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cbxAddFilter;
    private javax.swing.JLabel lblAddFilter;
    private javax.swing.JLabel lblFilters;
    private javax.swing.JLabel lblName;
    private javax.swing.JPanel pnlFilters;
    private javax.swing.JScrollPane scrFilters;
    private javax.swing.JTextField txtName;
    // End of variables declaration//GEN-END:variables

    public void setTaskQuery(TaskQuery query) {
        this.query = query;

        // set query name
        txtName.setText(query.getName());

        // remove old filter lines
        while (!lines.isEmpty())
            removeFilterLine(0);

        List<QueryFilter> filters = querySupport.createFiltersFromQuery(query);
        for (QueryFilter filter : filters)
            addFilter(filter);
    }

    @Override
    public TaskQuery getTaskQuery() {
        if (query == null) {
            query = querySupport.createQuery();
        }

        querySupport.setQueryName(query, txtName.getText());

        List<QueryFilter> filters = getFilters();
        querySupport.setQueryFromFilters(query, filters);

        return query;
    }

    @Override
    public void addChangeListener(ChangeListener changeListener) {
        synchronized (listeners) {
            listeners.add(changeListener);
        }
    }

    @Override
    public void removeChangeListener(ChangeListener changeListener) {
        synchronized (listeners) {
            listeners.remove(changeListener);
        }
    }

    @Override
    public boolean isValidConfiguration() {
        return true;
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    // filter lines for field will be added till all
    // values have a corresponding line
    private void addFilter(QueryFilter filter) {
        while (!filter.getValues().isEmpty())
            addFilterLine(filter);
    }

    // filter line will be created and one or more value
    // from filter values will be used and removed from list
    private void addFilterLine(QueryFilter filter) {
        QueryField field = filter.getField();

        // find insert position
        int insert = 0;
        while (insert < lines.size()
                && lines.get(insert).getOrder() <= field.getOrder())
            insert++;

        // create new line
        FilterLine newLine = new FilterLine(insert, filter,
                insert > 0 && lines.get(insert-1).getField().equals(field));

        // add line to list
        lines.add(insert, newLine);
        // adjust gridy for following lines
        for (int i = insert+1 ; i < lines.size() ; i++)
            lines.get(i).adjustGridY(1);

        // revalidate filters panel
        scrFilters.validate();
        scrFilters.repaint();
        
        // disable add filter
        // only one filter for checkbox and radio allowed
        if (field.getType() == QueryField.Type.CHECKBOX ||
                field.getType() == QueryField.Type.RADIO) {
            addCombo.setAddAllowed(field, false);
            filter.getValues().clear();
        }
    }

    private void removeFilterLine(int index) {
        // get data for line to be removed
        QueryField field = lines.get(index).getField();
        boolean newField = lines.get(index).isNewField();
        QueryFilter.Match match = lines.get(index).getMatch();

        // remove filter line ui
        lines.get(index).remove();

        // remove from list
        lines.remove(index);

        if (index < lines.size()) {
            // init line without new field
            if (newField && !lines.get(index).isNewField())
                lines.get(index).initNewField(match);
            // adjust gridy for following lines
            for (int i = index ; i < lines.size() ; i++)
                lines.get(i).adjustGridY(-1);
        }

        // revalidate filters panel
        scrFilters.validate();
        scrFilters.repaint();

        // enable add filter
        // only one filter for checkbox and radio allowed
        if (field.getType() == QueryField.Type.CHECKBOX ||
                field.getType() == QueryField.Type.RADIO)
            addCombo.setAddAllowed(field, true);
    }

    private List<QueryFilter> getFilters() {
        List<QueryFilter> filters = new LinkedList<QueryFilter>();

        QueryField field = null;
        QueryFilter.Match match = null;
        Set<Object> values = new LinkedHashSet<Object>();
        for (FilterLine line : lines) {
            if (line.isNewField()) {
                // add filter
                if (!values.isEmpty())
                    filters.add(new QueryFilter(field, match, values));
                // start new filter
                field = line.getField();
                match = line.getMatch();
                values = new LinkedHashSet<Object>();
            }
            // set values
            line.getValues(values);
        }
        // add last filter
        if (!values.isEmpty())
            filters.add(new QueryFilter(field, match, values));

        return filters;
    }

    private final JPanel fillerPanel = new JPanel();

    private class FilterLine {
        private int gridy;
        private final QueryField field;
        private boolean or; // this line is not the first for this field
        private JLabel lblField;
        private JComboBox cbxMatch;
        private JLabel lblOr;
        private final AbstractFilterValuePanel cmpValue;
        private final JLabel lblRemove;
        private final Insets insets = new Insets(0, 4, 0, 4);

        public FilterLine(int gridy, QueryFilter filter, boolean or) {
            this.gridy = gridy;
            this.field = filter.getField();
            this.or = or;

            // field
            if (!or) {
                lblField = new JLabel(field.getDisplay());
                pnlFilters.add(lblField, createGridBagConstraints(0));
            }

            switch (field.getType()) {
                case CHECKBOX:
                    cmpValue = new FilterCheckBoxPanel();
                    break;
                case RADIO:
                    cmpValue = new FilterRadioPanel(field.getOptions());
                    break;
                case SELECT:
                    if (!or)
                        cbxMatch = new FilterMatchComboBox(false);
                    cmpValue = new FilterComboBoxPanel(field.getOptions());
                    break;
                default:
                    if (!or)
                        cbxMatch = new FilterMatchComboBox(true);
                    cmpValue = new FilterTextPanel();
            }

            if (cbxMatch != null) {
                // add and set match
                pnlFilters.add(cbxMatch, createGridBagConstraints(1));
                cbxMatch.setSelectedItem(filter.getMatch());
            } else if (or) {
                lblOr = new JLabel(NbBundle.getMessage(QueryEditor.class, "LBL_Or"));
                pnlFilters.add(lblOr, createGridBagConstraints(1));
            }

            // value panel
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridx = (cbxMatch != null || or ? 2 : 1);
            constraints.gridwidth = (cbxMatch != null ? 1 : 2);
            constraints.gridy = gridy;
            constraints.anchor = GridBagConstraints.WEST;
            constraints.weightx = 1.0;
            constraints.insets = insets;
            pnlFilters.add(cmpValue, constraints);
            // set value
            cmpValue.setValues(filter.getValues());

            // remove button
            lblRemove = new JLabel(ImageUtilities.image2Icon(
                    ImageUtilities.loadImage("org/netbeans/cubeon/ui/query/editor/filter_remove.png"))); // NOI18N
            lblRemove.addMouseListener(new RemoveListener(this));
            pnlFilters.add(lblRemove, createGridBagConstraints(3));

            // move filler panel
            adjustGridY(fillerPanel, 1);
        }

        public QueryField getField() {
            return field;
        }

        public int getOrder() {
            return field.getOrder();
        }

        public int getGridY() {
            return gridy;
        }

        public boolean isNewField() {
            return !or;
        }

        public QueryFilter.Match getMatch() {
            if (cbxMatch != null)
                return (QueryFilter.Match) cbxMatch.getSelectedItem();
            else
                return QueryFilter.Match.IS;
        }

        public void getValues(Set<? super Object> values) {
            cmpValue.getValues(values);
        }

        // remove filter line
        public void remove() {
            if (!or)
                pnlFilters.remove(lblField);
            if (cbxMatch != null)
                pnlFilters.remove(cbxMatch);
            else if (or)
                pnlFilters.remove(lblOr);
            pnlFilters.remove(cmpValue);
            pnlFilters.remove(lblRemove);

            // move filler panel
            adjustGridY(fillerPanel, -1);
        }

        // if this filter line became a new field line after
        // the preceeding filter line has been removed
        // add field and match and remove OR label
        public void initNewField(QueryFilter.Match match) {
            // field
            lblField = new JLabel(field.getDisplay());
            pnlFilters.add(lblField, createGridBagConstraints(0));
            // add and set match
            cbxMatch = new FilterMatchComboBox(field.getType() != QueryField.Type.SELECT);
            pnlFilters.add(cbxMatch, createGridBagConstraints(1));
            cbxMatch.setSelectedItem(match);
            // remove OR
            pnlFilters.remove(lblOr);
            lblOr = null;
            or = false;
        }

        public void adjustGridY(int add) {
            gridy+= add;
            adjustGridY(lblField, add);
            adjustGridY(cbxMatch, add);
            adjustGridY(lblOr, add);
            adjustGridY(cmpValue, add);
            adjustGridY(lblRemove, add);
        }

        private GridBagConstraints createGridBagConstraints(int x) {
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridx = x;
            constraints.gridy = gridy;
            constraints.anchor = GridBagConstraints.EAST;
            constraints.insets = insets;
            return constraints;
        }

        private void adjustGridY(Component component, int add) {
            if (component != null) {
                GridBagLayout layout = (GridBagLayout) pnlFilters.getLayout();
                GridBagConstraints gridBagConstraints = layout.getConstraints(component);
                gridBagConstraints.gridy+= add;
                layout.setConstraints(component, gridBagConstraints);
            }
        }
    }

    private class RemoveListener extends MouseAdapter {
        private final FilterLine line;

        public RemoveListener(FilterLine line) {
            this.line = line;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            removeFilterLine(line.getGridY());
        }

    }
}
