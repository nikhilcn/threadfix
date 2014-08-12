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

import com.denimgroup.threadfix.data.dao.*;
import com.denimgroup.threadfix.data.entities.*;
import com.denimgroup.threadfix.importer.util.IntegerUtils;
import com.denimgroup.threadfix.importer.util.ResourceUtils;
import com.denimgroup.threadfix.logging.SanitizedLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import static com.denimgroup.threadfix.CollectionUtils.list;

/**
 * Created by mac on 8/12/14.
 */
@Service
public class ChannelVulnParserImpl implements ChannelVulnParser {

    private static final SanitizedLogger LOG = new SanitizedLogger(ChannelVulnParserImpl.class);

    @Autowired
    private ChannelTypeDao          channelTypeDao;
    @Autowired
    private ChannelVulnerabilityDao channelVulnerabilityDao;
    @Autowired
    private GenericVulnerabilityDao genericVulnerabilityDao;
    @Autowired
    private GenericSeverityDao      genericSeverityDao;
    @Autowired
    private ChannelSeverityDao      channelSeverityDao;

    private static final String CSV_SPLIT_CHARACTER = ",";

    @Override
    public List<String[]> updateAllScanners(String rootDirectory) {

        List<String[]> scannerResults = list();

        for (ScannerType type : ScannerType.values()) {

            String filePath = rootDirectory + "/" + type.getShortName() + ".csv";

            try (InputStream scannerStream = ResourceUtils.getResourceAsStream(filePath)) {

                if (scannerStream != null) {
                    LOG.info("Updating file " + filePath);
                    String[] scannerUpdateResult = updateScanner(scannerStream);
                    if (scannerUpdateResult != null) {
                        scannerResults.add(scannerUpdateResult);
                    }
                }
            } catch (IOException e) {
                LOG.error("Encountered IOException while trying to read the mappings file for " + type);
            }
        }

        return scannerResults;
    }

    private String[] updateScanner(InputStream is) {
        try {
            if (is != null) {
                ParsingState state = ParsingState.NONE;

                try (BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
                    String line = "";

                    int vulnsNo = 0;
                    int sevsNo = 0;

                    ChannelType channelType = null;
                    while ((line = br.readLine()) != null) {
                        if (line.startsWith("type.info")) {
                            state = ParsingState.TYPE;
                        } else if (line.startsWith("type.vulnerabilities")) {
                            state = ParsingState.VULNS;
                        } else if (line.startsWith("type.severities")) {
                            state = ParsingState.SEVERITIES;
                        } else {
                            if (state == ParsingState.TYPE) {
                                channelType = updateChannelTypeInfo(line);
                                if (channelType == null)
                                    LOG.warn("Was unable to update Channel Type info for " + line);
                            } else if (state == ParsingState.VULNS) {
                                if (channelType != null) {
                                    if (!updateChannelVuln(channelType, line))
                                        LOG.warn("Was unable to add " + line);
                                    else vulnsNo++;
                                }
                            } else if (state == ParsingState.SEVERITIES) {
                                if (channelType != null) {
                                    if (!updateChannelSeverity(channelType, line))
                                        LOG.warn("Was unable to add " + line);
                                    else sevsNo++;
                                }
                            }
                        }
                    }
                    if (channelType != null) {
                        LOG.info("Number of vulnerabilites added for " + channelType.getName() + ": " + vulnsNo);
                        LOG.info("Number of severities added for " + channelType.getName() + ": " + sevsNo);
                        return new String[]{channelType.getName(), String.valueOf(vulnsNo), String.valueOf(sevsNo)};
                    }
                }
            }
        } catch (IOException e) {
            LOG.error("IOException thrown while attempting to search csv file.", e);
        }
        return null;
    }

    private boolean updateChannelVuln(ChannelType channelType, String line) {


        // use comma as separator
        String[] elements = line.split(CSV_SPLIT_CHARACTER);
        if (elements.length < 3)
            return false;

        boolean changed = false;

        String cvName = elements[0].replace("&comma;", ",");
        String cvCode = elements[1].replace("&comma;", ",");
        String genericId = elements[2];

        ChannelVulnerability channelVulnerability = channelVulnerabilityDao.retrieveByCode(channelType, cvCode);

        Integer genericIdInt = IntegerUtils.getIntegerOrNull(genericId);

        if (genericIdInt == null) {
            LOG.warn("Failed to parse generic ID " + genericId);
        } else {
            GenericVulnerability genericVulnerability = genericVulnerabilityDao.retrieveByDisplayId(genericIdInt);

            if (genericVulnerability == null) {
                LOG.warn("Unable to find Generic Vulnerability for GenericId " + genericId);
                changed = false;
            } else {
                if (channelVulnerability != null) {
                    // Update
                    changed = updateChannelVulnerability(channelVulnerability, cvName, genericVulnerability);
                } else {
                    createNewChannelVulnerability(cvCode, cvName, genericVulnerability, channelType);
                    changed = true;
                }
            }
        }

        return changed;
    }

