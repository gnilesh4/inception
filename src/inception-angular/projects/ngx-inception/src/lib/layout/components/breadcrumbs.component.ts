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

import {Component, ElementRef, Input, OnInit} from '@angular/core';
import {Replace} from '../../core/util/replace';
import {BreadcrumbsService} from '../services/breadcrumbs.service';
import {Observable} from 'rxjs';

/**
 * The BreadcrumbsComponent class implements the breadcrumbs component.
 *
 * @author Marcus Portmann
 */
@Component({
  // tslint:disable-next-line
  selector: 'breadcrumbs',
  template: `
    <ol class="breadcrumb">
      <ng-template ngFor let-breadcrumb [ngForOf]="breadcrumbs | async" let-last=last>
        <li class="breadcrumb-item"
            *ngIf="(breadcrumb.label)"
            [ngClass]="{active: last}">
          <a *ngIf="!last && (!!breadcrumb.url)"
             [routerLink]="breadcrumb.url">{{breadcrumb.label}}</a>
          <span *ngIf="last || (!breadcrumb.url)">{{breadcrumb.label}}</span>
        </li>
      </ng-template>
    </ol>
  `
})
export class BreadcrumbsComponent implements OnInit {

  breadcrumbs: Observable<Array<{}>>;

  @Input() fixed = false;

  /**
   * Constructs a new BreadcrumbsComponent.
   *
   * @param elementRef         The element reference.
   * @param breadcrumbsService The breadcrumbs service.
   */
  constructor(private elementRef: ElementRef, private breadcrumbsService: BreadcrumbsService) {
    this.breadcrumbs = this.breadcrumbsService.breadcrumbs$;
  }

  ngOnInit(): void {
    Replace(this.elementRef);

    if (this.fixed) {
      const bodySelector = document.querySelector('body');

      if (bodySelector) {
        bodySelector.classList.add('breadcrumbs-fixed');
      }
    }
  }
}
