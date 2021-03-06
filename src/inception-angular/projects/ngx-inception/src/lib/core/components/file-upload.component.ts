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

import {
  Component,
  DoCheck,
  ElementRef,
  HostBinding,
  HostListener,
  Input,
  OnDestroy,
  OnInit,
  Optional,
  Renderer2,
  Self
} from '@angular/core';
import {ControlValueAccessor, FormGroupDirective, NgControl, NgForm} from '@angular/forms';
import {ErrorStateMatcher} from '@angular/material/core';
import {MatFormFieldControl} from '@angular/material/form-field';
import {FocusMonitor} from '@angular/cdk/a11y';
import {coerceBooleanProperty} from '@angular/cdk/coercion';


import {FileUploadMixinBase} from './file-upload-mixin';

/* tslint:disable:variable-name */
@Component({
  // tslint:disable-next-line:component-selector
  selector: 'file-upload',
  template: `
    <input #input type="file" [attr.multiple]="multiple? '' : null" [attr.accept]="accept">
    <span class="filename">{{ fileNames }}</span>
  `,
  styles: [`
    :host {
      display: inline-block;
    }

    :host:not(.file-input-disabled) {
      cursor: pointer;
    }

    input {
      width: 0;
      height: 0;
      opacity: 0;
      overflow: hidden;
      position: absolute;
      z-index: -1;
    }

    .filename {
      display: inline-block;
    }
  `
  ],
  providers: [{
    provide: MatFormFieldControl,
    useExisting: FileUploadComponent
  }
  ]
})
export class FileUploadComponent extends FileUploadMixinBase implements MatFormFieldControl<File[]>,
  ControlValueAccessor, OnInit, OnDestroy, DoCheck {
  static nextId = 0;

  focused = false;
  controlType = 'file-input';

  @Input() autofilled = false;
  @Input() placeholder = '';
  @Input() multiple = false;
  @Input() accept?: string;
  @Input() errorStateMatcher: ErrorStateMatcher;
  @HostBinding() id = `ngx-mat-file-input-${FileUploadComponent.nextId++}`;
  @HostBinding('attr.aria-describedby') describedBy = '';

  /**
   * @see https://angular.io/api/forms/ControlValueAccessor
   */
  constructor(private fm: FocusMonitor, private _elementRef: ElementRef, private _renderer: Renderer2,
              public _defaultErrorStateMatcher: ErrorStateMatcher, @Optional() @Self() public ngControl: NgControl,
              @Optional() public _parentForm: NgForm, @Optional() public _parentFormGroup: FormGroupDirective) {
    super(_defaultErrorStateMatcher, _parentForm, _parentFormGroup, ngControl);

    this.errorStateMatcher = _defaultErrorStateMatcher;

    if (this.ngControl != null) {
      this.ngControl.valueAccessor = this;
    }
    fm.monitor(_elementRef.nativeElement, true).subscribe(origin => {
      this.focused = !!origin;
      this.stateChanges.next();
    });
  }

  private _required = false;

  @Input() get required() {
    return this._required;
  }

  set required(req: boolean) {
    this._required = coerceBooleanProperty(req);
    this.stateChanges.next();
  }

  @Input() get value(): File[] | null {
    return this.empty ? null : this._elementRef.nativeElement.value || [];
  }

  set value(files: File[] | null) {
    if (files) {
      this.writeValue(files);
      this.stateChanges.next();
    }
  }

  /**
   * Whether the current input has files
   */
  get empty() {
    return !this._elementRef.nativeElement.value || this._elementRef.nativeElement.value.length === 0;
  }

  @HostBinding('class.mat-form-field-should-float') get shouldLabelFloat() {
    return this.focused || !this.empty || this.placeholder !== undefined;
  }

  @HostBinding('class.file-input-disabled') get isDisabled() {
    return this.disabled;
  }

  @Input() get disabled(): boolean {
    return this._elementRef.nativeElement.disabled;
  }

  set disabled(dis: boolean) {
    this.setDisabledState(coerceBooleanProperty(dis));
    this.stateChanges.next();
  }

  get fileNames() {
    if (this.value) {
      return this.value.map((f: File) => f.name).join(',');
    } else {
      return this.placeholder;
    }
  }

  setDescribedByIds(ids: string[]) {
    this.describedBy = ids.join(' ');
  }

  onContainerClick(event: MouseEvent) {
    if ((event.target as Element).tagName.toLowerCase() !== 'input' && !this.disabled) {
      this._elementRef.nativeElement.querySelector('input').focus();
      this.focused = true;
      this.open();
    }
  }

  writeValue(files: File[] | null): void {
    this._renderer.setProperty(this._elementRef.nativeElement, 'value', files);
  }

  registerOnChange(fn: (_: any) => void): void {
    this._onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this._onTouched = fn;
  }

  /**
   * Remove all files from the file input component
   * @param [event] optional event that may have triggered the clear action
   */
  clear(event?: Event) {
    if (event) {
      event.preventDefault();
      event.stopPropagation();
    }
    this.value = null;
    this._elementRef.nativeElement.querySelector('input').value = null;
    this._onChange(this.value);
  }

  @HostListener('change', ['$event']) change(event: Event) {
    const fileList: FileList | null = (event.target as HTMLInputElement).files;
    const fileArray: File[] = [];
    if (fileList) {
      // tslint:disable-next-line:prefer-for-of
      for (let i = 0; i < fileList.length; i++) {
        fileArray.push(fileList[i]);
      }
    }
    this.value = fileArray;
    this._onChange(this.value);
  }

  @HostListener('focusout') blur() {
    this.focused = false;
    this._onTouched();
  }

  setDisabledState(isDisabled: boolean): void {
    this._renderer.setProperty(this._elementRef.nativeElement, 'disabled', isDisabled);
  }

  ngOnInit() {
    this.multiple = coerceBooleanProperty(this.multiple);
  }

  open() {
    if (!this.disabled) {
      this._elementRef.nativeElement.querySelector('input').click();
    }
  }

  ngOnDestroy() {
    this.stateChanges.complete();
    this.fm.stopMonitoring(this._elementRef.nativeElement);
  }

  ngDoCheck(): void {
    if (this.ngControl) {
      // We need to re-evaluate this on every change detection cycle, because there are some
      // error triggers that we can't subscribe to (e.g. parent form submissions). This means
      // that whatever logic is in here has to be super lean or we risk destroying the performance.
      this.updateErrorState();
    }
  }

  private _onChange = (_: any) => {
  };

  private _onTouched = () => {
  };
}
