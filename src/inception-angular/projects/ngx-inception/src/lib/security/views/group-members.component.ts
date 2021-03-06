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

import {AfterViewInit, Component, HostBinding, OnDestroy, ViewChild} from '@angular/core';
import {MatDialogRef} from '@angular/material/dialog';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {finalize, first, tap} from 'rxjs/operators';
import {ActivatedRoute, Router} from '@angular/router';
import {merge, Subscription} from 'rxjs';
import {Error} from '../../core/errors/error';
import {AdminContainerView} from '../../layout/components/admin-container-view';
import {GroupMemberDatasource} from '../services/group-member.datasource';
import {TableFilterComponent} from '../../core/components/table-filter.component';
import {SecurityService} from '../services/security.service';
import {DialogService} from '../../dialog/services/dialog.service';
import {SpinnerService} from '../../layout/services/spinner.service';
import {BackNavigation} from '../../layout/components/back-navigation';
import {GroupMemberType} from '../services/group-member-type';
import {SortDirection} from '../../core/sorting/sort-direction';
import {SecurityServiceError} from '../services/security.service.errors';
import {AccessDeniedError} from '../../core/errors/access-denied-error';
import {SystemUnavailableError} from '../../core/errors/system-unavailable-error';
import {GroupMember} from '../services/group-member';
import {ConfirmationDialogComponent} from '../../dialog/components/confirmation-dialog.component';

/**
 * The GroupMembersComponent class implements the group members component.
 *
 * @author Marcus Portmann
 */
@Component({
  templateUrl: 'group-members.component.html',
  styleUrls: ['group-members.component.css']
})
export class GroupMembersComponent extends AdminContainerView implements AfterViewInit, OnDestroy {

  @HostBinding('class') hostClass = 'flex flex-column flex-fill';
  dataSource: GroupMemberDatasource;
  displayedColumns = ['memberName', 'memberType', 'actions'];
  @ViewChild(MatPaginator, {static: true}) paginator!: MatPaginator;
  @ViewChild(MatSort, {static: true}) sort!: MatSort;
  @ViewChild(TableFilterComponent, {static: true}) tableFilter!: TableFilterComponent;
  userDirectoryId: string;
  groupName: string;
  private subscriptions: Subscription = new Subscription();

  constructor(private router: Router, private activatedRoute: ActivatedRoute, private securityService: SecurityService,
              private dialogService: DialogService, private spinnerService: SpinnerService) {
    super();

    // Retrieve the route parameters
    const userDirectoryId = this.activatedRoute.snapshot.paramMap.get('userDirectoryId');

    if (!userDirectoryId) {
      throw(new Error('No userDirectoryId route parameter found'));
    }

    this.userDirectoryId = decodeURIComponent(userDirectoryId);

    const groupName = this.activatedRoute.snapshot.paramMap.get('groupName');

    if (!groupName) {
      throw(new Error('No groupName route parameter found'));
    }

    this.groupName = decodeURIComponent(groupName);

    this.dataSource = new GroupMemberDatasource(this.securityService);
  }

  get backNavigation(): BackNavigation {
    return new BackNavigation($localize`:@@security_group_members_back_navigation:Back`,
      ['../../..'], {
      relativeTo: this.activatedRoute,
      state: {userDirectoryId: this.userDirectoryId}
    });
  }

  get title(): string {
    return $localize`:@@security_group_members_title:Group Members`
  }

  addMemberToGroup(): void {
    // // noinspection JSIgnoredPromiseFromCall
    // this.router.navigate(['new'], {relativeTo: this.activatedRoute});
  }

  groupMemberTypeName(groupMemberType: GroupMemberType): string {
    if (groupMemberType === GroupMemberType.User) {
      return 'User';
    } else if (groupMemberType === GroupMemberType.Group) {
      return 'Group';
    } else {
      return 'Unknown';
    }
  }

  loadGroupMembers(): void {
    let filter = '';

    if (!!this.tableFilter.filter) {
      filter = this.tableFilter.filter;
      filter = filter.trim();
      filter = filter.toLowerCase();
    }

    const sortDirection = this.sort.direction === 'asc' ? SortDirection.Ascending : SortDirection.Descending;

    this.dataSource.load(this.userDirectoryId, this.groupName, filter, sortDirection, this.paginator.pageIndex,
      this.paginator.pageSize);
  }

  ngAfterViewInit(): void {
    this.subscriptions.add(this.dataSource.loading$.subscribe((next: boolean) => {
      if (next) {
        this.spinnerService.showSpinner();
      } else {
        this.spinnerService.hideSpinner();
      }
    }, (error: Error) => {
      // noinspection SuspiciousTypeOfGuard
      if ((error instanceof SecurityServiceError) || (error instanceof AccessDeniedError) ||
        (error instanceof SystemUnavailableError)) {
        // noinspection JSIgnoredPromiseFromCall
        this.router.navigateByUrl('/error/send-error-report', {state: {error}});
      } else {
        this.dialogService.showErrorDialog(error);
      }
    }));

    this.subscriptions.add(this.sort.sortChange.subscribe(() => {
      if (this.paginator) {
        this.paginator.pageIndex = 0;
      }
    }));

    this.subscriptions.add(this.tableFilter.changed.subscribe(() => {
      if (this.paginator) {
        this.paginator.pageIndex = 0;
      }
    }));

    this.subscriptions.add(merge(this.sort.sortChange, this.tableFilter.changed, this.paginator.page)
    .pipe(tap(() => {
      this.loadGroupMembers();
    })).subscribe());

    this.loadGroupMembers();
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  removeMemberFromGroup(groupMember: GroupMember): void {
    const dialogRef: MatDialogRef<ConfirmationDialogComponent, boolean> = this.dialogService.showConfirmationDialog({
      message: $localize`:@@security_group_members_confirm_remove_member_from_group:Are you sure you want to remove the group member from the group?`
    });

    dialogRef.afterClosed()
    .pipe(first())
    .subscribe((confirmation: boolean | undefined) => {
      if (confirmation === true) {
        this.spinnerService.showSpinner();

        this.securityService.removeMemberFromGroup(this.userDirectoryId, this.groupName, groupMember.memberType,
          groupMember.memberName)
        .pipe(first(), finalize(() => this.spinnerService.hideSpinner()))
        .subscribe(() => {
          this.loadGroupMembers();
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
}

