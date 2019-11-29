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

import {Component, Input} from '@angular/core';
import {NavigationItem} from '../services/navigation-item';

/**
 * The SidebarNavDropdownComponent class implements the sidebar nav dropdown component.
 *
 * @author Marcus Portmann
 */
@Component({
  // tslint:disable-next-line
  selector: 'sidebar-nav-dropdown',
  template: `
    <ng-template *ngIf="navItem">
      <a class="nav-link nav-dropdown-toggle" sidebarNavDropdownToggler>
        <i *ngIf="this.navItem.icon && !!this.navItem.icon" class="nav-icon {{ navItem.icon }}"></i>
        {{ navItem.name }}
        <span *ngIf="this.navItem.badge && !!this.navItem.badge" [ngClass]="'badge badge-' + navItem.badge.variant">
          {{ navItem.badge.text }}
        </span>
      </a>
      <ul class="nav-dropdown-items">
        <sidebar-nav-item *ngFor="let child of navItem.children" [navItem]='child'></sidebar-nav-item>
      </ul>
    </ng-template>
  `,
  styles: ['.nav-dropdown-toggle { cursor: pointer; }']
})
export class SidebarNavDropdownComponent {

  @Input() navItem?: NavigationItem;

  /**
   * Constructs a new SidebarNavDropdownComponent.
   */
  constructor() {
  }
}