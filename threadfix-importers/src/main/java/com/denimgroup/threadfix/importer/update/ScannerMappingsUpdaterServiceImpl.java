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

import com.denimgroup.threadfix.data.dao.DefaultConfigurationDao;
import com.denimgroup.threadfix.data.entities.DefaultConfiguration;
import com.denimgroup.threadfix.data.entities.ScannerType;
import com.denimgroup.threadfix.importer.interop.ScannerMappingsUpdaterService;
import com.denimgroup.threadfix.importer.util.ResourceUtils;
import com.denimgroup.threadfix.logging.SanitizedLogger;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.denimgroup.threadfix.CollectionUtils.list;

@Service
@Transactional(readOnly = false) // used to be true
class ScannerMappingsUpdaterServiceImpl implements ScannerMappingsUpdaterService {

    @Autowired
    private DefaultConfigurationDao defaultConfigurationDao;
    @Autowired
    private DefectTrackerParser defectTrackerParser;
    @Autowired
    private ChannelVulnParser   channelVulnParser;
    @Autowired
    private GenericVulnParser genericVulnParser;

    private static final String
            CSV_SPLIT_CHARACTER = ",",
            DATE_PATTERN        = "MM/dd/yyyy hh:mm:ss";

    private final SanitizedLogger log = new SanitizedLogger(ScannerMappingsUpdaterServiceImpl.class);

    /**
     * Add/Update ChannelVulnerabilities and their VulnerabilityMaps from reading csv file.
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    @Override
    public List<String[]> updateChannelVulnerabilities() throws IOException, URISyntaxException {
        return channelVulnParser.updateAllScanners("/mappings");
    }

    @Override
    public List<String> updateDefectTrackers() {
        return defectTrackerParser.updateDefectTrackers("/mappings/defect-trackers.csv");
    }

    @Override
    public List<String[]> updateGenericVulnerabilities() throws IOException, URISyntaxException {
        return genericVulnParser.updateGenericVulnerabilities("/mappings/genericVuln.csv");
    }

    @Override
    public List<String> getSupportedScanners() {
        List<String> scanners = list();

        ScannerType[] importers = ScannerType.values();

        if (importers != null) {
            for (ScannerType importer : importers) {
                scanners.add(importer.getFullName());
            }
        }

        Collections.sort(scanners);

        return scanners;
    }

    private DefaultConfiguration getDefaultConfiguration() {
        List<DefaultConfiguration> configurationList = defaultConfigurationDao.retrieveAll();
        DefaultConfiguration config;
        if (configurationList.size() == 0) {
            config = DefaultConfiguration.getInitialConfig();
        } else {
            config = configurationList.get(0);
        }

        return config;
    }

    @Override
    public void updateUpdatedDate() {
        DefaultConfiguration config = getDefaultConfiguration();

        config.setLastScannerMappingsUpdate(getPluginTimestamp());

        defaultConfigurationDao.saveOrUpdate(config);
    }

    @Override
    @Transactional
    public void updateMappings() {
        log.info("Start updating Scanner mapping from startup");

        try {
            updateGenericVulnerabilities();
            updateChannelVulnerabilities();
            updateDefectTrackers();
            updateUpdatedDate();

        } catch (URISyntaxException e) {
            String message = "There was error when reading files.";
            log.warn(message, e);
        } catch (IOException e) {
            String message = "There was error when updating mappings.";
            log.warn(message, e);
        }

        log.info("Ended updating Scanner mapping from startup");
    }

    @Override
    public ScanPluginCheckBean checkPluginJar() {
        DefaultConfiguration configuration = getDefaultConfiguration();

        if (configuration != null && configuration.getLastScannerMappingsUpdate() != null) {

            Calendar databaseDate = configuration.getLastScannerMappingsUpdate();
            Calendar pluginDate = getPluginTimestamp();

            if (pluginDate != null && !pluginDate.after(databaseDate)) {
                return new ScanPluginCheckBean(false, databaseDate, pluginDate);
            } else {
                return new ScanPluginCheckBean(true, databaseDate, pluginDate);
            }
        } else  {
            return new ScanPluginCheckBean(true, null, null);
        }
    }

    private Calendar getPluginTimestamp() {
        Calendar returnDate = null;

        try (InputStream versionFileStream = ResourceUtils.getResourceAsStream("/mappings/version.txt")) {

            String result = IOUtils.toString(versionFileStream);

            if (result != null && !result.trim().isEmpty()) {
                returnDate = getCalendarFromString(result.trim());
            }
        } catch (IOException e) {
            log.info("IOException thrown while attempting to read version file.", e);
        }

        return returnDate;
    }

    private Calendar getCalendarFromString(String dateString) {

        Date date = null;
        try {
            date = new SimpleDateFormat(DATE_PATTERN, Locale.US).parse(dateString);
        } catch (ParseException e) {
            log.warn("Parsing of date from '" + dateString + "' failed.", e);
        }

        if (date != null) {
            log.debug("Successfully parsed date: " + date + ".");
            Calendar scanTime = new GregorianCalendar();
            scanTime.setTime(date);
            return scanTime;
        }

        log.warn("There was an error parsing the date, check the format and regex.");
        return null;
    }

}
