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

// Import Angular modules
import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';

// Import Angular classes
import {RouterModule, Routes} from "@angular/router";

const routes: Routes = [
  {
    path: '',
    redirectTo: 'menu31',
    pathMatch: 'full',
  },
  {
    path: 'menu31',
    loadChildren: './menu31/menu31.module#Menu31Module',
    data: {
      title: 'Menu 3.1',
    }
  },
  {
    path: 'menu32',
    loadChildren: './menu32/menu32.module#Menu32Module',
    data: {
      title: 'Menu 3.2',
    }
  }
];

/**
 * The Menu3Module class implements a module that contains two child modules that each contain two
 * components that can be loaded into the AppContainerComponent component when the corresponding
 * menu option is selected.
 *
 * @author Marcus Portmann
 */
@NgModule({
  imports: [
    CommonModule,

    RouterModule.forChild(routes)
  ],
  declarations: []
})
export class Menu3Module {
}