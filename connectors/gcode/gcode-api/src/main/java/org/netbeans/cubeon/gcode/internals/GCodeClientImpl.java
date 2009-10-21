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

package org.netbeans.cubeon.gcode.internals;

import org.netbeans.cubeon.gcode.api.GCodeClient;
import org.netbeans.cubeon.gcode.api.GCodeException;
import org.netbeans.cubeon.gcode.api.GCodeSession;

/**
 *
 * @author Anuradha
 */
public class GCodeClientImpl implements  GCodeClient{

    public GCodeSession createSession(String project, String user, String password) throws GCodeException {
        return new GCodeSessionImpl(project, user, password);
    }

}
