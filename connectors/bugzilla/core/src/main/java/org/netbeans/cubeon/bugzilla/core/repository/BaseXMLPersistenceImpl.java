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
package org.netbeans.cubeon.bugzilla.core.repository;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.xml.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Contains common XML processing logic.
 *
 * @author radoslaw.holewa
 */
public class BaseXMLPersistenceImpl {

    /**
     * Creates complete content for given root element and it's childrens provided as a map of values.
     *
     * @param rootElement - root element, it will contain child elements created using provided map
     * @param values      - map of values
     * @param document    - document, it will be used to create child elements
     * @return - filled root element
     */
    public Element createCompleteElement(Element rootElement, Map<String, String> values, Document document) {
        Element childElement = null;
        for (String name : values.keySet()) {
            childElement = document.createElement(name);
            childElement.setTextContent(values.get(name));
            rootElement.appendChild(childElement);
        }
        return rootElement;
    }

    /**
     * Saves document to file.
     *
     * @param document - document to save
     */
    public void saveDocumentToFile(Document document, FileObject file) {
        FileLock fileLock = null;
        OutputStream out = null;
        try {
            fileLock = file.lock();
            out = file.getOutputStream(fileLock);
            XMLUtil.write(document, out, "UTF-8");
        } catch (IOException ex) {
            Exceptions.attachMessage(ex, "Error while saving DOM document to file.");
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            if (fileLock != null) {
                fileLock.releaseLock();
            }
        }
    }
}
