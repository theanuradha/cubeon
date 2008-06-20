/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.cubeon.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.cubeon.tasks.spi.Extension;
import org.netbeans.cubeon.tasks.spi.TaskEditorProvider;
import org.netbeans.cubeon.tasks.spi.TaskEditorProvider.EditorAttributeHandler;
import org.netbeans.cubeon.tasks.spi.TaskElement;
import org.netbeans.cubeon.tasks.spi.TaskElementChangeAdapter;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.ui.internals.TaskElementNode;
import org.netbeans.cubeon.ui.util.PaintUtils;
import org.openide.cookies.SaveCookie;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;

/**
 * Top component which displays something.
 */
final class TaskEditorTopComponent extends TopComponent implements SaveCookie, ChangeListener {

    private static final String PREFERRED_ID = "TaskEditorTopComponent";
    private static final Color c = UIManager.getDefaults().getColor("InternalFrame.activeTitleGradient");
    private static final Color start = new Color(255, 255, 255, 255);
    private static final Color end = new Color(255, 255, 255, 0);
    private static final RenderingHints hints;
    private final static GradientPaint GRADIENT_HEADER_LARGE = new GradientPaint(0, 0,
            Color.WHITE, 0, 33, c);
    private TaskElement element;
    private final TaskElementNode editorNode;
    private final EditorAttributeHandler eah;
    private final Extension extension;
    private final TaskElementChangeAdapter changeAdapter;
    

    static {
        hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        hints.put(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);


    }

    TaskEditorTopComponent(final TaskElement element) {
        this.element = element;

        initComponents();

        Lookup lookup = element.getLookup();

        TaskEditorProvider editorProvider = lookup.lookup(TaskEditorProvider.class);
        eah = editorProvider.createEditorAttributeHandler();

        setName(eah.getName());
        extension = element.getLookup().lookup(Extension.class);
        setIcon(element.getImage());
        TaskRepository taskRepository = element.getTaskRepository();

        lblHeader.setIcon(new ImageIcon(taskRepository.getImage()));
        lblHeader.setText(eah.getDisplayName());
        base.add(eah.getComponent(), BorderLayout.CENTER);

        setActivatedNodes(new Node[]{editorNode = TaskElementNode.createNode(element, this)});
        eah.addChangeListener(this);
        changeAdapter = new TaskElementChangeAdapter() {

            @Override
            public void nameChenged() {
                EventQueue.invokeLater(new Runnable() {

                    public void run() {
                        setName(eah.getName());
                        lblHeader.setText(eah.getDisplayName());
                    }
                });

            }

            @Override
            public void typeChenged() {
                EventQueue.invokeLater(new Runnable() {

                    public void run() {
                        setName(eah.getName());
                        setIcon(element.getImage());
                    }
                });

            }
        };
        extension.add(changeAdapter);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        base = new javax.swing.JPanel();
        header =         new JPanel() {

            private int count;

            /**
            * Paints a gradient in the background of this component
            */
            @Override
            protected void paintComponent(Graphics g) {
                int i = count++;
                Graphics2D g2 = (Graphics2D) g;

                g2.setPaint(GRADIENT_HEADER_LARGE);
                g2.fillRect(0, 0, getWidth(), getHeight());

                RenderingHints oldHints = g2.getRenderingHints();
                g2.setRenderingHints(hints);

                float width = getWidth();
                float height = getHeight();

                g2.translate(0, -30);

                PaintUtils.drawCurve(g2,
                    20.0f, -10.0f, 20.0f, -10.0f,
                    width / 2.0f - 40.0f, 10.0f,
                    0.0f, -5.0f,
                    width / 2.0f + 40, 1.0f,
                    0.0f, 5.0f,
                    50.0f, 5.0f, false,i,width,start,end );

                g2.translate(0, 30);
                g2.translate(0, height - 60);

                PaintUtils.drawCurve(g2,
                    30.0f, -15.0f, 50.0f, 15.0f,
                    width / 2.0f - 40.0f, 1.0f,
                    15.0f, -25.0f,
                    width / 2.0f, 1.0f / 2.0f,
                    0.0f, 25.0f,
                    15.0f, 9.0f, false, i, width, start, end);

                g2.translate(0, -height + 60);

                PaintUtils.drawCurve(g2,
                    height - 35.0f, -5.0f, height - 50.0f, 10.0f,
                    width / 2.0f - 40.0f, 1.0f,
                    height - 35.0f, -25.0f,
                    width / 2.0f, 1.0f / 2.0f,
                    height - 20.0f, 25.0f,
                    25.0f, 7.0f, true, i, width, start, end);

                g2.setRenderingHints(oldHints);
            }
        };
        javax.swing.JSeparator separator = new javax.swing.JSeparator();
        lblHeader = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        base.setBackground(new java.awt.Color(255, 255, 255));
        base.setLayout(new java.awt.BorderLayout());

        lblHeader.setFont(new java.awt.Font("Tahoma", 1, 13));
        lblHeader.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/cubeon/ui/repository.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(lblHeader, "Task Element Name"); // NOI18N

        org.jdesktop.layout.GroupLayout headerLayout = new org.jdesktop.layout.GroupLayout(header);
        header.setLayout(headerLayout);
        headerLayout.setHorizontalGroup(
            headerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, separator, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 711, Short.MAX_VALUE)
            .add(headerLayout.createSequentialGroup()
                .add(5, 5, 5)
                .add(lblHeader, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 443, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(263, Short.MAX_VALUE))
        );
        headerLayout.setVerticalGroup(
            headerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, headerLayout.createSequentialGroup()
                .add(lblHeader, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(separator, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        base.add(header, java.awt.BorderLayout.NORTH);

        add(base, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel base;
    private javax.swing.JPanel header;
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
    }

    public void stateChanged(ChangeEvent e) {
        editorNode.setModified(true);
    }

    @Override
    protected void componentClosed() {
        extension.remove(changeAdapter);
        try {
            editorNode.destroy();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
