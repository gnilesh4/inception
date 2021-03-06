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

import {AfterViewInit, Component, OnDestroy, ViewChild} from '@angular/core';
import {FormControl} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {debounceTime, finalize, first, map, startWith} from 'rxjs/operators';
import {ReplaySubject, Subject, Subscription} from 'rxjs';
import {MatTableDataSource} from '@angular/material/table';
import {MatDialogRef} from '@angular/material/dialog';
import {MatPaginator} from '@angular/material/paginator';
import {AdminContainerView} from '../../layout/components/admin-container-view';
import {UserDirectorySummary} from '../services/user-directory-summary';
import {SecurityService} from '../services/security.service';
import {DialogService} from '../../dialog/services/dialog.service';
import {SpinnerService} from '../../layout/services/spinner.service';
import {BackNavigation} from '../../layout/components/back-navigation';
import {SecurityServiceError} from '../services/security.service.errors';
import {AccessDeniedError} from '../../core/errors/access-denied-error';
import {SystemUnavailableError} from '../../core/errors/system-unavailable-error';
import {Error} from '../../core/errors/error';
import {UserDirectorySummaries} from '../services/user-directory-summaries';
import {ConfirmationDialogComponent} from '../../dialog/components/confirmation-dialog.component';

/**
 * The TenantUserDirectoriesComponent class implements the tenant user directories
 * component.
 *
 * @author Marcus Portmann
 */
@Component({
  templateUrl: 'tenant-user-directories.component.html',
  styleUrls: ['tenant-user-directories.component.css']
})
export class TenantUserDirectoriesComponent extends AdminContainerView implements AfterViewInit, OnDestroy {

  allUserDirectories: UserDirectorySummary[] = [];
  availableUserDirectories$: Subject<UserDirectorySummary[]> = new ReplaySubject<UserDirectorySummary[]>();
  dataSource = new MatTableDataSource<UserDirectorySummary>([]);
  displayedColumns = ['existingUserDirectoryName', 'actions'];
  filteredUserDirectories$: Subject<UserDirectorySummary[]> = new ReplaySubject<UserDirectorySummary[]>();
  newUserDirectoryFormControl: FormControl;
  tenantId: string;
  @ViewChild(MatPaginator, {static: true}) paginator!: MatPaginator;
  private subscriptions: Subscription = new Subscription();

  constructor(private router: Router, private activatedRoute: ActivatedRoute, private securityService: SecurityService,
              private dialogService: DialogService, private spinnerService: SpinnerService) {
    super();

    // Retrieve the route parameters
    const tenantId = this.activatedRoute.snapshot.paramMap.get('tenantId');

    if (!tenantId) {
      throw(new Error('No tenantId route parameter found'));
    }

    this.tenantId = decodeURIComponent(tenantId);

    // Initialise the form controls
    this.newUserDirectoryFormControl = new FormControl('');
  }

  get backNavigation(): BackNavigation {
    return new BackNavigation($localize`:@@security_tenant_user_directories_back_navigation:Back`,
      ['../..'], {
        relativeTo: this.activatedRoute
      });
  }

  get title(): string {
    return $localize`:@@security_tenant_user_directories_title:Tenant User Directories`
  }

  private static calculateAvailableUserDirectories(allUserDirectories: UserDirectorySummary[],
                                                   // tslint:disable-next-line:max-line-length
                                                   existingTenantUserDirectories: UserDirectorySummary[]): UserDirectorySummary[] {

    const availableUserDirectories: UserDirectorySummary[] = [];

    for (const possibleUserDirectory of allUserDirectories) {
      let foundExistingUserDirectory = false;

      for (const existingTenantUserDirectory of existingTenantUserDirectories) {
        if (possibleUserDirectory.id === existingTenantUserDirectory.id) {
          foundExistingUserDirectory = true;
          break;
        }
      }

      if (!foundExistingUserDirectory) {
        availableUserDirectories.push(possibleUserDirectory);
      }
    }

    return availableUserDirectories;
  }

  addUserDirectoryToTenant(): void {
    if (this.isUserDirectorySelected()) {
      this.spinnerService.showSpinner();

      this.securityService.addUserDirectoryToTenant(this.tenantId,
        this.newUserDirectoryFormControl.value.id)
      .pipe(first(), finalize(() => this.spinnerService.hideSpinner()))
      .subscribe(() => {
        this.loadUserDirectoriesForTenant();
        this.newUserDirectoryFormControl.setValue('');
      }, (error: Error) => {
        // noinspection SuspiciousTypeOfGuard
        if ((error instanceof SecurityServiceError) || (error instanceof AccessDeniedError) ||
          (error instanceof SystemUnavailableError)) {
          // noinspection JSIgnoredPromiseFromCall
          this.router.navigateByUrl('/error/send-error-report', {state: {error}});
        } else {
          this.dialogService.showErrorDialog(error);
        }
      });
    }
  }

