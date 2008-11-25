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
package org.netbeans.cubeon.trac.utils;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.cubeon.trac.api.TracException;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 *
 * @author Anuradha
 */
public class TracExceptionHandler {
    private static final Logger LOGGER=Logger.getLogger(TracExceptionHandler.class.getName());
    private TracExceptionHandler() {
    }

    public static void notify(TracException je) {
        LOGGER.log(Level.WARNING, je.getMessage());
        NotifyDescriptor descriptor = new NotifyDescriptor.Message(je.getMessage(),
                NotifyDescriptor.WARNING_MESSAGE);
        DialogDisplayer.getDefault().notify(descriptor);
    }
}
