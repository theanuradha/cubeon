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
package org.netbeans.cubeon.java.bridge.stacktrace;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.text.StyledDocument;
import org.netbeans.api.java.classpath.GlobalPathRegistry;
import org.netbeans.cubeon.analyzer.spi.StackTrace.Line;
import org.netbeans.cubeon.tasks.core.api.TagNode;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.LineCookie;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.text.Line.Set;
import org.openide.text.Line.ShowOpenType;
import org.openide.text.Line.ShowVisibilityType;
import org.openide.util.NbBundle;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha G
 */
public class STNode extends AbstractNode {

    private STResource resource;

    public STNode(STResource resource) {
        super(new ResourcesChildern(resource), Lookups.fixed(resource));
        this.resource = resource;
        setDisplayName(resource.getName());
        setShortDescription(resource.getDescription());
    }

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.loadImage("org/netbeans/cubeon/java/bridge/stacktrace.png");
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[0];
    }

    private static class ResourcesChildern extends Children.Keys<Line> {

        private STResource resource;

        public ResourcesChildern(STResource resource) {
            this.resource = resource;

        }

        @Override
        protected Node[] createNodes(Line resource) {
            String name = resource.getName();
            OpenLine openLine = new OpenLine(resource);
            return new Node[]{TagNode.createNode(name, name,
                        ImageUtilities.loadImage("org/netbeans/cubeon/java/bridge/line.png"),
                        openLine, new Action[]{openLine})
                    };
        }

        @Override
        protected void addNotify() {


            setKeys(resource.getTrace().getLines());


        }
    }

    void refresh() {
        setChildren(new ResourcesChildern(resource));
    }

    private static class OpenLine extends AbstractAction {

        private final Line line;

        public OpenLine(Line line) {
            this.line = line;
            putValue(NAME, NbBundle.getMessage(STNode.class, "LBL_Open_Source"));
        }

        public void actionPerformed(ActionEvent e) {


            if (line.getClazz() != null) {
                FileObject fileObject = GlobalPathRegistry.getDefault().findResource(line.getClazz());
                if (fileObject != null) {
                    doOpen(fileObject, line.getLineNumber());
                }
            }

        }

        private static boolean doOpen(FileObject fo, int line) {
            try {
                DataObject od = DataObject.find(fo);
                EditorCookie ec = od.getCookie(EditorCookie.class);
                LineCookie lc = od.getCookie(LineCookie.class);

                if (ec != null && lc != null && line != -1) {
                    StyledDocument doc = ec.openDocument();
                    if (doc != null) {
                        if (line != -1) {
                            Set lineSet = lc.getLineSet();
                            //Line l = lc.getLineSet().getCurrent(line-1);

                            if (lineSet.getCurrent(line - 1) != null) {
                                lineSet.getCurrent(line - 1).show(ShowOpenType.OPEN, ShowVisibilityType.FOCUS);
                                return true;
                            }
                        }
                    }
                }

                OpenCookie oc = (OpenCookie) od.getCookie(OpenCookie.class);

                if (oc != null) {
                    oc.open();
                    return true;
                }
            } catch (IOException e) {
                Logger.getLogger(STResource.class.getName()).warning(e.getMessage());
            }

            return false;
        }
    }
}
