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

/*
 * JiraFilterQueryEditor.java
 *
 * Created on Jul 9, 2008, 3:27:09 PM
 */
package org.netbeans.cubeon.jira.query.ui;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.cubeon.jira.query.AbstractJiraQuery;
import org.netbeans.cubeon.jira.query.JiraFilterQuery;
import org.netbeans.cubeon.jira.query.JiraQuerySupport;
import org.netbeans.cubeon.jira.repository.attributes.JiraFilter;
import org.netbeans.cubeon.tasks.spi.query.TaskQuery;
import org.netbeans.cubeon.tasks.spi.query.TaskQuerySupportProvider;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha
 */
public class JiraFilterQueryEditor extends javax.swing.JPanel implements ExplorerManager.Provider, TaskQuerySupportProvider.ConfigurationHandler {

    private JiraQuerySupport jiraQuerySupport;
    private JiraFilterQuery query;
    private BeanTreeView beanTreeView = new BeanTreeView();
    private ExplorerManager explorerManager = new ExplorerManager();

    /** Creates new form JiraFilterQueryEditor */
    public JiraFilterQueryEditor(JiraQuerySupport jiraQuerySupport) {
        initComponents();
        this.jiraQuerySupport = jiraQuerySupport;
        explorerManager.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
                    fireChangeEvent();
                }
            }
        });
        loadFilters();

    }

    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }

    private void loadFilters() {
        List<JiraFilter> filters = jiraQuerySupport.getJiraTaskRepository().getRepositoryAttributes().getFilters();
        Children.Array array = new Children.Array();
        AbstractNode root = new AbstractNode(array) {

            @Override
            public Image getOpenedIcon(int type) {
                return getIcon(type);
            }

            @Override
            public Image getIcon(int type) {
                return jiraQuerySupport.getJiraTaskRepository().getImage();
            }

        };
        root.setDisplayName("Jira Repository Filters");


        for (JiraFilter jiraFilter : filters) {
            array.add(new Node[]{new FilterNode(jiraFilter)});

        }
        explorerManager.setRootContext(root);
    }

    public void setQuery(JiraFilterQuery query) {
        this.query = query;
    }

    private JiraFilter getSelectedFilter() {
        Node[] selectedNodes = explorerManager.getSelectedNodes();
        if (selectedNodes.length > 0) {
            Node node = selectedNodes[0];
            JiraFilter filter = node.getLookup().lookup(JiraFilter.class);
            return filter;
        }
        return null;
    }

    public TaskQuery getTaskQuery() {
        if (query == null) {
            query = (JiraFilterQuery) jiraQuerySupport.createTaskQuery(AbstractJiraQuery.Type.FILTER);
        }
        query.setFilter(getSelectedFilter());
        return query;
    }
    private final Set<ChangeListener> listeners = new HashSet<ChangeListener>(1);

    public final void addChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }

    public final void removeChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }

    public boolean isValidConfiguration() {

        return getSelectedFilter() != null;
    }

    final void fireChangeEvent() {
        Iterator<ChangeListener> it;
        synchronized (listeners) {
            it = new HashSet<ChangeListener>(listeners).iterator();
        }
        ChangeEvent ev = new ChangeEvent(this);
        while (it.hasNext()) {
            it.next().stateChanged(ev);
        }
    }

    public JComponent getComponent() {
        return this;
    }

    private class FilterNode extends AbstractNode {

        private JiraFilter filter;

        public FilterNode(JiraFilter filter) {
            super(Children.LEAF, Lookups.fixed(filter));
            this.filter = filter;
            setDisplayName(filter.getName());
            setShortDescription(filter.getDescription());
        }

        @Override
        public Image getIcon(int type) {
            return  Utilities.loadImage("org/netbeans/cubeon/jira/query/ui/filter.png");
        }

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.JScrollPane jScrollPane1 = beanTreeView;

        jScrollPane1.setBorder(javax.swing.BorderFactory.createEtchedBorder(null, javax.swing.UIManager.getDefaults().getColor("CheckBoxMenuItem.selectionBackground")));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 332, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
