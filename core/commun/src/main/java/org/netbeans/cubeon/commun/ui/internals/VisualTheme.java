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
package org.netbeans.cubeon.commun.ui.internals;

import java.awt.Color;

/**
 *
 * @author Anuradha
 * this calss is base on org.netbeans.modules.xml.multiview.ui.SectionVisualTheme;
 */
public class VisualTheme {

  public  VisualTheme() {
    }
    /** Creates a new instance of SectionColorTheme */
    private Color documentBackgroundColor = Color.WHITE;
    private Color sectionActiveBackgroundColor = Color.WHITE;
    private Color documentMarginColor = Color.WHITE;
    private Color sectionHeaderColor = Color.WHITE;
    private Color containerHeaderColor = Color.WHITE;
    private Color sectionHeaderActiveColor = Color.WHITE;
    private Color fillerColor = javax.swing.UIManager.getDefaults().getColor("Button.background"); //NOI18N
    private Color tableHeaderColor = Color.WHITE;
    private Color tableGridColor = Color.WHITE;
    private Color sectionHeaderLineColor = Color.WHITE;
    private Color textColor = new java.awt.Color(0, 0, 0);
    private Color foldLineColor = new java.awt.Color(102, 102, 102);

    public Color getDocumentBackgroundColor() {
        return documentBackgroundColor;
    }

    public Color getMarginColor() {
        return documentMarginColor;
    }

    public Color getSectionHeaderColor() {
        return sectionHeaderColor;
    }

    public Color getContainerHeaderColor() {
        return containerHeaderColor;
    }

    public Color getSectionHeaderActiveColor() {
        return sectionHeaderActiveColor;
    }

    public Color getSectionActiveBackgroundColor() {
        return sectionActiveBackgroundColor;
    }

    public Color getTableHeaderColor() {
        return tableHeaderColor;
    }

    public Color getTableGridColor() {
        return tableGridColor;
    }

    public Color getSectionHeaderLineColor() {
        return sectionHeaderLineColor;
    }

    public Color getTextColor() {
        return textColor;
    }

    public Color getFillerColor() {
        return fillerColor;
    }

    public Color getFoldLineColor() {
        return foldLineColor;
    }

    public void setContainerHeaderColor(Color containerHeaderColor) {
        this.containerHeaderColor = containerHeaderColor;
    }

    public void setDocumentBackgroundColor(Color documentBackgroundColor) {
        this.documentBackgroundColor = documentBackgroundColor;
    }

    public void setDocumentMarginColor(Color documentMarginColor) {
        this.documentMarginColor = documentMarginColor;
    }

    public void setFillerColor(Color fillerColor) {
        this.fillerColor = fillerColor;
    }

    public void setFoldLineColor(Color foldLineColor) {
        this.foldLineColor = foldLineColor;
    }

    public void setSectionActiveBackgroundColor(Color sectionActiveBackgroundColor) {
        this.sectionActiveBackgroundColor = sectionActiveBackgroundColor;
    }

    public void setSectionHeaderActiveColor(Color sectionHeaderActiveColor) {
        this.sectionHeaderActiveColor = sectionHeaderActiveColor;
    }

    public void setSectionHeaderColor(Color sectionHeaderColor) {
        this.sectionHeaderColor = sectionHeaderColor;
    }

    public void setSectionHeaderLineColor(Color sectionHeaderLineColor) {
        this.sectionHeaderLineColor = sectionHeaderLineColor;
    }

    public void setTableGridColor(Color tableGridColor) {
        this.tableGridColor = tableGridColor;
    }

    public void setTableHeaderColor(Color tableHeaderColor) {
        this.tableHeaderColor = tableHeaderColor;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }


}
