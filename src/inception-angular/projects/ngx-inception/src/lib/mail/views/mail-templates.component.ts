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

import {AfterViewInit, Component, HostBinding, ViewChild} from '@angular/core';
import {MatDialogRef} from '@angular/material/dialog';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {MatTableDataSource} from '@angular/material/table';
import {finalize, first} from 'rxjs/operators';
import {ActivatedRoute, Router} from '@angular/router';
import {MailTemplateSummary} from '../services/mail-template-summary';
import {AdminContainerView} from '../../layout/components/admin-container-view';
import {MailService} from '../services/mail.service';
import {DialogService} from '../../dialog/services/dialog.service';
import {SpinnerService} from '../../layout/services/spinner.service';
import {ConfirmationDialogComponent} from '../../dialog/components/confirmation-dialog.component';
import {MailServiceError} from '../services/mail.service.errors';
import {AccessDeniedError} from '../../core/errors/access-denied-error';
import {SystemUnavailableError} from '../../core/errors/system-unavailable-error';
import {Error} from '../../core/errors/error';
import {MailTemplateContentType} from '../services/mail-template-content-type';

/**
 * The MailTemplatesComponent class implements the mail templates component.
 *
 * @author Marcus Portmann
 */
@Component({
  templateUrl: 'mail-templates.component.html',
  styleUrls: ['mail-templates.component.css']
})
export class MailTemplatesComponent extends AdminContainerView implements AfterViewInit {

  @HostBinding('class') hostClass = 'flex flex-column flex-fill';

  MailTemplateContentType = MailTemplateContentType;

  dataSource: MatTableDataSource<MailTemplateSummary> = new MatTableDataSource<MailTemplateSummary>();

  displayedColumns = ['name', 'contentType', 'actions'];

  @ViewChild(MatPaginator, {static: true}) paginator!: MatPaginator;

  @ViewChild(MatSort, {static: true}) sort!: MatSort;

  constructor(private router: Router, private activatedRoute: ActivatedRoute, private mailService: MailService,
              private dialogService: DialogService, private spinnerService: SpinnerService) {
    super();

    // Set the data source filter
    this.dataSource.filterPredicate = (data, filter): boolean => data.name.toLowerCase().includes(filter);
  }

  get title(): string {
    return 'Mail Templates';
  }

  applyFilter(filterValue: string): void {
    filterValue = filterValue.trim();
    filterValue = filterValue.toLowerCase();
    this.dataSource.filter = filterValue;
  }

  deleteMailTemplate(mailTemplateId: string): void {
    const dialogRef: MatDialogRef<ConfirmationDialogComponent, boolean> = this.dialogService.showConfirmationDialog({
      message: 'Are you sure you want to delete the mail template?'
    });

    dialogRef.afterClosed()
      .pipe(first())
      .subscribe((confirmation: boolean | undefined) => {
        if (confirmation === true) {
          this.spinnerService.showSpinner();

          this.mailService.deleteMailTemplate(mailTemplateId)
            .pipe(first(), finalize(() => this.spinnerService.hideSpinner()))
            .subscribe(() => {
              this.loadMailTemplates();
            }, (error: Error) => {
              // noinspection SuspiciousTypeOfGuard
              if ((error instanceof MailServiceError) || (error instanceof AccessDeniedError) ||
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

  editMailTemplate(mailTemplateId: string): void {
    // noinspection JSIgnoredPromiseFromCall
    this.router.navigate([encodeURIComponent(mailTemplateId) + '/edit'], {relativeTo: this.activatedRoute});
  }

  loadMailTemplates(): void {
    this.spinnerService.showSpinner();

    this.mailService.getMailTemplateSummaries()
      .pipe(first(), finalize(() => this.spinnerService.hideSpinner()))
      .subscribe((mailTemplateSummaries: MailTemplateSummary[]) => {
        this.dataSource.data = mailTemplateSummaries;
      }, (error: Error) => {
        // noinspection SuspiciousTypeOfGuard
        if ((error instanceof MailService) || (error instanceof AccessDeniedError) ||
          (error instanceof SystemUnavailableError)) {
          // noinspection JSIgnoredPromiseFromCall
          this.router.navigateByUrl('/error/send-error-report', {state: {error}});
        } else {
          this.dialogService.showErrorDialog(error);
        }
      });
  }

  newMailTemplate(): void {
    // noinspection JSIgnoredPromiseFromCall
    this.router.navigate(['new'], {relativeTo: this.activatedRoute});
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;

    this.loadMailTemplates();
  }
}
