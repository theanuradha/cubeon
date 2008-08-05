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

import java.util.ArrayList;
import java.util.List;
import org.netbeans.cubeon.analyzer.spi.StackTrace;
import org.netbeans.cubeon.analyzer.spi.StackTraceProvider;
import org.netbeans.cubeon.context.api.TaskContextManager;
import org.netbeans.cubeon.context.spi.TaskResource;
import org.netbeans.cubeon.context.spi.TaskResourceSet;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha
 */
public class STResourceSet implements TaskResourceSet {

    private final TaskElement element;
    private final STResourceProvider provider;
    private final TaskContextManager contextManager;

    public STResourceSet(TaskElement element, STResourceProvider provider) {
        this.element = element;
        this.provider = provider;
        contextManager = Lookup.getDefault().lookup(TaskContextManager.class);
    }

    public String getName() {
        return "Stack Trace(s)";
    }

    public Node getNode() {
        return new STResourcesNode(element, this);
    }

    public Lookup getLookup() {
        return Lookups.fixed(this);
    }

    public void addTaskResource(TaskResource tr) {
        //not aplicable
    }

    public void removeTaskResource(TaskResource tr) {
        //not aplicable
    }

    public List<TaskResource> getResources() {
        StackTraceProvider stp = element.getLookup().lookup(StackTraceProvider.class);
        assert stp != null;
        List<TaskResource> resources = new ArrayList<TaskResource>();
        List<StackTrace> analyze = stp.analyze();
        for (StackTrace trace : analyze) {
            resources.add(new STResource(trace));
        }
        return resources;
    }
}
