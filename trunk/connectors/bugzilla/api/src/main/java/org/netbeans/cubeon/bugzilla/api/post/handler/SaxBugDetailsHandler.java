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
package org.netbeans.cubeon.bugzilla.api.post.handler;

import org.netbeans.cubeon.bugzilla.api.model.BugDetails;
import org.netbeans.cubeon.bugzilla.api.model.LongDescription;
import org.netbeans.cubeon.bugzilla.api.model.User;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * SAX handler, it will be used to parse XML content from input stream.
 *
 * @author radoslaw.holewa
 */
public class SaxBugDetailsHandler extends BaseSaxHandler {

    private String actualElementName;
    private BugDetails bugDetails;
    private Object configuration;//TODO change this
    private DateFormat bugDateFormat;
    private DateFormat commentDateFormat;
    private Attributes attributes;
    private boolean inLongDesc = false;
    private LongDescription longDescription;

    public SaxBugDetailsHandler() {
        this(null);
    }

    public SaxBugDetailsHandler(Object configuration) {
        this.configuration = configuration;
        if (configuration == null) {
            bugDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm z");
            commentDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        }
    }

    public void startDocument() throws SAXException {
        bugDetails = new BugDetails();
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        BugzillaElement elementEnum = BugzillaElement.getInstance(qName);
        if (elementEnum != null) {
            if (!inLongDesc) {
                switch (elementEnum) {
                    case LONG_DESC:
                        startLongDescProcessing();
                        break;
                }
            } else {
                switch (elementEnum) {
                    case WHO:
                        User who = new User();
                        who.setName(attributes.getValue("name"));
                        longDescription.setWho(who);
                        break;
                }
            }
        }
        elementContent = new StringBuffer();
        actualElementName = qName;
        this.attributes = attributes;
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        BugzillaElement elementEnum = BugzillaElement.getInstance(qName);
        if (elementEnum != null) {
            String value = elementContent.toString();
            setBugDetailsValue(elementEnum, value);
        }
    }

    /**
     * @param elementEnum
     * @param value
     */
    private void setBugDetailsValue(BugzillaElement elementEnum, String value) {
        try {
            if (!inLongDesc) {
                switch (elementEnum) {
                    case BUG_ID:
                        bugDetails.setId(Integer.valueOf(value));
                        break;
                    case CREATION_TS:
                        bugDetails.setCreationDate(bugDateFormat.parse(value));
                        break;
                    case SHORT_DESC:
                        bugDetails.setShortDescription(value);
                        break;
                    case DELTA_TS:
                        bugDetails.setDeltaDate(commentDateFormat.parse(value));
                        break;
                    case REPORTER_ACCESSIBLE:
                        bugDetails.setReporterAccessible(TRUE_VALUE.equals(value));
                        break;
                    case CCLIST_ACCESSIBLE:
                        bugDetails.setCcListAccessible(TRUE_VALUE.equals(value));
                        break;
                    case CLASSIFICATION:
                        bugDetails.setClassification(value);
                        break;
                    case CLASSIFICATION_ID:
                        bugDetails.setClassificationId(Integer.valueOf(value));
                        break;
                    case PRODUCT:
                        bugDetails.setProduct(value);
                        break;
                    case COMPONENT:
                        bugDetails.setComponent(value);
                        break;
                    case VERSION:
                        bugDetails.setVersion(value);
                        break;
                    case REP_PLATFORM:
                        bugDetails.setReportPlatform(value);
                        break;
                    case OP_SYS:
                        bugDetails.setOs(value);
                        break;
                    case BUG_STATUS:
                        bugDetails.setBugStatus(value);
                        break;
                    case RESOLUTION:
                        bugDetails.setResolution(value);
                        break;
                    case KEYWORDS:
                        //TODO
                        break;
                    case PRIORITY:
                        bugDetails.setPriority(value);
                        break;
                    case BUG_SEVERITY:
                        bugDetails.setSeverity(value);
                        break;
                    case TARGET_MILESTONE:
                        bugDetails.setTargetMilestone(value);
                        break;
                    case VOTES:
                        bugDetails.setVotes(Integer.valueOf(value));
                        break;
                    case EVERCONFIRMED:
                        bugDetails.setEverConfirmed(TRUE_VALUE.equals(value));
                        break;
                    case REPORTER:
                        User reporter = new User();
                        reporter.setEmail(value);
                        reporter.setName(attributes.getValue("name"));
                        bugDetails.setReporter(reporter);
                        break;
                    case ASSIGNED_TO:
                        User assignedTo = new User();
                        assignedTo.setEmail(value);
                        assignedTo.setName(attributes.getValue("name"));
                        bugDetails.setAssignedTo(assignedTo);
                        break;
                    case CC:
                        bugDetails.getCc().add(value);
                        break;
                    case QA_CONTACT:
                        //TODO
                        break;
                }
            } else {
                switch (elementEnum) {
                    case WHO:
                        longDescription.getWho().setEmail(value);
                        break;
                    case BUG_WHEN:
                        longDescription.setWhen(commentDateFormat.parse(value));
                        break;
                    case THETEXT:
                        longDescription.setText(value);
                        break;
                    case LONG_DESC:
                        LongDescription longDesc = endLongDescProcessing();
                        bugDetails.getLongDescriptions().add(longDesc);
                        break;
                }
            }
        } catch (Exception e) {
            //we don't process errors
        }
    }

    private void startLongDescProcessing() {
        inLongDesc = true;
        longDescription = new LongDescription();
    }

    private LongDescription endLongDescProcessing() {
        inLongDesc = false;
        return longDescription;
    }

    /**
     * Returns object representation of parsed XML content.
     *
     * @return - instance of {@see org.netbeans.cubeon.bugzilla.api.model.BugDetails} which contains
     *         data retreived from parsed XML content.
     */
    public BugDetails getBugDetails() {
        return bugDetails;
    }

    /**
     * Enum of XML response elements names.
     */
    private enum BugzillaElement {

        BUG_ID,
        CREATION_TS,
        SHORT_DESC,
        DELTA_TS,
        REPORTER_ACCESSIBLE,
        CCLIST_ACCESSIBLE,
        CLASSIFICATION_ID,
        CLASSIFICATION,
        PRODUCT,
        COMPONENT,
        VERSION,
        REP_PLATFORM,
        OP_SYS,
        BUG_STATUS,
        RESOLUTION,
        KEYWORDS,
        PRIORITY,
        BUG_SEVERITY,
        TARGET_MILESTONE,
        VOTES,
        EVERCONFIRMED,
        REPORTER,
        ASSIGNED_TO,
        CC,
        QA_CONTACT,
        LONG_DESC,
        WHO,
        BUG_WHEN,
        THETEXT;

        /**
         * Returns instance of this enum using it's NOT case sensitive name.
         *
         * @param elementName - NOT case sensitive name of the enum
         * @return - returns enum or NULL in case there was no enum with given name
         */
        public static BugzillaElement getInstance(String elementName) {
            try {
                return BugzillaElement.valueOf(elementName.toUpperCase(Locale.US));
            } catch (IllegalArgumentException e) {
                /**
                 * valueOf method throws IllegalArgumentException in case it can't find enum
                 * but we don't want to throw exception to the method which invoked getInstance
                 * so that's the reason why we do nothing here
                 */
            }
            //we will return null in case there was no BugzillaElement found
            return null;
        }
    }
}
