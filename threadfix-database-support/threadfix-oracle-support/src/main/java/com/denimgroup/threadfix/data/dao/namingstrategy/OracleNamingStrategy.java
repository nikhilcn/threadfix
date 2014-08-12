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
package com.denimgroup.threadfix.data.dao.namingstrategy;

import org.hibernate.cfg.DefaultComponentSafeNamingStrategy;

public class OracleNamingStrategy extends DefaultComponentSafeNamingStrategy {

    @Override
    public String collectionTableName(String ownerEntity,
                                      String ownerEntityTable, String associatedEntity,
                                      String associatedEntityTable, String propertyName) {

        String result = "\"" + super.collectionTableName(
                ownerEntity, ownerEntityTable,
                associatedEntity,
                associatedEntityTable,
                propertyName) + "\"";

        return result;
    }

    @Override
    public String foreignKeyColumnName(String propertyName,
                                       String propertyEntityName, String propertyTableName,
                                       String referencedColumnName) {
        return "\"" + super.foreignKeyColumnName(
                propertyName,
                propertyEntityName,
                propertyTableName,
                referencedColumnName).replaceAll("\"", "") + "\"";
    }

    @Override
    public String logicalCollectionColumnName(String columnName,
                                              String propertyName, String referencedColumn) {
        return "\"" + super.logicalCollectionColumnName(
                columnName, propertyName,
                referencedColumn) + "\"";
    }

    @Override
    public String logicalCollectionTableName(String tableName,
                                             String ownerEntityTable, String associatedEntityTable,
                                             String propertyName) {
        return "\"" + super.logicalCollectionTableName(
                tableName, ownerEntityTable,
                associatedEntityTable,
                propertyName) + "\"";
    }

    @Override
    public String logicalColumnName(String columnName, String propertyName) {
        return "\"" + super.logicalColumnName(
                columnName, propertyName) + "\"";
    }

    @Override
    public String propertyToColumnName(String propertyName) {
        String result = super.propertyToColumnName(propertyName);

        if (result.length() > 30) {
            System.out.println(result + " is more than 30 characters");
            result = result.substring(0, 28);
        }

        result = "\"" + result.replace("\"", "") + "\"";

        return result.toUpperCase();
    }

    @Override
    public String classToTableName(String aClassName) {
        String result = super.classToTableName(aClassName);

        result = "\"" + (result.length() > 28 ? result.substring(0, 28) : result) + "\"";

        return result;
    }
}