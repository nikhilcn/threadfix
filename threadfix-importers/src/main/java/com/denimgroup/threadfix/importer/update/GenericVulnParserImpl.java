////////////////////////////////////////////////////////////////////////
//
//     Copyright (c) 2009-2014 Denim Group, Ltd.
//
//     The contents of this file are subject to the Mozilla Public License
//     Version 2.0 (the "License"); you may not use this file except in
//     compliance with the License. You may obtain a copy of the License at
//     http://www.mozilla.org/MPL/
//
//     Software distributed under the License is distributed on an "AS IS"
//     basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
//     License for the specific language governing rights and limitations
//     under the License.
//
//     The Original Code is ThreadFix.
//
//     The Initial Developer of the Original Code is Denim Group, Ltd.
//     Portions created by Denim Group, Ltd. are Copyright (C)
//     Denim Group, Ltd. All Rights Reserved.
//
//     Contributor(s): Denim Group, Ltd.
//
////////////////////////////////////////////////////////////////////////
package com.denimgroup.threadfix.importer.update;

import com.denimgroup.threadfix.data.dao.ChannelTypeDao;
import com.denimgroup.threadfix.data.dao.ChannelVulnerabilityDao;
import com.denimgroup.threadfix.data.dao.GenericVulnerabilityDao;
import com.denimgroup.threadfix.data.entities.ChannelType;
import com.denimgroup.threadfix.data.entities.ChannelVulnerability;
import com.denimgroup.threadfix.data.entities.GenericVulnerability;
import com.denimgroup.threadfix.importer.util.IntegerUtils;
import com.denimgroup.threadfix.importer.util.ResourceUtils;
import com.denimgroup.threadfix.logging.SanitizedLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import static com.denimgroup.threadfix.CollectionUtils.list;

/**
 * Created by mac on 8/12/14.
 */
@Service
public class GenericVulnParserImpl implements GenericVulnParser {

    private static final SanitizedLogger log = new SanitizedLogger(GenericVulnParserImpl.class);

    @Autowired
    private ChannelTypeDao          channelTypeDao;
    @Autowired
    private ChannelVulnerabilityDao channelVulnerabilityDao;
    @Autowired
    private GenericVulnerabilityDao genericVulnerabilityDao;
    @Autowired
    private ChannelVulnParser channelVulnParser;

    private static final String CSV_SPLIT_CHARACTER = ",";

    @Override
    public List<String[]> updateGenericVulnerabilities(String fileName) {
        List<String[]> genericResults = list();

        try (InputStream genericStream = ResourceUtils.getResourceAsStream(fileName)) {

            if (genericStream != null) {
                log.info("Updating file " + fileName);
                genericResults = updateGenericVuln(genericStream);
            }
        } catch (IOException e) {
            log.error("Encountered IOException while trying to read the generic Vulnerability file");
        }

        return genericResults;
    }

    private List<String[]> updateGenericVuln(InputStream is) {
        List<String[]> genericResults = list();
        int updatedNo = 0, addedNewNo = 0;
        String updatedList = "", addedNewList = "";
        try {
            if (is != null) {
                ParsingState state = ParsingState.NONE;

                try (BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
                    String line = "";
                    ChannelType manualChannel = channelTypeDao.retrieveByName("Manual");
                    while ((line = br.readLine()) != null) {
                        if (line.startsWith("type.info")) {
                            state = ParsingState.TYPE;
                        } else if (line.startsWith("type.vulnerabilities")) {
                            state = ParsingState.VULNS;
                        } else {
                            if (state == ParsingState.VULNS) {
                                String[] elements = line.split(CSV_SPLIT_CHARACTER);
                                if (elements.length < 2)
                                    log.warn("Line " + line + " information is incorrect.");
                                else {
                                    Integer genericIdInt = IntegerUtils.getIntegerOrNull(elements[0]);

                                    if (genericIdInt == null)
                                        log.warn("Failed to parse generic ID " + elements[0]);
                                    else {

                                        if (!isUpdateGenericVuln(genericIdInt, elements[1], manualChannel)) {
                                            addedNewNo++;
                                            addedNewList += (addedNewList.isEmpty()? "" : ", ") + genericIdInt;
                                        } else {
                                            updatedNo++;
                                            updatedList += (updatedList.isEmpty()? "" : ", ") + genericIdInt;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    log.info("Number of generic vulnerabilities added new : " + addedNewNo + ", include " + addedNewList);
                    log.info("Number of generic vulnerabilities updated : " + updatedNo + ", include " + updatedList);
                }
            }
        } catch (IOException e) {
            log.error("IOException thrown while attempting to search csv file.", e);
        }
        genericResults.add(new String[]{"New Added Vulnerability", String.valueOf(addedNewNo), addedNewList});
        genericResults.add(new String[]{"Updated Vulnerability", String.valueOf(updatedNo), updatedList});
        return genericResults;
    }

    private boolean isUpdateGenericVuln(int genericIdInt, String genericNewName, ChannelType manualType) {

        GenericVulnerability genericVulnerability = genericVulnerabilityDao.retrieveByDisplayId(genericIdInt);

        boolean isUpdate = genericVulnerability != null;
        String oldName = null;
        if (genericVulnerability == null) {
            log.info("Add new Generic Vulnerability with CWE Id " + genericIdInt);
            genericVulnerability = new GenericVulnerability();
            genericVulnerability.setCweId(genericIdInt);
        } else {
            log.info("Update Generic Vulnerability with Id " + genericIdInt);
            oldName = genericVulnerability.getName();
        }

        genericVulnerability.setName(genericNewName);
        genericVulnerabilityDao.saveOrUpdate(genericVulnerability);

        updateManualVuln(genericVulnerability,oldName, genericNewName, manualType);

        return isUpdate;
    }

    private void updateManualVuln(GenericVulnerability genericVulnerability, String oldName, String newName, ChannelType channelType) {
        if (channelType == null) return;

        ChannelVulnerability vulnerability;
        if (oldName != null) {
            log.info("Update Manual Vulnerability: " + oldName + " to: " + newName);
            vulnerability = channelVulnerabilityDao.retrieveByName(channelType, oldName);
            vulnerability.setCode(newName);
            vulnerability.setName(newName);
            channelVulnerabilityDao.saveOrUpdate(vulnerability);
        } else {
            log.info("Create new Manual Vulnerability: " + newName);
            channelVulnParser.createNewChannelVulnerability(newName, newName, genericVulnerability, channelType);
        }
    }


}
