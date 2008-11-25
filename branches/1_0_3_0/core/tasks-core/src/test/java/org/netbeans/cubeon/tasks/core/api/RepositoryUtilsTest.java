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

package org.netbeans.cubeon.tasks.core.api;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Anuradha
 */
public class RepositoryUtilsTest {

    public RepositoryUtilsTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of encodePassword method, of class RepositoryUtils.
     */
    @Test
    public void testEncodePassword() {
        System.out.println("encodePassword");
        String username = "cubeon";
        String password = "pass";
        String expResult = "\u020c\u021d\u020f\u020f";
        String result = RepositoryUtils.encodePassword(username, password);
         System.out.println(result);
        assertEquals(expResult, result);
       
    }

    /**
     * Test of decodePassword method, of class RepositoryUtils.
     */
    @Test
    public void testDecodePassword() {
        System.out.println("decodePassword");
        String username = "cubeon";
        String encodedPassword = "\u020c\u021d\u020f\u020f";
        String expResult = "pass";
        String result = RepositoryUtils.decodePassword(username, encodedPassword);
        assertEquals(expResult, result);
        
    }

}