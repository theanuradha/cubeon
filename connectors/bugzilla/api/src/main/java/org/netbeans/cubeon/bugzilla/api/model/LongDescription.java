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
package org.netbeans.cubeon.bugzilla.api.model;

import java.util.Date;

/**
 * Represents Bugzilla bug description.
 *
 * @author radoslaw.holewa
 */
public class LongDescription {

    /**
     * Information about person who has added description.
     */
    private User who;

    /**
     * Deate of creation.
     */
    private Date when;

    /**
     * Content of the description.
     */
    private String text;

    public User getWho() {
        return who;
    }

    public void setWho(User who) {
        this.who = who;
    }

    public Date getWhen() {
        return when;
    }

    public void setWhen(Date when) {
        this.when = when;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
