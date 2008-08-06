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
package org.netbeans.cubeon.ui.internals;

import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.Action;
import org.netbeans.cubeon.tasks.core.api.CubeonContext;
import org.netbeans.cubeon.tasks.core.api.CubeonContextListener;
import org.netbeans.cubeon.tasks.core.api.TaskFolder;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.tasks.spi.task.TaskBadgeProvider;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.netbeans.cubeon.tasks.spi.Extension;
import org.netbeans.cubeon.tasks.spi.repository.TaskPriorityProvider;
import org.netbeans.cubeon.tasks.spi.repository.TaskStatusProvider;
import org.netbeans.cubeon.tasks.spi.repository.TaskTypeProvider;
import org.netbeans.cubeon.tasks.spi.task.TaskElementActionsProvider;
import org.netbeans.cubeon.tasks.spi.task.TaskElementChangeAdapter;
import org.netbeans.cubeon.tasks.spi.task.TaskStatus;
import org.netbeans.cubeon.ui.taskelemet.CopyDetailsAction;
import org.netbeans.cubeon.ui.taskelemet.MoveToAction;
import org.netbeans.cubeon.ui.taskelemet.MoveToDefault;
import org.netbeans.cubeon.ui.taskelemet.OpenAction;
import org.netbeans.cubeon.ui.taskelemet.OpenInBrowserAction;
import org.netbeans.cubeon.ui.taskelemet.SynchronizeTaskAction;
import org.netbeans.cubeon.ui.taskelemet.TaskActiveAction;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataLoader;
import org.openide.loaders.DataLoaderPool;
import org.openide.loaders.DataObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Anuradha G
 */
public class TaskElementNode extends AbstractNode {

    private static final String TAG = "<font color=\"#808080\"> <s> ";//NOI18N
    private static final Logger LOG = Logger.getLogger(TaskElementNode.class.getName());
    private final TaskElement element;
    private final Extension extension;
    private SaveCookie cookie;
    private InstanceContent content;
    private DataObject dataObject;
    private final TaskElementChangeAdapter changeAdapter;
    private final CubeonContextListener contextListener;
    private TaskFolder container;
    private boolean extendedActions;
    private final CubeonContext context;

    public static TaskElementNode createNode(Children children, final TaskFolder container, final TaskElement element, boolean extendedActions) {

        InstanceContent content = new InstanceContent();
        content.add(element);
        final TaskElementNode node = new TaskElementNode(children, container, element, content);

        node.content = content;
        node.cookie = new SaveCookie() {

            public void save() throws IOException {
                node.setModified(false);
            }
        };
        node.extendedActions = extendedActions;

        return node;
    }

    public static TaskElementNode createNode(Children children, final TaskElement element, SaveCookie cookie) {
        InstanceContent content = new InstanceContent();
        content.add(element);
        final TaskElementNode node = new TaskElementNode(children, null, element, content);

        node.content = content;
        node.cookie = cookie;
        node.extendedActions = true;

        return node;
    }

    private TaskElementNode(Children children, final TaskFolder container, final TaskElement element, InstanceContent content) {
        super(children, new AbstractLookup(content));
        context = Lookup.getDefault().lookup(CubeonContext.class);
        this.container = container;
        this.element = element;
        setDisplayName(element.getName());
        setShortDescription(extractTaskDescription(element));
        extension = element.getLookup().lookup(Extension.class);

        this.cookie = new SaveCookie() {

            public void save() throws IOException {
                setModified(false);
            }
        };
        contextListener = new CubeonContextListener() {

            public void taskActivated(TaskElement element) {
                if (element.equals(element)) {
                    fireDisplayNameChange(getDisplayName() + "_#", element.getName());//NOI18N
                }
            }

            public void taskDeactivated(TaskElement element) {
                if (element.equals(element)) {
                    fireDisplayNameChange(getDisplayName() + "_#", element.getName());//NOI18N
                }
            }
        };
        context.addContextListener(contextListener);
        changeAdapter = new TaskElementChangeAdapter() {

            @Override
            public void nameChenged() {
                setDisplayName(element.getName());
                setShortDescription(extractTaskDescription(element));
            }

            @Override
            public void priorityChenged() {
                fireIconChange();
                setShortDescription(extractTaskDescription(element));
            }

            @Override
            public void typeChenged() {
                fireIconChange();
                setShortDescription(extractTaskDescription(element));
            }

            @Override
            public void statusChenged() {
                fireDisplayNameChange(getDisplayName() + "_#", element.getName());//NOI18N
                setShortDescription(extractTaskDescription(element));
            }

            @Override
            public void stateChange() {
                fireIconChange();
            }
        };
        extension.add(changeAdapter);
    }

