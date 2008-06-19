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
import javax.swing.Action;
import org.netbeans.cubeon.tasks.spi.TaskBadgeProvider;
import org.netbeans.cubeon.tasks.spi.TaskElement;
import org.netbeans.cubeon.tasks.spi.Extension;
import org.netbeans.cubeon.tasks.spi.TaskElementActionsProvider;
import org.netbeans.cubeon.tasks.spi.TaskElementChangeAdapter;
import org.netbeans.cubeon.ui.taskelemet.OpenAction;
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

    private static final String TAG = "<font color=\"#808080\"> <s> ";
    private final TaskElement element;
    private final Extension extension;
    private SaveCookie cookie;
    private InstanceContent content;
    private DataObject dataObject;
    private final TaskElementChangeAdapter changeAdapter;

    public static TaskElementNode createNode(final TaskElement element) {
        InstanceContent content = new InstanceContent();
        content.add(element);
        final TaskElementNode node = new TaskElementNode(element, content);

        node.content = content;
        node.cookie = new SaveCookie() {

            public void save() throws IOException {
                node.setModified(false);
            }
        };
        return node;
    }

    public static TaskElementNode createNode(final TaskElement element, SaveCookie cookie) {
        InstanceContent content = new InstanceContent();
        content.add(element);
        final TaskElementNode node = new TaskElementNode(element, content);

        node.content = content;
        node.cookie = cookie;


        return node;
    }

    private TaskElementNode(final TaskElement element, InstanceContent content) {
        super(Children.LEAF, new AbstractLookup(content));
        this.element = element;
        setDisplayName(element.getName());
        setShortDescription(extractTaskDescription(element));
        extension = element.getLookup().lookup(Extension.class);

        this.cookie = new SaveCookie() {

            public void save() throws IOException {
                setModified(false);
            }
        };
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
                fireDisplayNameChange(getDisplayName() + "_#", element.getName());
                setShortDescription(extractTaskDescription(element));
            }
        };
        extension.add(changeAdapter);
    }

    private static String extractTaskDescription(TaskElement element) {
        StringBuffer buffer = new StringBuffer("<html>");
        buffer.append("<b>").append(element.getId()).append(" :</b> ");
        buffer.append(element.getName()).append("<p>");
        // buffer.append("<img src=\"").append("ADDURL").append("\" width=\"7\" height=\"16\" />");
        buffer.append(element.getPriority().toString()).append(", ").
                append(element.getType().getText());
        buffer.append("<p>").append(element.getStatus().getText());
        buffer.append("</html>");
        return buffer.toString();

    }

    @Override
    public void destroy() throws IOException {
        super.destroy();
        extension.remove(changeAdapter);
    }

    public void setModified(boolean modified) {
        setDisplayName(element.getName() + (modified ? "*" : ""));
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
        final List<TaskElementActionsProvider> providers =
                new ArrayList<TaskElementActionsProvider>(
                Lookup.getDefault().lookupAll(TaskElementActionsProvider.class));
        boolean sepetatorAdded = false;
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
        return actions.toArray(new Action[0]);
    }

    @Override
    public String getHtmlDisplayName() {
        StringBuffer buffer = new StringBuffer("<html>");
        if (element.isCompleted()) {

            buffer.append(TAG);
        }
        buffer.append(element.getName());
        buffer.append("</html>");
        return buffer.toString();
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
}


