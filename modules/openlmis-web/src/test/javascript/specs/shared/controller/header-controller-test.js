/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe("HeaderController", function () {

  beforeEach(module('openlmis.services'));
  beforeEach(module('openlmis'));

  beforeEach(module('openlmis.localStorage'));
  beforeEach(module('ui.directives'));

  var scope, loginConfig;

  beforeEach(inject(function ($rootScope, $controller) {
    loginConfig = {a: {}, b: {}};
    scope = $rootScope.$new();
    $controller(HeaderController, {$scope: scope, loginConfig: loginConfig});
  }));

  it('should set login config in scope', function () {
    expect(scope.loginConfig).toEqual(loginConfig);
  })
});