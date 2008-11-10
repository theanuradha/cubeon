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
 * TracCommentsEditor.java
 *
 * Created on Jul 12, 2008, 9:49:18 PM
 */
package org.netbeans.cubeon.trac.tasks.ui;

import java.awt.Image;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.Action;
import org.netbeans.cubeon.trac.api.TicketChange;
import org.netbeans.cubeon.trac.api.TicketChange.FieldChange;
import org.netbeans.cubeon.trac.tasks.TracTask;
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
public class TracCommentsEditor extends javax.swing.JPanel implements ExplorerManager.Provider {

    private static final long serialVersionUID = 1L;
    private TracTaskEditorUI editorUI;
    private ExplorerManager explorerManager = new ExplorerManager();
    private BeanTreeView treeView = new BeanTreeView();
    private final DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();

    /** Creates new form TracCommentsEditor */
    public TracCommentsEditor(TracTaskEditorUI editorUI) {
        this.editorUI = editorUI;
        initComponents();

        explorerManager.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
                    Node[] selectedNodes = explorerManager.getSelectedNodes();
                    if (selectedNodes.length > 0) {
                        TicketChange comment = selectedNodes[0].getLookup().lookup(TicketChange.class);
                        if (comment!=null) {
                            lblAuthor.setText(comment.getAuthor() + " Wrote :");
                            txtDisplayComment.setText(buildChangesSet(comment));
                            pnlComment.setVisible(true);
                        } else {
                            txtDisplayComment.setText(null);
                            pnlComment.setVisible(false);
                        }
                    } else {
                        txtDisplayComment.setText(null);
                        pnlComment.setVisible(false);
                    }
                }
            }
        });
    }

    private String buildChangesSet(TicketChange comment) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(comment.getComment()).append("\n");
        if (!comment.getFieldChanges().isEmpty()) {
            buffer.append(NbBundle.getMessage(TracCommentsEditor.class, "LBL_Field_Changes"));
            List<FieldChange> fieldChanges = comment.getFieldChanges();
            for (FieldChange fieldChange : fieldChanges) {
                buffer.append("\n").append(fieldChange.getField()).append(" : ");
                buffer.append(fieldChange.getOldValue()).append(" => ");
                buffer.append(fieldChange.getNewValuve());
            }
        }
        return buffer.toString();
    }

    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }

    void refresh() {
        txtComment.getDocument().removeDocumentListener(editorUI.documentListener);
        loadComments();
        txtComment.setText(editorUI.getTask().getNewComment());
        txtComment.getDocument().addDocumentListener(editorUI.documentListener);
        txtComment.setEditable(!editorUI.getTask().isLocal());

    }

    private void loadComments() {
        txtDisplayComment.setText(null);
        pnlComment.setVisible(false);
        final TracTask tracTask = editorUI.getTask();
        Children.Array array = new Children.Array();
        Node node = new AbstractNode(array) {

            @Override
            public Image getIcon(int type) {
                return Utilities.loadImage("org/netbeans/cubeon/trac/comments.png");
            }

            @Override
            public Image getOpenedIcon(int type) {
                return getIcon(type);
            }

            @Override
            public String getHtmlDisplayName() {
                StringBuffer buffer = new StringBuffer();
                buffer.append("<b>").append(NbBundle.getMessage(TracCommentsEditor.class, "LBL_Comments"));
                buffer.append("</b> <font color=\"#808080\"> : ");
                buffer.append(tracTask.getTicketChanges().size());
                return buffer.toString();
            }

            @Override
            public Action[] getActions(boolean context) {
                return new Action[]{};
            }
        };
        for (TicketChange comment : tracTask.getTicketChanges()) {
            array.add(new Node[]{new CommentNode(comment)});
        }
        explorerManager.setRootContext(node);
    }

    private class CommentNode extends AbstractNode {

        private TicketChange comment;

        public CommentNode(TicketChange comment) {
            super(Children.LEAF, Lookups.fixed(comment));
            this.comment = comment;
            setDisplayName(comment.getComment());
            setShortDescription(buildHtmlDescription(comment));
        }

        @Override
        public String getHtmlDisplayName() {
            StringBuffer buffer = new StringBuffer();

            buffer.append(comment.getAuthor()).append(" : ");

            buffer.append(dateFormat.format(new Date(comment.getTime()))).append(" :");


            buffer.append("<font color=\"#808080\"> ");
            buffer.append("<xmp>").append(trimed(buildChangesSet(comment))).append("</xmp>");
            return buffer.toString();
        }

        @Override
        public Image getIcon(int type) {
            return Utilities.loadImage("org/netbeans/cubeon/trac/comment.png");
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

    private String buildHtmlDescription(TicketChange comment) {
        StringBuffer buffer = new StringBuffer("<html> ");
        List<String> splitMultiLine = splitMultiLine(buildChangesSet(comment));
        for (String string : splitMultiLine) {
            buffer.append("<xmp>");
            buffer.append(string);
            buffer.append("</xmp>");
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

    private String reply(TicketChange comment) {
        StringBuffer buffer = new StringBuffer(comment.getAuthor());
        buffer.append(" Wrote").append(",");
        List<String> multiLine = splitMultiLine(buildChangesSet(comment));
        for (String line : multiLine) {
            buffer.append("\n").append(">").append(line);
        }
        return buffer.append("\n").toString();//move to next line
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

        lblCommemt = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtComment = new javax.swing.JEditorPane();
        pnlComments = new javax.swing.JPanel();
        javax.swing.JScrollPane jScrollPane1 = treeView;
        pnlComment = new javax.swing.JPanel();
        jsDisplay = new javax.swing.JScrollPane();
        txtDisplayComment = new javax.swing.JTextArea();
        lblAuthor = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();

        setBackground(new java.awt.Color(255, 255, 255));
        setName(NbBundle.getMessage(TracCommentsEditor.class, "LBL_Comments","-")); // NOI18N

        lblCommemt.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblCommemt.setForeground(new java.awt.Color(102, 102, 102));
        lblCommemt.setText(NbBundle.getMessage(TracCommentsEditor.class, "TracCommentsEditor.lblCommemt.text","-")); // NOI18N

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setViewportView(txtComment);

        pnlComments.setBackground(new java.awt.Color(255, 255, 255));
        pnlComments.setBorder(javax.swing.BorderFactory.createEtchedBorder(null, javax.swing.UIManager.getDefaults().getColor("CheckBoxMenuItem.selectionBackground")));
        pnlComments.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setBorder(null);
        pnlComments.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        pnlComment.setBackground(new java.awt.Color(255, 255, 255));

        jsDisplay.setBorder(null);
        jsDisplay.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        txtDisplayComment.setColumns(20);
        txtDisplayComment.setEditable(false);
        txtDisplayComment.setLineWrap(true);
        txtDisplayComment.setRows(5);
        txtDisplayComment.setWrapStyleWord(true);
        jsDisplay.setViewportView(txtDisplayComment);

        lblAuthor.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblAuthor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/cubeon/trac/comment.png"))); // NOI18N
        lblAuthor.setText(NbBundle.getMessage(TracCommentsEditor.class, "TracCommentsEditor.lblAuthor.text", new Object[] {})); // NOI18N

        org.jdesktop.layout.GroupLayout pnlCommentLayout = new org.jdesktop.layout.GroupLayout(pnlComment);
        pnlComment.setLayout(pnlCommentLayout);
        pnlCommentLayout.setHorizontalGroup(
            pnlCommentLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlCommentLayout.createSequentialGroup()
                .add(lblAuthor, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 701, Short.MAX_VALUE)
                .addContainerGap())
            .add(jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 711, Short.MAX_VALUE)
            .add(jSeparator2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 711, Short.MAX_VALUE)
            .add(pnlCommentLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(jsDisplay, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 711, Short.MAX_VALUE))
        );
        pnlCommentLayout.setVerticalGroup(
            pnlCommentLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlCommentLayout.createSequentialGroup()
                .add(2, 2, 2)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, 0)
                .add(lblAuthor)
                .add(0, 0, 0)
                .add(jSeparator2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(104, Short.MAX_VALUE))
            .add(pnlCommentLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlCommentLayout.createSequentialGroup()
                    .addContainerGap(22, Short.MAX_VALUE)
                    .add(jsDisplay, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 104, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );

        pnlComments.add(pnlComment, java.awt.BorderLayout.SOUTH);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane2)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, lblCommemt, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 715, Short.MAX_VALUE)
                    .add(pnlComments, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 715, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(pnlComments, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 218, Short.MAX_VALUE)
                .add(11, 11, 11)
                .add(lblCommemt)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JScrollPane jsDisplay;
    private javax.swing.JLabel lblAuthor;
    private javax.swing.JLabel lblCommemt;
    private javax.swing.JPanel pnlComment;
    private javax.swing.JPanel pnlComments;
    private javax.swing.JEditorPane txtComment;
    private javax.swing.JTextArea txtDisplayComment;
    // End of variables declaration//GEN-END:variables
}
