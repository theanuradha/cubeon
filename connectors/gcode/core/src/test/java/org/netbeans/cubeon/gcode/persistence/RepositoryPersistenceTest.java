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
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;

/**
 *
 * @author Anuradha
 */
public class RepositoryPersistenceTest extends TestCase {

    public RepositoryPersistenceTest(String testName) {
        super(testName);
    }

    public void testRepositoryInfos() throws Exception {
        File file = File.createTempFile("RepositoryPersistenceTest", null);
        System.out.println("testRepositoryInfos");
        List<RepositoryInfo> repositoryInfos = new ArrayList<RepositoryInfo>();
        repositoryInfos.add(new RepositoryInfo("test1", "Test 1", "Test Desc 1", "test-project1",
                "user", "password"));
        repositoryInfos.add(new RepositoryInfo("test2", "Test 2", "Test Desc 2", "test-project2",
                "user", "password"));
        RepositoryPersistence instance = new RepositoryPersistence(file);
        instance.persistRepositoryInfos(repositoryInfos);
        System.out.println(new String(fileToByteArray(file)));
        List<RepositoryInfo> result = instance.getRepositoryInfos();
        assertEquals(2, result.size());

    }

    byte[] fileToByteArray(File file) throws FileNotFoundException, IOException {
        byte[] buffer = new byte[(int) file.length()];
        BufferedInputStream input = new BufferedInputStream(new FileInputStream(file));
        input.read(buffer, 0, buffer.length);
        input.close();
        return buffer;
    }
}
