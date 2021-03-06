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

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.User;
import org.openlmis.core.service.UserService;
import org.openlmis.distribution.domain.Distribution;
import org.openlmis.distribution.domain.FacilityDistributionData;
import org.openlmis.distribution.service.DistributionService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;

import static org.openlmis.web.response.OpenLmisResponse.SUCCESS;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Controller
@NoArgsConstructor
public class DistributionController extends BaseController {

  @Autowired
  DistributionService distributionService;

  @Autowired
  UserService userService;
  public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

  @RequestMapping(value = "/distributions", method = POST, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> create(@RequestBody Distribution distribution, HttpServletRequest request) {
    Distribution existingDistribution = distributionService.get(distribution);

    if (existingDistribution != null) {
      return returnInitiatedDistribution(distribution, existingDistribution);
    }

    distribution.setCreatedBy(loggedInUserId(request));
    distribution.setModifiedBy(loggedInUserId(request));

    Distribution initiatedDistribution = distributionService.create(distribution);

    OpenLmisResponse openLmisResponse = new OpenLmisResponse("distribution", initiatedDistribution);
    openLmisResponse.addData(SUCCESS, messageService.message("message.distribution.created.success",
      distribution.getDeliveryZone().getName(), distribution.getProgram().getName(), distribution.getPeriod().getName()));
    return openLmisResponse.response(CREATED);
  }

  @RequestMapping(value = "/distributions/{id}/facilities/{facilityId}", method = PUT, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> sync(@RequestBody FacilityDistributionData facilityDistributionData, @PathVariable Long id,
                                               @PathVariable Long facilityId, HttpServletRequest httpServletRequest) {
    facilityDistributionData.setFacilityId(facilityId);
    facilityDistributionData.setDistributionId(id);
    facilityDistributionData.setCreatedBy(loggedInUserId(httpServletRequest));
    return OpenLmisResponse.response("syncStatus", distributionService.sync(facilityDistributionData));
  }


  private ResponseEntity<OpenLmisResponse> returnInitiatedDistribution(Distribution distribution, Distribution existingDistribution) {
    existingDistribution.setDeliveryZone(distribution.getDeliveryZone());
    existingDistribution.setPeriod(distribution.getPeriod());
    existingDistribution.setProgram(distribution.getProgram());

    OpenLmisResponse openLmisResponse = new OpenLmisResponse("distribution", existingDistribution);
    User createdByUser = userService.getById(existingDistribution.getCreatedBy());
    openLmisResponse.addData("message", messageService.message("message.distribution.already.exists",
      createdByUser.getUserName(), DATE_FORMAT.format(existingDistribution.getCreatedDate())));
    openLmisResponse.addData(SUCCESS, messageService.message("message.distribution.created.success",
      distribution.getDeliveryZone().getName(), distribution.getProgram().getName(), distribution.getPeriod().getName()));
    return openLmisResponse.response(OK);
  }
}
