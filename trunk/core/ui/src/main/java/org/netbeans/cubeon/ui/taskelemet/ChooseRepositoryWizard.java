/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.cubeon.ui.taskelemet;

import java.awt.Component;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.netbeans.cubeon.tasks.spi.TaskRepository;
import org.netbeans.cubeon.ui.taskelemet.NewTaskWizardAction.WizardObject;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;

class ChooseRepositoryWizard implements WizardDescriptor.Panel<WizardObject> {

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private ChooseRepository component;
    private List<TaskRepository> repositorys;

    public ChooseRepositoryWizard( List<TaskRepository> repositorys) {

        this.repositorys = repositorys;
    }
    
    
    
    
    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    public Component getComponent() {
        if (component == null) {
            component = new ChooseRepository(this,repositorys);
        }
        return component;
    }

    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        return HelpCtx.DEFAULT_HELP;
    // If you have context help:
    // return new HelpCtx(SampleWizardPanel1.class);
    }

    public boolean isValid() {

        return component.isTaskRepositorySelected();

    }
    private final Set<ChangeListener> listeners = new HashSet<ChangeListener>(1); 

    public final void addChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }

    public final void removeChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }

    final void fireChangeEvent() {
        Iterator<ChangeListener> it;
        synchronized (listeners) {
            it = new HashSet<ChangeListener>(listeners).iterator();
        }
        ChangeEvent ev = new ChangeEvent(this);
        while (it.hasNext()) {
            it.next().stateChanged(ev);
        }
    }
    // You can use a settings object to keep track of state. Normally the
    // settings object will be the WizardDescriptor, so you can use
    // WizardDescriptor.getProperty & putProperty to store information entered
    // by the user.
    public void readSettings(WizardObject settings) {
    }

    public void storeSettings(WizardObject settings) {
        settings.setRepository(component.getTaskRepository());
    }
}

