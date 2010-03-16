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
import java.net.MalformedURLException;
import java.net.URL;
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
import org.openide.awt.HtmlBrowser.URLDisplayer;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.ImageUtilities;
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
        beanTreeView.setRootVisible(false);
        beanTreeView.setPopupAllowed(false);
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
        Children.Array myArray = new Children.Array();
        Children.Array publicArray = new Children.Array();
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
        Node my = new AbstractNode(myArray) {

            @Override
            public String getDisplayName() {
                return "My Repository Filters";
            }

            @Override
            public Image getOpenedIcon(int type) {
                return getIcon(type);
            }

            @Override
            public Image getIcon(int type) {
                return jiraQuerySupport.getJiraTaskRepository().getImage();
            }
        };
        array.add(new Node[]{
                    my, new AbstractNode(publicArray) {

                @Override
                public String getDisplayName() {
                    return "Public Repository Filters";
                }

                @Override
                public Image getOpenedIcon(int type) {
                    return getIcon(type);
                }

                @Override
                public Image getIcon(int type) {
                    return jiraQuerySupport.getJiraTaskRepository().getImage();
                }
            }
                });

        for (JiraFilter jiraFilter : filters) {
            if (jiraQuerySupport.getJiraTaskRepository().getUserName().equals(
                    jiraFilter.getAuthor())) {
                myArray.add(new Node[]{new FilterNode(jiraFilter)});
            } else {
                publicArray.add(new Node[]{new FilterNode(jiraFilter)});
            }
        }
        explorerManager.setRootContext(root);
        beanTreeView.expandNode(my);
        explorerManager.setExploredContext(my);

    }

    public void setQuery(JiraFilterQuery query) {
        this.query = query;
        if (query != null && query.getFilter() != null) {
            Node rootContext = explorerManager.getRootContext();
            Node[] nodes = rootContext.getChildren().getNodes(true);
            for (Node node : nodes) {
                JiraFilter filter = node.getLookup().lookup(JiraFilter.class);
                if (query.getFilter().equals(filter)) {
                    explorerManager.setExploredContext(node, new Node[]{node});
                    break;
                }
            }
        }
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
            return ImageUtilities.loadImage("org/netbeans/cubeon/jira/query/ui/filter.png");
        }
    }

    private void updateFilters() {
        btnUpdateFilters.setEnabled(false);
        RequestProcessor.getDefault().post(new Runnable() {

            public void run() {
                jiraQuerySupport.getJiraTaskRepository().updateFilters();
                loadFilters();
                setQuery(query);
                btnUpdateFilters.setEnabled(true);

            }
        });
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
        btnUpdateFilters = new javax.swing.JButton();
        lblNotice = new javax.swing.JLabel();

        jScrollPane1.setBorder(javax.swing.BorderFactory.createEtchedBorder(null, javax.swing.UIManager.getDefaults().getColor("CheckBoxMenuItem.selectionBackground")));

        btnUpdateFilters.setText(NbBundle.getMessage(JiraFilterQueryEditor.class, "JiraFilterQueryEditor.btnUpdateFilters.text","-")); // NOI18N
        btnUpdateFilters.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateFiltersActionPerformed(evt);
            }
        });

        lblNotice.setText(org.openide.util.NbBundle.getMessage(JiraFilterQueryEditor.class, "JiraFilterQueryEditor.lblNotice.text")); // NOI18N
        lblNotice.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblNoticeMouseClicked(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, lblNotice, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, btnUpdateFilters))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(lblNotice, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 47, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btnUpdateFilters)
                .add(11, 11, 11))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnUpdateFiltersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateFiltersActionPerformed

        updateFilters();
    }//GEN-LAST:event_btnUpdateFiltersActionPerformed

    private void lblNoticeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblNoticeMouseClicked
       try {
            URLDisplayer.getDefault().
                    showURL(new URL(
                    jiraQuerySupport.getJiraTaskRepository().getURL()+
                    "/secure/IssueNavigator.jspa?mode=show&createNew=true"));//NOI18N
        } catch (MalformedURLException ex) {
            //ignore
        }
    }//GEN-LAST:event_lblNoticeMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnUpdateFilters;
    private javax.swing.JLabel lblNotice;
    // End of variables declaration//GEN-END:variables
}
