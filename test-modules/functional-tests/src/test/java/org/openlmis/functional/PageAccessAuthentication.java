/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.functional;


import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.AccessDeniedPage;
import org.openlmis.pageobjects.HomePage;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Listeners;

import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;


@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class PageAccessAuthentication extends TestCaseHelper {

  @Before
  public void setUp() throws Exception {
    super.setup();
  }

    @When("^I access initiate requisition page through URL$")
    public void accessInitiateRequisitionPageThroughURL() throws Exception {
        testWebDriver.waitForElementToAppear(new HomePage(testWebDriver).getLogoutLink());
        testWebDriver.getUrl(baseUrlGlobal + "public/pages/logistics/rnr/index.html#/init-rnr");
        testWebDriver.sleep(2000);
    }

    @When("^I access create facility page through URL$")
    public void accessCreateFacilityPageThroughURL() throws Exception {
        testWebDriver.waitForElementToAppear(new HomePage(testWebDriver).getLogoutLink());
        testWebDriver.getUrl(baseUrlGlobal + "public/pages/admin/facility/index.html#/create-facility");
        testWebDriver.sleep(2000);
    }

    @Then("^I should see unauthorized access message$")
    public void verifyUnauthorizedAccessPage() throws Exception {
        assertEquals("You are not authorized to view the requested page.", new AccessDeniedPage(testWebDriver).getAccessDeniedText());
    }

  @After
  public void tearDown() throws Exception {
      if (!testWebDriver.getElementById("username").isDisplayed()) {
          HomePage homePage = new HomePage(testWebDriver);
          homePage.logout(baseUrlGlobal);
        dbWrapper.deleteData();
        dbWrapper.closeConnection();
      }

  }

}

