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

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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

/**
 * The <code>IdentityDocument</code> class holds the information for an identity document.
 *
 * <p>See: https://spec.edmcouncil.org/fibo/ontology/FND/AgentsAndPeople/People/IdentityDocument
 *
 * @author Marcus Portmann
 */
@Schema(description = "A legal document which may be used to verify aspects of a person's identity")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"type", "countryOfIssue", "dateOfIssue", "dateOfExpiry", "number"})
@XmlRootElement(name = "IdentityDocument", namespace = "http://party.inception.digital")
@XmlType(
    name = "IdentityDocument",
    namespace = "http://party.inception.digital",
    propOrder = {"type", "countryOfIssue", "dateOfIssue", "dateOfExpiry", "number"})
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(schema = "party", name = "identity_documents")
@IdClass(IdentityDocumentId.class)
public class IdentityDocument implements Serializable {

  private static final long serialVersionUID = 1000000;

  /** The code identifying the country of issue for the identity document. */
  @Schema(
      description = "The code identifying the country of issue for the identity document",
      required = true)
  @JsonProperty(required = true)
  @XmlElement(name = "CountryOfIssue", required = true)
  @NotNull
  @Size(min = 1, max = 10)
  @Id
  @Column(name = "country_of_issue", nullable = false)
  private String countryOfIssue;

  /** The date and time the identity document was created. */
  @JsonIgnore
  @XmlTransient
  @CreationTimestamp
  @Column(name = "created", nullable = false, updatable = false)
  private LocalDateTime created;

  /** The optional date of expiry for the identity document. */
  @Schema(description = "The optional date of expiry for the identity document")
  @JsonProperty
  @XmlElement(name = "DateOfExpiry")
  @Column(name = "date_of_expiry")
  private LocalDate dateOfExpiry;

  /** The date of issue for the identity document. */
  @Schema(description = "The date of issue for the identity document", required = true)
  @JsonProperty(required = true)
  @XmlElement(name = "DateOfIssue", required = true)
  @NotNull
  @Id
  @Column(name = "date_of_issue", nullable = false)
  private LocalDate dateOfIssue;

  /** The number for the identity document. */
  @Schema(description = "The number for the identity document", required = true)
  @JsonProperty(required = true)
  @XmlElement(name = "Number", required = true)
  @NotNull
  @Size(min = 1, max = 50)
  @Column(name = "number", length = 50, nullable = false)
  private String number;

  /** The person the identity document is associated with. */
  @Schema(hidden = true)
  @JsonBackReference
  @XmlTransient
  @Id
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "person_id")
  private Person person;

  /** The code identifying the type of identity document. */
  @Schema(description = "The code identifying the type of identity document", required = true)
  @JsonProperty(required = true)
  @XmlElement(name = "Type", required = true)
  @NotNull
  @Size(min = 1, max = 10)
  @Id
  @Column(name = "type", nullable = false)
  private String type;

  /** The date and time the identity document was last updated. */
  @JsonIgnore
  @XmlTransient
  @UpdateTimestamp
  @Column(name = "updated", insertable = false)
  private LocalDateTime updated;

  /** Constructs a new <code>IdentityDocument</code>. */
  public IdentityDocument() {}

  /**
   * Constructs a new <code>IdentityDocument</code>.
   *
   * @param type the code identifying the type of identity document
   * @param countryOfIssue the code identifying the country of issue for the identity document
   * @param dateOfIssue the date of issue for the identity document
   * @param number the number for the identity document
   */
  public IdentityDocument(
      String type, String countryOfIssue, LocalDate dateOfIssue, String number) {
    this.type = type;
    this.countryOfIssue = countryOfIssue;
    this.dateOfIssue = dateOfIssue;
    this.number = number;
  }

  /**
   * Constructs a new <code>IdentityDocument</code>.
   *
   * @param type the code identifying the type of identity document
   * @param countryOfIssue the code identifying the country of issue for the identity document
   * @param dateOfIssue the date of issue for the identity document
   * @param dateOfExpiry the optional date of expiry for the identity document
   * @param number thge number for the identity document
   */
  public IdentityDocument(
      String type,
      String countryOfIssue,
      LocalDate dateOfIssue,
      LocalDate dateOfExpiry,
      String number) {
    this.type = type;
    this.countryOfIssue = countryOfIssue;
    this.dateOfIssue = dateOfIssue;
    this.dateOfExpiry = dateOfExpiry;
    this.number = number;
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

    IdentityDocument other = (IdentityDocument) object;

    return Objects.equals(person, other.person)
        && Objects.equals(type, other.type)
        && Objects.equals(countryOfIssue, other.countryOfIssue)
        && Objects.equals(dateOfIssue, other.dateOfIssue);
  }

  /**
   * Returns the code identifying the country of issue for the identity document.
   *
   * @return the code identifying the country of issue for the identity document
   */
  public String getCountryOfIssue() {
    return countryOfIssue;
  }

  /**
   * Returns the date and time the identity document was created.
   *
   * @return the date and time the identity document was created
   */
  public LocalDateTime getCreated() {
    return created;
  }

  /**
   * Returns the optional date of expiry for the identity document.
   *
   * @return the optional date of expiry for the identity document
   */
  public LocalDate getDateOfExpiry() {
    return dateOfExpiry;
  }

  /**
   * Returns the date of issue for the identity document.
   *
   * @return the date of issue for the identity document
   */
  public LocalDate getDateOfIssue() {
    return dateOfIssue;
  }

  /**
   * The number for the identity document.
   *
   * @return the number for the identity document
   */
  public String getNumber() {
    return number;
  }

  /**
   * Returns the person the identity document is associated with.
   *
   * @return the person the identity document is associated with
   */
  @Schema(hidden = true)
  public Person getPerson() {
    return person;
  }

  /**
   * Returns the code identifying the type of identity document.
   *
   * @return the code identifying the type of identity document
   */
  public String getType() {
    return type;
  }

  /**
   * Returns the date and time the identity document was last updated.
   *
   * @return the date and time the identity document was last updated
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
    return ((person == null) ? 0 : person.hashCode())
        + ((type == null) ? 0 : type.hashCode())
        + ((countryOfIssue == null) ? 0 : countryOfIssue.hashCode())
        + ((dateOfIssue == null) ? 0 : dateOfIssue.hashCode());
  }

  /**
   * Set the code identifying the country of issue for the identity document.
   *
   * @param countryOfIssue the code identifying the country of issue for the identity document
   */
  public void setCountryOfIssue(String countryOfIssue) {
    this.countryOfIssue = countryOfIssue;
  }

  /**
   * Set the optional date of expiry for the identity document.
   *
   * @param dateOfExpiry the optional date of expiry for the identity document
   */
  public void setDateOfExpiry(LocalDate dateOfExpiry) {
    this.dateOfExpiry = dateOfExpiry;
  }

  /**
   * Set the date of issue for the identity document.
   *
   * @param dateOfIssue the date of issue for the identity document
   */
  public void setDateOfIssue(LocalDate dateOfIssue) {
    this.dateOfIssue = dateOfIssue;
  }

  /**
   * Set the number for the identity document.
   *
   * @param number the number for the identity document
   */
  public void setNumber(String number) {
    this.number = number;
  }

  /**
   * Set the person the identity document is associated with.
   *
   * @param person the person the identity document is associated with
   */
  @Schema(hidden = true)
  public void setPerson(Person person) {
    this.person = person;
  }

  /**
   * Set the code identifying the type of identity document.
   *
   * @param type the code identifying the type of identity document
   */
  public void setType(String type) {
    this.type = type;
  }
}
