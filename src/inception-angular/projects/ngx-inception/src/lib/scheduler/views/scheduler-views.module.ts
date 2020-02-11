/*
 * Copyright 2019 Marcus Portmann
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
import {JobsComponent} from './jobs.component';
import {JobsTitleResolver} from './jobs-title-resolver';
import {JobTitleResolver} from './job-title-resolver';
import {EditJobTitleResolver} from './edit-job-title-resolver';
import {NewJobTitleResolver} from './new-job-title-resolver';
import {EditJobComponent} from './edit-job.component';
import {NewJobComponent} from './new-job.component';
import {JobParameterDialogComponent} from './job-parameter-dialog.component';
import {CanActivateFunctionGuard} from '../../security/routing/can-activate-function-guard';
import {ConfigurationModule} from '../../configuration/configuration.module';
import {CoreModule} from '../../core/core.module';
import {DialogModule} from '../../dialog/dialog.module';
import {LayoutModule} from '../../layout/layout.module';

const routes: Routes = [{
  path: 'jobs',
  resolve: {
    title: JobsTitleResolver
  },
  children: [{
    path: '',
    canActivate: [CanActivateFunctionGuard],
    component: JobsComponent,
    data: {
      authorities: ['ROLE_Administrator', 'FUNCTION_Scheduler.SchedulerAdministration',
        'FUNCTION_Scheduler.JobAdministration'
      ]
    }
  }, {
    path: 'new',
    pathMatch: 'full',
    canActivate: [CanActivateFunctionGuard],
    component: NewJobComponent,
    data: {
      authorities: ['ROLE_Administrator', 'FUNCTION_Scheduler.SchedulerAdministration',
        'FUNCTION_Scheduler.JobAdministration'
      ]
    },
    resolve: {
      title: NewJobTitleResolver
    }
  }, {
    path: ':jobId',
    pathMatch: 'full',
    redirectTo: ':jobId/edit'
  }, {
    path: ':jobId',
    resolve: {
      title: JobTitleResolver
    },
    children: [{
      path: 'edit',
      canActivate: [CanActivateFunctionGuard],
      component: EditJobComponent,
      data: {
        authorities: ['ROLE_Administrator', 'FUNCTION_Scheduler.SchedulerAdministration',
          'FUNCTION_Scheduler.JobAdministration'
        ]
      },
      resolve: {
        title: EditJobTitleResolver
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
    EditJobComponent, JobParameterDialogComponent, JobsComponent, NewJobComponent
  ],
  entryComponents: [

    // Components
    JobParameterDialogComponent
  ],
  imports: [

    // Angular modules
    CommonModule, FormsModule, ReactiveFormsModule, RouterModule.forChild(routes),

    // Inception modules
    ConfigurationModule.forRoot(), CoreModule.forRoot(), DialogModule.forRoot(), LayoutModule.forRoot()
  ],
  providers: [

    // Resolvers
    JobsTitleResolver, JobTitleResolver, EditJobTitleResolver, NewJobTitleResolver
  ]
})
export class SchedulerViewsModule {
}


