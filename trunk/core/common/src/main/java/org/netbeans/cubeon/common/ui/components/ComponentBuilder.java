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

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author Anuradha
 */
public abstract class ComponentBuilder {

    private int componentPreferredWidth = 160;
    private int lablePreferredWidth = 75;
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

    public JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setPreferredSize(new Dimension(lablePreferredWidth, label.getPreferredSize().height));
        return label;
    }

    public JLabel createLabelField(String text) {
        JLabel labelField = new JLabel(text);
        labelField.setPreferredSize(new Dimension(componentPreferredWidth, labelField.getPreferredSize().height));

        return labelField;
    }

    public JTextField createTextField() {
        JTextField textField = new JTextField();
        textField.setPreferredSize(new Dimension(componentPreferredWidth, textField.getPreferredSize().height));
        textField.getDocument().addDocumentListener(documentListener);
        return textField;
    }

    public JComponent createCheckbox() {
        JCheckBox checkBox = new JCheckBox();
        checkBox.setPreferredSize(new Dimension(componentPreferredWidth, checkBox.getPreferredSize().height));
        checkBox.addActionListener(actionListener);
        checkBox.setOpaque(false);
        return checkBox;
    }

    public JTextField createEmptyField() {
        JTextField textField = new JTextField();
        textField.setPreferredSize(new Dimension(componentPreferredWidth, textField.getPreferredSize().height));
        textField.setEditable(false);
        textField.setBorder(null);
        textField.setOpaque(false);
        return textField;
    }

    public JComboBox createComboBox() {
        JComboBox comboBox = new JComboBox();
        comboBox.setPreferredSize(new Dimension(componentPreferredWidth, comboBox.getPreferredSize().height));
        comboBox.addItemListener(itemListener);
        return comboBox;
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
