-- ----------------------------------------------------------------------
-- Complete Report of all Data
--
-- ----------------------------------------------------------------------

-- ------------------------------------
-- ROLES ------------------------------
-- ------------------------------------
INSERT INTO Role (id, createdDate, modifiedDate, displayname, active,canGenerateReports,canGenerateWafRules,canManageApiKeys,canManageApplications,canManageDefectTrackers,canManageRemoteProviders,canManageRoles,canManageTeams,canManageUsers,canManageWafs,canModifyVulnerabilities,canSubmitDefects,canUploadScans,canViewErrorLogs,canViewJobStatuses) VALUES (1, now(), now(), 'Administrator', 1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1);
INSERT INTO Role (id, createdDate, modifiedDate, displayname, active,canGenerateReports,canGenerateWafRules,canManageApiKeys,canManageApplications,canManageDefectTrackers,canManageRemoteProviders,canManageRoles,canManageTeams,canManageUsers,canManageWafs,canModifyVulnerabilities,canSubmitDefects,canUploadScans,canViewErrorLogs,canViewJobStatuses) VALUES (2, now(), now(), 'User', 1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0);

INSERT INTO User (id, active, approved, createdDate, modifiedDate, name, password, roleId, salt, failedPasswordAttemptWindowStart, failedPasswordAttempts, lastLoginDate, lastPasswordChangedDate, locked, hasGlobalGroupAccess, isLdapUser) VALUES (1, 1, 1, now(), now(), 'user', '1ad7cb5116eea07356140e718895d3bad52220589e00efe99108031cc93df924', 1, 'd4639110-c8f1-4654-887f-aa9634bc457b', now(), 0,now(),now(),0,1,0);

-- ------------------------------------
-- DEFAULTS ---------------------------
-- ------------------------------------
INSERT INTO DefaultConfiguration (globalGroupEnabled, defaultRoleId) VALUES (1,1);

-- ------------------------------------
-- INSERT REMOTEPROVIDER TYPES --------
-- ------------------------------------
INSERT INTO RemoteProviderType (name, channelTypeId, hasApiKey, hasUserNamePassword, isEuropean, encrypted) VALUES ('WhiteHat Sentinel', (SELECT id FROM ChannelType WHERE name = 'WhiteHat Sentinel'), true, false, false, false);
INSERT INTO RemoteProviderType (name, channelTypeId, hasApiKey, hasUserNamePassword, isEuropean, encrypted) VALUES ('Veracode', (SELECT id FROM ChannelType WHERE name = 'Veracode'), false, true, false, false);
INSERT INTO RemoteProviderType (name, channelTypeId, hasApiKey, hasUserNamePassword, isEuropean, encrypted) VALUES ('QualysGuard WAS', (SELECT id FROM ChannelType WHERE name = 'QualysGuard WAS'), false, true, false, false);

-- ------------------------------------
-- INSERT APPLICATION CRITICALITIES ---
-- ------------------------------------
INSERT INTO ApplicationCriticality (name) VALUES ('Low');
INSERT INTO ApplicationCriticality (name) VALUES ('Medium');
INSERT INTO ApplicationCriticality (name) VALUES ('High');
INSERT INTO ApplicationCriticality (name) VALUES ('Critical');

-- ------------------------------------
-- INSERT WAFTYPES --------------------
-- ------------------------------------
INSERT INTO WafType (name, initialID) VALUES ('Snort', 100000);
INSERT INTO WafType (name, initialID) VALUES ('mod_security', 100000);
INSERT INTO WafType (name, initialID) VALUES ('BIG-IP ASM', 100000);
INSERT INTO WafType (name, initialID) VALUES ('Imperva SecureSphere', 100000);
INSERT INTO WafType (name, initialID) VALUES ('DenyAll rWeb', 100000);

INSERT INTO WafRuleDirective (directive, wafTypeId) VALUES ('alert', (SELECT id FROM WafType WHERE name = 'Snort'));
INSERT INTO WafRuleDirective (directive, wafTypeId) VALUES ('log', (SELECT id FROM WafType WHERE name = 'Snort'));
INSERT INTO WafRuleDirective (directive, wafTypeId) VALUES ('pass', (SELECT id FROM WafType WHERE name = 'Snort'));
INSERT INTO WafRuleDirective (directive, wafTypeId) VALUES ('activate', (SELECT id FROM WafType WHERE name = 'Snort'));
INSERT INTO WafRuleDirective (directive, wafTypeId) VALUES ('dynamic', (SELECT id FROM WafType WHERE name = 'Snort'));
INSERT INTO WafRuleDirective (directive, wafTypeId) VALUES ('drop', (SELECT id FROM WafType WHERE name = 'Snort'));
INSERT INTO WafRuleDirective (directive, wafTypeId) VALUES ('reject', (SELECT id FROM WafType WHERE name = 'Snort'));
INSERT INTO WafRuleDirective (directive, wafTypeId) VALUES ('sdrop', (SELECT id FROM WafType WHERE name = 'Snort'));

-- These require additional data, which we can't handle right now.
-- INSERT INTO WafRuleDirective (directive, wafTypeId) VALUES ('proxy', (SELECT id FROM WafType WHERE name = 'mod_security'));
-- INSERT INTO WafRuleDirective (directive, wafTypeId) VALUES ('redirect', (SELECT id FROM WafType WHERE name = 'mod_security'));

INSERT INTO WafRuleDirective (directive, wafTypeId) VALUES ('deny', (SELECT id FROM WafType WHERE name = 'mod_security'));
INSERT INTO WafRuleDirective (directive, wafTypeId) VALUES ('drop', (SELECT id FROM WafType WHERE name = 'mod_security'));
INSERT INTO WafRuleDirective (directive, wafTypeId) VALUES ('pass', (SELECT id FROM WafType WHERE name = 'mod_security'));
INSERT INTO WafRuleDirective (directive, wafTypeId) VALUES ('allow', (SELECT id FROM WafType WHERE name = 'mod_security'));

INSERT INTO WafRuleDirective (directive, wafTypeId) VALUES ('transparent', (SELECT id FROM WafType WHERE name = 'BIG-IP ASM'));
INSERT INTO WafRuleDirective (directive, wafTypeId) VALUES ('blocking', (SELECT id FROM WafType WHERE name = 'BIG-IP ASM'));

INSERT INTO WafRuleDirective (directive, wafTypeId) VALUES ('-', (SELECT id FROM WafType WHERE name = 'Imperva SecureSphere'));

INSERT INTO WafRuleDirective (directive, wafTypeId) VALUES ('deny', (SELECT id FROM WafType WHERE name = 'DenyAll rWeb'));
INSERT INTO WafRuleDirective (directive, wafTypeId) VALUES ('warning', (SELECT id FROM WafType WHERE name = 'DenyAll rWeb'));

-- ------------------------------------
-- INSERT GENERIC MAPPINGS
-- ------------------------------------
INSERT INTO GenericSeverity (Name, intValue) VALUES ('Critical', 5);
INSERT INTO GenericSeverity (Name, intValue) VALUES ('High', 4);
INSERT INTO GenericSeverity (Name, intValue) VALUES ('Medium', 3);
INSERT INTO GenericSeverity (Name, intValue) VALUES ('Low', 2);
INSERT INTO GenericSeverity (Name, intValue) VALUES ('Info', 1);

