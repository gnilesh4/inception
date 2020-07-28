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
import {ActivatedRouteSnapshot, CanActivate, Router} from '@angular/router';
import {SecurityService} from '../services/security.service';

/**
 * The DisabledFunctionGuard class implements the routing guard that restricts access to a disabled
 * route.
 *
 * @author Marcus Portmann
 */
@Injectable()
export class DisabledFunctionGuard implements CanActivate {

  /**
   * Constructs a new DisabledFunctionGuard.
   *
   * @param router          The router.
   * @param securityService The security service.
   */
  constructor(private router: Router, private securityService: SecurityService) {
  }

  canActivate(activatedRouteSnapshot: ActivatedRouteSnapshot): boolean {
    return false;
  }
}
