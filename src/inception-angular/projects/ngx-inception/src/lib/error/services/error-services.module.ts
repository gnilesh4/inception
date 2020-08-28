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

import {ModuleWithProviders, NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {HttpClientModule} from '@angular/common/http';
import {ErrorService} from "./error.service";

/**
 * The ErrorServicesModule class implements the Inception Error Services Module.
 *
 * @author Marcus Portmann
 */
@NgModule({
  declarations: [],
  imports: [
    // Angular modules
    CommonModule, HttpClientModule
  ],
  exports: []
})
export class ErrorServicesModule {
  constructor() {
  }

  static forRoot(): ModuleWithProviders<ErrorServicesModule> {
    return {
      ngModule: ErrorServicesModule,
      providers: [
        ErrorService
      ]
    };
  }
}