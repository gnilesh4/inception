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
import {CodesService} from '../../services/codes/codes.service';
import {Error} from '../../errors/error';
import {CodeCategory} from '../../services/codes/code-category';
import {finalize, first} from 'rxjs/operators';
import {CodesServiceError} from '../../services/codes/codes.service.errors';
import {SystemUnavailableError} from '../../errors/system-unavailable-error';
import {AccessDeniedError} from '../../errors/access-denied-error';
import {AdminContainerView} from '../../components/layout/admin-container-view';

/**
 * The EditCodeCategoryComponent class implements the edit code category component.
 *
 * @author Marcus Portmann
 */
@Component({
  templateUrl: 'edit-code-category.component.html',
  styleUrls: ['edit-code-category.component.css'],
})
export class EditCodeCategoryComponent extends AdminContainerView implements AfterViewInit {

  codeCategoryId: string;

  dataFormControl: FormControl;

  editCodeCategoryForm: FormGroup;

  idFormControl: FormControl;

  nameFormControl: FormControl;

  constructor(private router: Router, private activatedRoute: ActivatedRoute,
              private formBuilder: FormBuilder, private i18n: I18n,
              private codesService: CodesService, private dialogService: DialogService,
              private spinnerService: SpinnerService) {
    super();

    // Retrieve parameters
    this.codeCategoryId = this.activatedRoute.snapshot.paramMap.get('codeCategoryId')!;

    // Initialise form controls
    this.dataFormControl = new FormControl('');

    this.idFormControl = new FormControl({value: '', disabled: true}, [Validators.required, Validators.maxLength(100)]);

    this.nameFormControl = new FormControl('', [Validators.required, Validators.maxLength(100)]);

    // Initialise form
    this.editCodeCategoryForm = new FormGroup({
      data: this.dataFormControl,
      id: this.idFormControl,
      name: this.nameFormControl
    });
  }

  get title(): string {
    return this.i18n({
      id: '@@edit_code_category_component_title',
      value: 'Edit Code Category'
    })
  }

  ngAfterViewInit(): void {
    this.spinnerService.showSpinner();

    this.codesService.getCodeCategory(this.codeCategoryId)
      .pipe(first(), finalize(() => this.spinnerService.hideSpinner()))
      .subscribe((codeCategory: CodeCategory) => {
        this.idFormControl.setValue(codeCategory.id);
        this.nameFormControl.setValue(codeCategory.name);
        this.dataFormControl.setValue(codeCategory.data);
      }, (error: Error) => {
        // noinspection SuspiciousTypeOfGuard
        if ((error instanceof CodesServiceError) || (error instanceof AccessDeniedError) ||
          (error instanceof SystemUnavailableError)) {
          // noinspection JSIgnoredPromiseFromCall
          this.router.navigateByUrl('/error/send-error-report', {state: {error}});
        } else {
          this.dialogService.showErrorDialog(error);
        }
      });
  }

  onCancel(): void {
    // noinspection JSIgnoredPromiseFromCall
    this.router.navigate(['../..'], {relativeTo: this.activatedRoute});
  }

  onOK(): void {
    if (this.editCodeCategoryForm.valid) {
      const data = this.dataFormControl.value;

      const codeCategory: CodeCategory = new CodeCategory(this.codeCategoryId,
        this.nameFormControl.value, (!data || 0 === data.length) ? null : data);

      this.spinnerService.showSpinner();

      this.codesService.updateCodeCategory(codeCategory)
        .pipe(first(), finalize(() => this.spinnerService.hideSpinner()))
        .subscribe(() => {
          // noinspection JSIgnoredPromiseFromCall
          this.router.navigate(['../..'], {relativeTo: this.activatedRoute});
        }, (error: Error) => {
          // noinspection SuspiciousTypeOfGuard
          if ((error instanceof CodesServiceError) || (error instanceof AccessDeniedError) ||
            (error instanceof SystemUnavailableError)) {
            // noinspection JSIgnoredPromiseFromCall
            this.router.navigateByUrl('/error/send-error-report', {state: {error}});
          } else {
            this.dialogService.showErrorDialog(error);
          }
        });
    }
  }
}
