/*
 * Copyright 2020 Marcus Portmann
 *
 * Licensed under the Apache License, Version 2.0 (the 'License');
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {CommonModule} from '@angular/common';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {ReportDefinitionsComponent} from './report-definitions.component';
import {NewReportDefinitionComponent} from './new-report-definition.component';
import {EditReportDefinitionComponent} from './edit-report-definition.component';
import {EditReportDefinitionTitleResolver} from './edit-report-definition-title-resolver';
import {ReportDefinitionsTitleResolver} from './report-definitions-title-resolver';
import {NewReportDefinitionTitleResolver} from './new-report-definition-title-resolver';
import {ReportDefinitionTitleResolver} from './report-definition-title-resolver';
import {ConfigurationModule} from '../../configuration/configuration.module';
import {CoreModule} from '../../core/core.module';
import {DialogModule} from '../../dialog/dialog.module';
import {LayoutModule} from '../../layout/layout.module';
import {CanActivateFunctionGuard} from '../../security/routing/can-activate-function-guard';
import {ReportingTitleResolver} from './reporting-title-resolver';

const routes: Routes = [{
  path: 'report-definitions',
  resolve: {
    title: ReportDefinitionsTitleResolver
  },
  children: [{
    path: '',
    canActivate: [CanActivateFunctionGuard],
    component: ReportDefinitionsComponent,
    data: {
      authorities: ['ROLE_Administrator', 'FUNCTION_Reporting.ReportingAdministration',
        'FUNCTION_Reporting.ReportDefinitionAdministration'
      ]
    }
  }, {
    path: 'new',
    pathMatch: 'full',
    canActivate: [CanActivateFunctionGuard],
    component: NewReportDefinitionComponent,
    data: {
      authorities: ['ROLE_Administrator', 'FUNCTION_Reporting.ReportingAdministration',
        'FUNCTION_Reporting.ReportDefinitionAdministration'
      ]
    },
    resolve: {
      title: NewReportDefinitionTitleResolver
    }
  }, {
    path: ':reportDefinitionId',
    pathMatch: 'full',
    redirectTo: ':reportDefinitionId/edit'
  },

    {
      path: ':reportDefinitionId',
      resolve: {
        title: ReportDefinitionTitleResolver
      },
      children: [{
        path: 'edit',
        canActivate: [CanActivateFunctionGuard],
        component: EditReportDefinitionComponent,
        data: {
          authorities: ['ROLE_Administrator', 'FUNCTION_Reporting.ReportingAdministration',
            'FUNCTION_Reporting.ReportDefinitionAdministration'
          ]
        },
        resolve: {
          title: EditReportDefinitionTitleResolver
        }
      }
      ]
    }
  ]
}
];

@NgModule({
  declarations: [

    // Components
    EditReportDefinitionComponent, ReportDefinitionsComponent, NewReportDefinitionComponent
  ],
  imports: [

    // Angular modules
    CommonModule, FormsModule, ReactiveFormsModule, RouterModule.forChild(routes),

    // Inception modules
    ConfigurationModule.forRoot(), CoreModule.forRoot(), DialogModule.forRoot(), LayoutModule.forRoot()
  ],
  providers: [

    // Resolvers
    EditReportDefinitionTitleResolver, NewReportDefinitionTitleResolver, ReportDefinitionsTitleResolver,
    ReportDefinitionTitleResolver
  ]
})
export class ReportingViewsModule {
}



