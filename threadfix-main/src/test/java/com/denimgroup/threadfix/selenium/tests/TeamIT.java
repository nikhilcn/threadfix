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
package com.denimgroup.threadfix.selenium.tests;

import com.denimgroup.threadfix.CommunityTests;
import com.denimgroup.threadfix.selenium.pages.TeamDetailPage;
import com.denimgroup.threadfix.selenium.pages.TeamIndexPage;
import com.denimgroup.threadfix.selenium.utils.DatabaseUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category(CommunityTests.class)
public class TeamIT extends BaseIT {

	@Test
	public void testCreateTeam(){
		String newOrgName = "testCreateOrganization" + getRandomString(3);

        TeamIndexPage teamIndexPage = loginPage.login("user", "password").clickOrganizationHeaderLink();
		assertFalse("The organization was already present.", teamIndexPage.isTeamPresent(newOrgName));

        teamIndexPage = teamIndexPage.clickAddTeamButton()
                .setTeamName(newOrgName)
                .addNewTeam();

		assertTrue("The validation is not present", teamIndexPage.isCreateValidationPresent(newOrgName));
		assertTrue("The organization was not present in the table.", teamIndexPage.isTeamPresent(newOrgName));
	}

    // TODO possible deletion due to tables having a height of zero, the aren't 'not displayed'
    @Ignore
    @Test
    public void testExpandAndCollapseAllTeams(){
        String teamName1 = getRandomString(8);
        String teamName2 = getRandomString(8);

        DatabaseUtils.createTeam(teamName1);
        DatabaseUtils.createTeam(teamName2);

        TeamIndexPage teamIndexPage = loginPage.login("user", "password").clickOrganizationHeaderLink();

        teamIndexPage = teamIndexPage.expandAllTeams();

        assertTrue("All teams were not expanded properly.", teamIndexPage.areAllTeamsExpanded());

        teamIndexPage = teamIndexPage.collapseAllTeams();

         assertTrue("All teams were not collapsed properly.", teamIndexPage.areAllTeamsCollapsed());

    }


	@Test
	public void longTeamNameEditModalHeader(){
		String newOrgName = getRandomString(70);

        TeamIndexPage teamIndexPage = loginPage.login("user", "password")
                .clickOrganizationHeaderLink()
                .clickAddTeamButton()
                .setTeamName(newOrgName);
		
		assertTrue("Header width was incorrect with long team name",teamIndexPage.getLengthError().contains("Maximum length is 60."));
	}

    //TODO Need to update this when the validation is done in the form and not return a failure
	@Test
	public void testCreateOrganizationBoundaries(){
		String emptyString = "";
		String whiteSpaceString = "           ";
		
		String emptyInputError = "Name is required.";
		
		String longInput = "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee";
        String longInputFormatted ="eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee";

		// Test empty input
        TeamIndexPage teamIndexPage = loginPage.login("user", "password")
                .clickOrganizationHeaderLink()
                .clickAddTeamButton()
                .setTeamName(emptyString)
                .addNewTeamInvalid();
		
		assertTrue("The correct error text was not present",
                emptyInputError.equals(teamIndexPage.getErrorMessage("requiredError")));
		
		// Test whitespace input
		teamIndexPage = teamIndexPage.setTeamName(whiteSpaceString)
                .addNewTeamInvalid();
		assertTrue("The correct error text was not present",
                emptyInputError.equals(teamIndexPage.getErrorMessage("requiredError")));
		
		// Test browser length limit
//		teamIndexPage = teamIndexPage.setTeamName(longInput)
//                  .addNewTeamInvalid();
//
//		assertTrue("The organization name was not cropped correctly.", teamIndexPage.isTeamPresent(longInputFormatted));
//
//        teamIndexPage = teamIndexPage.setTeamName(longInputFormatted)
//                .addNewTeam();
//
//		//Test name duplication checking
//		teamIndexPage = teamIndexPage.clickOrganizationHeaderLink()
//                .clickAddTeamButton()
//                .setTeamName(longInputFormatted)
//                .addNewTeamInvalid();
//
//		assertTrue(teamIndexPage.getNameErrorMessage().equals("That name is already taken."));
	}

