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
import {MatDialogModule} from '@angular/material';
import {DialogService} from './services/dialog.service';
import {
  ConfirmationDialogComponent, ErrorDialogComponent, InformationDialogComponent, WarningDialogComponent
} from './components';

/**
 * The DialogModule class implements the Inception Dialog Module.
 *
 * @author Marcus Portmann
 */
@NgModule({
  declarations: [

    // Components
    ConfirmationDialogComponent, ErrorDialogComponent, InformationDialogComponent, WarningDialogComponent
  ],
  imports: [

    // Angular modules
    CommonModule,

    // Material modules
    MatDialogModule,

    // Inception modules
    CoreModule.forRoot()
  ],
  exports: [

    // Angular modules
    CommonModule,

    // Material modules
    MatDialogModule,

    // Inception modules
    CoreModule,

    // Components
    ConfirmationDialogComponent, ErrorDialogComponent, InformationDialogComponent, WarningDialogComponent
  ]
})
export class DialogModule {
  constructor() {
    console.log('Initialising the Inception Dialog Module');
  }

  static forRoot(): ModuleWithProviders {
    return {
      ngModule: DialogModule,
      providers: [

        // Services
        DialogService
      ]
    };
  }
}