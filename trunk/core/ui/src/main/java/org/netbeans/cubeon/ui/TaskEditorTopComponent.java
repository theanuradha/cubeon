/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.cubeon.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;

import java.awt.Insets;
import java.io.IOException;
import java.util.List;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.cubeon.context.api.TaskContextManager;
import org.netbeans.cubeon.tasks.core.api.CubeonContext;
import org.netbeans.cubeon.tasks.core.api.CubeonContextListener;
import org.netbeans.cubeon.tasks.core.api.TasksFileSystem;
import org.netbeans.cubeon.tasks.spi.Notifier;
import org.netbeans.cubeon.tasks.spi.Notifier.NotifierReference;
import org.netbeans.cubeon.tasks.spi.task.TaskEditorProvider;
import org.netbeans.cubeon.tasks.spi.task.TaskEditorProvider.EditorAttributeHandler;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.netbeans.cubeon.tasks.spi.task.TaskElementChangeAdapter;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.ui.internals.TaskElementNode;
import org.netbeans.cubeon.ui.taskelemet.SynchronizeTaskAction;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.cookies.SaveCookie;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.ImageUtilities;
import org.openide.windows.TopComponent;

/**
 * Top component which displays something.
 */
final class TaskEditorTopComponent extends TopComponent implements SaveCookie, ChangeListener, CubeonContextListener {

    private static final String PREFERRED_ID = "TaskEditorTopComponent";
    private static final long serialVersionUID = 1L;
    private final TaskElement element;
    private final TaskElementNode editorNode;
    private final EditorAttributeHandler eah;
    private final Notifier<TaskElementChangeAdapter> extension;
    private NotifierReference<TaskElementChangeAdapter> notifierReference;
    private final TaskEditorFactoryImpl factoryImpl;
    private final CubeonContext context;
    private final TaskContextManager contextManager;

    TaskEditorTopComponent(TaskEditorFactoryImpl factoryImpl, final TaskElement element) {
        this.element = element;
        this.factoryImpl = factoryImpl;
        this.context = Lookup.getDefault().lookup(CubeonContext.class);
        contextManager = Lookup.getDefault().lookup(TaskContextManager.class);
        initComponents();
        jButton1.setAction(SynchronizeTaskAction.createSynchronizeTaskAction(element));
        jButton1.setText(null);
        jToolBar1.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        Lookup lookup = element.getLookup();

        TaskEditorProvider editorProvider = lookup.lookup(TaskEditorProvider.class);
        eah = editorProvider.createEditorAttributeHandler();

        _updateNameInEDT(eah.getName());
        extension = element.getNotifier();
        setIcon(element.getImage());
        TaskRepository taskRepository = element.getTaskRepository();

        lblHeader.setIcon(new ImageIcon(taskRepository.getImage()));
        lblHeader.setText(eah.getDisplayName());
        buildEditor();

        List<Action> actions = eah.getActions();
        for (Action action : actions) {
            if (action == null) {
                jToolBar1.addSeparator();
            } else {
                JButton button = new JButton(action);
                button.setText(null);
                button.setOpaque(false);
                jToolBar1.add(button);
            }
        }

        setActivatedNodes(new Node[]{editorNode = TaskElementNode.createNode(Children.LEAF, element, this)});
        eah.addChangeListener(this);

        notifierReference = extension.add(new TaskElementChangeAdapter() {

            @Override
            public void nameChenged() {
                EventQueue.invokeLater(new Runnable() {

                    public void run() {
                        _updateNameInEDT(eah.getName());
                        lblHeader.setText(eah.getDisplayName());
                    }
                });

            }

            @Override
            public void typeChenged() {
                EventQueue.invokeLater(new Runnable() {

                    public void run() {
                        _updateNameInEDT(eah.getName());
                        setIcon(element.getImage());
                    }
                });

            }
        });
        _focus();
        context.addContextListener(this);
    }

