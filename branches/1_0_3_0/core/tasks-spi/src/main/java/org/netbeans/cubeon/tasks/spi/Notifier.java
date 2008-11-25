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

    private final WeakHashMap<NotifierReference<T>, T> weakHashMap =
            new WeakHashMap<NotifierReference<T>, T>();
    private final static Logger Notifier = Logger.getLogger(Notifier.class.getName());

    /**
     * 
     * @param inst
     */
    public final void remove(NotifierReference<T> reference) {
        weakHashMap.remove(reference);
        Notifier.fine("ON remove SIZE :" + weakHashMap.size());
    }

    /**
     * 
     * @param inst
     */
    public final NotifierReference<T> add(T inst) {
        NotifierReference<T> notifierReference=new NotifierReference<T>(inst);
        weakHashMap.put(notifierReference, inst);
        Notifier.fine("ON ADD SIZE :" + weakHashMap.size());
        return notifierReference;
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

    public final static class NotifierReference<T> {

        private T t;

        public NotifierReference(T t) {
            this.t = t;
        }

        public T getT() {
            return t;
        }
    }
}
