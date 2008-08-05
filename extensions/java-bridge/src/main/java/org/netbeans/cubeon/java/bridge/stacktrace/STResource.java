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

import org.netbeans.cubeon.analyzer.spi.StackTrace;
import org.netbeans.cubeon.context.spi.TaskResource;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha G
 */
public class STResource implements TaskResource {

    private final StackTrace trace;

    public STResource(StackTrace trace) {
        this.trace = trace;
    }

    public String getName() {
        return trace.getFistLineText();
    }

    public String getDescription() {
        return getName();
    }

    public StackTrace getTrace() {
        return trace;
    }

    public Node getNode() {
        return new STNode(this);
    }

    public void open() {
        //do nothing
    }

    public Lookup getLookup() {
        return Lookups.fixed(this);
    }
}
