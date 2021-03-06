
/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.pod.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openlmis.pod.domain.POD;
import org.openlmis.pod.domain.PODLineItem;
import org.openlmis.rnr.domain.Rnr;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface PODMapper {

  @Insert({"INSERT INTO pod_line_items (podId, productCode, quantityReceived, createdBy, modifiedBy) values",
    "(#{podId}, #{productCode}, #{quantityReceived}, #{createdBy}, #{modifiedBy} )"})
  @Options(useGeneratedKeys = true)
  void insertPODLineItem(PODLineItem podLineItem);

  @Select("SELECT * FROM pod_line_items where podId = #{podId}")
  List<PODLineItem> getPODLineItemsByPODId(Long podId);

  @Insert({"INSERT INTO pod (orderId, facilityId, programId, periodId, receivedDate, createdBy, modifiedBy) values ",
    "(#{orderId}, #{facilityId}, #{programId}, #{periodId}, DEFAULT, #{createdBy}, #{modifiedBy} )"})
  @Options(useGeneratedKeys = true)
  void insertPOD(POD pod);

  @Select("SELECT * FROM pod WHERE orderId = #{orderId}")
  POD getPODByOrderId(Long orderId);

  @Select({"SELECT PLI.* FROM pod_line_items PLI INNER JOIN pod P ON PLI.podId = P.id " +
    "WHERE P.facilityId = #{requisition.facility.id} ",
    "AND P.programId = #{requisition.program.id} ",
    "AND P.createdDate >= #{startDate} ",
    "AND PLI.productCode = #{productCode}",
    "ORDER BY p.createdDate DESC LIMIT #{n}"})
  List<PODLineItem> getNPodLineItems(@Param("productCode") String productCode, @Param("requisition") Rnr requisition,
                                     @Param("n") int n, @Param("startDate") Date startDate);
}
