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

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.SAXException;

/**
 * //@todo class description
 *
 * @author radoslaw.holewa
 */
public abstract class BaseSaxHandler extends DefaultHandler {

    public static final String TRUE_VALUE = "1";
    
    protected StringBuffer elementContent;

    /**
     * Copies content of actualy processed element to elementContent variable.
     * 
     * @param ch
     * @param start
     * @param length
     * @throws org.xml.sax.SAXException
     */
    public void characters(char ch[], int start, int length) throws SAXException {
        elementContent.append(ch, start, length);
    }
}
