--
-- This program is part of the OpenLMIS logistics management information system platform software.
-- Copyright © 2013 VillageReach
--
-- This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
--  
-- This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
-- You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
--

INSERT INTO processing_schedules (code, name, description) VALUES ('Q1stM', 'QuarterMonthly', 'QuarterMonth');
INSERT INTO processing_schedules (code, name, description) VALUES ('M', 'Monthly', 'Month');


INSERT INTO processing_periods
(name, description, startDate, endDate, numberOfMonths, scheduleId, modifiedBy) VALUES
('Period1', 'first period', '2012-12-01', '2013-02-28 23:59:59', 2, (SELECT
                                                                       id
                                                                     FROM processing_schedules
                                                                     WHERE code = 'Q1stM'), (SELECT
                                                                                               id
                                                                                             FROM users
                                                                                             LIMIT 1)),
('Period2', 'second period', '2013-03-01', '2013-05-31 23:59:59', 2, (SELECT
                                                                        id
                                                                      FROM processing_schedules
                                                                      WHERE code = 'Q1stM'), (SELECT
                                                                                                id
                                                                                              FROM users
                                                                                              LIMIT 1)),
('Dec2012', 'Dec2012', '2012-12-01', '2012-12-31 23:59:59', 1, (SELECT
                                                                  id
                                                                FROM processing_schedules
                                                                WHERE code = 'M'), (SELECT
                                                                                      id
                                                                                    FROM users
                                                                                    LIMIT 1)),
('Jan2013', 'Jan2013', '2013-01-01', '2013-01-31 23:59:59', 1, (SELECT
                                                                  id
                                                                FROM processing_schedules
                                                                WHERE code = 'M'), (SELECT
                                                                                      id
                                                                                    FROM users
                                                                                    LIMIT 1)),
('Feb2013', 'Feb2013', '2013-02-01', '2013-02-28 23:59:59', 1, (SELECT
                                                                  id
                                                                FROM processing_schedules
                                                                WHERE code = 'M'), (SELECT
                                                                                      id
                                                                                    FROM users
                                                                                    LIMIT 1)),
('Mar2013', 'Mar2013', '2013-03-01', '2013-03-31 23:59:59', 1, (SELECT
                                                                  id
                                                                FROM processing_schedules
                                                                WHERE code = 'M'), (SELECT
                                                                                      id
                                                                                    FROM users
                                                                                    LIMIT 1)),
('Apr2013', 'Apr2013', '2013-04-01', '2013-04-30 23:59:59', 1, (SELECT
                                                                  id
                                                                FROM processing_schedules
                                                                WHERE code = 'M'), (SELECT
                                                                                      id
                                                                                    FROM users
                                                                                    LIMIT 1)),
('May2013', 'May2013', '2013-05-01', '2013-05-31 23:59:59', 1, (SELECT
                                                                  id
                                                                FROM processing_schedules
                                                                WHERE code = 'M'), (SELECT
                                                                                      id
                                                                                    FROM users
                                                                                    LIMIT 1)),
('June2013', 'June2013', '2013-06-01', '2013-06-30 23:59:59', 1, (SELECT
                                                                    id
                                                                  FROM processing_schedules
                                                                  WHERE code = 'M'), (SELECT
                                                                                        id
                                                                                      FROM users
                                                                                      LIMIT 1)),
('Current Period', 'Current Period', '2013-07-01', '2016-06-30 23:59:59', 1, (SELECT
                                                                                id
                                                                              FROM processing_schedules
                                                                              WHERE code = 'M'), (SELECT
                                                                                                    id
                                                                                                  FROM users
                                                                                                  LIMIT 1));