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
package org.netbeans.cubeon.bugzilla.api.post.handler;

import org.netbeans.cubeon.bugzilla.api.model.RepositoryAttributes;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * SAX handler, it will parse configuration setings received from Bugzilla repository.
 *
 * @author radoslaw.holewa
 */
public class SaxRepositoryConfigurationHandler extends BaseSaxHandler {

    /**
     * Repository configuration, it contains product names, components, versions etc.
     */
    private RepositoryAttributes repositoryConfiguration;

    public void startDocument() throws SAXException {
        repositoryConfiguration = new RepositoryAttributes();
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
    }

    /**
     * Returns repository configuration, it contains product names, components, versions etc.
     *
     * @return - repository configuration
     */
    public RepositoryAttributes getRepositoryConfiguration() {
        return repositoryConfiguration;
    }
}
