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

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.cubeon.persistence.Persistence;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.util.Exceptions;
import org.openide.xml.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Tomas Knappek
 */
public abstract class AbstractXmlPersistence<T> implements Persistence<T> {

    private Document _doc = null;

    protected static final String TAG_ID = "id";

    protected abstract String getFileName();
    protected abstract String getBaseDir();
    protected abstract String getTagRoot();
    protected abstract String getTagElements();
    protected abstract String getTagElement();
    protected abstract T mapToObject(Element e);
    protected abstract Element mapToElement(T t);

    

    public List<String> getAllIds() {
        return getElementIds();
    }

    public T getById(String id) {
        Element e = getDocument().getElementById(id);
        return mapToObject(e);
    }

    public void remove(String id) {
        removeElement(id);
    }

    public void add(T type) {
        Element e = mapToElement(type);
        addElement(e);
    }

    public List<T> getAll() {
        NodeList all = getDocument().getElementsByTagName(getTagElement());
        List<T> ret = new ArrayList<T>();
        for (int i = 0; i < all.getLength(); i++) {
            Node node = all.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                ret.add(mapToObject((Element) node));
            }

        }
        return ret;
    }

    public void save() {
        saveDocument();
    }


    protected final Document getDocument() {
        if (_doc == null) {
            InputStream in = null;
            try {
                FileObject file = getFile(getBaseDir(), getFileName(), false);
                if (file != null) {
                    in = file.getInputStream();
                    _doc = XMLUtil.parse(new InputSource(in), false, true, null, null);
                } else {
                    assert getTagRoot() != null;
                    _doc = XMLUtil.createDocument(getTagRoot(), null, null, null);
                    _doc.getDocumentElement().appendChild(_doc.createElement(getTagElements()));
                }
            } catch (SAXException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } finally {
                closeStream(in);
            }
        }
        return _doc;
    }

    protected final void saveDocument() {
        if (_doc != null) {
            FileLock lck = null;
            OutputStream out = null;
            try {
                FileObject file = getFile(getBaseDir(), getFileName(), true);               
                lck = file.lock();
                out = file.getOutputStream(lck);
                XMLUtil.write(_doc, out, "UTF-8"); //NOI18N
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } finally {
                closeStream(out);
                if (lck != null) {
                    lck.releaseLock();
                }
            }
        }
    }

    protected final void addElement(Element element) {
        if (element.hasAttribute(TAG_ID)) {
            String id = element.getAttribute(TAG_ID);
            removeElement(id);
        }
        saveDocument();
        Element elements = findElement(getDocument().getDocumentElement(), getTagElements());
        elements.appendChild(element);

    }

    protected final void removeElement(String id) {
        if (id == null) return;
        Element elements = findElement(getDocument().getDocumentElement(), getTagElements());
        NodeList children = elements.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                if (element.hasAttribute(TAG_ID)) {
                    String eid = element.getAttribute(TAG_ID);
                    if (id.equals(eid)) {
                        elements.removeChild(element);
                        break;
                    }
                }
            }
        }
    }

    protected final List<String> getElementIds() {
        List<String> ids = new ArrayList<String>();
        NodeList list = getDocument().getElementsByTagName(getTagElement());
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                if (element.hasAttribute(TAG_ID)) {
                    ids.add(element.getAttribute(TAG_ID));
                }
            }
        }
        return ids;
    }

    protected static final Element findElement(Element parent, String name) {
        NodeList l = parent.getChildNodes();
        int len = l.getLength();
        for (int i = 0; i < len; i++) {
            if (l.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element el = (Element) l.item(i);
                if (name.equals(el.getNodeName())) {
                    return el;
                }
            }
        }
        return null;
    }

    private static FileObject getFile(String dirname, String filename, boolean create) throws IOException {
        assert dirname != null;
        assert filename != null;
        FileObject dir = FileUtil.createFolder(Repository.getDefault().
                getDefaultFileSystem().getRoot(), dirname);
        assert dir != null;
        FileObject file = dir.getFileObject(filename);
        if (file == null && create) {
            file = dir.createData(filename);
        }
        return file;
    }

    private static void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException ex) {
                //ignore
            }
        }
    }
}
