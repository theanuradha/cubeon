/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.cubeon.ui.repository;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepositoryType;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepositoryType.ConfigurationHandler;
import org.netbeans.cubeon.ui.repository.NewRepositoryWizardAction.WizardObject;
import org.openide.util.NbBundle;

final class RepositorySettings extends JPanel {

    private TaskRepositoryType type;
    private TaskRepositoryType.ConfigurationHandler handler;

    /** Creates new form RepositorySettings */
    RepositorySettings() {
        initComponents();
    }

    @Override
    public String getName() {
        return "Configure";
    }

    void setWizardObject(WizardObject object) {

        this.type = object.getType();
        assert type != null;
        handler = type.createConfigurationHandler();
        TaskRepository repository = object.getRepository();
        if (repository != null) {
            handler.setTaskRepository(repository);
        }
        lblMainHeader.setText(NbBundle.getMessage(RepositorySettings.class,
                "LBL_Create_New", type.getName()));
        lblSubHeader.setText(NbBundle.getMessage(RepositorySettings.class,
                "LBL_Create_New_Dec"));
        pnlHolder.removeAll();
        pnlHolder.add(handler.getComponent(), BorderLayout.CENTER);
        pnlHolder.repaint();
        pnlHolder.updateUI();
        handler.getComponent().requestFocusInWindow();
    }

    ConfigurationHandler getHandler() {
        return handler;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlHeader = new javax.swing.JPanel();
        lblMainHeader = new javax.swing.JLabel();
        lblSubHeader = new javax.swing.JLabel();
        lblIcon = new javax.swing.JLabel();
        pnlHolder = new javax.swing.JPanel();

        pnlHeader.setBackground(new java.awt.Color(255, 255, 255));
        pnlHeader.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(137, 140, 149)));

        lblMainHeader.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(lblMainHeader, "_MAIN_HEADING_");

        org.openide.awt.Mnemonics.setLocalizedText(lblSubHeader, "_SUB_DECRIPTION_"); // NOI18N

        lblIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/cubeon/ui/repository_add.png"))); // NOI18N

        org.jdesktop.layout.GroupLayout pnlHeaderLayout = new org.jdesktop.layout.GroupLayout(pnlHeader);
        pnlHeader.setLayout(pnlHeaderLayout);
        pnlHeaderLayout.setHorizontalGroup(
            pnlHeaderLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlHeaderLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlHeaderLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(pnlHeaderLayout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(lblSubHeader, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE))
                    .add(lblMainHeader, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE))
                .add(45, 45, 45)
                .add(lblIcon, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnlHeaderLayout.setVerticalGroup(
            pnlHeaderLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlHeaderLayout.createSequentialGroup()
                .add(pnlHeaderLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlHeaderLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(lblMainHeader)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(lblSubHeader))
                    .add(lblIcon, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .add(12, 12, 12))
        );

        pnlHolder.setLayout(new java.awt.BorderLayout());

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlHeader, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(pnlHolder, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(pnlHeader, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 55, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, 0)
                .add(pnlHolder, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 345, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblIcon;
    private javax.swing.JLabel lblMainHeader;
    private javax.swing.JLabel lblSubHeader;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPanel pnlHolder;
    // End of variables declaration//GEN-END:variables
}