    private static String extractTaskDescription(TaskElement element) {
        StringBuffer buffer = new StringBuffer("<html>");//NOI18N
        buffer.append("<b>").append(element.getId()).append(" :</b> ");//NOI18N
        buffer.append(element.getName()).append("<p>");//NOI18N
        // buffer.append("<img src=\"").append("ADDURL").append("\" width=\"7\" height=\"16\" />");
        TaskRepository repository = element.getTaskRepository();
        TaskPriorityProvider tpp = repository.getLookup().lookup(TaskPriorityProvider.class);
        if (tpp != null) {
            buffer.append(tpp.getTaskPriority(element).toString()).append(", ");//NOI18N
        }
        TaskTypeProvider ttp = repository.getLookup().lookup(TaskTypeProvider.class);
        if (ttp != null) {
            buffer.append(ttp.getTaskType(element).toString());
        }
        TaskStatusProvider tsp = repository.getLookup().lookup(TaskStatusProvider.class);
        if (tsp != null) {
            TaskStatus taskStatus = tsp.getTaskStatus(element);
            if (taskStatus != null) {
                buffer.append("<p>").append(taskStatus.toString());//NOI18N
            }
        }
        buffer.append("</html>");//NOI18N
        return buffer.toString();

    }

    public boolean isModified() {
        return getDataObject().isModified();
    }

    public void setModified(boolean modified) {
        setDisplayName(element.getName() + (modified ? "*" : ""));//NOI18N
        getDataObject().setModified(modified);
        if (modified) {
            content.add(cookie);
        } else {
            content.remove(cookie);
        }
    }

    @Override
    public Action getPreferredAction() {
        return new OpenAction(element);
    }

    @Override
    public Action[] getActions(boolean arg0) {
        List<Action> actions = new ArrayList<Action>();
        actions.add(new OpenAction(element));
        actions.add(new OpenInBrowserAction(element));
        actions.add(new TaskActiveAction(element));
        actions.add(null);
        actions.add(new CopyDetailsAction(element));
        if (container != null) {
            actions.add(new MoveToDefault(container, element));
        }

        boolean sepetatorAdded = false;
        actions.add(new MoveToAction(container, element));

        if (extendedActions) {



            final List<TaskElementActionsProvider> providers =
                    new ArrayList<TaskElementActionsProvider>(
                    Lookup.getDefault().lookupAll(TaskElementActionsProvider.class));

            for (TaskElementActionsProvider provider : providers) {
                Action[] as = provider.getActions(element);
                for (Action action : as) {
                    //check null and addSeparator
                    if (action == null) {
                        //check sepetatorAdd to prevent adding duplicate Separators
                        if (!sepetatorAdded) {
                            //mark sepetatorAdd to true
                            sepetatorAdded = true;
                            actions.add(action);

                        }
                        continue;
                    }
                    actions.add(action);
                    sepetatorAdded = false;
                }
            }
        }
        if (!sepetatorAdded) {
            actions.add(null);
        }
        actions.add(new SynchronizeTaskAction(element));
        return actions.toArray(new Action[0]);
    }

    @Override
    public String getHtmlDisplayName() {
        StringBuffer buffer = new StringBuffer("<html>");//NOI18N

        if (element.isCompleted()) {

            buffer.append(TAG);
        }
        if (element.equals(context.getActive())) {

            buffer.append("<b>");//NOI18N
        }

        buffer.append("<xmp>");//NOI18N
        buffer.append(element.getDisplayName());
        buffer.append("</xmp>");//NOI18N
        buffer.append("</html>");//NOI18N
        return buffer.toString();
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }

    @Override
    public Image getIcon(int arg0) {

        Image image = element.getImage();
        //badging task element with bages
        Collection<? extends TaskBadgeProvider> badgeProviders =
                Lookup.getDefault().lookupAll(TaskBadgeProvider.class);

        for (TaskBadgeProvider provider : badgeProviders) {
            image = provider.bageTaskIcon(element, image);

        }

        return image;
    }

    private DataObject getDataObject() {
        synchronized (this) {
            if (dataObject == null) {
                try {
                    FileObject file = new TaskElementFileObject(element, this);
                    //set preferred  loader to our one 
                    DataLoaderPool.setPreferredLoader(file, DataLoader.getLoader(TaskElementLoader.class));
                    dataObject = DataObject.find(file);
                    assert dataObject != null;
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
        return dataObject;
    }

    @Override
    protected void finalize() throws Throwable {
        context.removeContextListener(contextListener);
        extension.remove(changeAdapter);
        super.finalize();
        LOG.fine(new StringBuffer("Finalize Node :").append(element.getDisplayName()).toString());//NOI18N
    }
}


