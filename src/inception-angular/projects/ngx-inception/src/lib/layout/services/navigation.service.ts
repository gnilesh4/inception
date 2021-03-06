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

import {Injectable} from '@angular/core';
import {NavigationItem} from './navigation-item';
import {ReplaySubject, Subject} from 'rxjs';
import {map} from 'rxjs/operators';
import {Session} from '../../security/services/session';
import {SecurityService} from '../../security/services/security.service';

/**
 * The Navigation Service implementation.
 *
 * @author Marcus Portmann
 */
@Injectable({
  providedIn: 'root'
})
export class NavigationService {

  userNavigation$: Subject<NavigationItem[]> = new ReplaySubject<NavigationItem[]>();

  private navigation: NavigationItem[] = [];

  /**
   * Constructs a new NavigationService.
   *
   * @param securityService The security service.
   */
  constructor(private securityService: SecurityService) {
    console.log('Initializing the Navigation Service');

    this.securityService.session$.pipe(map((session: Session | null) => {
      this.userNavigation$.next(Object.assign([], this.filterNavigationItems(this.navigation, session)));
    })).subscribe();
  }

  private static hasAccessToNavigationItem(authorities: string[], session: Session): boolean {
    for (const authority of authorities) {
      if (session.hasAuthority(authority)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Initialize the navigation.
   *
   * @param navigation The navigation.
   */
  initNavigation(navigation: NavigationItem[]) {
    this.navigation = navigation;

    this.userNavigation$.next(this.filterNavigationItems(navigation, null));
  }

  private filterNavigationItems(navigationItems: NavigationItem[], session: Session | null): NavigationItem[] {
    if (!navigationItems) {
      return navigationItems;
    }

    const filteredNavigationItems: NavigationItem[] = [];

    for (const navigationItem of navigationItems) {
      const authorities = (navigationItem.authorities == null) ? [] : navigationItem.authorities;

      if (authorities.length > 0) {
        if (session) {
          if (NavigationService.hasAccessToNavigationItem(authorities, session)) {
            const filteredChildNavigationItems: NavigationItem[] = this.filterNavigationItems(navigationItem.children,
              session);

            filteredNavigationItems.push(
              new NavigationItem(navigationItem.icon, navigationItem.name, navigationItem.url,
                navigationItem.authorities, filteredChildNavigationItems, navigationItem.cssClass,
                navigationItem.variant, navigationItem.badge, navigationItem.divider, navigationItem.title));
          }
        }
      } else {
        const filteredChildNavigationItems: NavigationItem[] = this.filterNavigationItems(navigationItem.children,
          session);

        filteredNavigationItems.push(
          new NavigationItem(navigationItem.icon, navigationItem.name, navigationItem.url, navigationItem.authorities,
            filteredChildNavigationItems, navigationItem.cssClass, navigationItem.variant, navigationItem.badge,
            navigationItem.divider, navigationItem.title));
      }
    }

    return filteredNavigationItems;
  }
}
