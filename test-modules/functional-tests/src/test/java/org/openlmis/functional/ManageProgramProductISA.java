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
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.openlmis.pageobjects.ProgramProductISAPage;
import org.openqa.selenium.WebElement;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestBase.*;


@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ManageProgramProductISA extends TestCaseHelper {

  public String program, userSIC, password;
  public ProgramProductISAPage programProductISAPage;


  @BeforeMethod(groups = "admin")
  @Before
  public void setUp() throws Exception {
    super.setup();
  }

    @When("^I have data available for program product ISA$")
    public void setUpTestDataForProgramProductISA() throws IOException, SQLException {
        setupProgramProductTestDataWithCategories("P1", "antibiotic1", "C1", "VACCINES");
        setupProgramProductTestDataWithCategories("P2", "antibiotic2", "C2", "VACCINES");
        setupProgramProductTestDataWithCategories("P3", "antibiotic3", "C3", "TB");
        setupProgramProductTestDataWithCategories("P4", "antibiotic4", "C4", "TB");
        dbWrapper.updateProgramToAPushType("TB", false);
    }

    @Given("^I access program product ISA page for \"([^\"]*)\"$")
    public void accessProgramProductISAPage(String program) throws IOException {
        programProductISAPage = navigateProgramProductISAPage(program);
    }

    @When("^I type ratio \"([^\"]*)\" dosesPerYear \"([^\"]*)\" wastage \"([^\"]*)\" bufferPercentage \"([^\"]*)\" adjustmentValue \"([^\"]*)\" minimumValue \"([^\"]*)\" maximumValue \"([^\"]*)\"$")
    public void fillProgramProductISA(String ratio, String dosesPerYear, String wastage, String bufferPercentage,
                                      String adjustmentValue, String minimumValue, String maximumValue) throws IOException {
        programProductISAPage.fillProgramProductISA(ratio, dosesPerYear, wastage, bufferPercentage, adjustmentValue, minimumValue, maximumValue);
    }

    @Then("^I verify calculated ISA value having population \"([^\"]*)\" ratio \"([^\"]*)\" dosesPerYear \"([^\"]*)\" wastage \"([^\"]*)\" bufferPercentage \"([^\"]*)\" adjustmentValue \"([^\"]*)\" minimumValue \"([^\"]*)\" maximumValue \"([^\"]*)\"$")
    public void verifyCalculatedISA(String population,String ratio, String dosesPerYear, String wastage, String bufferPercentage,
                                    String adjustmentValue, String minimumValue, String maximumValue) throws IOException {
        String actualISA = programProductISAPage.fillPopulation(population);
        String expectedISA = calculateISA(ratio, dosesPerYear, wastage, bufferPercentage, adjustmentValue, minimumValue, maximumValue, population);
        assertEquals(expectedISA, actualISA);
    }

    @Then("^I click cancel$")
    public void clickCancel() throws IOException {
        programProductISAPage.cancelISA();
    }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function")
  public void testMinimumProgramProductISA(String userSIC, String password, String program) throws Exception {
    setUpTestDataForProgramProductISA();
    Login(userSIC,password);
    ProgramProductISAPage programProductISAPage = navigateProgramProductISAPage(program);
    programProductISAPage.fillProgramProductISA("1", "2", "3", "4", "5", "10", "1000");
    String actualISA = programProductISAPage.fillPopulation("1");
    String expectedISA = calculateISA("1", "2", "3", "4", "5", "10", "1000", "1");
    assertEquals(expectedISA, actualISA);
    programProductISAPage.cancelISA();
    HomePage homePage = new HomePage(testWebDriver);
    homePage.navigateHomePage();
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function")
  public void testMaximumProgramProductISA(String userSIC, String password, String program) throws Exception {
    setUpTestDataForProgramProductISA();
    Login(userSIC,password);
    ProgramProductISAPage programProductISAPage = navigateProgramProductISAPage(program);
    programProductISAPage.fillProgramProductISA("1", "2", "3", "4", "55", "10", "50");
    String actualISA = programProductISAPage.fillPopulation("1");
    String expectedISA = calculateISA("1", "2", "3", "4", "55", "10", "50", "1");
    assertEquals(expectedISA, actualISA);
    programProductISAPage.cancelISA();
    HomePage homePage = new HomePage(testWebDriver);
    homePage.navigateHomePage();
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function")
  public void testProgramProductISA(String userSIC, String password, String program) throws Exception {
    setUpTestDataForProgramProductISA();
    Login(userSIC,password);
    ProgramProductISAPage programProductISAPage = navigateProgramProductISAPage(program);
    programProductISAPage.fillProgramProductISA("1", "2", "3", "4", "5", "5", "1000");
    String actualISA = programProductISAPage.fillPopulation("1");
    String expectedISA = calculateISA("1", "2", "3", "4", "5", "5", "1000", "1");
    assertEquals(expectedISA, actualISA);
    programProductISAPage.cancelISA();
    HomePage homePage = new HomePage(testWebDriver);
    homePage.navigateHomePage();
  }


  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function")
  public void testISAFormula(String userSIC, String password, String program) throws Exception {
    setUpTestDataForProgramProductISA();
    Login(userSIC,password);
    ProgramProductISAPage programProductISAPage = navigateProgramProductISAPage(program);
    programProductISAPage.fillProgramProductISA("999.999", "999", "999.999", "999.999", "999999", "5", "1000");
    programProductISAPage.fillPopulation("1");
    String isaFormula = programProductISAPage.getISAFormulaFromISAFormulaModal();
    String expectedISAFormula = "(population) * 9.99999 * 999 * 999.999 / 12 * 10.99999 + 999999";
    assertEquals(expectedISAFormula, isaFormula);
    programProductISAPage.saveISA();
    programProductISAPage.verifyISAFormula(isaFormula);
    HomePage homePage = new HomePage(testWebDriver);
    homePage.navigateHomePage();
  }


  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function-Search")
  public void testSearchBox(String userSIC, String password, String program, String productName) throws Exception {
    setUpTestDataForProgramProductISA();
    Login(userSIC,password);
    ProgramProductISAPage programProductISAPage = navigateConfigureProductISAPage();
    programProductISAPage.selectProgram(program);
    programProductISAPage.searchProduct(productName);
    programProductISAPage.verifySearchResults(productName);
    HomePage homePage = new HomePage(testWebDriver);
    homePage.navigateHomePage();
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function-Verify-Push-Type-Program")
  public void testPushTypeProgramsInDropDown(String userSIC, String password, String program1, String program2) throws Exception {
    setUpTestDataForProgramProductISA();
    Login(userSIC,password);
    ProgramProductISAPage programProductISAPage = navigateConfigureProductISAPage();

    List<WebElement> valuesPresentInDropDown = programProductISAPage.getAllSelectOptionsFromProgramDropDown();
    List<String> programValuesToBeVerified = new ArrayList<String>();
    programValuesToBeVerified.add(program1);
    verifyAllSelectFieldValues(programValuesToBeVerified, valuesPresentInDropDown);

    dbWrapper.updateProgramToAPushType(program2, true);

    HomePage homePage = new HomePage(testWebDriver);
    homePage.navigateHomePage();
    homePage.navigateProgramProductISA();
    valuesPresentInDropDown = programProductISAPage.getAllSelectOptionsFromProgramDropDown();
    List<String> programValuesToBeVerifiedAfterUpdate = new ArrayList<String>();
    programValuesToBeVerifiedAfterUpdate.add(program1);
    programValuesToBeVerifiedAfterUpdate.add(program2);
    verifyAllSelectFieldValues(programValuesToBeVerifiedAfterUpdate, valuesPresentInDropDown);

  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function-Multiple-Programs")
  public void testProgramProductsMappings(String userSIC, String password, String program1, String program2,
                                          String product1, String product2,
                                          String product3, String product4) throws Exception {
    setUpTestDataForProgramProductISA();
    Login(userSIC,password);
    ProgramProductISAPage programProductISAPage = navigateConfigureProductISAPage();

    programProductISAPage.selectValueFromProgramDropDown(program1);
    String productsList = programProductISAPage.getProductsDisplayingOnConfigureProgramISAPage();
    assertTrue("Product " + product1 + " should be displayed", productsList.contains(product1));
    assertTrue("Product " + product2 + " should be displayed", productsList.contains(product2));

    dbWrapper.updateProgramToAPushType(program2, true);

    HomePage homePage = new HomePage(testWebDriver);
    homePage.navigateHomePage();
    homePage.navigateProgramProductISA();
    programProductISAPage.selectValueFromProgramDropDown(program2);
    productsList = programProductISAPage.getProductsDisplayingOnConfigureProgramISAPage();
    assertTrue("Product " + product3 + " should be displayed", productsList.contains(product3));
    assertTrue("Product " + product4 + " should be displayed", productsList.contains(product4));

  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function")
  public void testVerifyMandatoryFields(String userSIC, String password, String program) throws Exception {
    setUpTestDataForProgramProductISA();
    Login(userSIC,password);
    ProgramProductISAPage programProductISAPage = navigateProgramProductISAPage(program);
    programProductISAPage.fillProgramProductISA("", "1", "2", "3", "4", "10", "10");
    programProductISAPage.verifyFieldsOnISAModalWindow();
    programProductISAPage.saveISA();
    programProductISAPage.verifyMandatoryFieldsToBeFilled();
    programProductISAPage.verifyErrorMessageDiv();
  }


  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function")
  public void testVerifyMonthlyRestockAmountFieldAvailability(String userSIC, String password, String program) throws Exception {
    setUpTestDataForProgramProductISA();
    Login(userSIC,password);
    ProgramProductISAPage programProductISAPage = navigateProgramProductISAPage(program);
    programProductISAPage.fillProgramProductISA("1", "2", "3", "4", "0", "10", "10");
    programProductISAPage.verifyMonthlyRestockAmountPresent();
    programProductISAPage.cancelISA();
    HomePage homePage = new HomePage(testWebDriver);
    homePage.navigateHomePage();
  }

  private ProgramProductISAPage navigateProgramProductISAPage(String program) throws IOException {
    HomePage homePage = new HomePage(testWebDriver);
    ProgramProductISAPage programProductISAPage = homePage.navigateProgramProductISA();
    programProductISAPage.selectProgram(program);
    programProductISAPage.editFormula();
    return programProductISAPage;
  }


  private ProgramProductISAPage navigateConfigureProductISAPage() throws IOException {
    HomePage homePage = new HomePage(testWebDriver);
    ProgramProductISAPage programProductISAPage = homePage.navigateProgramProductISA();
    return programProductISAPage;
  }

    private HomePage Login(String userSIC, String password) throws IOException {
        LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
        HomePage homePage = loginPage.loginAs(userSIC, password);
        return homePage;
    }

  private void verifyAllSelectFieldValues(List<String> valuesToBeVerified, List<WebElement> valuesPresentInDropDown) {
    String collectionOfValuesPresentINDropDown = "";
    int valuesToBeVerifiedCounter = valuesToBeVerified.size();
    int valuesInSelectFieldCounter = valuesPresentInDropDown.size();

    if (valuesToBeVerifiedCounter == valuesInSelectFieldCounter - 1) {
      for (WebElement webElement : valuesPresentInDropDown) {
        collectionOfValuesPresentINDropDown = collectionOfValuesPresentINDropDown + webElement.getText().trim();
      }
      for (String values : valuesToBeVerified) {
        assertTrue(collectionOfValuesPresentINDropDown.contains(values));
      }
    } else {
      fail("Values in select field are not same in number as values to be verified");
    }

  }

  @AfterMethod(groups = "admin")
  @After
  public void tearDown() throws Exception {
    testWebDriver.sleep(500);
    if(!testWebDriver.getElementById("username").isDisplayed()) {
      HomePage homePage = new HomePage(testWebDriver);
      homePage.logout(baseUrlGlobal);
      dbWrapper.deleteData();
      dbWrapper.closeConnection();
    }

  }


  @DataProvider(name = "Data-Provider-Function")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {"Admin123", "Admin123", "VACCINES"}
    };

  }

  @DataProvider(name = "Data-Provider-Function-Search")
  public Object[][] parameterIntTestSearch() {
    return new Object[][]{
      {"Admin123", "Admin123", "VACCINES", "antibiotic1"}
    };

  }

  @DataProvider(name = "Data-Provider-Function-Verify-Push-Type-Program")
  public Object[][] parameterIntTestPushTypeProgram() {
    return new Object[][]{
      {"Admin123", "Admin123", "VACCINES", "TB"}
    };

  }

  @DataProvider(name = "Data-Provider-Function-Multiple-Programs")
  public Object[][] parameterIntTestMultipleProducts() {
    return new Object[][]{
      {"Admin123", "Admin123", "VACCINES", "TB", "antibiotic1", "antibiotic2", "antibiotic3", "antibiotic4"}
    };

  }
}

