/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.pod.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.exception.DataException;
import org.openlmis.rnr.domain.Rnr;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class POD extends BaseModel {

  private Long orderId;
  private Long facilityId;
  private Long programId;
  private Long periodId;
  private List<PODLineItem> podLineItems;

  public POD(Long id) {
    this.id = id;
  }

  public void validate() {
    if (orderId == null || podLineItems == null || podLineItems.size() == 0) {
      throw new DataException("error.restapi.mandatory.missing");
    }
    for (PODLineItem lineItem : podLineItems) {
      lineItem.validate();
      lineItem.setCreatedBy(createdBy);
      lineItem.setModifiedBy(modifiedBy);
    }
  }

  public void fillPOD(Rnr requisition) {
    this.facilityId = requisition.getFacility().getId();
    this.programId = requisition.getProgram().getId();
    this.periodId = requisition.getPeriod().getId();
  }
}
