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

package digital.inception.reference;

// ~--- JDK imports ------------------------------------------------------------

import java.io.Serializable;
import java.util.Objects;

/**
 * The <code>ContactMechanismPurposeId</code> class implements the ID class for the <code>
 * ContactMechanismPurpose</code> class.
 *
 * @author Marcus Portmann
 */
@SuppressWarnings("unused")
public class ContactMechanismPurposeId implements Serializable {

  private static final long serialVersionUID = 1000000;

  /** The code for the contact mechanism type. */
  private String code;

  /** The Unicode locale identifier for the contact mechanism type. */
  private String localeId;

  /** The code for the contact mechanism type the contact mechanism purpose is associated with. */
  private String type;

  /** Constructs a new <code>ContactMechanismPurposeId</code>. */
  public ContactMechanismPurposeId() {}

  /**
   * Constructs a new <code>ContactMechanismPurposeId</code>.
   *
   * @param type the code for the contact mechanism type the contact mechanism purpose is
   *     associated with
   * @param code the code for the contact mechanism type
   * @param localeId the Unicode locale identifier for the contact mechanism type
   */
  public ContactMechanismPurposeId(String type, String code, String localeId) {
    this.type = type;
    this.code = code;
    this.localeId = localeId;
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   *
   * @param object the reference object with which to compare
   * @return <code>true</code> if this object is the same as the object argument otherwise <code>
   * false</code>
   */
  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }

    if (object == null) {
      return false;
    }

    if (getClass() != object.getClass()) {
      return false;
    }

    ContactMechanismPurposeId other = (ContactMechanismPurposeId) object;

    return Objects.equals(type, other.type)
        && Objects.equals(code, other.code)
        && Objects.equals(localeId, other.localeId);
  }

  /**
   * Returns the code for the contact mechanism type.
   *
   * @return the code for the contact mechanism type
   */
  public String getCode() {
    return code;
  }

  /**
   * Returns the Unicode locale identifier for the contact mechanism type.
   *
   * @return the Unicode locale identifier for the contact mechanism type
   */
  public String getLocaleId() {
    return localeId;
  }

  /**
   * Returns the code for the contact mechanism type the contact mechanism purpose is associated
   * with.
   *
   * @return the code for the contact mechanism type the contact mechanism purpose is associated
   *     with
   */
  public String getType() {
    return type;
  }

  /**
   * Returns a hash code value for the object.
   *
   * @return a hash code value for the object
   */
  @Override
  public int hashCode() {
    return ((type == null) ? 0 : type.hashCode())
        + ((code == null) ? 0 : code.hashCode())
        + ((localeId == null) ? 0 : localeId.hashCode());
  }

  /**
   * Set the code for the contact mechanism type.
   *
   * @param code the code for the contact mechanism type
   */
  public void setCode(String code) {
    this.code = code;
  }

  /**
   * Set the Unicode locale identifier for the contact mechanism type.
   *
   * @param locale the Unicode locale identifier for the contact mechanism type
   */
  public void setLocaleId(String locale) {
    this.localeId = locale;
  }

  /**
   * Set the code for the contact mechanism type the contact mechanism purpose is associated with.
   *
   * @param type the code for the contact mechanism type the contact mechanism purpose is
   *     associated with
   */
  public void setType(String type) {
    this.type = type;
  }
}
