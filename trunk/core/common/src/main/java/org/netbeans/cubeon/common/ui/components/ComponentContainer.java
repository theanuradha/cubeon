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
package org.netbeans.cubeon.common.ui.components;

import java.awt.Dimension;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.java.dev.designgridlayout.DesignGridLayout;
import net.java.dev.designgridlayout.INonGridRow;
import net.java.dev.designgridlayout.IRowCreator;
import net.java.dev.designgridlayout.ISpannableGridRow;

/**
 *
 * @author Anuradha
 */
public class ComponentContainer extends JPanel {

    private final DesignGridLayout layout;
    private IRowCreator activeRowCreator;
    private ISpannableGridRow activeRow;
    private INonGridRow activeNonGridRow;

    public ComponentContainer() {
        layout = new DesignGridLayout(this);
        layout.margins(0, 1, 0, 2);
        activeRowCreator = layout.row();
    }

    public void nextSection() {
        activeRowCreator = layout.row();
        activeRow = null;
        activeNonGridRow = null;
    }

    public void resizeableSection() {
        activeRow = (activeRow == null ? activeRowCreator.grid()
                : activeRow.grid());
    }

    public void resizeableSection(String sectionName) {
        activeRow = (activeRow == null ? activeRowCreator.grid(new JLabel(sectionName))
                : activeRow.grid(new JLabel(sectionName)));
    }

    public void emptySection() {
        layout.emptyRow();
        nextSection();
    }

    public void addComponent(JComponent... components) {
        activeNonGridRow = activeNonGridRow == null ?
            activeRowCreator.left().add(components) : activeNonGridRow.add(components);
    }

    public void addComponentGroup(JComponent... components) {
        activeNonGridRow = activeNonGridRow == null ?
            activeRowCreator.left().addMulti(components) : activeNonGridRow.addMulti(components);
    }

    public void addResizeableComponent(JComponent... components) {
        assert activeRow != null : "no active resizeableSection ";
        activeRow.add(components);
    }

    public void addResizeableComponentGroup(JComponent... components) {
        assert activeRow != null : "no active resizeableSection ";
        activeRow.add(components);
    }

    public void addResizeableSpace() {
        assert activeRow != null : "no active resizeableSection ";
        activeRow.empty();
    }

    public void fillSection() {
        assert activeNonGridRow != null : "no active NonGridRow ";
        activeNonGridRow.fill();
    }


}
