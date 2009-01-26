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
 * TaskEditorPanels.java
 *
 * Created on Jun 7, 2008, 4:49:17 PM
 */
package org.netbeans.cubeon.local.ui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.cubeon.local.LocalTask;
import org.netbeans.cubeon.local.repository.LocalTaskPriorityProvider;
import org.netbeans.cubeon.local.repository.LocalTaskRepository;
import org.netbeans.cubeon.local.repository.LocalTaskStatusProvider;
import org.netbeans.cubeon.local.repository.LocalTaskTypeProvider;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.netbeans.cubeon.tasks.spi.task.TaskPriority;
import org.netbeans.cubeon.tasks.spi.task.TaskStatus;
import org.netbeans.cubeon.tasks.spi.task.TaskType;
import org.openide.awt.HtmlBrowser.URLDisplayer;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 *
 * @author Anuradha G
 */
class TaskEditorPanels extends javax.swing.JPanel {

    private final Set<ChangeListener> listeners = new HashSet<ChangeListener>(1);
    private LocalTask localTask;
    final DocumentListener documentListener = new DocumentListener() {

        public void insertUpdate(DocumentEvent arg0) {
            run();
        }

        public void removeUpdate(DocumentEvent arg0) {
            run();
        }

        public void changedUpdate(DocumentEvent arg0) {
            run();
        }

        private void run() {
            EventQueue.invokeLater(new Runnable() {

                public void run() {
                    fireChangeEvent();
                }
            });

        }
    };
    ItemListener itemListener = new ItemListener() {

        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                EventQueue.invokeLater(new Runnable() {

                    public void run() {
                        fireChangeEvent();
                    }
                });
            }
        }
    };

    /** Creates new form TaskEditorPanels */
    public TaskEditorPanels(LocalTask localTask) {
        this.localTask = localTask;
        initComponents();
        refresh();
    }

    @Override
    public String getName() {
        return localTask != null ? localTask.getName() : "Local Task";//NOI18N
    }

    String getDisplayName() {
        return localTask.getId();
    }

    String getShortDescription() {
        return localTask.getName();
    }

    final void addChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }

    final void removeChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }

    TaskElement save() {

        if (!localTask.getPriority().equals(cmbPriority.getSelectedItem())) {
            localTask.setPriority((TaskPriority) cmbPriority.getSelectedItem());
        }
        if (!localTask.getStatus().equals(cmbStatus.getSelectedItem())) {
            localTask.setStatus((TaskStatus) cmbStatus.getSelectedItem());
        }
        if (!localTask.getDescription().equals(txtDescription.getText().trim())) {
            localTask.setDescription(txtDescription.getText().trim());
        }
        if (!localTask.getType().equals(cmbType.getSelectedItem())) {
            localTask.setType((TaskType) cmbType.getSelectedItem());
        }
        localTask.setUrlString(txtUrl.getText().trim());
        localTask.getTaskRepository().persist(localTask);

        return localTask;
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

    private void showURL() {
        try {
            URLDisplayer.getDefault().showURL(new URL(txtUrl.getText()));

        } catch (MalformedURLException ex) {
            //donothing
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

        pnlUrl = new javax.swing.JPanel();
        UrlTools = new javax.swing.JToolBar();
        txtUrl = new javax.swing.JTextField();
        pnlDescription = new javax.swing.JPanel();
        spDescription = new javax.swing.JScrollPane();
        txtDescription = new javax.swing.JEditorPane();
        lblPriority = new javax.swing.JLabel();
        lblType = new javax.swing.JLabel();
        cmbType = new javax.swing.JComboBox();
        cmbPriority = new javax.swing.JComboBox();
        cmbStatus = new javax.swing.JComboBox();
        lblStatus = new javax.swing.JLabel();

        pnlUrl.setBackground(new java.awt.Color(255, 255, 255));

        UrlTools.setFloatable(false);
        UrlTools.setRollover(true);
        UrlTools.setOpaque(false);

        org.jdesktop.layout.GroupLayout pnlUrlLayout = new org.jdesktop.layout.GroupLayout(pnlUrl);
        pnlUrl.setLayout(pnlUrlLayout);
        pnlUrlLayout.setHorizontalGroup(
            pnlUrlLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlUrlLayout.createSequentialGroup()
                .addContainerGap()
                .add(txtUrl, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 457, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(UrlTools, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        pnlUrlLayout.setVerticalGroup(
            pnlUrlLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlUrlLayout.createSequentialGroup()
                .add(pnlUrlLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(UrlTools, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(txtUrl, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlDescription.setBackground(new java.awt.Color(255, 255, 255));

        spDescription.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        spDescription.setViewportView(txtDescription);

        org.jdesktop.layout.GroupLayout pnlDescriptionLayout = new org.jdesktop.layout.GroupLayout(pnlDescription);
        pnlDescription.setLayout(pnlDescriptionLayout);
        pnlDescriptionLayout.setHorizontalGroup(
            pnlDescriptionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 489, Short.MAX_VALUE)
            .add(pnlDescriptionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlDescriptionLayout.createSequentialGroup()
                    .addContainerGap()
                    .add(spDescription, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 469, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        pnlDescriptionLayout.setVerticalGroup(
            pnlDescriptionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 281, Short.MAX_VALUE)
            .add(pnlDescriptionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlDescriptionLayout.createSequentialGroup()
                    .add(0, 0, 0)
                    .add(spDescription, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        setBackground(new java.awt.Color(255, 255, 255));

        lblPriority.setText(NbBundle.getMessage(TaskEditorPanels.class, "TaskEditorPanels.lblPriority.text")); // NOI18N

        lblType.setText(NbBundle.getMessage(TaskEditorPanels.class, "TaskEditorPanels.lblType.text")); // NOI18N

        lblStatus.setText(NbBundle.getMessage(TaskEditorPanels.class, "TaskEditorPanels.lblStatus.text")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblPriority)
                    .add(lblType))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(cmbPriority, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(cmbType, 0, 118, Short.MAX_VALUE))
                .add(48, 48, 48)
                .add(lblStatus)
                .add(18, 18, 18)
                .add(cmbStatus, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(new java.awt.Component[] {cmbStatus, cmbType}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblType)
                    .add(lblStatus)
                    .add(cmbStatus, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(cmbType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblPriority)
                    .add(cmbPriority, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToolBar UrlTools;
    private javax.swing.JComboBox cmbPriority;
    private javax.swing.JComboBox cmbStatus;
    private javax.swing.JComboBox cmbType;
    private javax.swing.JLabel lblPriority;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblType;
    private javax.swing.JPanel pnlDescription;
    private javax.swing.JPanel pnlUrl;
    private javax.swing.JScrollPane spDescription;
    private javax.swing.JEditorPane txtDescription;
    private javax.swing.JTextField txtUrl;
    // End of variables declaration//GEN-END:variables
    // End of variables declaration

    public List<Action> getActions() {
        return new ArrayList<Action>(0);
    }

    void refresh() {

        txtDescription.getDocument().removeDocumentListener(documentListener);
        txtUrl.getDocument().removeDocumentListener(documentListener);


        cmbPriority.removeItemListener(itemListener);
        cmbStatus.removeItemListener(itemListener);
        cmbType.removeItemListener(itemListener);


        txtDescription.setText(localTask.getDescription());
        txtUrl.setText(localTask.getUrlString());

        cmbPriority.removeAllItems();
        LocalTaskRepository taskRepository = localTask.getTaskRepository().getLookup().lookup(LocalTaskRepository.class);
        LocalTaskPriorityProvider ltpp = taskRepository.getLocalTaskPriorityProvider();
        for (TaskPriority priority : ltpp.getTaskPriorities()) {
            cmbPriority.addItem(priority);
        }
        cmbPriority.setSelectedItem(localTask.getPriority());

        cmbStatus.removeAllItems();
        LocalTaskStatusProvider statusProvider = taskRepository.getLocalTaskStatusProvider();
        for (TaskStatus status : statusProvider.getStatusList()) {
            cmbStatus.addItem(status);
        }
        cmbStatus.setSelectedItem(localTask.getStatus());

        cmbType.removeAllItems();

        LocalTaskTypeProvider localTaskTypeProvider = taskRepository.getLocalTaskTypeProvider();
        for (TaskType type : localTaskTypeProvider.getTaskTypes()) {
            cmbType.addItem(type);
        }

        cmbType.setSelectedItem(localTask.getType());


        txtDescription.getDocument().addDocumentListener(documentListener);
        txtUrl.getDocument().addDocumentListener(documentListener);


        cmbPriority.addItemListener(itemListener);
        cmbStatus.addItemListener(itemListener);
        cmbType.addItemListener(itemListener);
    }

    JComponent getAttributesComponent() {
        return this;
    }

    JComponent getURLComponent() {
        return pnlUrl;
    }

    JComponent getDescriptionComponent() {
        return pnlDescription;
    }

    Action[] getURLToolbarActions() {
        return new Action[]{new AbstractAction(NbBundle.getMessage(TaskEditorPanels.class, "LBL_Open_Url"),
                    new ImageIcon(ImageUtilities.loadImage("org/netbeans/cubeon/local/web.png"))) {

                public void actionPerformed(ActionEvent e) {
                    showURL();
                }
            }};
    }

    public String getAttributesHtmlSummary() {
        StringBuffer buffer = new StringBuffer("<html>");

        buffer.append("<font color=\"#808080\">");
        buffer.append("  ");

        buffer.append(NbBundle.getMessage(TaskEditorPanels.class, "TaskEditorPanels.lblPriority.text")).append(" ");
        buffer.append(localTask.getPriority());


        buffer.append("   ,   ").append(NbBundle.getMessage(TaskEditorPanels.class, "TaskEditorPanels.lblType.text")).append(" ");
        buffer.append(localTask.getType());


        buffer.append("   ,   ").append(NbBundle.getMessage(TaskEditorPanels.class, "TaskEditorPanels.lblStatus.text")).append(" ");
        buffer.append(localTask.getStatus());

        buffer.append("</html>");
        return buffer.toString();
    }
}
