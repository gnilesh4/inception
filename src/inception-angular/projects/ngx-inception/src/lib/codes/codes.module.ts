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

import {ModuleWithProviders, NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {CoreModule} from '../core/core.module';
import {HttpClientModule} from '@angular/common/http';

/**
 * The CodesViewsModule class implements the Inception Codes Module.
 *
 * @author Marcus Portmann
 */
@NgModule({
  declarations: [],
  imports: [

    // Angular modules
    CommonModule, HttpClientModule,

    // Inception modules
    CoreModule.forRoot()
  ],
  exports: [

    // Angular modules
    CommonModule, HttpClientModule,

    // Inception modules
    CoreModule
  ]
})
export class CodesModule {
  constructor() {
    console.log('Initializing the Inception Codes Module');
  }

  static forRoot(): ModuleWithProviders {
    return {
      ngModule: CodesModule
    };
  }
}
