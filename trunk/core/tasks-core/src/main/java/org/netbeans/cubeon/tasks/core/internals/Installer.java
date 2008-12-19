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
package org.netbeans.cubeon.tasks.core.internals;

import java.util.List;
import org.netbeans.cubeon.tasks.core.api.CubeonContext;
import org.netbeans.cubeon.tasks.core.api.TaskRepositoryHandler;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.openide.modules.ModuleInstall;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;

/**
 * Task core lifecycle.
 */
public class Installer extends ModuleInstall {

    private final static int MILIS_IN_MIN = 60000;

    @Override
    public void restored() {
        schedule(MILIS_IN_MIN * 2);
    }

    private void schedule(final int delay) {
        RequestProcessor.getDefault().post(new Runnable() {

            public void run() {
                final CubeonContext context = Lookup.getDefault().lookup(CubeonContext.class);

                final TaskRepositoryHandler handler = context.getLookup().lookup(TaskRepositoryHandler.class);

                List<TaskRepository> taskRepositorys = handler.getTaskRepositorys();
                for (TaskRepository repository : taskRepositorys) {
                    //synchronize repo
                    repository.synchronize();
                }
                schedule(MILIS_IN_MIN * 20);//TODO move this to option
            }
        }, delay);
    }
}
