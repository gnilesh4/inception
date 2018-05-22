/*
 * Copyright 2018 Marcus Portmann
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

import { Injectable } from "@angular/core";
import {Router} from "@angular/router";

import {AdminContainerComponent} from "../../components/layout/admin-container";
import {NavigationItem} from "./navigation-item";

/**
 * The NavigationService class provides the Navigation Service implementation.
 *
 * @author Marcus Portmann
 */
@Injectable()
export class NavigationService {

  private readonly navigation: NavigationItem[] = [];

  /**
   * Constructs a new NavigationService.
   */
  constructor(private router: Router) {
  }

  /**
   * Add the navigation item to the navigation.
   *
   * @param {NavigationItem} navigationItem The navigation item to add.
   */
  addNavigationItem(navigationItem: NavigationItem) {

    // Add the navigation item to the navigation
    this.navigation.push(navigationItem);

    // Add the dynamic route to the router for the navigation item
    this.router.config.unshift({path: navigationItem.routePath, component: AdminContainerComponent, loadChildren: navigationItem.routeLoadChildren });
  }

  /**
   * Retrieve the navigation.
   *
   * @return {NavigationItem[]}
   */
  getNavigation(): NavigationItem[] {
    return this.navigation;
  }
}

