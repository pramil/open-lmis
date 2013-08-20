/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openlmis.core.repository;

import org.openlmis.core.domain.Configuration;
import org.openlmis.core.repository.mapper.ConfigurationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ConfigurationRepository {
  @Autowired
  private ConfigurationMapper configurationMapper;

  public Configuration getConfiguration() {
    return configurationMapper.getConfiguration();
  }
}