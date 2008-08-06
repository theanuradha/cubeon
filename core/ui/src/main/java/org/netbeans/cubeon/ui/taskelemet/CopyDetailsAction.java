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
package org.netbeans.cubeon.ui.taskelemet;


import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.openide.util.NbBundle;

/**
 *
 * @author Anuradha G
 */
public class CopyDetailsAction extends AbstractAction implements ClipboardOwner{

  
    private TaskElement element;

    public CopyDetailsAction(TaskElement taskElement) {
       
        this.element = taskElement;
      
        putValue(NAME, NbBundle.getMessage(CopyDetailsAction.class, "LBL_Copy_Details"));
    }

    public void actionPerformed(ActionEvent e) {

        Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection content=new StringSelection(element.getDisplayName());
        systemClipboard.setContents(content, this);
        
    }

    public void lostOwnership(Clipboard clipboard, Transferable contents) {
       
    }

   
}
