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
package org.netbeans.cubeon.context.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.netbeans.cubeon.context.api.TaskContextHandler;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.Mutex;
import org.openide.xml.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Anuradha
 */
public class ContextHandlerImpl implements TaskContextHandler {

  
    private static final String TAG_ROOT = "context";//NOI18N
    private final FileObject contextdir;
    private final TaskElement element;
    private final Mutex mutex = new Mutex();

    public ContextHandlerImpl(FileObject contextdir, TaskElement element) {
        this.contextdir = contextdir;
        this.element = element;
    }

    public Mutex getMutex() {
        return mutex;
    }

    public Document getContextDocument() {
        assert mutex.isReadAccess()|| mutex.isWriteAccess();
        final FileObject config = contextdir.getFileObject(element.getId() + ".xml");
        Document doc = null;
        if (config != null) {
            InputStream in = null;
            try {
                in = config.getInputStream();
                doc = XMLUtil.parse(new InputSource(in), false, true, null, null);

            } catch (SAXException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }



        } else {
            doc = XMLUtil.createDocument("context", null, null, null);

        }
        return doc;
    }

    public Element getRootElement(Document doc) {
        Element rootElement = doc.getDocumentElement();
        if (rootElement == null) {
            rootElement = doc.createElement(TAG_ROOT);
        }
        return rootElement;
    }

    public void saveContextDocument(Document doc) {
        assert mutex.isWriteAccess();
        FileObject config = contextdir.getFileObject(element.getId() + ".xml");

        FileLock lck = null;
        OutputStream out = null;
        try {
            if (config == null) {

                config = contextdir.createData(element.getId() + ".xml");
            }
            lck = config.lock();
            out = config.getOutputStream(lck);
            XMLUtil.write(doc, out, "UTF-8"); //NOI18N
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            if (lck != null) {
                lck.releaseLock();
            }
        }

    }
}