	@Test
	public void testEditTeam(){
		String newTeamName = "testEditTeam" + getRandomString(4);
		String editedTeamName = "testEditTeam" + getRandomString(4);
        DatabaseUtils.createTeam(newTeamName);

        TeamIndexPage teamIndexPage = loginPage.login("user", "password").clickOrganizationHeaderLink();

        TeamDetailPage teamDetailPage = teamIndexPage.clickOrganizationHeaderLink()
                .clickViewTeamLink(newTeamName)
                .clickEditOrganizationLink()
                .setNameInput(editedTeamName)
                .clickUpdateButtonValid();

		assertTrue("Editing did not change the name.", teamDetailPage.getOrgName().contains(editedTeamName));
		
		teamIndexPage = teamDetailPage.clickOrganizationHeaderLink();
		assertTrue("Organization Page did not save the name correctly.",  teamIndexPage.isTeamPresent(editedTeamName));
	}

    @Test
    public void testViewMore() {
        String teamName = "testViewMore" + getRandomString(3);
        DatabaseUtils.createTeam(teamName);

        TeamIndexPage teamIndexPage = loginPage.login("user", "password").clickOrganizationHeaderLink();

        TeamDetailPage teamDetailPage = teamIndexPage.clickViewTeamLink(teamName);

        assertTrue("View Team link did not work properly.", teamDetailPage.isTeamNameDisplayedCorrectly(teamName));
    }

    // TODO Wait for the graphs to have id's, then test. Right now, indexing would be required to locate the graph.
    @Ignore
    @Test
    public void testTeamGraphs() {
        String teamName = getRandomString(8);
        String appName = getRandomString(8);
        String file = ScanContents.getScanFilePath();

        DatabaseUtils.createTeam(teamName);
        DatabaseUtils.createApplication(teamName, appName);
        DatabaseUtils.uploadScan(teamName, appName, file);

        TeamIndexPage teamIndexPage = loginPage.login("user", "password")
                .clickOrganizationHeaderLink()
                .expandTeamRowByName(teamName);

        assertFalse("The graph of the expanded team was not shown properly.", teamIndexPage.isGraphDisplayed(teamName, appName));
    }

	//TODO needs revision when error messages are updated
	@Test
	public void testEditOrganizationBoundaries(){
		String orgName = "testEditOrgBound" + getRandomString(3);
		String orgNameDuplicateTest = "testEditOrgBound2" + getRandomString(3);
		
		String emptyInputError = "Name is required.";
		
		String longInput = getRandomString(119);

        DatabaseUtils.createTeam(orgName);
        DatabaseUtils.createTeam(orgNameDuplicateTest);

        TeamDetailPage teamDetailPage = loginPage.login("user", "password").clickOrganizationHeaderLink()
                .clickViewTeamLink(orgName);
		
		// Test edit with no changes
		teamDetailPage = teamDetailPage.clickEditOrganizationLink()
                .clickUpdateButtonValid();
		assertTrue("Organization Page did not save the name correctly.",teamDetailPage.getOrgName().contains(orgName));
		
		// Test empty input
		teamDetailPage = teamDetailPage.clickEditOrganizationLink()
                .setNameInput("")
                .clickUpdateButtonInvalid();
		assertTrue("The correct error text was not present", emptyInputError.equals(teamDetailPage.getErrorMessage("requiredError")));
		
		// Test whitespace input
		teamDetailPage = teamDetailPage.setNameInput("           ")
                .clickUpdateButtonInvalid();
		assertTrue("The correct error text was not present", emptyInputError.equals(teamDetailPage.getErrorMessage("requiredError")));
		
		// Test browser length limit
//		teamDetailPage = teamDetailPage.clickCloseEditModal()
//                .clickEditOrganizationLink()
//                .setNameInput(longInput)
//                .clickUpdateButtonValid();
        orgName = longInput.substring(0, 60);

        teamDetailPage = teamDetailPage
                .setNameInput(orgName)
                .clickUpdateButtonValid();

		assertTrue("The organization name was not cropped correctly.", teamDetailPage.isTeamNameDisplayedCorrectly(orgName));
		
		// Test name duplication checking
//		teamDetailPage = teamDetailPage.clickEditOrganizationLink()
//                .setNameInput(orgNameDuplicateTest)
//                .clickUpdateButtonInvalid();
//
//		assertTrue(teamDetailPage.getErrorText().equals("That name is already taken."));
	}
}