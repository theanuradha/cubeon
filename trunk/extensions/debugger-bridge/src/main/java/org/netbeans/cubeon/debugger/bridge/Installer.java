/*
 *  Copyright 2009 Anuradha.
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
package org.netbeans.cubeon.debugger.bridge;

import org.netbeans.api.debugger.Breakpoint;
import org.netbeans.api.debugger.DebuggerManager;
import org.netbeans.api.debugger.DebuggerManagerAdapter;
import org.netbeans.cubeon.tasks.core.api.CubeonContext;
import org.netbeans.cubeon.tasks.core.api.CubeonContextListener;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.openide.modules.ModuleInstall;
import org.openide.util.Lookup;

/**
 * Manages a module's lifecycle. Remember that an installer is optional and
 * often not needed at all.
 */
public class Installer extends ModuleInstall {

    @Override
    public void restored() {
        final DebuggerManager debuggerManager = DebuggerManager.getDebuggerManager();
        final CubeonContext context = Lookup.getDefault().lookup(CubeonContext.class);
        assert context != null;

        context.addContextListener(new CubeonContextListener() {

            public void taskActivated(TaskElement task) {
                Breakpoint[] breakpoints = debuggerManager.getBreakpoints();
                for (Breakpoint breakpoint : breakpoints) {
                    if (isAssosoateBreakPoint(task, breakpoint)) {
                        breakpoint.enable();
                    }
                }
            }

            public void taskDeactivated(TaskElement task) {
                Breakpoint[] breakpoints = debuggerManager.getBreakpoints();
                for (Breakpoint breakpoint : breakpoints) {
                    if (isAssosoateBreakPoint(task, breakpoint)) {
                        breakpoint.disable();
                    }
                }
            }
        });

        debuggerManager.addDebuggerListener(new DebuggerManagerAdapter() {

            @Override
            public void breakpointAdded(Breakpoint breakpoint) {
                if (context.getActive() != null) {

                    breakpoint.setGroupName(encodeGroupName(context.getActive()));
                }
            }
        });


    }

    private boolean isAssosoateBreakPoint(TaskElement task, Breakpoint breakpoint) {
        String displayName = task.getDisplayName();
        String decodeGroupName = decodeGroupName(task);
        //check for both decode group name and display name to backward compatibility
        return breakpoint.getGroupName().startsWith(decodeGroupName) || displayName.equals(breakpoint.getGroupName());
    }

    private String encodeGroupName(TaskElement task) {

        String groupName = decodeGroupName(task) +
                " - " + task.getDescription();

        return groupName.trim();

    }

    private String decodeGroupName(TaskElement task) {
        String repoId = task.getTaskRepository().getId();
        return repoId + ":" + task.getId();
    }
}
