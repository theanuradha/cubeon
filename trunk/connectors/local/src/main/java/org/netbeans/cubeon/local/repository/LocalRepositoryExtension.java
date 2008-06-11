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
package org.netbeans.cubeon.local.repository;

import java.awt.Image;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.netbeans.cubeon.tasks.spi.Extension;
import org.openide.util.Utilities;

/**
 *
 * @author Anuradha
 */
public class LocalRepositoryExtension implements Extension {

    private final Set<ChangeAdapter> listeners = new HashSet<ChangeAdapter>(5);
    private LocalTaskRepository repository;

    public LocalRepositoryExtension(LocalTaskRepository repository) {
        this.repository = repository;
    }

    public String getHtmlDisplayName() {

        return repository.getName();
    }

    public void addChangeAdapter(ChangeAdapter adapter) {
        synchronized (listeners) {
            listeners.add(adapter);
        }
    }

    public void removeChangeAdapter(ChangeAdapter adapter) {
        synchronized (listeners) {
            listeners.remove(adapter);
        }
    }

    public Image getImage() {
        return Utilities.loadImage("org/netbeans/cubeon/local/nodes/local-repository.png");
    }
    //events---------------------------
    void fireNameChenged() {
        Iterator<ChangeAdapter> it;
        synchronized (listeners) {
            it = new HashSet<ChangeAdapter>(listeners).iterator();
        }

        while (it.hasNext()) {
            it.next().nameChenged();
        }
    }

    void fireDescriptionChenged() {
        Iterator<ChangeAdapter> it;
        synchronized (listeners) {
            it = new HashSet<ChangeAdapter>(listeners).iterator();
        }

        while (it.hasNext()) {
            it.next().descriptionChenged();
        }
    }

    void firePriorityChenged() {
        Iterator<ChangeAdapter> it;
        synchronized (listeners) {
            it = new HashSet<ChangeAdapter>(listeners).iterator();
        }

        while (it.hasNext()) {
            it.next().priorityChenged();
        }
    }

    void fireStatusChenged() {
        Iterator<ChangeAdapter> it;
        synchronized (listeners) {
            it = new HashSet<ChangeAdapter>(listeners).iterator();
        }

        while (it.hasNext()) {
            it.next().statusChenged();
        }
    }
}
