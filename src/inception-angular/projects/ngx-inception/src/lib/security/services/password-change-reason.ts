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

/**
 * The PasswordChangeReason enumeration defines the possible reasons for why a user's password was
 * changed.
 *
 * @author Marcus Portmann
 */
export enum PasswordChangeReason {

  /**
   * User.
   */
  User = 0,

  /**
   * Administrative.
   */
  Administrative = 1,

  /**
   * Reset.
   */
  Reset = 2
}
