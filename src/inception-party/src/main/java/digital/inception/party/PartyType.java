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

package digital.inception.party;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * The <code>PartyType</code> enumeration defines the possible party types.
 *
 * @author Marcus Portmann
 */
@Schema(description = "The party type")
@XmlEnum
@XmlType(name = "PartyType", namespace = "http://party.inception.digital")
public enum PartyType {
  @XmlEnumValue("Organization")
  ORGANIZATION("organization", "Organization"),
  @XmlEnumValue("Person")
  PERSON("person", "Person"),
  @XmlEnumValue("Unknown")
  UNKNOWN("unknown", "Unknown");

  private final String code;

  private final String description;

  PartyType(String code, String description) {
    this.code = code;
    this.description = description;
  }

  /**
   * Returns the party type given by the specified code value.
   *
   * @param code the code value identifying the party type
   * @return the party type given by the specified code value
   */
  @JsonCreator
  public static PartyType fromCode(String code) {
    switch (code) {
      case "organization":
        return PartyType.ORGANIZATION;

      case "person":
        return PartyType.PERSON;

      default:
        throw new RuntimeException(
            "Failed to determine the party type with the invalid code (" + code + ")");
    }
  }

  /**
   * Returns the party type for the specified numeric code.
   *
   * @param numericCode the numeric code identifying the party type
   * @return the party type given by the specified numeric code value
   */
  public static PartyType fromNumericCode(int numericCode) {
    switch (numericCode) {
      case 0:
        return PartyType.UNKNOWN;
      case 1:
        return PartyType.ORGANIZATION;
      case 2:
        return PartyType.PERSON;
      default:
        throw new RuntimeException(
            "Failed to determine the party type for the numeric code (" + numericCode + ")");
    }
  }

  /**
   * Returns the numeric code for the party type.
   *
   * @param partyType the party type
   * @return the numeric code for the party type
   */
  public static int toNumericCode(PartyType partyType) {
    switch (partyType) {
      case UNKNOWN:
        return 1;
      case ORGANIZATION:
        return 1;
      case PERSON:
        return 2;
      default:
        throw new RuntimeException(
            "Failed to determine the numeric code for the party type (" + partyType.code() + ")");
    }
  }

  /**
   * Returns the code value identifying for the party type.
   *
   * @return the code value identifying for the party type
   */
  @JsonValue
  public String code() {
    return code;
  }

  /**
   * Returns the description for the party type.
   *
   * @return the description for the party type
   */
  public String description() {
    return description;
  }
}
