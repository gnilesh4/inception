/*
 * Copyright 2018 Marcus Portmann
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

import {ApiError} from "./api-error";

/**
 * The Error class provides the base class that all error classes should be derived from.
 *
 * @author Marcus Portmann
 */
export class Error {

  /**
   * The optional cause of the error.
   */
  cause?: any;

  /**
   * The error message.
   */
  message: string;

  /**
   * The date and time the error occurred.
   */
  timestamp: Date;

  /**
   * Constructs a new Error.
   *
   * @param message The error message.
   * @param cause   The optional cause of the error.
   */
  constructor(message: string, cause?: any) {
    if ((cause) && (cause instanceof ApiError)) {
      this.timestamp = (<ApiError>cause).timestamp;
    }
    else {
      this.timestamp = new Date();
    }

    this.message = message;
    this.cause = cause;
  }
}


