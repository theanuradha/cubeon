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
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Collection;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.cubeon.tasks.core.api.TaskEditorFactory;
import org.netbeans.cubeon.tasks.spi.TaskBadgeProvider;
import org.netbeans.cubeon.tasks.spi.TaskElement;
import org.netbeans.cubeon.tasks.spi.Extension;
import org.netbeans.cubeon.tasks.spi.TaskElementChangeAdapter;
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
    private static final String TAG="<font color=\"#808080\"> <s> ";
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
        setShortDescription(element.getDescription());
        extension = element.getLookup().lookup(Extension.class);
        try {
            FileObject file = new TaskElementFileObject(element, this);
            //set preferred  loader to our one 
            DataLoaderPool.setPreferredLoader(file, DataLoader.getLoader(TaskElementLoader.class));
            dataObject = DataObject.find(file);
            assert dataObject != null;
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        this.cookie = new SaveCookie() {

            public void save() throws IOException {
                setModified(false);
            }
        };
        changeAdapter = new TaskElementChangeAdapter() {

            @Override
            public void nameChenged() {
                setDisplayName(element.getName());
            }

            @Override
            public void descriptionChenged() {
                setShortDescription(element.getDescription());
            }

            @Override
            public void priorityChenged() {
                fireIconChange();
            }

            @Override
            public void typeChenged() {
                fireIconChange();
            }

            @Override
            public void statusChenged() {
                fireDisplayNameChange(getDisplayName() + "_#", element.getName());
            }
        };
        extension.add(changeAdapter);
    }

    @Override
    public void destroy() throws IOException {
        super.destroy();
        extension.remove(changeAdapter);
    }

    public void setModified(boolean modified) {
        setDisplayName(element.getName() + (modified ? "*" : ""));
        dataObject.setModified(modified);
        if (modified) {
            content.add(cookie);
        } else {
            content.remove(cookie);
        }
    }

    @Override
    public Action getPreferredAction() {
        return new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                TaskEditorFactory factory = Lookup.getDefault().lookup(TaskEditorFactory.class);
                factory.createTaskEditor(element);
            }
        };
    }

    @Override
    public Action[] getActions(boolean arg0) {
        return new Action[]{//TODO
                };
    }

    @Override
    public String getHtmlDisplayName() {
        if (element.isCompleted()) {


            String html =TAG;
           
            return html.concat( element.getName());
        }

        return getDisplayName();
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
}


