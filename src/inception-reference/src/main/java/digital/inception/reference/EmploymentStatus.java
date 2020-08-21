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
 * The <code>EmploymentStatus</code> class holds the information for a possible employment status.
 *
 * @author Marcus Portmann
 */
@Schema(description = "EmploymentStatus")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"code", "locale", "sortIndex", "name", "description"})
@XmlRootElement(name = "EmploymentStatus", namespace = "http://reference.inception.digital")
@XmlType(
    name = "EmploymentStatus",
    namespace = "http://reference.inception.digital",
    propOrder = {"code", "locale", "sortIndex", "name", "description"})
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(schema = "reference", name = "employment_statuses")
@IdClass(EmploymentStatusId.class)
public class EmploymentStatus {

  /** The code for the employment status. */
  @Schema(description = "The code for the employment status", required = true)
  @JsonProperty(required = true)
  @XmlElement(name = "Code", required = true)
  @NotNull
  @Size(min = 1, max = 10)
  @Id
  @Column(name = "code", nullable = false)
  private String code;

  /** The description for the employment status. */
  @Schema(description = "The description for the employment status", required = true)
  @JsonProperty(required = true)
  @XmlElement(name = "Description", required = true)
  @NotNull
  @Size(max = 200)
  @Column(name = "description", nullable = false)
  private String description;

  /** The Unicode locale identifier for the employment status. */
  @Schema(description = "The Unicode locale identifier for the employment status", required = true)
  @JsonProperty(required = true)
  @XmlElement(name = "Locale", required = true)
  @NotNull
  @Size(min = 2, max = 10)
  @Id
  @Column(name = "locale", nullable = false)
  private String locale;

  /** The name of the employment status. */
  @Schema(description = "The name of the employment status", required = true)
  @JsonProperty(required = true)
  @XmlElement(name = "Name", required = true)
  @NotNull
  @Size(min = 1, max = 50)
  @Column(name = "name", nullable = false)
  private String name;

  /** The sort index for the employment status. */
  @Schema(description = "The sort index for the employment status", required = true)
  @JsonProperty(required = true)
  @XmlElement(name = "SortIndex", required = true)
  @NotNull
  @Column(name = "sort_index", nullable = false)
  private Integer sortIndex;

  /** Constructs a new <code>EmploymentStatus</code>. */
  public EmploymentStatus() {}

  /**
   * Returns the code for the employment status.
   *
   * @return the code for the employment status
   */
  public String getCode() {
    return code;
  }

  /**
   * Returns the description for the employment status.
   *
   * @return the description for the employment status
   */
  public String getDescription() {
    return description;
  }

  /**
   * Returns the Unicode locale identifier for the employment status.
   *
   * @return the Unicode locale identifier for the employment status
   */
  public String getLocale() {
    return locale;
  }

  /**
   * Returns the name of the employment status.
   *
   * @return the name of the employment status
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the sort index for the employment status.
   *
   * @return the sort index for the employment status
   */
  public Integer getSortIndex() {
    return sortIndex;
  }

  /**
   * Set the code for the employment status.
   *
   * @param code the code for the employment status
   */
  public void setCode(String code) {
    this.code = code;
  }

  /**
   * Set the description for the employment status.
   *
   * @param description the description for the employment status
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Set the Unicode locale identifier for the employment status.
   *
   * @param localeId the Unicode locale identifier for the employment status
   */
  public void setLocale(String localeId) {
    this.locale = localeId;
  }

  /**
   * Set the name of the employment status.
   *
   * @param name the name of the employment status
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Set the sort index for the employment status.
   *
   * @param sortIndex the sort index for the employment status
   */
  public void setSortIndex(Integer sortIndex) {
    this.sortIndex = sortIndex;
  }
}
