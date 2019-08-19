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

import {AfterViewInit, Component} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {DialogService} from '../../services/dialog/dialog.service';
import {SpinnerService} from '../../services/layout/spinner.service';
import {I18n} from '@ngx-translate/i18n-polyfill';
import {Error} from '../../errors/error';
import {finalize, first} from 'rxjs/operators';
import {SystemUnavailableError} from '../../errors/system-unavailable-error';
import {AccessDeniedError} from '../../errors/access-denied-error';
import {ConfigurationService} from '../../services/configuration/configuration.service';
import {Configuration} from '../../services/configuration/configuration';
import {ConfigurationServiceError} from '../../services/configuration/configuration.service.errors';
import {AdminContainerView} from '../../components/layout/admin-container-view';
import {BackNavigation} from '../../components/layout/back-navigation';
import {User} from '../../services/security/user';
import {v4 as uuid} from 'uuid/interfaces';
import {UserStatus} from '../../services/security/user-status';
import {SecurityServiceError} from '../../services/security/security.service.errors';
import {Code} from '../../services/codes/code';
import {CodesServiceError} from '../../services/codes/codes.service.errors';
import {SecurityService} from '../../services/security/security.service';

/**
 * The EditUserComponent class implements the edit user component.
 *
 * @author Marcus Portmann
 */
@Component({
  templateUrl: 'edit-user.component.html',
  styleUrls: ['edit-user.component.css'],
})
export class EditUserComponent extends AdminContainerView implements AfterViewInit {

  editUserForm: FormGroup;

  user?: User;

  constructor(private router: Router, private activatedRoute: ActivatedRoute,
              private formBuilder: FormBuilder, private i18n: I18n,
              private securityService: SecurityService,
              private dialogService: DialogService, private spinnerService: SpinnerService) {
    super();

    // Initialise form
    this.editUserForm = new FormGroup({
      email: new FormControl('', [Validators.maxLength(4000)]),
      firstName: new FormControl('', [Validators.maxLength(4000)]),
      lastName: new FormControl('', [Validators.maxLength(4000)]),
      mobileNumber: new FormControl('', [Validators.maxLength(4000)]),
      phoneNumber: new FormControl('', [Validators.maxLength(4000)]),
      username: new FormControl('', [Validators.required, Validators.maxLength(4000)])
    });
  }

  get backNavigation(): BackNavigation {
    const userDirectoryId = decodeURIComponent(
      this.activatedRoute.snapshot.paramMap.get('userDirectoryId')!);

    return new BackNavigation(this.i18n({
        id: '@@edit_user_component_back_title',
        value: 'Users'
      }), ['../../..'],
      {relativeTo: this.activatedRoute, state: {userDirectoryId}});
  }

  get title(): string {
    return this.i18n({
      id: '@@edit_user_component_title',
      value: 'Edit User'
    })
  }

  ngAfterViewInit(): void {
    const userDirectoryId = decodeURIComponent(
      this.activatedRoute.snapshot.paramMap.get('userDirectoryId')!);
    const username = decodeURIComponent(this.activatedRoute.snapshot.paramMap.get('username')!);

    // Retrieve the existing user and initialise the form fields
    this.spinnerService.showSpinner();

    this.securityService.getUser(userDirectoryId, username)
      .pipe(first(), finalize(() => this.spinnerService.hideSpinner()))
      .subscribe((user: User) => {
        this.editUserForm.get('email')!.setValue(user.email);
        this.editUserForm.get('firstName')!.setValue(user.firstName);
        this.editUserForm.get('lastName')!.setValue(user.lastName);
        this.editUserForm.get('mobileNumber')!.setValue(user.mobileNumber);
        this.editUserForm.get('phoneNumber')!.setValue(user.phoneNumber);
        this.editUserForm.get('username')!.setValue(user.username);
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

  onCancel(): void {
    const userDirectoryId = decodeURIComponent(
      this.activatedRoute.snapshot.paramMap.get('userDirectoryId')!);

    // noinspection JSIgnoredPromiseFromCall
    this.router.navigate(['../../..'],
      {relativeTo: this.activatedRoute, state: {userDirectoryId}});
  }

  onOK(): void {
    // if (this.user && this.editUserForm.valid) {
    //   this.user.firstName = this.firstNameFormControl.value;
    //   this.user.lastName = this.lastNameFormControl.value;
    //   this.user.mobileNumber = this.mobileNumberFormControl.value;
    //   this.user.phoneNumber = this.phoneNumberFormControl.value;
    //   this.user.email = this.emailFormControl.value;
    //
    //   this.spinnerService.showSpinner();
    //
    //   this.securityService.updateUser(this.user, false, false)
    //     .pipe(first(), finalize(() => this.spinnerService.hideSpinner()))
    //     .subscribe(() => {
    //       // noinspection JSIgnoredPromiseFromCall
    //       this.router.navigate(['../..'],
    //         {relativeTo: this.activatedRoute, state: {this.userDirectoryId}});
    //     }, (error: Error) => {
    //       // noinspection SuspiciousTypeOfGuard
    //       if ((error instanceof SecurityServiceError) || (error instanceof AccessDeniedError) ||
    //         (error instanceof SystemUnavailableError)) {
    //         // noinspection JSIgnoredPromiseFromCall
    //         this.router.navigateByUrl('/error/send-error-report', {state: {error}});
    //       } else {
    //         this.dialogService.showErrorDialog(error);
    //       }
    //     });
    // }
  }
}
