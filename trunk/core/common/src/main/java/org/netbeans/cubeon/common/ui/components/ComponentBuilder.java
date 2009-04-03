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
package org.netbeans.cubeon.common.ui.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.basic.BasicComboPopup;

/**
 *
 * @author Anuradha
 */
public abstract class ComponentBuilder {

    private int componentPreferredWidth = 160;
    private int lablePreferredWidth = 75;
    private int componentHight = 20;
    private boolean notifyMode;

    public int getLablePreferredWidth() {
        return lablePreferredWidth;
    }

    public void setLablePreferredWidth(int lablePreferredWidth) {
        this.lablePreferredWidth = lablePreferredWidth;
    }

    public int getComponentPreferredWidth() {
        return componentPreferredWidth;
    }

    public void setComponentPreferredWidth(int componentPreferredWidth) {
        this.componentPreferredWidth = componentPreferredWidth;
    }

    public int getComponentHight() {
        return componentHight;
    }

    public void setComponentHight(int componentHight) {
        this.componentHight = componentHight;
    }

    public JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setToolTipText(text);

        label.setPreferredSize(new Dimension(lablePreferredWidth,
                componentHight));
        return label;
    }

    public JLabel createLabelField(String text) {
        JLabel labelField = new JLabel(text);
        labelField.setPreferredSize(new Dimension(componentPreferredWidth, componentHight));

        return labelField;
    }

    public JTextField createTextField() {
        JTextField textField = new JTextField();
        textField.setPreferredSize(new Dimension(componentPreferredWidth, componentHight));
        textField.getDocument().addDocumentListener(documentListener);
        return textField;
    }

    public JEditorPane createEditorField() {

        JEditorPane textField = new JEditorPane();
        textField.setPreferredSize(new Dimension(componentPreferredWidth, componentHight * 3));
        textField.getDocument().addDocumentListener(documentListener);
        return textField;
    }

    public JScrollPane addToScrollPane(JComponent component) {
    
        JScrollPane scrollPane = new JScrollPane(component);
     
        return scrollPane;
    }
    public JScrollPane addToScrollPane(JList component) {
        JPanel container =new JPanel(new BorderLayout());
        container.add(component,BorderLayout.CENTER);
        JScrollPane scrollPane = new JScrollPane(container);
        scrollPane.setPreferredSize(new Dimension(componentPreferredWidth, componentHight * 4));
        return scrollPane;
    }

    public JComponent createCheckbox() {
        JCheckBox checkBox = new JCheckBox();
        checkBox.setPreferredSize(new Dimension(componentPreferredWidth, componentHight));
        checkBox.addActionListener(actionListener);
        checkBox.setOpaque(false);
        return checkBox;
    }

    public JTextField createEmptyField() {
        JTextField textField = new JTextField();
        textField.setPreferredSize(new Dimension(componentPreferredWidth, componentHight));
        textField.setEditable(false);
        textField.setBorder(null);
        textField.setOpaque(false);
        return textField;
    }

    public JComboBox createComboBox() {
        JComboBox comboBox = new JComboBox() {

            private static final int PADDING_RIGHT = 7;
            private static final int PADDING_HEIGHT = 2;

            @Override
            public void firePopupMenuWillBecomeVisible() {
                resizePopup();
                super.firePopupMenuWillBecomeVisible();
            }

            @Override
            public void setSelectedItem(Object anObject) {
                super.setSelectedItem(anObject);
                setToolTipText(anObject != null ? anObject.toString() : null);
            }

            @Override
            public void setSelectedIndex(int anIndex) {
                super.setSelectedIndex(anIndex);
                Object selectedItem = getSelectedItem();
                setToolTipText(selectedItem != null ? selectedItem.toString() : null);
            }

            private void resizePopup() {
                BasicComboPopup popup = (BasicComboPopup) getUI().getAccessibleChild(this, 0); //Popup

                if (popup == null) {
                    return;
                }

                final Component comp = popup.getComponent(0); //JScrollPane
                JScrollPane scrollpane = null;
                int offset = 0;

                if (comp instanceof JScrollPane) {
                    scrollpane = (JScrollPane) comp;

                    if (scrollpane.getVerticalScrollBar().isVisible()) {
                        offset += scrollpane.getVerticalScrollBar().getPreferredSize().width;
                    }
                }

                int width = (int) getSize().getWidth() - offset;
                int height = 0;

                for (int i = 0; i < getItemCount(); i++) {
                    width = Math.max(width,
                            getRenderer().getListCellRendererComponent(popup.getList(), getItemAt(i), i, false, false).getPreferredSize().width + PADDING_RIGHT);

                    if (i < getMaximumRowCount()) {
                        height += getRenderer().getListCellRendererComponent(popup.getList(), getItemAt(i), i, false, false).getPreferredSize().height;
                    }
                }

                // add Offset if needed
                width += ((getMaximumRowCount() >= getItemCount()) ? 0 : offset);

                popup.setPreferredSize(new Dimension(width, height + PADDING_HEIGHT));
                popup.setLayout(new BorderLayout());
                popup.add(comp, BorderLayout.CENTER);
            }
        };
        comboBox.setPreferredSize(new Dimension(componentPreferredWidth, componentHight));
        comboBox.addItemListener(itemListener);
        return comboBox;
    }

    public JList createListBox() {
        JList listbox = new JList();
        listbox.addListSelectionListener(listSelectionListener);

        return listbox;
    }

    public boolean isNotifyMode() {
        return notifyMode;
    }

    public void setNotifyMode(boolean notifyMode) {
        this.notifyMode = notifyMode;
    }
    //Component value change listener
    private final ListSelectionListener listSelectionListener = new ListSelectionListener() {

        public void valueChanged(ListSelectionEvent e) {
            if (isNotifyMode()) {
                EventQueue.invokeLater(new Runnable() {

                    public void run() {
                        ComponentBuilder.this.valueChanged();
                    }
                });
            }
        }
    };
    private final ItemListener itemListener = new ItemListener() {

        public void itemStateChanged(ItemEvent e) {
            if (isNotifyMode() && e.getStateChange() == ItemEvent.SELECTED) {
                EventQueue.invokeLater(new Runnable() {

                    public void run() {
                        ComponentBuilder.this.valueChanged();
                    }
                });
            }
        }
    };
    private final DocumentListener documentListener = new DocumentListener() {

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
            if (isNotifyMode()) {
                EventQueue.invokeLater(new Runnable() {

                    public void run() {
                        ComponentBuilder.this.valueChanged();
                    }
                });
            }

        }
    };
    private final ActionListener actionListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            if (isNotifyMode()) {
                EventQueue.invokeLater(new Runnable() {

                    public void run() {
                        ComponentBuilder.this.valueChanged();
                    }
                });
            }
        }
    };

    public DocumentListener getDocumentListener() {
        return documentListener;
    }

    public ItemListener getItemListener() {
        return itemListener;
    }

    public ListSelectionListener getListSelectionListener() {
        return listSelectionListener;
    }

    public ActionListener getActionListener() {
        return actionListener;
    }

    public abstract void valueChanged();
}
