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

package digital.inception.oauth2.server.authorization.token;

// ~--- non-JDK imports --------------------------------------------------------

import digital.inception.core.service.ServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// ~--- JDK imports ------------------------------------------------------------

/**
 * The <code>InvalidOAuth2RefreshTokenException</code> exception is thrown to indicate an error
 * condition as a result of an invalid OAuth2 refresh token.
 *
 * <p>NOTE: This is a checked exception to prevent the automatic rollback of the current
 * transaction.
 *
 * @author Marcus Portmann
 */
@ResponseStatus(
    value = HttpStatus.INTERNAL_SERVER_ERROR,
    reason = "The OAuth2 refresh token is invalid")
public class InvalidOAuth2RefreshTokenException extends ServiceException {

  private static final long serialVersionUID = 1000000;

  /**
   * Constructs a new <code>InvalidOAuth2RefreshTokenException</code> with the specified message.
   */
  public InvalidOAuth2RefreshTokenException() {
    super("InvalidOAuth2RefreshTokenError", "The OAuth2 refresh token is invalid");
  }
}