    private void _focus() {
        if (element.equals(context.getActive())) {
            focus.setToolTipText(NbBundle.getMessage(TaskEditorTopComponent.class, "LBL_InactiveTask"));
            focus.setIcon(new ImageIcon(ImageUtilities.loadImage("org/netbeans/cubeon/ui/focus_off.png")));
        } else {
            focus.setToolTipText(NbBundle.getMessage(TaskEditorTopComponent.class, "LBL_ActiveTask"));
            focus.setIcon(new ImageIcon(ImageUtilities.loadImage("org/netbeans/cubeon/ui/focus_on.png")));
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.JSeparator separator = new javax.swing.JSeparator();
        base = new javax.swing.JPanel();
        header = new javax.swing.JPanel();
        lblHeader = new javax.swing.JLabel();
        jToolBar1 = new javax.swing.JToolBar();
        focus = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        base.setBackground(new java.awt.Color(255, 255, 255));
        base.setLayout(new java.awt.BorderLayout(0, 2));

        header.setBackground(new java.awt.Color(255, 255, 255));
        header.setMaximumSize(new java.awt.Dimension(32767, 34));
        header.setMinimumSize(new java.awt.Dimension(0, 34));
        header.setPreferredSize(new java.awt.Dimension(0, 34));

        lblHeader.setFont(new java.awt.Font("Tahoma", 1, 13));
        lblHeader.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/cubeon/ui/repository.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(lblHeader, "Task Element Name"); // NOI18N

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setOpaque(false);
        jToolBar1.setPreferredSize(new java.awt.Dimension(49, 23));

        focus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/cubeon/ui/focus_off.png"))); // NOI18N
        focus.setToolTipText(NbBundle.getMessage(TaskEditorTopComponent.class, "TaskEditorTopComponent.focus.toolTipText","-")); // NOI18N
        focus.setFocusable(false);
        focus.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        focus.setOpaque(false);
        focus.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        focus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                focusActionPerformed(evt);
            }
        });
        jToolBar1.add(focus);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/cubeon/ui/refresh.png"))); // NOI18N
        jButton1.setToolTipText(NbBundle.getMessage(TaskEditorTopComponent.class, "TaskEditorTopComponent.jButton1.toolTipText","-")); // NOI18N
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setOpaque(false);
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton1);

        org.jdesktop.layout.GroupLayout headerLayout = new org.jdesktop.layout.GroupLayout(header);
        header.setLayout(headerLayout);
        headerLayout.setHorizontalGroup(
            headerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, headerLayout.createSequentialGroup()
                .addContainerGap()
                .add(lblHeader, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 485, Short.MAX_VALUE)
                .add(18, 18, 18)
                .add(jToolBar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 188, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        headerLayout.setVerticalGroup(
            headerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(lblHeader, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
            .add(jToolBar1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
        );

        base.add(header, java.awt.BorderLayout.NORTH);

        add(base, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void focusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_focusActionPerformed
        if (element.equals(context.getActive())) {
            context.setActive(null);
            TasksFileSystem fileSystem = Lookup.getDefault().lookup(TasksFileSystem.class);
            TaskExplorerTopComponent.findInstance().selectView(fileSystem.getFilesystemView());


        } else {
            context.setActive(element);
            TaskExplorerTopComponent.findInstance().selectView(contextManager.getContextView());
        }
    }//GEN-LAST:event_focusActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel base;
    private javax.swing.JButton focus;
    private javax.swing.JPanel header;
    private javax.swing.JButton jButton1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblHeader;
    // End of variables declaration//GEN-END:variables

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }

    @Override
    protected String preferredID() {
        return PREFERRED_ID;
    }

    public void save() throws IOException {
        eah.save();
        editorNode.setModified(false);
        _updateNameInEDT(eah.getName());
    }

    public void refresh() {
        eah.refresh();
        editorNode.setModified(false);
        _updateNameInEDT(eah.getName());
    }

    public void stateChanged(ChangeEvent e) {
        _updateNameInEDT(eah.getName() + "*");
        editorNode.setModified(true);
    }

    @Override
    protected void componentClosed() {
        extension.remove(notifierReference);

        editorNode.destroy();

        context.removeContextListener(this);
        factoryImpl.notifyRemove(element);
    }

    @Override
    public boolean canClose() {
        if (editorNode.isModified()) {
            NotifyDescriptor d =
                    new NotifyDescriptor.Confirmation(NbBundle.getMessage(TaskEditorTopComponent.class,
                    "LBL_Task_Modified", new Object[]{"'" + element.getId() + " : " + element.getName() + "'"}),
                    NbBundle.getMessage(TaskEditorTopComponent.class, "CTL_Question"),
                    NotifyDescriptor.YES_NO_CANCEL_OPTION);
            Object notify = DialogDisplayer.getDefault().notify(d);
            if (notify == NotifyDescriptor.OK_OPTION) {
                try {
                    save();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            } else if (notify == NotifyDescriptor.NO_OPTION) {

                editorNode.setModified(false);
            } else if (notify == NotifyDescriptor.CANCEL_OPTION) {
                return false;
            }
        }

        return true;
    }

    private void buildEditor() {
        JComponent[] component = eah.getComponent();

        if (component.length == 1) {
            base.add(component[0], BorderLayout.CENTER);
        } else {
            Insets oldInsets = UIManager.getInsets("TabbedPane.contentBorderInsets");
            // bottom insets is 1 because the tabs are bottom aligned
            UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));
            JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM, JTabbedPane.SCROLL_TAB_LAYOUT);
            UIManager.put("TabbedPane.contentBorderInsets", oldInsets);

            base.add(tabbedPane, BorderLayout.CENTER);
            for (JComponent jc : component) {
                tabbedPane.addTab(jc.getName(), jc);
            }
        }

    }

    public EditorAttributeHandler getAttributeHandler() {
        return eah;
    }

    public TaskElement gettTaskElement() {
        return element;
    }

    private void _updateNameInEDT(final String name) {
        EventQueue.invokeLater(new Runnable() {

            public void run() {
                //TODO : move to options
                if (name.length() > 30) {
                    setName(name.substring(0, 27) + "...");
                } else {
                    setName(name);
                }
                setToolTipText(editorNode.getShortDescription());
            }
        });

    }

    public void taskActivated(TaskElement element) {
        if (this.element.equals(element)) {
            _focus();
        }
    }

    public void taskDeactivated(TaskElement element) {
        if (this.element.equals(element)) {
            _focus();
        }
    }
}
