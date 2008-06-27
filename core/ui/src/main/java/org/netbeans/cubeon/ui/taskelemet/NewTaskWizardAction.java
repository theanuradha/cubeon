/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.cubeon.ui.taskelemet;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import org.netbeans.cubeon.tasks.core.api.CubeonContext;
import org.netbeans.cubeon.tasks.core.api.TaskEditorFactory;
import org.netbeans.cubeon.tasks.core.api.TaskFolder;
import org.netbeans.cubeon.tasks.core.api.TaskFolderRefreshable;
import org.netbeans.cubeon.tasks.core.api.TaskRepositoryHandler;
import org.netbeans.cubeon.tasks.core.api.TasksFileSystem;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

public final class NewTaskWizardAction extends AbstractAction {

    private final TaskFolder taskFolder;
    private WizardDescriptor.Panel<WizardObject>[] panels;

    public NewTaskWizardAction(String name) {
        //set null to flag add to default
        taskFolder = null;
        init(name);
    }

    public NewTaskWizardAction(String name, TaskFolder taskFolder) {
        this.taskFolder = taskFolder;
        init(name);
    }

    private void init(String name) {
        putValue(NAME, name);
    }

    /**
     * Initialize panels representing individual wizard's steps and sets
     * various properties for them influencing wizard appearance.
     */
    private WizardDescriptor.Panel[] getPanels() {
        if (panels == null) {
            //lookup CubeonContext
            CubeonContext cubeonContext = Lookup.getDefault().lookup(CubeonContext.class);
            assert cubeonContext != null : "CubeonContext can't be null";
            //lookup TaskRepositoryHandler
            TaskRepositoryHandler repositoryHandler = cubeonContext.getLookup().lookup(TaskRepositoryHandler.class);

            List<TaskRepository> repositorys = repositoryHandler.getTaskRepositorys();
            Collections.sort(repositorys, new Comparator<TaskRepository>() {

                public int compare(TaskRepository o1, TaskRepository o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });
            if (repositorys.size() == 1) {
                panels = new WizardDescriptor.Panel[]{
                            new TaskAttributesWizard(repositorys.get(0))
                        };
            } else {
                panels = new WizardDescriptor.Panel[]{
                            new ChooseRepositoryWizard(repositorys),
                            new TaskAttributesWizard()
                        };
            }

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
            repository.persist(element);
            TaskFolder tf = taskFolder;
            //check null and add to defult folder
            if (tf == null) {
                TasksFileSystem fileSystem = Lookup.getDefault().lookup(TasksFileSystem.class);
                assert fileSystem != null;
                tf = fileSystem.getDefaultFolder();

            }
            tf.addTaskElement(element);

            TaskFolderRefreshable refreshable = tf.getLookup().lookup(TaskFolderRefreshable.class);
            assert refreshable != null;
            refreshable.refreshNode();
            //open newly created Task 
            TaskEditorFactory factory = Lookup.getDefault().lookup(TaskEditorFactory.class);
            factory.createTaskEditor(element);
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
