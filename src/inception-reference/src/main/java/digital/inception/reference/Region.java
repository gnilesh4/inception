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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * The <code>Region</code> class holds the information for a possible region.
 *
 * @author Marcus Portmann
 */
@Schema(
    description =
        "An area, especially part of a country or the world having definable characteristics but not always fixed boundaries")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"country", "code", "localeId", "sortIndex", "name", "description"})
@XmlRootElement(name = "Region", namespace = "http://reference.inception.digital")
@XmlType(
    name = "Region",
    namespace = "http://reference.inception.digital",
    propOrder = {"country", "code", "localeId", "sortIndex", "name", "description"})
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(schema = "reference", name = "regions")
@IdClass(RegionId.class)
public class Region implements Serializable {

  private static final long serialVersionUID = 1000000;

  /** The code for the region. */
  @Schema(description = "The code for the region", required = true)
  @JsonProperty(required = true)
  @XmlElement(name = "Code", required = true)
  @NotNull
  @Size(min = 1, max = 10)
  @Id
  @Column(name = "code", nullable = false)
  private String code;

  /** The code for the country the region is associated with. */
  @Schema(description = "The code for the country the region is associated with", required = true)
  @JsonProperty(required = true)
  @XmlElement(name = "Country", required = true)
  @NotNull
  @Size(min = 1, max = 10)
  @Id
  @Column(name = "country", nullable = false)
  private String country;

  /** The description for the region. */
  @Schema(description = "The description for the region", required = true)
  @JsonProperty(required = true)
  @XmlElement(name = "Description", required = true)
  @NotNull
  @Size(max = 200)
  @Column(name = "description", nullable = false)
  private String description;

  /** The Unicode locale identifier for the region. */
  @Schema(description = "The Unicode locale identifier for the region", required = true)
  @JsonProperty(required = true)
  @XmlElement(name = "LocaleId", required = true)
  @NotNull
  @Size(min = 2, max = 10)
  @Id
  @Column(name = "locale_id", nullable = false)
  private String localeId;

  /** The name of the region. */
  @Schema(description = "The name of the region", required = true)
  @JsonProperty(required = true)
  @XmlElement(name = "Name", required = true)
  @NotNull
  @Size(min = 1, max = 50)
  @Column(name = "name", nullable = false)
  private String name;

  /** The sort index for the region. */
  @Schema(description = "The sort index for the region", required = true)
  @JsonProperty(required = true)
  @XmlElement(name = "SortIndex", required = true)
  @NotNull
  @Column(name = "sort_index", nullable = false)
  private Integer sortIndex;

  /** Constructs a new <code>Region</code>. */
  public Region() {}

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

    Region other = (Region) object;

    return Objects.equals(country, other.country)
        && Objects.equals(code, other.code)
        && Objects.equals(localeId, other.localeId);
  }

  /**
   * Returns the code for the region.
   *
   * @return the code for the region
   */
  public String getCode() {
    return code;
  }

  /**
   * Returns the code for the country the region is associated with.
   *
   * @return the code for the country the region is associated with
   */
  public String getCountry() {
    return country;
  }

  /**
   * Returns the description for the region.
   *
   * @return the description for the region
   */
  public String getDescription() {
    return description;
  }

  /**
   * Returns the Unicode locale identifier for the region.
   *
   * @return the Unicode locale identifier for the region
   */
  public String getLocaleId() {
    return localeId;
  }

  /**
   * Returns the name of the region.
   *
   * @return the name of the region
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the sort index for the region.
   *
   * @return the sort index for the region
   */
  public Integer getSortIndex() {
    return sortIndex;
  }

  /**
   * Returns a hash code value for the object.
   *
   * @return a hash code value for the object
   */
  @Override
  public int hashCode() {
    return ((country == null) ? 0 : country.hashCode())
        + ((code == null) ? 0 : code.hashCode())
        + ((localeId == null) ? 0 : localeId.hashCode());
  }

  /**
   * Set the code for the region.
   *
   * @param code the code for the region
   */
  public void setCode(String code) {
    this.code = code;
  }

  /**
   * Set the code for the country the region is associated with.
   *
   * @param country the code for the country the region is associated with
   */
  public void setCountry(String country) {
    this.country = country;
  }

  /**
   * Set the description for the region.
   *
   * @param description the description for the region
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Set the Unicode locale identifier for the region.
   *
   * @param localeId the Unicode locale identifier for the region
   */
  public void setLocaleId(String localeId) {
    this.localeId = localeId;
  }

  /**
   * Set the name of the region.
   *
   * @param name the name of the region
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Set the sort index for the region.
   *
   * @param sortIndex the sort index for the region
   */
  public void setSortIndex(Integer sortIndex) {
    this.sortIndex = sortIndex;
  }
}
