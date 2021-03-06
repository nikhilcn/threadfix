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
package com.denimgroup.threadfix.service;

import com.denimgroup.threadfix.data.dao.RealtimeMetaDataScanDao;
import com.denimgroup.threadfix.data.entities.RealtimeMetaDataScan;
import com.denimgroup.threadfix.data.entities.ApplicationChannel;
import com.denimgroup.threadfix.data.entities.RemoteProviderApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = false)
public class RealtimeMetaDataScanServiceImpl implements RealtimeMetaDataScanService {

    @Autowired
    private RealtimeMetaDataScanDao metaDataScanDao = null;


    @Override
    public Integer update(RealtimeMetaDataScan realtimeMetaDataScan) {
       return metaDataScanDao.update(realtimeMetaDataScan);
    }

    @Override
    public RealtimeMetaDataScan reteriveByApplicationChannelID(ApplicationChannel channel) {
        return metaDataScanDao.reteriveByApplicationChannelID(channel);
    }

    @Override
    public void deleteByRemoteProviderApplication(RemoteProviderApplication remoteProviderApplication) {
         RealtimeMetaDataScan metaDataScan =   metaDataScanDao.reteriveByRemoteProviderApplicationID(
                 remoteProviderApplication);
        metaDataScanDao.delete(metaDataScan);
    }


}
