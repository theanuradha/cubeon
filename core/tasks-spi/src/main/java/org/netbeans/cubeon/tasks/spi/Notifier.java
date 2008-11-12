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
package org.netbeans.cubeon.tasks.spi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.WeakHashMap;
import java.util.logging.Logger;

/**
 *
 * @author Anuradha G
 */
public class Notifier<T> {

    private final WeakHashMap<T, T> weakHashMap = new WeakHashMap<T, T>();
    private final static Logger Notifier = Logger.getLogger(Notifier.class.getName());

    /**
     * 
     * @param inst
     */
    public final void remove(T inst) {
        weakHashMap.remove(inst);
        Notifier.info("ON remove SIZE :" + weakHashMap.size());
    }

    /**
     * 
     * @param inst
     */
    public final void add(T inst) {
        weakHashMap.put(inst, inst);
        Notifier.info("ON ADD SIZE :" + weakHashMap.size());
    }

    public final Collection<T> getAll() {
        List<T> ts = new ArrayList<T>();
        for (T t : weakHashMap.values()) {
            if (t != null) {
                ts.add(t);
            }
        }
        return ts;
    }
}