    private boolean updateChannelVulnerability(ChannelVulnerability channelVulnerability,
                                               String channelVulnerabilityName,
                                               GenericVulnerability genericVulnerability) {
        boolean changed = false;

        if (!channelVulnerability.getName().equalsIgnoreCase(channelVulnerabilityName)) {
            channelVulnerability.setName(channelVulnerabilityName);
            changed = true;
        }
        if (channelVulnerability.getGenericVulnerability() == null ||
                channelVulnerability.getGenericVulnerability().getId().equals(genericVulnerability.getId())) {

            if (channelVulnerability.getGenericVulnerability() != null) {
                for (VulnerabilityMap map: channelVulnerability.getVulnerabilityMaps()) {
                    map.setChannelVulnerability(null);
                }
            }

            VulnerabilityMap map = new VulnerabilityMap();
            map.setMappable(true);
            map.setChannelVulnerability(channelVulnerability);
            map.setGenericVulnerability(genericVulnerability);
            channelVulnerability.setVulnerabilityMaps(Arrays.asList(map));
            channelVulnerabilityDao.saveOrUpdate(channelVulnerability);
            changed = true;
        }

        return changed;
    }

    @Override
    public void createNewChannelVulnerability(String channelVulnerabilityCode,
                                              String channelVulnerabilityName,
                                              GenericVulnerability genericVulnerability,
                                              ChannelType channelType) {
        ChannelVulnerability channelVulnerability = new ChannelVulnerability();
        channelVulnerability.setCode(channelVulnerabilityCode);
        channelVulnerability.setName(channelVulnerabilityName);
        channelVulnerability.setChannelType(channelType);

        VulnerabilityMap map = new VulnerabilityMap();
        map.setMappable(true);
        map.setChannelVulnerability(channelVulnerability);
        map.setGenericVulnerability(genericVulnerability);
        channelVulnerability.setVulnerabilityMaps(Arrays.asList(map));
        channelVulnerabilityDao.saveOrUpdate(channelVulnerability);
    }

    private boolean updateChannelSeverity(ChannelType channelType, String line) {
        // use comma as separator
        String[] elements = line.split(CSV_SPLIT_CHARACTER);
        if (elements.length < 4)
            return false;

        String csName = elements[0];
        String csCode = elements[1];
        String csNumericValue = elements[2];
        String genericSeverityId = elements[3];
        ChannelSeverity cs = channelSeverityDao.retrieveByCode(channelType,csCode);

        try {
            if (cs == null) {
                cs = new ChannelSeverity();
                cs.setCode(csCode);
            }
            cs.setName(csName);
            cs.setChannelType(channelType);
            cs.setNumericValue(Integer.valueOf(csNumericValue));
            GenericSeverity gs = genericSeverityDao.retrieveByIntValue(Integer.valueOf(genericSeverityId));
            if (gs == null) {
                LOG.warn("Unable to find Generic Severity for SeverityId " + genericSeverityId);
                return false;
            }
            SeverityMap map = cs.getSeverityMap();
            if (map == null)
                map = new SeverityMap();
            map.setChannelSeverity(cs);
            map.setGenericSeverity(gs);
            cs.setSeverityMap(map);
            channelSeverityDao.saveOrUpdate(cs);
            return true;
        } catch (NumberFormatException e) {
            LOG.warn("Numberic Value  " + csNumericValue + " or " + genericSeverityId + " is not a number");
        }

        return false;
    }


    private ChannelType updateChannelTypeInfo(String line) {

        ChannelType channelType = null;
        // use comma as separator
        String[] elements = line.split(CSV_SPLIT_CHARACTER);
        if (elements.length > 0) {

            String name = elements[0];
            channelType = channelTypeDao.retrieveByName(name);

            if (channelType == null) {
                if (elements.length < 4) {
                    LOG.error("Channel type information has " + elements.length + " sections instead of 4.");

                } else {
                    LOG.info("Creating new Channel Type " + name);
                    channelType = new ChannelType();
                    channelType.setName(name);
                    channelType.setUrl(elements[1]);
                    channelType.setVersion(elements[2]);
                    channelType.setExportInfo(elements[3]);

                    channelTypeDao.saveOrUpdate(channelType);
                }
            }
        }

        return channelType;
    }

}
