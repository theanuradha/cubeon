/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.cubeon.ui.query;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import org.netbeans.cubeon.tasks.core.api.CubeonContext;
import org.netbeans.cubeon.tasks.core.api.TaskRepositoryHandler;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.tasks.spi.query.TaskQuery;
import org.netbeans.cubeon.tasks.spi.query.TaskQuerySupportProvider;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

public final class NewQueryWizardAction extends AbstractAction {

    private WizardDescriptor.Panel<WizardObject>[] panels;

    public NewQueryWizardAction(String name) {

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

            if (repositorys.size() == 1) {
                panels = new WizardDescriptor.Panel[]{new TaskQueryAttributesWizard(repositorys.get(0))};
            } else {
                panels = new WizardDescriptor.Panel[]{
                            new ChooseRepositoryWizard(repositorys),
                            new TaskQueryAttributesWizard()
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

            TaskQuery query = wizardObject.getQuery();
            assert query != null;
            TaskRepository repository = wizardObject.getRepository();
            assert repository != null;
            TaskQuerySupportProvider tqsp = repository.getLookup().lookup(TaskQuerySupportProvider.class);
            tqsp.addTaskQuery(query);
            //open query results view
            ResultsTopComponent component = ResultsTopComponent.findInstance();
            component.open();
            component.requestActive();

            component.showResults(query);

        }
    }

    static class WizardObject {

        private TaskQuery query;
        private TaskRepository repository;

        public WizardObject() {
        }

        public WizardObject(TaskQuery query, TaskRepository repository) {
            this.query = query;
            this.repository = repository;
        }

        public TaskRepository getRepository() {
            return repository;
        }

        public void setRepository(TaskRepository repository) {
            this.repository = repository;
        }

        public TaskQuery getQuery() {
            return query;
        }

        public void setQuery(TaskQuery query) {
            this.query = query;
        }
    }
}