  applyFilter(filterValue: string): void {
    filterValue = filterValue.trim();
    filterValue = filterValue.toLowerCase();
    this.dataSource.filter = filterValue;
  }

  displayUserDirectory(userDirectorySummary: UserDirectorySummary): string {
    return userDirectorySummary.name;
  }

  isUserDirectorySelected(): boolean {
    if (this.newUserDirectoryFormControl.value as UserDirectorySummary) {
      if (typeof (this.newUserDirectoryFormControl.value.id) !== 'undefined' &&
        this.newUserDirectoryFormControl.value.id !== null) {
        return true;
      }
    }

    return false;
  }

  loadUserDirectoriesForTenant(): void {
    this.spinnerService.showSpinner();

    this.securityService.getUserDirectorySummariesForTenant(this.tenantId)
    .pipe(first(), finalize(() => this.spinnerService.hideSpinner()))
    .subscribe((userDirectorySummaries: UserDirectorySummary[]) => {
      this.dataSource.data = userDirectorySummaries;

      const availableUserDirectories = TenantUserDirectoriesComponent.calculateAvailableUserDirectories(
        this.allUserDirectories, this.dataSource.data);

      this.subscriptions.add(this.newUserDirectoryFormControl.valueChanges.pipe(startWith(''),
        debounceTime(500), map((value) => {
        this.filteredUserDirectories$.next(this.filterUserDirectories(availableUserDirectories, value));
      })).subscribe());

      this.availableUserDirectories$.next(availableUserDirectories);
    }, (error: Error) => {
      // noinspection SuspiciousTypeOfGuard
      if ((error instanceof SecurityServiceError) || (error instanceof AccessDeniedError) ||
        (error instanceof SystemUnavailableError)) {
        // noinspection JSIgnoredPromiseFromCall
        this.router.navigateByUrl('/error/send-error-report', {state: {error}});
      } else {
        this.dialogService.showErrorDialog(error);
      }
    });
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;

    // Retrieve the existing user and initialise the form fields
    this.spinnerService.showSpinner();

    this.securityService.getUserDirectorySummaries()
    .pipe(first(), finalize(() => this.spinnerService.hideSpinner()))
    .subscribe((userDirectorySummaries: UserDirectorySummaries) => {
      this.allUserDirectories = userDirectorySummaries.userDirectorySummaries;

      this.loadUserDirectoriesForTenant();
    }, (error: Error) => {
      // noinspection SuspiciousTypeOfGuard
      if ((error instanceof SecurityServiceError) || (error instanceof AccessDeniedError) ||
        (error instanceof SystemUnavailableError)) {
        // noinspection JSIgnoredPromiseFromCall
        this.router.navigateByUrl('/error/send-error-report', {state: {error}});
      } else {
        this.dialogService.showErrorDialog(error);
      }
    });
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  removeUserDirectoryFromTenant(userDirectoryId: string) {
    const dialogRef: MatDialogRef<ConfirmationDialogComponent, boolean> = this.dialogService.showConfirmationDialog({
      message: $localize`:@@security_tenant_user_directories_confirm_remove_user_directory_from_tenant:Are you sure you want to remove the user directory from the tenant?`
    });

    dialogRef.afterClosed()
    .pipe(first())
    .subscribe((confirmation: boolean | undefined) => {
      if (confirmation === true) {
        this.spinnerService.showSpinner();

        this.securityService.removeUserDirectoryFromTenant(this.tenantId, userDirectoryId)
        .pipe(first(), finalize(() => this.spinnerService.hideSpinner()))
        .subscribe(() => {
          this.loadUserDirectoriesForTenant();
          this.newUserDirectoryFormControl.setValue('');
        }, (error: Error) => {
          // noinspection SuspiciousTypeOfGuard
          if ((error instanceof SecurityServiceError) || (error instanceof AccessDeniedError) ||
            (error instanceof SystemUnavailableError)) {
            // noinspection JSIgnoredPromiseFromCall
            this.router.navigateByUrl('/error/send-error-report', {state: {error}});
          } else {
            this.dialogService.showErrorDialog(error);
          }
        });
      }
    });
  }

  private filterUserDirectories(userDirectories: UserDirectorySummary[],
                                value: string | UserDirectorySummary): UserDirectorySummary[] {
    let filterValue = '';

    if (typeof value === 'string') {
      filterValue = (value as string).toLowerCase();
    } else if (typeof value === 'object') {
      filterValue = (value as UserDirectorySummary).name.toLowerCase();
    }

    return userDirectories.filter(userDirecory => userDirecory.name.toLowerCase().indexOf(filterValue) === 0);
  }
}
