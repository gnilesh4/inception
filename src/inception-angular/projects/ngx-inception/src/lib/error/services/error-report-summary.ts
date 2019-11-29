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

/**
 * The ErrorReportSummary class holds the summary information for an error report.
 *
 * @author Marcus Portmann
 */
export class ErrorReportSummary {

  /**
   * The ID used to uniquely identify the application that generated the error report.
   */
  applicationId: string;

  /**
   * The version of the application that generated the error report.
   */
  applicationVersion: string;

  /**
   * The date and time the error report was created.
   */
  created: Date;

  /**
   * The description of the error.
   */
  description: string;

  /**
   * The optional Universally Unique Identifier (UUID) used to uniquely identify the device the
   * error report originated from.
   */
  deviceId?: string;

  /**
   * The Universally Unique Identifier (UUID) used to uniquely identify the error report.
   */
  id: string;

  /**
   * The optional username identifying the user associated with the error report.
   */
  who?: string;

  /**
   * Constructs a new ErrorReportSummary.
   *
   * @param id                 The Universally Unique Identifier (UUID) used to uniquely identify
   *                           the error report.
   * @param applicationId      The ID used to uniquely identify the application that generated the
   *                           error report.
   * @param applicationVersion The version of the application that generated the error report.
   * @param description        The description of the error.
   * @param created            The date and time the error report was created.
   * @param who                The optional username identifying the user associated with the error
   *                           report.
   * @param deviceId           The optional Universally Unique Identifier (UUID) used to uniquely
   *                           identify the device the error report originated from.
   */
  constructor(id: string, applicationId: string, applicationVersion: string, description: string, created: Date,
              who?: string, deviceId?: string) {
    this.id = id;
    this.applicationId = applicationId;
    this.applicationVersion = applicationVersion;
    this.description = description;
    this.created = created;
    this.who = who;
    this.deviceId = deviceId;
  }
}