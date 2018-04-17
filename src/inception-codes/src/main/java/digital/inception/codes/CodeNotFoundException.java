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

package digital.inception.codes;

//~--- non-JDK imports --------------------------------------------------------

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

//~--- JDK imports ------------------------------------------------------------

/**
 * The <code>CodeNotFoundException</code> exception is thrown to indicate an error condition
 * as a result of a code that could not be found.
 * <p/>
 * NOTE: This is a checked exception to prevent the automatic rollback of the current transaction.
 *
 * @author Marcus Portmann
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "The code could not be found")
public class CodeNotFoundException extends Exception
{
  private static final long serialVersionUID = 1000000;

  /**
   * Constructs a new <code>CodeNotFoundException</code>.
   *
   * @param codeCategoryId the Universally Unique Identifier (UUID) used to uniquely identify the
   *                       code category
   * @param codeId         the ID used to uniquely identify the code
   */
  public CodeNotFoundException(UUID codeCategoryId, String codeId)
  {
    super("The code with ID (" + codeId + ") for the code category wih ID (" + codeCategoryId
        + ") could not be found");
  }
}
