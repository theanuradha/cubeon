/*
 *  Copyright 2008 g.hartmann.
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

package org.netbeans.cubeon.ui.query.editor;

import java.awt.FlowLayout;
import java.util.Set;
import javax.swing.JPanel;

/**
 *
 * @author g.hartmann
 */
public abstract class AbstractFilterValuePanel extends JPanel {

    public AbstractFilterValuePanel() {
        super(new FlowLayout(FlowLayout.LEFT, 0, 2));
    }

    /**
     * Sets the ui from the list of values.
     * One or more values is taken from the list, displayed
     * in the ui and removed from the list.
     * At least one value will always be supllied by the list.
     *
     * @param values the list of values
     */
    public abstract void setValues(Set<?> values);

    /**
     * Gets one or more values from the ui and adds them to
     * the list.
     * 
     * @param values the list of values
     */
    public abstract void getValues(Set<? super Object> values);

}
