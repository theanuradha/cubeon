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
package org.netbeans.cubeon.bugzilla.api.util;

import java.util.Comparator;

/**
 * Utility class, contains static method which might be used to compare two versions of Bugzilla.
 *
 * @author radoslaw.holewa
 */
public class BugzillaVersionComparationUtil {

    /**
     * @param first
     * @param second
     * @return
     */
    public static int compareTwoVersions(String first, String second) {
        int result = 0;
        if (!first.equals(second)) {
            result = compareNotEqualsVersions(first, second);
        }
        return result;
    }

    private static int compareNotEqualsVersions(String first, String second) {
        return 0;  //todo implement this
    }

    public class VersionsComparator implements Comparator<String> {

        public int compare(String first, String second) {
            return compareTwoVersions(first, second);
        }
    }
}
