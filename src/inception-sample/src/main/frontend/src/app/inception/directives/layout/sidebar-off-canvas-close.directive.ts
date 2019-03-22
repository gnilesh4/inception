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

import {Directive, HostListener} from "@angular/core";

/**
 * The SidebarOffCanvasCloseDirective class implements the sidebar off canvas close directive.
 *
 * @author Marcus Portmann
 */
@Directive({
  selector: '[sidebarOffCanvasClose]'
})
export class SidebarOffCanvasCloseDirective {

  constructor() { }

  @HostListener('click', ['$event'])
  toggleOpen($event: any): void {
    $event.preventDefault();

    if (this.hasClass(document.querySelector('body'), 'sidebar-off-canvas')) {
      this.toggleClass(document.querySelector('body'), 'sidebar-opened');
    }
  }

  /**
   * Check whether the element has the class with the specified name.
   *
   * @param {any} target       The target element.
   * @param {string} className The class name to check for.
   *
   * @return True if the element has a class with the specified name or false otherwise.
   */
  private hasClass(target: any, className: string): boolean {
    return new RegExp('(\\s|^)' + className + '(\\s|$)').test(target.className);
  }

  /**
   * Toggle the class on the element.
   *
   * @param {any} element      The element.
   * @param {string} className The class name.
   */
  private toggleClass(element: any, className: string): void {
    let newClass = ' ' + element.className.replace( /[\t\r\n]/g, ' ' ) + ' ';
    if (this.hasClass(element, className)) {
      while (newClass.indexOf(' ' + className + ' ') >= 0 ) {
        newClass = newClass.replace( ' ' + className + ' ' , ' ' );
      }
      element.className = newClass.replace(/^\s+|\s+$/g, '');
    } else {
      element.className += ' ' + className;
    }
  }
}
