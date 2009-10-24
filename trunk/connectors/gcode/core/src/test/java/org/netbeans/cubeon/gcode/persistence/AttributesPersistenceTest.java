/*
 *  Copyright 2009 Anuradha.
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

package org.netbeans.cubeon.gcode.persistence;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import junit.framework.TestCase;

/**
 *
 * @author Anuradha
 */
public class AttributesPersistenceTest extends TestCase {
    
    public AttributesPersistenceTest(String testName) {
        super(testName);
    }

    public void testPersistAttributes() throws IOException {
        File file = File.createTempFile("AttributesPersistenceTest", null);
        System.out.println("testPersistAttributes");
        AttributesPersistence persistence = new AttributesPersistence(file);
        persistence.loadDefultAttributes();
        persistence.persistAttributes();
        System.out.println(new String(fileToByteArray(file)));
         AttributesPersistence savedPersistence = new AttributesPersistence(file);
         assertTrue(persistence.getLabels().containsAll(savedPersistence.getLabels()));
         assertTrue(persistence.getClosedStatuses().containsAll(savedPersistence.getClosedStatuses()));
         assertTrue(persistence.getOpenStatueses().containsAll(savedPersistence.getOpenStatueses()));
    }

byte[] fileToByteArray(File file) throws FileNotFoundException, IOException {
        byte[] buffer = new byte[(int) file.length()];
        BufferedInputStream input = new BufferedInputStream(new FileInputStream(file));
        input.read(buffer, 0, buffer.length);
        input.close();
        return buffer;
    }
}
