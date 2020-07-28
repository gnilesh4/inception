/*
 * Copyright 2020 Marcus Portmann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {Organization} from './organization';
import {SortDirection} from './sort-direction';

/**
 * The Organizations class holds the results of a request to retrieve a list of organizations.
 *
 * @author Marcus Portmann
 */
export class Organizations {

  /**
   * The optional filter that was applied to the organizations.
   */
  filter?: string;

  /**
   * The optional page index.
   */
  pageIndex?: number;

  /**
   * The optional page size.
   */
  pageSize?: number;

  /**
   * The organizations.
   */
  organizations: Organization[];

  /**
   * The optional sort direction that was applied to the organizations.
   */
  sortDirection?: SortDirection;

  /**
   * The total number of organizations.
   */
  total: number;

  /**
   * Constructs a new Organizations.
   *
   * @param organizations The organizations.
   * @param total         The total number of organizations.
   * @param filter        The optional filter that was applied to the organizations.
   * @param sortDirection The optional sort direction that was applied to the organizations.
   * @param pageIndex     The optional page index.
   * @param pageSize      The optional page size.
   */
  constructor(organizations: Organization[], total: number, filter?: string, sortDirection?: SortDirection,
              pageIndex?: number, pageSize?: number) {
    this.organizations = organizations;
    this.total = total;
    this.filter = filter;
    this.sortDirection = sortDirection;
    this.pageIndex = pageIndex;
    this.pageSize = pageSize;
  }
}
