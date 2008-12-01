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

package org.netbeans.cubeon.javanet.persistence.impl;

import org.netbeans.cubeon.persistence.Persistence;
import org.netbeans.cubeon.persistence.PersistenceFactory;
import org.netbeans.cubeon.persistence.RepoPersistence;

/**
 *
 * @author Tomas Knappek
 */
public class JavanetXmlPersistenceFactory extends PersistenceFactory {

    @Override
    protected <E extends Persistence> E createPersistence(Class<E> c) {
        if (RepoPersistence.class.isAssignableFrom(c)) {
            return (E) new JavanetXmlRepoPersistence();
        } else {
            throw new IllegalArgumentException();
        }
    }

}
