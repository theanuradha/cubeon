/*
 *  Copyright 2009 Anuradha.
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
package org.netbeans.cubeon.common.ui.internals;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import org.netbeans.cubeon.common.ui.GroupPanel;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;
import org.netbeans.cubeon.common.ui.ComponentGroup;
import org.netbeans.cubeon.common.ui.Group;


import org.openide.util.ImageUtilities;
import org.openide.util.actions.Presenter;

/**
 *
 * @author Anuradha
 *
 * this class base on org.netbeans.modules.xml.multiview.ui.SectionPanel
 */
public class ComponentGroupPanel extends javax.swing.JPanel implements GroupPanel {

    private GroupView groupView;
    private boolean active;
    private JComponent innerPanel;
    private ComponentGroup componentGroup;
    private int index;
    private final Image IMAGE_UNSELECTED = ImageUtilities.loadImage("org/netbeans/cubeon/common/ui/internals/plus.gif"); // NOI18N
    private final Image IMAGE_SELECTED = ImageUtilities.loadImage("org/netbeans/cubeon/common/ui/internals/minus.gif"); // NOI18N
    private FocusListener sectionFocusListener = new FocusAdapter() {

        @Override
        public void focusGained(FocusEvent e) {
            setActive(true);
        }
    };

    public ComponentGroupPanel(GroupView groupView, ComponentGroup componentGroup) {
        this(groupView, componentGroup, false, false);
    }

    public ComponentGroupPanel(GroupView groupView, ComponentGroup componentGroup, boolean open) {
        this(groupView, componentGroup, open, false);
    }

    public ComponentGroupPanel(GroupView groupView, final ComponentGroup componentGroup,
            boolean autoExpand, boolean addFocusListenerToButton) {

        this.componentGroup = componentGroup;
        initComponents();
        setGroupView(groupView);
        foldButton.setIcon(new javax.swing.ImageIcon(IMAGE_UNSELECTED));
        foldButton.setSelectedIcon(new javax.swing.ImageIcon(IMAGE_SELECTED));

        titleButton.setText(componentGroup.getName());
        titleButton.setToolTipText(componentGroup.getDescription());
        titleButton.addMouseListener(new org.openide.awt.MouseUtils.PopupMouseAdapter() {

            protected void showPopup(java.awt.event.MouseEvent e) {
                if (!ComponentGroupPanel.this.isActive()) {
                    ComponentGroupPanel.this.setActive(true);
                }
                JPopupMenu menu = new JPopupMenu();
                Action[] haeaderActions = componentGroup.getHaeaderActions();
                boolean sepetatorAdded = false;
                for (Action action : haeaderActions) {
                    //check null and addSeparator
                    if (action == null) {
                        //check sepetatorAdd to prevent adding duplicate Separators
                        if (!sepetatorAdded) {
                            //mark sepetatorAdd to true
                            sepetatorAdded = true;
                            menu.addSeparator();

                        }
                        continue;
                    }
                    //mark sepetatorAdd to false
                    sepetatorAdded = false;
                    //check for Presenter.Popup
                    if (action instanceof Presenter.Popup) {
                        Presenter.Popup popup = (Presenter.Popup) action;
                        menu.add(popup.getPopupPresenter());
                        continue;
                    }

                    menu.add(new JMenuItem(action));
                }
                menu.show(titleButton, e.getX(), e.getY());
            }
        });
        if (autoExpand) {
            open();
        }
        if (addFocusListenerToButton) {
            titleButton.addFocusListener(sectionFocusListener);
        }
        setToolbarActions(componentGroup.getToolbarActions());
    }

    void setGroupView(GroupView groupView) {
        this.groupView = groupView;
        VisualTheme theme = groupView.getTheme();
        headerSeparator.setForeground(theme.getSectionHeaderLineColor());
        fillerLine.setForeground(theme.getFoldLineColor());
        fillerEnd.setForeground(theme.getFoldLineColor());
        fillerLine.setVisible(false);
        fillerEnd.setVisible(false);
        setBackground(theme.getDocumentBackgroundColor());
        titlePanel.setBackground(theme.getSectionHeaderColor());
        actionPanel.setBackground(theme.getDocumentBackgroundColor());
    }

    public GroupView getGroupView() {
        return groupView;
    }

