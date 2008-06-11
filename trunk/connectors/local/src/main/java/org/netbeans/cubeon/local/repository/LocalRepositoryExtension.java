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
import org.netbeans.cubeon.tasks.spi.Extension;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Anuradha
 */
public class LocalRepositoryExtension implements Extension {

    private LocalTaskRepository repository;
    private InstanceContent content;
    private Lookup lookup;

    public LocalRepositoryExtension(LocalTaskRepository repository) {
        this.repository = repository;
        content = new InstanceContent();
        lookup = new AbstractLookup(content);
    }

    public String getHtmlDisplayName() {

        return repository.getName();
    }

    public final void remove(Object inst) {
        content.remove(inst);
    }

    public final void add(Object inst) {
        content.add(inst);
    }

    public Image getImage() {
        return Utilities.loadImage("org/netbeans/cubeon/local/nodes/local-repository.png");
    }

    public Lookup getLookup() {
        return lookup;
    }
    //events---------------------------
}
