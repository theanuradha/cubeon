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
 * JiraCommentsEditor.java
 *
 * Created on Jul 12, 2008, 9:49:18 PM
 */
package org.netbeans.cubeon.jira.tasks.ui;

import java.awt.Image;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import org.netbeans.cubeon.jira.repository.attributes.JiraComment;
import org.netbeans.cubeon.jira.tasks.JiraTask;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha
 */
public class JiraCommentsEditor extends javax.swing.JPanel implements ExplorerManager.Provider {

    private JiraTaskEditorUI editorUI;
    private ExplorerManager explorerManager = new ExplorerManager();
    private BeanTreeView treeView = new BeanTreeView();
    private final DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();

    /** Creates new form JiraCommentsEditor */
    public JiraCommentsEditor(JiraTaskEditorUI editorUI) {
        this.editorUI = editorUI;
        initComponents();

    }

    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }

    void refresh() {
        txtComment.getDocument().removeDocumentListener(editorUI.documentListener);
        loadComments();
        txtComment.setText(editorUI.getJiraTask().getNewComment());
        txtComment.getDocument().addDocumentListener(editorUI.documentListener);
        if (editorUI.getJiraTask().isLocal()) {
            txtComment.setEditable(false);
        }
    }

    private void loadComments() {
        final JiraTask jiraTask = editorUI.getJiraTask();
        Children.Array array = new Children.Array();
        Node node = new AbstractNode(array) {

            @Override
            public Image getIcon(int type) {
                return Utilities.loadImage("org/netbeans/cubeon/jira/comments.png");
            }

            @Override
            public Image getOpenedIcon(int type) {
                return getIcon(type);
            }

            @Override
            public String getHtmlDisplayName() {
                StringBuffer buffer = new StringBuffer();
                buffer.append("<b>").append(NbBundle.getMessage(JiraCommentsEditor.class, "LBL_Comments"));
                buffer.append("</b> <font color=\"#808080\"> : ");
                buffer.append(jiraTask.getComments().size());
                return buffer.toString();
            }

            @Override
            public Action[] getActions(boolean context) {
                return new Action[]{};
            }
        };
        for (JiraComment comment : jiraTask.getComments()) {
            array.add(new Node[]{new CommentNode(comment)});
        }
        explorerManager.setRootContext(node);
    }

    private class CommentNode extends AbstractNode {

        private JiraComment comment;

        public CommentNode(JiraComment comment) {
            super(Children.LEAF, Lookups.fixed(comment));
            this.comment = comment;
            setDisplayName(comment.getBody());
            setShortDescription(buildHtmlDescription(comment));
        }

        @Override
        public String getHtmlDisplayName() {
            StringBuffer buffer = new StringBuffer();

            buffer.append(comment.getAuthor()).append(" : ");
            if (comment.getCreated() != null) {
                buffer.append(dateFormat.format(comment.getCreated())).append(" :");
            }

            buffer.append("<font color=\"#808080\"> ");
            buffer.append(trimed(comment.getBody()));
            return buffer.toString();
        }

        @Override
        public Image getIcon(int type) {
            return Utilities.loadImage("org/netbeans/cubeon/jira/comment.png");
        }

        @Override
        public Action[] getActions(boolean context) {
            return new Action[]{};
        }

        @Override
        public Transferable drag() throws IOException {
            return new StringSelection(reply(comment));
        }
    }

    private String trimed(String text) {
        String subString = text;
        if (text.length() > 100) {
            subString = text.substring(0, 100) + "...";
        }
        return subString;
    }

    private String buildHtmlDescription(JiraComment comment) {
        StringBuffer buffer = new StringBuffer("<html>");
        List<String> splitMultiLine = splitMultiLine(comment.getBody());
        for (String string : splitMultiLine) {
            buffer.append(string);
            buffer.append("<p>");
        }
        buffer.append("</html>");
        return buffer.toString();

    }

    private static List<String> splitMultiLine(String input) {
        List<String> list = new ArrayList<String>();
        String[] strs = input.split("\\r|\\n"); //NOI18N
        for (int i = 0; i < strs.length; i++) {
            if (strs[i].length() > 0) {
                list.add(strs[i]);
            }
        }
        return list;
    }

    private String reply(JiraComment comment) {
        StringBuffer buffer = new StringBuffer(comment.getAuthor());
        buffer.append(" Wrote").append(",");
        List<String> multiLine = splitMultiLine(comment.getBody());
        for (String line : multiLine) {
            buffer.append("\n").append(">").append(line);
        }
        return buffer.toString();
    }

    String getNewComment() {
        return txtComment.getText().trim();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.JScrollPane jScrollPane1 = treeView;
        lblCommemt = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtComment = new javax.swing.JEditorPane();

        setBackground(new java.awt.Color(255, 255, 255));
        setName(NbBundle.getMessage(JiraCommentsEditor.class, "LBL_Comments","-")); // NOI18N

        jScrollPane1.setBorder(javax.swing.BorderFactory.createEtchedBorder(null, javax.swing.UIManager.getDefaults().getColor("CheckBoxMenuItem.selectionBackground")));

        lblCommemt.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblCommemt.setForeground(new java.awt.Color(102, 102, 102));
        lblCommemt.setText(NbBundle.getMessage(JiraCommentsEditor.class, "JiraCommentsEditor.lblCommemt.text","-")); // NOI18N

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setViewportView(txtComment);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 691, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 691, Short.MAX_VALUE)
                    .addComponent(lblCommemt, javax.swing.GroupLayout.DEFAULT_SIZE, 691, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblCommemt)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblCommemt;
    private javax.swing.JEditorPane txtComment;
    // End of variables declaration//GEN-END:variables
}
