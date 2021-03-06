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

package digital.inception.codes;

// ~--- non-JDK imports --------------------------------------------------------

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

// ~--- JDK imports ------------------------------------------------------------

/**
 * The <code>CodeCategory</code> class holds the information for a code category.
 *
 * @author Marcus Portmann
 */
@Schema(description = "A collection of related codes")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"id", "name", "data"})
@XmlRootElement(name = "CodeCategory", namespace = "http://codes.inception.digital")
@XmlType(
    name = "CodeCategory",
    namespace = "http://codes.inception.digital",
    propOrder = {"id", "name", "data"})
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(schema = "codes", name = "code_categories")
public class CodeCategory implements Serializable {

  private static final long serialVersionUID = 1000000;

  /** The date and time the code category was created. */
  @JsonIgnore
  @XmlTransient
  @CreationTimestamp
  @Column(name = "created", nullable = false, updatable = false)
  private LocalDateTime created;

  /** The optional code data for the code category. */
  @Schema(description = "The optional code data for the code category")
  @JsonProperty
  @XmlElement(name = "Data")
  @Column(name = "data")
  private String data;

  /** The ID uniquely identifying the code category. */
  @Schema(description = "The ID uniquely identifying the code category", required = true)
  @JsonProperty(required = true)
  @XmlElement(name = "Id", required = true)
  @NotNull
  @Size(min = 1, max = 100)
  @Id
  @Column(name = "id", nullable = false, length = 100)
  private String id;

  /** The name of the code category. */
  @Schema(description = "The name of the code category", required = true)
  @JsonProperty(required = true)
  @XmlElement(name = "Name", required = true)
  @NotNull
  @Size(max = 100)
  @Column(name = "name", nullable = false, length = 100)
  private String name;

  /** The date and time the code category was last updated. */
  @JsonIgnore
  @XmlTransient
  @UpdateTimestamp
  @Column(name = "updated", insertable = false)
  private LocalDateTime updated;

  /** Constructs a new <code>CodeCategory</code>. */
  public CodeCategory() {}

  /**
   * Constructs a new <code>CodeCategory</code>.
   *
   * @param id the ID uniquely identifying the code category
   * @param name the name of the code category
   */
  public CodeCategory(String id, String name) {
    this.id = id;
    this.name = name;
  }

  /**
   * Constructs a new <code>CodeCategory</code>.
   *
   * @param id the ID uniquely identifying the code category
   * @param name the name of the code category
   * @param data the optional code data for the code category
   */
  public CodeCategory(String id, String name, String data) {
    this.id = id;
    this.name = name;
    this.data = data;
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

    CodeCategory other = (CodeCategory) object;

    return Objects.equals(id, other.id);
  }

  /**
   * Returns the date and time the code category was created.
   *
   * @return the date and time the code category was created
   */
  public LocalDateTime getCreated() {
    return created;
  }

  /**
   * Returns the optional code data for the code category.
   *
   * @return the optional code data for the code category
   */
  public String getData() {
    return data;
  }

  /**
   * Returns the ID uniquely identifying the code category.
   *
   * @return the ID uniquely identifying the code category
   */
  public String getId() {
    return id;
  }

  /**
   * Returns the name of the code category.
   *
   * @return the name of the code category
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the date and time the code category was last updated.
   *
   * @return the date and time the code category was last updated
   */
  public LocalDateTime getUpdated() {
    return updated;
  }

  /**
   * Returns a hash code value for the object.
   *
   * @return a hash code value for the object
   */
  @Override
  public int hashCode() {
    return (id == null) ? 0 : id.hashCode();
  }

  /**
   * Set the optional code data for the code category.
   *
   * @param data the optional code data for the code category
   */
  public void setData(String data) {
    this.data = data;
  }

  /**
   * Set the ID uniquely identifying the code category.
   *
   * @param id the ID uniquely identifying the code category
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Set the name of the code category.
   *
   * @param name the name of the code category
   */
  public void setName(String name) {
    this.name = name;
  }
}
