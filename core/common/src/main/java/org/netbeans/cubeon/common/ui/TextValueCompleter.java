/* ==========================================================================
 * Copyright 2006 Mevenide Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * =========================================================================
 */
package org.netbeans.cubeon.common.ui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

/**
 * inner class does the matching of the JTextComponent's
 * document to completion strings kept in an ArrayList
 * @author mkleint
 * @author Anuradha
 */
public class TextValueCompleter implements DocumentListener {

    private static final String ACTION_FILLIN = "fill-in"; //NOI18N
    private static final String ACTION_HIDEPOPUP = "hidepopup"; //NOI18N
    private static final String ACTION_LISTDOWN = "listdown"; //NOI18N
    private static final String ACTION_LISTPAGEDOWN = "listpagedown"; //NOI18N
    private static final String ACTION_LISTUP = "listup"; //NOI18N
    private static final String ACTION_LISTPAGEUP = "listpageup"; //NOI18N
    private static final String ACTION_SHOWPOPUP = "showpopup"; //NOI18N
    private Collection<String> completions;
    private JList completionList;
    private DefaultListModel completionListModel;
    private JScrollPane listScroller;
    private Popup popup;
    private JTextComponent field;
    private String separators;
    private CaretListener caretListener;
    private boolean loading;
    private static final String LOADING = "Loading...";
    private final CallBackFilter callBackFilter;

    public TextValueCompleter(Collection<String> completions, JTextComponent fld) {
        this(completions, fld, new DefultCallBackFilter());
    }

