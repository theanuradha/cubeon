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

import org.netbeans.cubeon.javanet.tasks.JavanetTask;
import org.w3c.dom.Element;

/**
 *
 * @author Tomas Knappek
 */
public class JavanetXmlTaskPersistence extends AbstractXmlPersistence<JavanetTask> {

    @Override
    protected String getFileName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected String getBaseDir() {
        return "cubeon/javanet_repositories";
    }

    @Override
    protected String getTagRoot() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected String getTagElements() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected String getTagElement() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected JavanetTask mapToObject(Element e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected Element mapToElement(JavanetTask t) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
