/*
 * Copyright 2019 Marcus Portmann
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

import {SortDirection} from './sort-direction';
import {User} from "./user";

/**
 * The Users class holds the results of a request to retrieve a list of users.
 *
 * @author Marcus Portmann
 */
export class Users {

  /**
   * The optional filter that was applied to the users.
   */
  filter?: string;

  /**
   * The optional page size.
   */
  pageIndex?: number;

  /**
   * The optional page index.
   */
  pageSize?: number;

  /**
   * The users.
   */
  users: User[];

  /**
   * The optional sort direction that was applied to the users.
   */
  sortDirection?: SortDirection;

  /**
   * The total number of users.
   */
  total: number;

  /**
   * Constructs a new Users.
   *
   * @param users         The users.
   * @param total         The total number of users.
   * @param filter        The optional filter that was applied to the users.
   * @param sortDirection The optional sort direction that was applied to the users.
   * @param pageIndex     The optional page index.
   * @param pageSize      The optional page size.
   */
  constructor(users: User[], total: number, filter?: string,
              sortDirection?: SortDirection, pageIndex?: number, pageSize?: number) {
    this.users = users;
    this.total = total;
    this.filter = filter;
    this.sortDirection = sortDirection;
    this.pageIndex = pageIndex;
    this.pageSize = pageSize;
  }
}
