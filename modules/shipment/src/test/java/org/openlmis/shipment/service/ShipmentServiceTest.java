/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.shipment.service;


import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.ProductService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.shipment.domain.ShipmentFileInfo;
import org.openlmis.shipment.domain.ShipmentLineItem;
import org.openlmis.shipment.repository.ShipmentRepository;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.shipment.builder.ShipmentLineItemBuilder.*;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ShipmentServiceTest {

  @Mock
  private ShipmentRepository shipmentRepository;


  @Mock
  private ProductService productService;

  @InjectMocks
  private ShipmentService shipmentService;

  @Rule
  public ExpectedException exException = ExpectedException.none();

  @Test
  public void shouldInsertShipmentIfNewLineItem() throws Exception {
    ShipmentLineItem shipmentLineItem = make(a(defaultShipmentLineItem,
      with(productCode, "P10"),
      with(orderId, 1L),
      with(quantityShipped, 500)));


    when(productService.getIdForCode("P10")).thenReturn(1l);
    when(shipmentRepository.getShippedLineItem(shipmentLineItem)).thenReturn(null);

    shipmentService.insertOrUpdate(shipmentLineItem);

    verify(productService).getIdForCode("P10");
    verify(shipmentRepository).getShippedLineItem(shipmentLineItem);
    verify(shipmentRepository).insertShippedLineItem(shipmentLineItem);
  }

  @Test
  public void shouldUpdateShipmentIfComesAgainInSameFile() throws Exception {
    ShipmentLineItem shipmentLineItem = make(a(defaultShipmentLineItem,
      with(productCode, "P10"),
      with(orderId, 1L),
      with(quantityShipped, 500)));


    when(productService.getIdForCode("P10")).thenReturn(1l);
    when(shipmentRepository.getShippedLineItem(shipmentLineItem)).thenReturn(shipmentLineItem);

    shipmentService.insertOrUpdate(shipmentLineItem);

    verify(productService).getIdForCode("P10");
    verify(shipmentRepository).getShippedLineItem(shipmentLineItem);
    verify(shipmentRepository).updateShippedLineItem(shipmentLineItem);
  }

  @Test
  public void shouldNotInsertShipmentIfProductCodeIsNotValid() throws Exception {

    ShipmentLineItem shipmentLineItem = make(a(defaultShipmentLineItem,
      with(productCode, "P10"),
      with(orderId, 1L),
      with(quantityShipped, 500)));

    when(productService.getIdForCode("P10")).thenReturn(null);


    exException.expect(DataException.class);
    exException.expectMessage("error.unknown.product");

    shipmentService.insertOrUpdate(shipmentLineItem);
  }

  @Test
  public void shouldNotInsertShipmentIfQuantityNegative() throws Exception {
    ShipmentLineItem shipmentLineItem = make(a(defaultShipmentLineItem,
      with(productCode, "P10"),
      with(orderId, 1L),
      with(quantityShipped, -1)));

    when(productService.getIdForCode("P10")).thenReturn(1l);
    exException.expect(DataException.class);
    exException.expectMessage("error.negative.shipped.quantity");

    shipmentService.insertOrUpdate(shipmentLineItem);
  }

  @Test
  public void shouldInsertShipmentInfo() throws Exception {
    ShipmentFileInfo shipmentFileInfo = new ShipmentFileInfo();
    shipmentService.insertShipmentFileInfo(shipmentFileInfo);
    verify(shipmentRepository).insertShipmentFileInfo(shipmentFileInfo);
  }

}
