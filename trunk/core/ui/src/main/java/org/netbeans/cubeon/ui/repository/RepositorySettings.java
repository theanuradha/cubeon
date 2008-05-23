/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.cubeon.ui.repository;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import org.netbeans.cubeon.tasks.spi.TaskRepositoryType;
import org.netbeans.cubeon.tasks.spi.TaskRepositoryType.ConfigurationHandler;
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
        return "Step #2";
    }

    void setWizardObject(WizardObject object) {

        this.type = object.getType();
        assert type != null;
        handler = type.createConfigurationHandler();
        lblMainHeader.setText(NbBundle.getMessage(RepositorySettings.class,
                "LBL_Create_New", type.getName()));
        lblSubHeader.setText(NbBundle.getMessage(RepositorySettings.class,
                "LBL_Create_New_Dec"));
        pnlHolder.removeAll();
        pnlHolder.add(handler.getComponent(), BorderLayout.CENTER);
        pnlHolder.repaint();
        pnlHolder.updateUI();
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
        pnlHeader.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblMainHeader.setFont(new java.awt.Font("Tahoma", 1, 11));
        org.openide.awt.Mnemonics.setLocalizedText(lblMainHeader, "_MAIN_HEADING_");

        org.openide.awt.Mnemonics.setLocalizedText(lblSubHeader, "_SUB_DECRIPTION_"); // NOI18N

        lblIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/cubeon/ui/repository_add.png"))); // NOI18N

        javax.swing.GroupLayout pnlHeaderLayout = new javax.swing.GroupLayout(pnlHeader);
        pnlHeader.setLayout(pnlHeaderLayout);
        pnlHeaderLayout.setHorizontalGroup(
            pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHeaderLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlHeaderLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(lblSubHeader, javax.swing.GroupLayout.DEFAULT_SIZE, 403, Short.MAX_VALUE))
                    .addComponent(lblMainHeader, javax.swing.GroupLayout.DEFAULT_SIZE, 413, Short.MAX_VALUE))
                .addGap(45, 45, 45)
                .addComponent(lblIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnlHeaderLayout.setVerticalGroup(
            pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHeaderLayout.createSequentialGroup()
                .addGroup(pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlHeaderLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblMainHeader)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblSubHeader))
                    .addComponent(lblIcon, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE))
                .addContainerGap())
        );

        pnlHolder.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlHeader, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlHolder, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlHeader, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(pnlHolder, javax.swing.GroupLayout.DEFAULT_SIZE, 345, Short.MAX_VALUE))
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

