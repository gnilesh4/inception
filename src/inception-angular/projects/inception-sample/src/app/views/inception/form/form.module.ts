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

import {CommonModule} from '@angular/common';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {ExampleFormComponent} from './example-form.component';
import {CoreModule} from 'ngx-inception';
import {ReferenceFormComponent} from "./reference-form.component";

const routes: Routes = [{
  path: '',
  redirectTo: 'example-form'
}, {
  path: 'example-form',
  component: ExampleFormComponent,
  data: {
    title: 'Example Form',
  }
}, {
  path: 'reference-form',
  component: ReferenceFormComponent,
  data: {
    title: 'Reference Form',
  }
}
];

@NgModule({
  imports: [
    // Angular modules
    CommonModule, FormsModule, ReactiveFormsModule, RouterModule.forChild(routes),

    // Inception modules
    CoreModule
  ],
  declarations: [ExampleFormComponent, ReferenceFormComponent],
  providers: []
})
export class FormModule {
}
