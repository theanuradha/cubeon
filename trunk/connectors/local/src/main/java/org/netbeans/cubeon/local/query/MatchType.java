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
package org.netbeans.cubeon.local.query;

import org.openide.util.NbBundle;

/**
 *
 * @author Anuradha G
 */
public enum MatchType {

    CONTAIN(1),
    STARTS_WITH(2),
    ENDS_WITH(3),
    EQUALS(4);
    private int i;

    MatchType(int i) {
        this.i = i;
    }

    @Override
    public String toString() {
        switch (i) {
            case 1:
                return NbBundle.getMessage(MatchType.class, "LBL_Contains");
            case 2:
                return NbBundle.getMessage(MatchType.class, "LBL_Starts_With");
            case 3:
                return NbBundle.getMessage(MatchType.class, "LBL_End_With");
            case 4:
                return NbBundle.getMessage(MatchType.class, "LBL_Equals");

        }
        return "<Empty>";
    }
}
