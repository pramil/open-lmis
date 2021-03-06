/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.rnr.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.*;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.service.RequisitionService;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface RequisitionMapper {

  @Insert("INSERT INTO requisitions(facilityId, programId, periodId, status, emergency, modifiedBy, createdBy) " +
      "VALUES (#{facility.id}, #{program.id}, #{period.id}, #{status}, #{emergency}, #{modifiedBy}, #{createdBy})")
  @Options(useGeneratedKeys = true)
  void insert(Rnr requisition);

  @Update({"UPDATE requisitions SET",
      "modifiedBy = #{modifiedBy}, modifiedDate = CURRENT_TIMESTAMP, status = #{status},",
      "fullSupplyItemsSubmittedCost = #{fullSupplyItemsSubmittedCost},",
      "nonFullSupplyItemsSubmittedCost = #{nonFullSupplyItemsSubmittedCost},",
      "supervisoryNodeId = #{supervisoryNodeId}",
      "WHERE id = #{id}"})
  void update(Rnr requisition);

  @Select("SELECT * FROM requisitions WHERE id = #{rnrId}")
  @Results(value = {
      @Result(property = "id", column = "id"),
      @Result(property = "program.id", column = "programId"),
      @Result(property = "facility.id", column = "facilityId"),
      @Result(property = "period.id", column = "periodId"),
      @Result(property = "fullSupplyLineItems", javaType = List.class, column = "id",
          many = @Many(select = "org.openlmis.rnr.repository.mapper.RnrLineItemMapper.getRnrLineItemsByRnrId")),
      @Result(property = "nonFullSupplyLineItems", javaType = List.class, column = "id",
          many = @Many(select = "org.openlmis.rnr.repository.mapper.RnrLineItemMapper.getNonFullSupplyRnrLineItemsByRnrId")),
      @Result(property = "regimenLineItems", javaType = List.class, column = "id",
          many = @Many(select = "org.openlmis.rnr.repository.mapper.RegimenLineItemMapper.getRegimenLineItemsByRnrId"))
  })
  Rnr getById(Long rnrId);

  @Select({"SELECT id, emergency, programId, facilityId, periodId, modifiedDate",
      "FROM requisitions ",
      "WHERE programId =  #{programId}",
      "AND supervisoryNodeId =  #{supervisoryNode.id} AND status IN ('AUTHORIZED', 'IN_APPROVAL')"})
  @Results({@Result(property = "program.id", column = "programId"),
      @Result(property = "facility.id", column = "facilityId"),
      @Result(property = "period.id", column = "periodId")})
  List<Rnr> getAuthorizedRequisitions(RoleAssignment roleAssignment);

  @Select("SELECT * FROM requisitions WHERE facilityId = #{facility.id} AND programId= #{program.id} AND periodId = #{period.id}")
  @Results(value = {
      @Result(property = "id", column = "id"),
      @Result(property = "facility.id", column = "facilityId"),
      @Result(property = "program.id", column = "programId"),
      @Result(property = "period.id", column = "periodId"),
      @Result(property = "fullSupplyLineItems", javaType = List.class, column = "id",
          many = @Many(select = "org.openlmis.rnr.repository.mapper.RnrLineItemMapper.getRnrLineItemsByRnrId")),
      @Result(property = "nonFullSupplyLineItems", javaType = List.class, column = "id",
          many = @Many(select = "org.openlmis.rnr.repository.mapper.RnrLineItemMapper.getNonFullSupplyRnrLineItemsByRnrId")),
      @Result(property = "regimenLineItems", javaType = List.class, column = "id",
          many = @Many(select = "org.openlmis.rnr.repository.mapper.RegimenLineItemMapper.getRegimenLineItemsByRnrId")),
  })
  Rnr getRequisitionWithLineItems(@Param("facility") Facility facility, @Param("program") Program program, @Param("period") ProcessingPeriod period);

  @Select({"SELECT * FROM requisitions R",
      "WHERE facilityId = #{facilityId}",
      "AND programId = #{programId} ",
      "AND status NOT IN ('INITIATED', 'SUBMITTED')",
      "AND emergency = false",
      "ORDER BY (select startDate from processing_periods where id=R.periodId) DESC LIMIT 1"})
  @Results(value = {
      @Result(property = "facility.id", column = "facilityId"),
      @Result(property = "program.id", column = "programId"),
      @Result(property = "period.id", column = "periodId")
  })
  Rnr getLastRegularRequisitionToEnterThePostSubmitFlow(@Param(value = "facilityId") Long facilityId, @Param(value = "programId") Long programId);

  @Select({"SELECT * FROM requisitions WHERE",
      "facilityId = #{facility.id} AND",
      "programId = #{program.id} AND ",
      "periodId = ANY (#{periods}::INTEGER[]) AND ",
      "status NOT IN ('INITIATED', 'SUBMITTED')"})
  @Results(value = {
      @Result(property = "facility.id", column = "facilityId"),
      @Result(property = "program.id", column = "programId"),
      @Result(property = "period.id", column = "periodId")
  })
  List<Rnr> getPostSubmitRequisitions(@Param("facility") Facility facility, @Param("program") Program program, @Param("periods") String periodIds);

  @Select({"SELECT * FROM requisitions WHERE",
      "facilityId = #{facilityId} AND",
      "programId = #{programId} AND ",
      "periodId = #{periodId} AND ",
      "emergency = false"
  })
  @Results(value = {
      @Result(property = "facility.id", column = "facilityId"),
      @Result(property = "program.id", column = "programId"),
      @Result(property = "period.id", column = "periodId")
  })
  Rnr getRequisitionWithoutLineItems(@Param("facilityId") Long facilityId, @Param("programId") Long programId, @Param("periodId") Long periodId);

  @Select("SELECT * FROM requisitions WHERE id = #{rnrId}")
  @Results(value = {
      @Result(property = "id", column = "id"),
      @Result(property = "program.id", column = "programId"),
      @Result(property = "facility.id", column = "facilityId"),
      @Result(property = "period.id", column = "periodId"),
      @Result(property = "supplyingFacility.id", column = "supplyingFacilityId")
  })
  Rnr getLWById(Long rnrId);

  @Select("SELECT * FROM requisitions WHERE facilityId = #{facility.id} AND programId= #{program.id} AND periodId = #{period.id} AND emergency = FALSE")
  @Results(value = {
      @Result(property = "id", column = "id"),
      @Result(property = "facility.id", column = "facilityId"),
      @Result(property = "program.id", column = "programId"),
      @Result(property = "period.id", column = "periodId"),
      @Result(property = "fullSupplyLineItems", javaType = List.class, column = "id",
          many = @Many(select = "org.openlmis.rnr.repository.mapper.RnrLineItemMapper.getRnrLineItemsByRnrId")),
  })
  Rnr getRegularRequisitionWithLineItems(@Param("facility") Facility facility, @Param("program") Program program, @Param("period") ProcessingPeriod period);

  @Select({"SELECT * FROM requisitions WHERE",
      "facilityId = #{facilityId} AND",
      "programId = #{programId} AND",
      "emergency = TRUE AND",
      "status IN ('INITIATED', 'SUBMITTED') ORDER BY createdDate DESC"
  })
  @Results(value = {
      @Result(property = "facility.id", column = "facilityId"),
      @Result(property = "program.id", column = "programId"),
      @Result(property = "period", column = "periodId", javaType = ProcessingPeriod.class,
          one = @One(select = "org.openlmis.core.repository.mapper.ProcessingPeriodMapper.getById"))
  })
  List<Rnr> getInitiatedOrSubmittedEmergencyRequisitions(@Param("facilityId") Long facilityId,
                                                         @Param("programId") Long programId);

  @SelectProvider(type = ApprovedRequisitionSearch.class, method = "getApprovedRequisitionsByCriteria")
  @Results(value = {
      @Result(property = "program.id", column = "programId"),
      @Result(property = "facility.id", column = "facilityId"),
      @Result(property = "period.id", column = "periodId"),
      @Result(property = "supplyingFacility.id", column = "supplyingFacilityId")
  })
  List<Rnr> getApprovedRequisitionsForCriteriaAndPageNumber(@Param("searchType") String searchType, @Param("searchVal") String searchVal,
                                                            @Param("pageNumber") Integer pageNumber, @Param("pageSize") Integer pageSize,
                                                            @Param("userId") Long userId, @Param("right") Right right,
                                                            @Param("sortBy") String sortBy, @Param("sortDirection") String sortDirection);

  @SelectProvider(type = ApprovedRequisitionSearch.class, method = "getCountOfApprovedRequisitionsForCriteria")
  Integer getCountOfApprovedRequisitionsForCriteria(@Param("searchType") String searchType, @Param("searchVal") String searchVal,
                                                    @Param("userId") Long userId, @Param("right") Right right);

  @Select({"SELECT facilityid FROM requisitions WHERE id = #{id}"})
  Long getFacilityId(Long id);

  @Select({"SELECT * FROM requisitions WHERE facilityId = #{facility.id} AND programId = #{program.id} AND emergency = false",
      "ORDER BY createdDate DESC LIMIT 1"})
  @Results(value = {
      @Result(property = "facility.id", column = "facilityId"),
      @Result(property = "program.id", column = "programId"),
      @Result(property = "period.id", column = "periodId")
  })
  Rnr getLastRegularRequisition(@Param("facility") Facility facility, @Param("program") Program program);

  public class ApprovedRequisitionSearch {

    @SuppressWarnings("UnusedDeclaration")
    public static String getApprovedRequisitionsByCriteria(Map<String, Object> params) {
      StringBuilder sql = new StringBuilder();
      sql.append("SELECT DISTINCT R.id, R.emergency, R.programId, R.facilityId, R.periodId, R.status, R.supervisoryNodeId," +
          " R.modifiedDate as modifiedDate, RSC.createdDate as submittedDate, P.name AS programName, F.name AS facilityName," +
          " F.code AS facilityCode, SF.name AS supplyingDepotName, PP.startDate as periodStartDate, PP.endDate as periodEndDate" +
          " FROM Requisitions R INNER JOIN requisition_status_changes RSC ON R.id = RSC.rnrId AND RSC.status = 'SUBMITTED' " +
          " INNER JOIN processing_periods PP ON PP.id = R.periodId ");

      appendQueryClausesBySearchType(sql, params);

      Integer pageNumber = (Integer) params.get("pageNumber");
      Integer pageSize = (Integer) params.get("pageSize");
      String sortBy = (String) params.get("sortBy");
      String sortDirection = (String) params.get("sortDirection");

      return sql.append("ORDER BY " + sortBy + " " + sortDirection).append(" LIMIT ").append(pageSize)
          .append(" OFFSET ").append((pageNumber - 1) * pageSize).toString();
    }

    @SuppressWarnings("UnusedDeclaration")
    public static String getCountOfApprovedRequisitionsForCriteria(Map params) {
      StringBuilder sql = new StringBuilder();
      sql.append("SELECT COUNT(DISTINCT R.id) FROM Requisitions R ");

      appendQueryClausesBySearchType(sql, params);
      return sql.toString();
    }

    private static void appendQueryClausesBySearchType(StringBuilder sql, Map<String, Object> params) {
      String searchType = (String) params.get("searchType");
      String searchVal = ((String) params.get("searchVal")).toLowerCase();
      Long userId = (Long) params.get("userId");
      Right right = (Right) params.get("right");

      if (userId != null && right != null) {
        sql.append("INNER JOIN supply_lines S ON R.supervisoryNodeId = S.supervisoryNodeId " +
            "INNER JOIN fulfillment_role_assignments FRA ON S.supplyingFacilityId = FRA.facilityId " +
            "INNER JOIN role_rights RR ON FRA.roleId = RR.roleId " +
            "INNER JOIN Programs P ON P.id = R.programId " +
            "INNER JOIN Facilities F ON F.id = R.facilityId " +
            "LEFT JOIN Supply_lines SL ON (SL.supervisoryNodeId = R.supervisoryNodeId AND SL.programId = R.programId) " +
            "LEFT JOIN Facilities SF ON SL.supplyingFacilityId = SF.id ");
      }

      if (searchVal.isEmpty()) {
        sql.append("WHERE ");
      } else if (searchType.isEmpty() || searchType.equalsIgnoreCase(RequisitionService.SEARCH_ALL)) {
        sql.append("WHERE (LOWER(P.name) LIKE '%" + searchVal + "%' OR LOWER(F.name) LIKE '%" +
            searchVal + "%' OR LOWER(F.code) LIKE '%" + searchVal + "%' OR LOWER(SF.name) LIKE '%" + searchVal + "%') AND ");
      } else if (searchType.equalsIgnoreCase(RequisitionService.SEARCH_FACILITY_CODE)) {
        sql.append("WHERE LOWER(F.code) LIKE '%" + searchVal + "%' AND ");
      } else if (searchType.equalsIgnoreCase(RequisitionService.SEARCH_FACILITY_NAME)) {
        sql.append("WHERE LOWER(F.name) LIKE '%" + searchVal + "%' AND ");
      } else if (searchType.equalsIgnoreCase(RequisitionService.SEARCH_PROGRAM_NAME)) {
        sql.append("WHERE LOWER(P.name) LIKE '%" + searchVal + "%' AND ");
      } else if (searchType.equalsIgnoreCase(RequisitionService.SEARCH_SUPPLYING_DEPOT_NAME)) {
        sql.append("WHERE LOWER(SF.name) LIKE '%" + searchVal + "%' AND ");
      }
      sql.append("FRA.userid = " + userId + " AND RR.rightName = '" + right + "' AND ");
      sql.append("R.status = 'APPROVED'");
    }
  }
}

