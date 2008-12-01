/*
 *  Copyright 2008 Tomas Knappek.
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

package org.netbeans.cubeon.persistence;

import org.netbeans.cubeon.javanet.persistence.JavanetRepoPersistence;
import org.netbeans.cubeon.javanet.persistence.impl.JavanetXmlPersistenceFactory;


/**
 *
 * @author Tomas Knappek
 */
public abstract class PersistenceFactory {

    public static final <E extends Persistence> E getPersistence(Class<E> c) {
        return getFactory(c).createPersistence(c);
    }

    /**
     * private factory method
     * @param <E>
     * @param c
     * @return
     */
    private static <E extends Persistence> PersistenceFactory getFactory(Class<E> c) {
        if (c.equals(JavanetRepoPersistence.class)) {
            return new JavanetXmlPersistenceFactory();
        } else {
            throw new IllegalArgumentException("Unknown class: " + c);
        }
    }

    protected abstract <E extends Persistence> E createPersistence(Class<E> c);

}
