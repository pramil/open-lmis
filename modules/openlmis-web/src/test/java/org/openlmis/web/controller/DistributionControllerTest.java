/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.openlmis.core.domain.User;
import org.openlmis.core.service.MessageService;
import org.openlmis.core.service.UserService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.distribution.domain.Distribution;
import org.openlmis.distribution.domain.FacilityDistributionData;
import org.openlmis.distribution.service.DistributionService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import java.util.Date;
import java.util.Map;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.core.builder.UserBuilder.defaultUser;
import static org.openlmis.distribution.builder.DistributionBuilder.*;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class DistributionControllerTest {

  public static final Long USER_ID = 1l;
  @Mock
  DistributionService service;

  @Mock
  UserService userService;

  @Mock
  MessageService messageService;

  private MockHttpSession session;

  private MockHttpServletRequest httpServletRequest;


  @InjectMocks
  DistributionController controller;

  @Before
  public void setUp() throws Exception {
    httpServletRequest = new MockHttpServletRequest();
    session = new MockHttpSession();
    httpServletRequest.setSession(session);
    session.setAttribute(UserAuthenticationSuccessHandler.USER_ID, USER_ID);
  }

  @Test
  public void shouldCreateDistribution() throws Exception {
    String username = "User";
    session.setAttribute(UserAuthenticationSuccessHandler.USER, username);
    Distribution distribution = make(a(defaultDistribution));

    Distribution expectedDistribution = new Distribution();
    when(service.create(distribution)).thenReturn(expectedDistribution);
    when(service.get(distribution)).thenReturn(null);
    when(messageService.message("message.distribution.created.success", null, null, null)
    ).thenReturn("Distribution created successfully");

    ResponseEntity<OpenLmisResponse> response = controller.create(distribution, httpServletRequest);

    assertThat((Distribution) response.getBody().getData().get("distribution"), is(expectedDistribution));
    assertThat((String) response.getBody().getData().get("success"), is("Distribution created successfully"));
    assertThat(response.getStatusCode(), is(CREATED));
    verify(service).get(distribution);
    verify(service).create(distribution);

  }

  @Test
  public void shouldReturnExistingDistributionWithWarningIfAlreadyExist() throws Exception {
    Long createdById = 10L;
    Date creationTimeStamp = new Date();
    Distribution distribution = make(a(defaultDistribution));

    Distribution existingDistribution = make(a(defaultDistribution,
      with(createdBy, createdById),
      with(createdDate, creationTimeStamp)));

    when(service.get(distribution)).thenReturn(existingDistribution);

    User user = make(a(defaultUser));
    when(userService.getById(createdById)).thenReturn(user);

    when(messageService.message("message.distribution.already.exists", user.getUserName(), DistributionController.DATE_FORMAT.format(creationTimeStamp))).
      thenReturn("Distribution already initiated by XYZ at 2013-05-03 12:10");
    when(messageService.message("message.distribution.created.success", null, null, null)
    ).thenReturn("Distribution created successfully");

    ResponseEntity<OpenLmisResponse> response = controller.create(distribution, httpServletRequest);
    assertThat(response.getStatusCode(), is(OK));
    Map<String, Object> responseData = response.getBody().getData();

    assertThat((String) responseData.get("message"),
      is("Distribution already initiated by XYZ at 2013-05-03 12:10"));
    assertThat((Distribution) responseData.get("distribution"), is(existingDistribution));
    assertThat((String) response.getBody().getData().get("success"), is("Distribution created successfully"));
  }

  @Test
  public void shouldSyncFacilityDistributionData() {
    Long distributionId = 1l;
    Long facilityId = 3l;
    FacilityDistributionData facilityDistributionData = new FacilityDistributionData(distributionId, facilityId);

    when(service.sync(facilityDistributionData)).thenReturn("Synced");
    ResponseEntity<OpenLmisResponse> response = controller.sync(facilityDistributionData, distributionId, facilityId, httpServletRequest);

    assertThat(response.getStatusCode(), is(HttpStatus.OK));
    assertThat((String)response.getBody().getData().get("syncStatus") , is("Synced"));
    verify(service).sync(facilityDistributionData);
    assertThat(facilityDistributionData.getFacilityId(), is(facilityId));
    assertThat(facilityDistributionData.getDistributionId(), is(distributionId));
  }

  @Test
  public void shouldReturnErrorIfAlreadySynced() throws Exception {
    Long distributionId = 1l;
    Long facilityId = 3l;
    FacilityDistributionData facilityDistributionData = new FacilityDistributionData(distributionId, facilityId);

    when(service.sync(facilityDistributionData)).thenReturn("Failed");

    ResponseEntity<OpenLmisResponse> response = controller.sync(facilityDistributionData, distributionId, facilityId, httpServletRequest);

    assertThat((String)response.getBody().getData().get("syncStatus") , is("Failed"));
  }
}
