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
package org.netbeans.cubeon.commun.ui.internals;

import org.netbeans.cubeon.commun.ui.GroupPanel;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.List;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import org.netbeans.cubeon.commun.ui.ContainerGroup;

import org.netbeans.cubeon.commun.ui.Group;

import org.openide.awt.Mnemonics;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha
 * 
 * this class base on org.netbeans.modules.xml.multiview.ui.SectionContainer
 */
public class ContainerGroupPanel extends javax.swing.JPanel implements GroupPanel {

    private ContainerGroup containerGroup;
    private GroupView groupView;
    private boolean active;
    private int sectionCount = 0;
    private int index;
    private final Image IMAGE_UNSELECTED = ImageUtilities.loadImage("org/netbeans/cubeon/commun/ui/internals/plus.gif"); // NOI18N
    private final Image IMAGE_SELECTED = ImageUtilities.loadImage("org/netbeans/cubeon/commun/ui/internals/minus.gif"); // NOI18N

    public ContainerGroupPanel(GroupView groupView, ContainerGroup containerGroup) {

        this.containerGroup = containerGroup;

        initComponents();
        setGroupView(groupView);
        Mnemonics.setLocalizedText(titleButton, containerGroup.getName());
        titleButton.setToolTipText(containerGroup.getDescription());

        titleButton.addMouseListener(new org.openide.awt.MouseUtils.PopupMouseAdapter() {

            protected void showPopup(java.awt.event.MouseEvent e) {
                JPopupMenu popup = new JPopupMenu();
                popup.add("TODO ADD ITEMS");
                popup.show(titleButton, e.getX(), e.getY());
            }
        });

        if (containerGroup.isFoldable()) {
            foldButton.setSelected(true);
        } else {
            remove(fillerLine);
            remove(fillerEnd);
            remove(foldButton);
        }
        setIcon(true);
        List<Group> groups = containerGroup.getGroups();

        Lookup lookup = Lookups.fixed(groupView);
        for (Group group : groups) {
            GroupPanel groupPanel = group.createGroupPanel(lookup);


            addSection(groupPanel, group.isOpen());
        }

    }

     void setGroupView(GroupView groupView) {
        this.groupView = groupView;
        VisualTheme theme = groupView.getTheme();
        setBackground(theme.getDocumentBackgroundColor());
        headerSeparator.setForeground(theme.getSectionHeaderLineColor());
        titlePanel.setBackground(containerGroup.isFoldable() ? theme.getSectionHeaderColor() : theme.getContainerHeaderColor());
        actionPanel.setBackground(theme.getDocumentBackgroundColor());
        fillerLine.setForeground(theme.getFoldLineColor());
        fillerEnd.setForeground(theme.getFoldLineColor());
    }

    /** Method from NodeSectionPanel interface */
    public void open() {
        if (containerGroup.isFoldable()) {
            foldButton.setSelected(true);
            contentPanel.setVisible(true);
            fillerLine.setVisible(true);
            fillerEnd.setVisible(true);
            setIcon(true);
        }
    }

