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
import {AbstractControl, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from "@angular/router";
import {DialogService} from "../../services/dialog/dialog.service";
import {SpinnerService} from "../../services/layout/spinner.service";
import {I18n} from "@ngx-translate/i18n-polyfill";
import {CodesService} from "../../services/codes/codes.service";
import {Error} from "../../errors/error";
import {finalize, first} from "rxjs/operators";
import {CodesServiceError} from "../../services/codes/codes.service.errors";
import {SystemUnavailableError} from "../../errors/system-unavailable-error";
import {Code} from "../../services/codes/code";
import {AccessDeniedError} from "../../errors/access-denied-error";

/**
 * The NewCodeComponent class implements the new code component.
 *
 * @author Marcus Portmann
 */
@Component({
  templateUrl: 'new-code.component.html',
  styleUrls: ['new-code.component.css'],
})
export class NewCodeComponent implements OnInit {

  codeCategoryId: string;

  newCodeForm: FormGroup;

  constructor(private router: Router, private activatedRoute: ActivatedRoute,
              private formBuilder: FormBuilder, private i18n: I18n,
              private codesService: CodesService, private dialogService: DialogService,
              private spinnerService: SpinnerService) {
    this.newCodeForm = this.formBuilder.group({
      // tslint:disable-next-line
      id: ['', [Validators.required, Validators.maxLength(100)]],
      name: ['', [Validators.required, Validators.maxLength(100)]],
      value: ['', Validators.required],
    });
  }

  get idFormControl(): AbstractControl {
    return this.newCodeForm.get('id');
  }

  get nameFormControl(): AbstractControl {
    return this.newCodeForm.get('name');
  }

  get valueFormControl(): AbstractControl {
    return this.newCodeForm.get('value');
  }

  ngOnInit(): void {
    this.codeCategoryId = this.activatedRoute.snapshot.paramMap.get('codeCategoryId');
  }

  onCancel(): void {
    // noinspection JSIgnoredPromiseFromCall
    this.router.navigate(['..'], {relativeTo: this.activatedRoute});
  }

  onOK(): void {
    if (this.newCodeForm.valid) {
      let code: Code = new Code(this.idFormControl.value, this.codeCategoryId,
        this.nameFormControl.value, this.valueFormControl.value);

      this.spinnerService.showSpinner();

      this.codesService.createCode(code).pipe(first(),
        finalize(() => this.spinnerService.hideSpinner()))
        .subscribe(() => {
          // noinspection JSIgnoredPromiseFromCall
          this.router.navigate(['..'], {relativeTo: this.activatedRoute});
        }, (error: Error) => {
          if ((error instanceof CodesServiceError) || (error instanceof AccessDeniedError) || (error instanceof SystemUnavailableError)) {
            // noinspection JSIgnoredPromiseFromCall
            this.router.navigateByUrl('/error/send-error-report', {state: {error: error}});
          } else {
            this.dialogService.showErrorDialog(error);
          }
        });
    }
  }
}
