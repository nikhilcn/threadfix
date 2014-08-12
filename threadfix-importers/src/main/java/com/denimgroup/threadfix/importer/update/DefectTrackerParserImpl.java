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

import com.denimgroup.threadfix.data.dao.DefectTrackerTypeDao;
import com.denimgroup.threadfix.data.entities.DefectTrackerType;
import com.denimgroup.threadfix.importer.util.ResourceUtils;
import com.denimgroup.threadfix.logging.SanitizedLogger;
import org.apache.commons.lang3.StringUtils;
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
public class DefectTrackerParserImpl implements DefectTrackerParser {

    private static final SanitizedLogger LOG = new SanitizedLogger(DefectTrackerParserImpl.class);

    @Autowired
    private DefectTrackerTypeDao defectTrackerTypeDao;

    @Override
    public List<String> updateDefectTrackers(String fileName) {

        List<String> defectTrackers = list();

        try (InputStream genericStream = ResourceUtils.getResourceAsStream(fileName)) {

            if (genericStream != null) {
                LOG.info("Updating file " + fileName);
                defectTrackers = createDefectTrackers(genericStream);
            }

        } catch (IOException e) {
            LOG.error("Encountered IOException while trying to read the generic Vulnerability file", e);
        }

        return defectTrackers;
    }

    private List<String> createDefectTrackers(InputStream genericStream) throws IOException {

        List<String> names = list();

        BufferedReader reader = new BufferedReader(new InputStreamReader(genericStream));

        String line = reader.readLine();

        while (line != null) {

            String[] splitLine = StringUtils.split(line, ',');

            if (splitLine.length == 2) {
                DefectTrackerType type = defectTrackerTypeDao.retrieveByName(splitLine[0]);

                if (type == null) {
                    // let's create one
                    type = new DefectTrackerType();

                    type.setName(splitLine[0]);
                    type.setFullClassName(splitLine[1]);

                    defectTrackerTypeDao.saveOrUpdate(type);
                    names.add(splitLine[0]);

                    LOG.info("Created a Defect Tracker with name " + splitLine[0]);

                } else {
                    LOG.info("Already had an entry for " + splitLine[0]);
                }

            } else {
                LOG.error("Line had " + splitLine.length + " sections instead of 2: " + line);
            }

            line = reader.readLine();
        }

        return names;
    }


}
