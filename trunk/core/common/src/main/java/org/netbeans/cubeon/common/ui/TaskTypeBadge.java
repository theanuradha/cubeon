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
package org.netbeans.cubeon.common.ui;

import java.awt.Image;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Anuradha
 */
public enum TaskTypeBadge {

    DEFACT, ENHANCEMENT, FEATURE, TASK;

    public static Image getBadge(TaskTypeBadge badge) {
        switch (badge) {
            case DEFACT:
                return ImageUtilities.loadImage("org/netbeans/cubeon/common/ui/types/bullet_defact.png");
            case ENHANCEMENT:
                return ImageUtilities.loadImage("org/netbeans/cubeon/common/ui/types/bullet_enhancement.png");
            case FEATURE:
                return ImageUtilities.loadImage("org/netbeans/cubeon/common/ui/types/bullet_feature.png");
            case TASK:
                return ImageUtilities.loadImage("org/netbeans/cubeon/common/ui/types/bullet_task.png");
        }
        throw new UnsupportedOperationException("TaskTypeBadge Should be one of DEFACT, ENHANCEMENT, FEATURE, TASK");
    }

    public static Image getTaskImage() {
        return ImageUtilities.loadImage("org/netbeans/cubeon/common/ui/types/task.png");

    }
}