    protected void openInnerPanel() {

        if (innerPanel != null) {
            return;
        }
        innerPanel = createInnerpanel();
        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
        fillerLine.setVisible(true);
        fillerEnd.setVisible(true);
        innerPanel.addFocusListener(sectionFocusListener);
        add(innerPanel, gridBagConstraints);
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                ComponentGroupPanel.this.scrollRectToVisible(new Rectangle(10, ComponentGroupPanel.this.getHeight()));
            }
        });
        assert groupView != null : "to active ContainerGroupPanel must be added to GroupView";
        innerPanel.setBackground(
                active ? groupView.getTheme().getSectionActiveBackgroundColor() : groupView.getTheme().getDocumentBackgroundColor());
    }

    protected JComponent createInnerpanel() {
        return componentGroup.getComponent();
    }

    protected void closeInnerPanel() {
        if (innerPanel != null) {
            innerPanel.removeFocusListener(sectionFocusListener);
            remove(innerPanel);
            innerPanel = null;
        }
        fillerLine.setVisible(false);
        fillerEnd.setVisible(false);
    }

    public String getTitle() {
        return componentGroup.getName();
    }

    public Group getGroup() {
        return componentGroup;
    }

    /**
     * Method from NodeSectionPanel interface
     */
    public void open() {
        foldButton.setSelected(true);
        openInnerPanel();
    }

    /**
     * Method from NodeSectionPanel interface
     */
    public void scroll() {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                ComponentGroupPanel.this.scrollRectToVisible(new Rectangle(10, ComponentGroupPanel.this.getHeight()));
            }
        });
    }

    /**
     * Method from NodeSectionPanel interface
     */
    public void setActive(boolean active) {
        assert groupView != null : "to active ContainerGroupPanel must be added to GroupView";
        titlePanel.setBackground(
                active ? groupView.getTheme().getSectionHeaderActiveColor() : groupView.getTheme().getSectionHeaderColor());

        if (innerPanel != null) {
            innerPanel.setBackground(
                    active ? groupView.getTheme().getSectionActiveBackgroundColor() : groupView.getTheme().getDocumentBackgroundColor());
        }

        if (active && !this.equals(groupView.getActiveGroupPanel())) {

            groupView.setActiveGroupPanel(this);

        }
        this.active = active;
    }

    /**
     * Method from NodeSectionPanel interface
     */
    public boolean isActive() {
        return active;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        foldButton = new javax.swing.JToggleButton();
        headerSeparator = new javax.swing.JSeparator();
        actionPanel = new javax.swing.JPanel();
        fillerLine = new javax.swing.JSeparator();
        fillerEnd = new javax.swing.JSeparator();
        titlePanel = new javax.swing.JPanel();
        titleButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        foldButton.setToolTipText(org.openide.util.NbBundle.getMessage(ComponentGroupPanel.class, "HINT_FOLD_BUTTON")); // NOI18N
        foldButton.setBorder(null);
        foldButton.setBorderPainted(false);
        foldButton.setContentAreaFilled(false);
        foldButton.setFocusPainted(false);
        foldButton.setFocusable(false);
        foldButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                foldButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 2, 0, 2);
        add(foldButton, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
        add(headerSeparator, gridBagConstraints);

        actionPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 2, 0));
        actionPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 8));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(actionPanel, gridBagConstraints);

        fillerLine.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        add(fillerLine, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 2);
        add(fillerEnd, gridBagConstraints);

        titlePanel.setLayout(new java.awt.BorderLayout());

        titleButton.setFont(titleButton.getFont().deriveFont(titleButton.getFont().getStyle() | java.awt.Font.BOLD, titleButton.getFont().getSize()));
        titleButton.setBorderPainted(false);
        titleButton.setContentAreaFilled(false);
        titleButton.setFocusPainted(false);
        titleButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        titleButton.setMargin(new java.awt.Insets(0, 4, 0, 4));
        titleButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                titleButtonActionPerformed(evt);
            }
        });
        titlePanel.add(titleButton, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(titlePanel, gridBagConstraints);
    }// </editor-fold>

    private void titleButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (!foldButton.isSelected()) {
            openInnerPanel();
            foldButton.setSelected(true);
        } else {
            if (isActive()) {
                closeInnerPanel();
                foldButton.setSelected(false);
            }
        }
        if (!isActive()) {
            setActive(true);
        }

    }

    private void foldButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (foldButton.isSelected()) {
            openInnerPanel();
        } else {
            closeInnerPanel();
        }
    }
    // Variables declaration - do not modify
    private javax.swing.JPanel actionPanel;
    private javax.swing.JSeparator fillerEnd;
    private javax.swing.JSeparator fillerLine;
    private javax.swing.JToggleButton foldButton;
    private javax.swing.JSeparator headerSeparator;
    private javax.swing.JButton titleButton;
    private javax.swing.JPanel titlePanel;
    // End of variables declaration

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    void setToolbarActions(Action[] actions) {

        for (int i = 0; i < actions.length; i++) {
            final javax.swing.JButton headerButton = new javax.swing.JButton(actions[i]);
            headerButton.setBorder(null);
            headerButton.setBorderPainted(false);
            headerButton.setContentAreaFilled(false);

            headerButton.setFocusable(false);
            //remove text from toolbar actions
            headerButton.setToolTipText(headerButton.getText());
            headerButton.setText(null);
            headerButton.addMouseListener(new MouseInputAdapter() {

                @Override
                public void mouseEntered(MouseEvent e) {
                    headerButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    headerButton.setCursor(Cursor.getDefaultCursor());
                }

            });
            actionPanel.add(headerButton);
        }
    }

    protected JComponent getFillerLine() {
        return fillerLine;
    }

    protected JComponent getFillerEnd() {
        return fillerEnd;
    }

    protected JToggleButton getFoldButton() {
        return foldButton;
    }

    protected JSeparator getHeaderSeparator() {
        return headerSeparator;
    }

    protected JButton getTitleButton() {
        return titleButton;
    }
}