    /** Method from NodeSectionPanel interface */
    public void scroll() {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                ContainerGroupPanel.this.scrollRectToVisible(new Rectangle(10, ContainerGroupPanel.this.getHeight()));
            }
        });
    }

    /** Method from NodeSectionPanel interface */
    public void setActive(boolean active) {
        assert groupView != null : "to active ContainerGroupPanel must be added to GroupView";
        titlePanel.setBackground(active ? groupView.getTheme().getSectionHeaderActiveColor()
                : (containerGroup.isFoldable() ? groupView.getTheme().getSectionHeaderColor()
                : groupView.getTheme().getContainerHeaderColor()));
        if (active && !this.equals(groupView.getActiveGroupPanel())) {
            groupView.setActiveGroupPanel(this);

        }
        this.active = active;
    }

    /** Method from NodeSectionPanel interface */
    public boolean isActive() {
        return active;
    }

    public void addSection(GroupPanel section, boolean open) {
        addSection(section);
        if (open) {
            section.open();
            section.scroll();
            section.setActive(true);
        }
    }

    /** Method from ContainerPanel interface */
    public void addSection(GroupPanel section) {
        GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = sectionCount;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        contentPanel.add((JPanel) section, gridBagConstraints);
        section.setIndex(sectionCount);

        sectionCount++;
    }

    /** Method from ContainerPanel interface */
    public void removeSection(GroupPanel section) {
        int panelIndex = section.getIndex();
        contentPanel.remove((JPanel) section);

        // the rest components have to be moved up
        java.awt.Component[] components = contentPanel.getComponents();
        java.util.AbstractList removedPanels = new java.util.ArrayList();
        for (int i = 0; i < components.length; i++) {
            if (components[i] instanceof GroupPanel) {
                GroupPanel pan = (GroupPanel) components[i];
                int panIndex = pan.getIndex();
                if (panIndex > panelIndex) {
                    contentPanel.remove((JPanel) pan);
                    pan.setIndex(panIndex - 1);
                    removedPanels.add(pan);
                }
            }
        }
        for (int i = 0; i < removedPanels.size(); i++) {
            GroupPanel pan = (GroupPanel) removedPanels.get(i);
            java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = pan.getIndex();
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.weightx = 1.0;
            //gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 6);
            contentPanel.add((JPanel) pan, gridBagConstraints);
        }

        sectionCount--;
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
        contentPanel = new javax.swing.JPanel();
        actionPanel = new javax.swing.JPanel();
        fillerLine = new javax.swing.JSeparator();
        fillerEnd = new javax.swing.JSeparator();
        titlePanel = new javax.swing.JPanel();
        titleButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        foldButton.setIcon(new javax.swing.ImageIcon(IMAGE_SELECTED)); // NOI18N
        foldButton.setToolTipText(org.openide.util.NbBundle.getMessage(ContainerGroupPanel.class, "HINT_FOLD_BUTTON")); // NOI18N
        foldButton.setBorder(null);
        foldButton.setBorderPainted(false);
        foldButton.setContentAreaFilled(false);
        foldButton.setFocusPainted(false);
        foldButton.setSelectedIcon(new javax.swing.ImageIcon(IMAGE_UNSELECTED)); // NOI18N
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

        contentPanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
        add(contentPanel, gridBagConstraints);

        actionPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 2, 0));
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
        titleButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
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

        if (containerGroup.isFoldable()) {
            if (!foldButton.isSelected()) {
                open();
                foldButton.setSelected(true);
            } else {
                if (isActive()) {
                    foldButton.setSelected(false);
                    contentPanel.setVisible(false);
                    fillerLine.setVisible(false);
                    fillerEnd.setVisible(false);
                    setIcon(false);
                }
            }
        }
        if (!isActive()) {
            setActive(true);
        }
    }

    private void foldButtonActionPerformed(java.awt.event.ActionEvent evt) {

        contentPanel.setVisible(foldButton.isSelected());
        fillerLine.setVisible(foldButton.isSelected());
        fillerEnd.setVisible(foldButton.isSelected());
        setIcon(foldButton.isSelected());
    }
    // Variables declaration - do not modify
    private javax.swing.JPanel actionPanel;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JSeparator fillerEnd;
    private javax.swing.JSeparator fillerLine;
    private javax.swing.JToggleButton foldButton;
    private javax.swing.JSeparator headerSeparator;
    private javax.swing.JButton titleButton;
    private javax.swing.JPanel titlePanel;
    // End of variables declaration

    /** Method from NodeSectionPanel interface */
    public void setIndex(int index) {
        this.index = index;
    }

    /** Method from NodeSectionPanel interface */
    public int getIndex() {
        return index;
    }
    private javax.swing.JButton[] headerButtons;

    public void setHeaderActions(Action[] actions) {
        headerButtons = new javax.swing.JButton[actions.length];
        for (int i = 0; i < actions.length; i++) {
            headerButtons[i] = new javax.swing.JButton(actions[i]);
            headerButtons[i].setMargin(new java.awt.Insets(0, 14, 0, 14));
            headerButtons[i].setOpaque(false);
            actionPanel.add(headerButtons[i]);
        }
    }

    public javax.swing.JButton[] getHeaderButtons() {
        return headerButtons;
    }

    public boolean isFoldable() {
        return containerGroup.isFoldable();
    }

    private void setIcon(boolean opened) {
        java.awt.Image image = null;
        if (opened) {
            //todo add open icon
        } else {
            //todo remove open icon
        }

        if (image != null) {
            titleButton.setIcon(new javax.swing.ImageIcon(image));
        }
    }

    public ContainerGroup getGroup() {
        return containerGroup;
    }
}
