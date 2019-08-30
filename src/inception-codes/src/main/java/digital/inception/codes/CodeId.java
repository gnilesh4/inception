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

package digital.inception.codes;

//~--- JDK imports ------------------------------------------------------------

import java.io.Serializable;

/**
 * The <code>CodeId</code> class implements the ID class for the <code>Code</code> class.
 *
 * @author Marcus Portmann
 */
@SuppressWarnings("unused")
public class CodeId
  implements Serializable
{
  private static final long serialVersionUID = 1000000;

  /**
   * The ID used to uniquely identify the code category the code is associated with.
   */
  private String codeCategoryId;

  /**
   * The ID used to uniquely identify the code.
   */
  private String id;

  /**
   * Constructs a new <code>CodeId</code>.
   */
  public CodeId() {}

  /**
   * Constructs a new <code>CodeId</code>.
   *
   * @param codeCategoryId the ID used to uniquely identify the code category the code is
   *                       associated with
   * @param id             the ID used to uniquely identify the code
   */
  public CodeId(String codeCategoryId, String id)
  {
    this.codeCategoryId = codeCategoryId;
    this.id = id;
  }
}
