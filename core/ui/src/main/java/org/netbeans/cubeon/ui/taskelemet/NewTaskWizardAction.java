/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.cubeon.ui.taskelemet;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import java.util.Collection;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import org.netbeans.cubeon.context.api.TaskFolder;
import org.netbeans.cubeon.context.api.TaskFolderRefreshable;
import org.netbeans.cubeon.context.spi.RepositorysViewRefreshable;
import org.netbeans.cubeon.tasks.spi.TaskElement;
import org.netbeans.cubeon.tasks.spi.TaskRepository;
import org.netbeans.cubeon.tasks.spi.TaskRepositoryType;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

public final class NewTaskWizardAction extends AbstractAction {

    private TaskFolder taskFolder;
    private WizardDescriptor.Panel<WizardObject>[] panels;

    public NewTaskWizardAction(TaskFolder taskFolder) {
        this.taskFolder = taskFolder;
        putValue(NAME, NbBundle.getMessage(NewTaskWizardAction.class, "LBL_Repo_New"));
    }

    /**
     * Initialize panels representing individual wizard's steps and sets
     * various properties for them influencing wizard appearance.
     */
    private WizardDescriptor.Panel[] getPanels() {
        if (panels == null) {
            panels = new WizardDescriptor.Panel[]{
                        new ChooseRepositoryWizard(),
                        new TaskAttributesWizard()
                    };
            String[] steps = new String[panels.length];
            for (int i = 0; i < panels.length; i++) {
                Component c = panels[i].getComponent();
                // Default step name to component name of panel. Mainly useful
                // for getting the name of the target chooser to appear in the
                // list of steps.
                steps[i] = c.getName();
                if (c instanceof JComponent) { // assume Swing components
                    JComponent jc = (JComponent) c;
                    // Sets step number of a component
                    jc.putClientProperty("WizardPanel_contentSelectedIndex", new Integer(i));
                    // Sets steps names for a panel
                    jc.putClientProperty("WizardPanel_contentData", steps);
                    // Turn on subtitle creation on each step
                    jc.putClientProperty("WizardPanel_autoWizardStyle", Boolean.FALSE);
                    // Show steps on the left side with the image on the background
                    jc.putClientProperty("WizardPanel_contentDisplayed", Boolean.FALSE);
                    // Turn on numbering of all steps
                    jc.putClientProperty("WizardPanel_contentNumbered", Boolean.FALSE);
                }
            }
        }
        return panels;
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    public void actionPerformed(ActionEvent e) {
        final WizardObject wizardObject = new WizardObject();
        WizardDescriptor wizardDescriptor = new WizardDescriptor(getPanels(), wizardObject);
        // {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()
        wizardDescriptor.setTitleFormat(new MessageFormat("{0}"));
        Dialog dialog = DialogDisplayer.getDefault().createDialog(wizardDescriptor);
        dialog.setVisible(true);
        dialog.toFront();
        if (wizardDescriptor.getValue() == WizardDescriptor.FINISH_OPTION) {

            TaskElement element = wizardObject.getTaskElement();
            assert element != null;
            TaskRepository repository = wizardObject.getRepository();
            assert repository != null;

            taskFolder.addTaskElement(element);

            TaskFolderRefreshable refreshable = taskFolder.getLookup().lookup(TaskFolderRefreshable.class);
            assert refreshable != null;
            refreshable.refreshContent();
        }
    }

    static class WizardObject {

        private TaskElement taskElement;
        private TaskRepository repository;

        public WizardObject() {
        }

        public WizardObject(TaskElement taskElement, TaskRepository repository) {
            this.taskElement = taskElement;
            this.repository = repository;
        }

        public TaskRepository getRepository() {
            return repository;
        }

        public void setRepository(TaskRepository repository) {
            this.repository = repository;
        }

        public TaskElement getTaskElement() {
            return taskElement;
        }

        public void setTaskElement(TaskElement taskElement) {
            this.taskElement = taskElement;
        }
    }
}
