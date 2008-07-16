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
package org.netbeans.cubeon.tasks.core.api;

import java.awt.Image;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Utilities;

/**
 *
 * @author Anuradha G
 */
public class TagNode extends AbstractNode {

    private Action preferredAction;
    private Action[] actions;
    private Image image = Utilities.loadImage("org/netbeans/cubeon/tasks/core/empty.png");

    private TagNode(String name, String description) {
        super(Children.LEAF);
        setDisplayName(name);
        setShortDescription("<html>"+description+"</html>");
    }

    @Override
    public String getHtmlDisplayName() {
        return getDisplayName();
    }

    @Override
    public Action getPreferredAction() {

        return preferredAction != null ? preferredAction : super.getPreferredAction();
    }

    @Override
    public Action[] getActions(boolean context) {
        return actions != null ? actions : new Action[0];
    }

    @Override
    public Image getIcon(int type) {
        return image;
    }

    public static Node createNode(String name, String description) {
        return new TagNode(name, description);
    }

    public static Node createNode(String name, String description,
            Action preferredAction, Action... actions) {
        TagNode node = new TagNode(name, description);
        node.preferredAction = preferredAction;
        node.actions = actions;
        return node;
    }
}