    public TextValueCompleter(Collection<String> completions, JTextComponent fld, CallBackFilter callBackFilter) {
        this.completions = completions;
        this.field = fld;
        this.callBackFilter = callBackFilter;
        field.getDocument().addDocumentListener(this);
        field.addFocusListener(new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent e) {
                EventQueue.invokeLater(new Runnable() {

                    public void run() {
                        if (!completionList.hasFocus()) {
                            hidePopup();
                        }
                    }
                });
            }
        });
        caretListener = new CaretListener() {

            public void caretUpdate(CaretEvent arg0) {
                // only consider caret updates if the popup window is visible
                if (completionList.isDisplayable() && completionList.isVisible()) {
                    buildAndShowPopup();
                }
            }
        };
        field.addCaretListener(caretListener);
        completionListModel = new DefaultListModel();
        completionList = new JList(completionListModel);
        completionList.setPrototypeCellValue("lets have it at least this wide and add some more just in case"); //NOI18N
        completionList.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1) {
                    field.getDocument().removeDocumentListener(TextValueCompleter.this);
                    String compelted = completionList.getSelectedValue().toString();
                    applyCompletion(completionList.getSelectedValue().toString());
                    if (TextValueCompleter.this.callBackFilter.needSeparators(compelted)) {
                        hidePopup();
                    } else {
                        buildPopup();
                        if (completionListModel.isEmpty()) {
                            hidePopup();
                        }
                    }
                    field.getDocument().addDocumentListener(TextValueCompleter.this);
                }
            }
        });
        completionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listScroller = new JScrollPane(completionList,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        field.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), ACTION_LISTDOWN);
        field.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), ACTION_LISTUP);
        field.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0), ACTION_LISTPAGEUP);
        field.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0), ACTION_LISTPAGEDOWN);
        field.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, KeyEvent.CTRL_DOWN_MASK), ACTION_SHOWPOPUP);
        field.getActionMap().put(ACTION_LISTDOWN, new AbstractAction() { //NOI18N

            public void actionPerformed(ActionEvent e) {
                if (popup == null) {
                    buildAndShowPopup();
                }
                completionList.setSelectedIndex(Math.min(completionList.getSelectedIndex() + 1, completionList.getModel().getSize()));
                completionList.ensureIndexIsVisible(completionList.getSelectedIndex());
            }
        });
        field.getActionMap().put(ACTION_LISTUP, new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                if (popup == null) {
                    buildAndShowPopup();
                }
                completionList.setSelectedIndex(Math.max(completionList.getSelectedIndex() - 1, 0));
                completionList.ensureIndexIsVisible(completionList.getSelectedIndex());
            }
        });
        field.getActionMap().put(ACTION_LISTPAGEDOWN, new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                completionList.setSelectedIndex(Math.min(completionList.getSelectedIndex() + completionList.getVisibleRowCount(), completionList.getModel().getSize()));
                completionList.ensureIndexIsVisible(completionList.getSelectedIndex());
            }
        });
        field.getActionMap().put(ACTION_LISTPAGEUP, new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                completionList.setSelectedIndex(Math.max(completionList.getSelectedIndex() - completionList.getVisibleRowCount(), 0));
                completionList.ensureIndexIsVisible(completionList.getSelectedIndex());
            }
        });
        field.getActionMap().put(ACTION_FILLIN, new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                field.getDocument().removeDocumentListener(TextValueCompleter.this);
                String compelted = null;
                if (completionList.getSelectedValue() != null) {
                    compelted = completionList.getSelectedValue().toString();
                    applyCompletion(compelted);
                }
                if (compelted == null || TextValueCompleter.this.callBackFilter.needSeparators(compelted)) {
                    hidePopup();
                } else {
                    buildPopup();
                    if (completionListModel.isEmpty()) {
                        hidePopup();
                    }
                }
                field.getDocument().addDocumentListener(TextValueCompleter.this);
            }
        });
        field.getActionMap().put(ACTION_HIDEPOPUP, new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                hidePopup();
            }
        });
        field.getActionMap().put(ACTION_SHOWPOPUP, new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                buildAndShowPopup();
            }
        });
    }

    public TextValueCompleter(Collection<String> completions, JTextComponent fld, String separators, CallBackFilter callBackFilter) {
        this(completions, fld, callBackFilter);
        this.separators = separators;
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
        if (loading) {
            completionListModel.removeAllElements();
            completionListModel.addElement(LOADING);
        } else {
            completionListModel.removeElement(LOADING);
        }
    }

    private void buildPopup() {
        completionListModel.clear();
        Collection<String> collection = callBackFilter.getFilterdCollection(getCompletionPrefix(), completions);
        int index = 0;
        for (String item : collection) {
            completionListModel.add(index++, item);
        }
    }

    private void applyCompletion(String completed) {
        field.removeCaretListener(caretListener);
        if (separators != null) {
            int pos = field.getCaretPosition();
            String currentText = field.getText();
            int caretPosition = 0;
            StringTokenizer tok = new StringTokenizer(currentText, separators, true);
            int tokens = tok.countTokens();
            int count = 0;
            String newValue = ""; //NOI18N
            while (tok.hasMoreTokens()) {
                String token = tok.nextToken();
                if (count + token.length() >= pos) {
                    if (separators.indexOf(token.charAt(0)) != -1) {
                        newValue = newValue + token;
                    }
                    newValue = newValue + completed;
                    if (callBackFilter.needSeparators(completed)) {
                        newValue += separators;
                    }
                    caretPosition = newValue.length();
                    while (tok.hasMoreTokens()) {
                        newValue = newValue + tok.nextToken();
                    }
                    field.setText(newValue);

                    field.setCaretPosition(caretPosition);
                    field.addCaretListener(caretListener);
                    return;
                } else {
                    count = count + token.length();
                    newValue = newValue + token;
                }
            }
            newValue = newValue + completed;
            if (callBackFilter.needSeparators(completed)) {
                newValue += separators;
            }
            field.setText(newValue);
            field.setCaretPosition(newValue.length());
        } else {
            field.setText(completed);
        }
        field.addCaretListener(caretListener);
    }

    private String getCompletionPrefix() {
        if (separators != null) {
            int pos = field.getCaretPosition();
            String currentText = field.getText();
            StringTokenizer tok = new StringTokenizer(currentText, separators, true);
            int count = 0;
            String lastToken = ""; //NOI18N
            while (tok.hasMoreTokens()) {
                String token = tok.nextToken();
                if (count + token.length() >= pos) {
                    if (separators.indexOf(token.charAt(0)) != -1) {
                        return ""; //NOI18N
                    }
                    return Pattern.quote(token.substring(0, pos - count));
                } else {
                    count = count + token.length();
                    lastToken = token;
                }
            }
            if (lastToken.length() > 0 && separators.indexOf(lastToken.charAt(0)) == -1) {
                return Pattern.quote(lastToken);
            }
            return ""; //NOI18N
        } else {
            return Pattern.quote(field.getText().trim());
        }
    }

    private void showPopup() {
        hidePopup();
        if (completionListModel.getSize() == 0) {
            return;
        }
        // figure out where the text field is,
        // and where its bottom left is
        java.awt.Point los = field.getLocationOnScreen();
        int popX = los.x;
        int caretPosition = field.getCaretPosition();
        if (caretPosition > 0 && field.getText().length() >= caretPosition) {
            int stringWidth = field.getFontMetrics(field.getFont()).stringWidth(field.getText().substring(0, caretPosition));
            popX += stringWidth;
        }
        int popY = los.y + field.getHeight();
        popup = PopupFactory.getSharedInstance().getPopup(field, listScroller, popX, popY);
        field.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), ACTION_HIDEPOPUP);
        field.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), ACTION_FILLIN);
        popup.show();
        if (completionList.getSelectedIndex() != -1) {
            completionList.ensureIndexIsVisible(completionList.getSelectedIndex());
        }
    }

    private void hidePopup() {
        field.getInputMap().remove(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
        field.getInputMap().remove(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
        if (popup != null) {
            popup.hide();
            popup = null;
        }
    }

    private void buildAndShowPopup() {
        buildPopup();
        showPopup();
    }

    // DocumentListener implementation
    public void insertUpdate(DocumentEvent e) {
        if (field.isFocusOwner()) {
            buildAndShowPopup();
        }
    }

    public void removeUpdate(DocumentEvent e) {
        if (field.isFocusOwner() && completionList.isDisplayable() && completionList.isVisible()) {
            buildAndShowPopup();
        }
    }

    public void changedUpdate(DocumentEvent e) {
        if (field.isFocusOwner()) {
            buildAndShowPopup();
        }
    }

    public void setValueList(Collection<String> values) {
        assert SwingUtilities.isEventDispatchThread();
        completionListModel.removeAllElements();
        completions = values;
        if (field.isFocusOwner() && completionList.isDisplayable() && completionList.isVisible()) {
            buildAndShowPopup();
        }
    }

    public static interface CallBackFilter {

        boolean needSeparators(String compelted);

        Collection<String> getFilterdCollection(String prifix, Collection<String> completions);
    }

    public static class DefultCallBackFilter implements CallBackFilter {

        public Collection<String> getFilterdCollection(String prifix, Collection<String> completions) {
            List<String> list = new ArrayList<String>();
            Pattern pattern = Pattern.compile(prifix + ".+"); //NOI18N

            for (String completion : completions) {
                // check if match
                Matcher matcher = pattern.matcher(completion);
                if (matcher.matches()) {
                    if (!list.contains(completion)) {
                        list.add(completion);
                    }

                }
            }
            return list;
        }

        public boolean needSeparators(String compelted) {
            return true;
        }
    }
}

