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

/**
 *
 * @author Anuradha
 */
public final class RepositoryUtils {

    private RepositoryUtils() {
    }

    public static String encodePassword(String username, String password) {
        String encode = "";
        int key = extractBitValue(username);
        char[] toCharArray = password.toCharArray();
        for (char c : toCharArray) {
            encode += (char)(key ^ c);
        }
        return encode;
    }

    public static String decodePassword(String username, String encodedPassword) {
        String decode = "";
        int key = extractBitValue(username);
        char[] toCharArray = encodedPassword.toCharArray();
        for (char c : toCharArray) {
            decode += (char)(c ^ key);
        }
        return decode;
    }

    private static int extractBitValue(String username) {
        int i = 0;
        char[] toCharArray = username.toCharArray();
        for (char c : toCharArray) {
            i += c;
        }
        return i;

    }
}
