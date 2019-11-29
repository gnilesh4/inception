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

import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {SecurityService} from '../../services/security/security.service';
import {finalize, first, map} from 'rxjs/operators';
import {ActivatedRoute, Router} from '@angular/router';
import {Error} from '../../errors/error';
import {SpinnerService} from '../../services/layout/spinner.service';
import {DialogService} from '../../services/dialog/dialog.service';
import {I18n} from '@ngx-translate/i18n-polyfill';
import {SystemUnavailableError} from '../../errors/system-unavailable-error';
import {AccessDeniedError} from '../../errors/access-denied-error';
import {SecurityServiceError} from '../../services/security/security.service.errors';
import {MatDialogRef} from '@angular/material/dialog';
import {InformationDialogComponent} from '../../components/dialogs';

/**
 * The ExpiredPasswordComponent class implements the expired password component.
 *
 * @author Marcus Portmann
 */
@Component({
  templateUrl: 'expired-password.component.html'
})
export class ExpiredPasswordComponent implements OnInit {

  confirmNewPasswordFormControl: FormControl;

  expiredPasswordForm: FormGroup;

  newPasswordFormControl: FormControl;

  passwordFormControl: FormControl;

  usernameFormControl: FormControl;

  /**
   * Constructs a new ExpiredPasswordComponent.
   *
   * @param router          The router.
   * @param activatedRoute  The activated route.
   * @param i18n            The internationalization service.
   * @param dialogService   The dialog service.
   * @param securityService The security service.
   * @param spinnerService  The spinner service.
   */
  constructor(private router: Router, private activatedRoute: ActivatedRoute, private i18n: I18n, private dialogService: DialogService,
              private securityService: SecurityService, private spinnerService: SpinnerService) {

    // Initialise the form controls
    this.confirmNewPasswordFormControl = new FormControl('', [Validators.required, Validators.maxLength(100)]);
    this.newPasswordFormControl = new FormControl('', [Validators.required, Validators.maxLength(100)]);
    this.passwordFormControl = new FormControl('', [Validators.required, Validators.maxLength(100)]);
    this.usernameFormControl = new FormControl({
      value: '',
      disabled: true
    });

    // Initialise the form
    this.expiredPasswordForm = new FormGroup({
      confirmNewPassword: this.confirmNewPasswordFormControl,
      newPassword: this.newPasswordFormControl,
      password: this.passwordFormControl,
      username: this.usernameFormControl
    });
  }

  cancel(): void {
    // noinspection JSIgnoredPromiseFromCall
    this.router.navigate(['..'], {
      relativeTo: this.activatedRoute
    });
  }

  changePassword(): void {
    if (this.expiredPasswordForm.valid) {
      const username = this.usernameFormControl.value;
      const password = this.passwordFormControl.value;
      const newPassword = this.newPasswordFormControl.value;
      const confirmNewPassword = this.confirmNewPasswordFormControl.value;

      // Check that the password and confirmation password match
      if (newPassword !== confirmNewPassword) {
        this.dialogService.showErrorDialog(new Error(this.i18n({
          id: '@@login_expired_password_component_passwords_do_not_match',
          value: 'The passwords do not match.'
        })));

        return;
      }

      this.spinnerService.showSpinner();

      this.securityService.changePassword(username, password, newPassword)
        .pipe(first(), finalize(() => this.spinnerService.hideSpinner()))
        .subscribe(() => {

          const dialogRef: MatDialogRef<InformationDialogComponent, boolean> = this.dialogService.showInformationDialog({
            message: this.i18n({
              id: '@@login_expired_password_component_your_password_was_successfully_changed',
              value: 'Your password was successfully changed.'
            })
          });

          dialogRef.afterClosed()
            .pipe(first())
            .subscribe(() => {
              // noinspection JSIgnoredPromiseFromCall
              this.router.navigate(['..'], {
                relativeTo: this.activatedRoute,
                state: {username}
              });
            });
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

  ngOnInit(): void {
    this.activatedRoute.paramMap
      .pipe(first(), map(() => window.history.state))
      .subscribe((state) => {
        if (state.username) {
          this.usernameFormControl.setValue(state.username);
        } else {
          // noinspection JSIgnoredPromiseFromCall
          this.router.navigate(['..'], {
            relativeTo: this.activatedRoute
          });
        }
      });
  }
}