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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * The <code>Party</code> class holds the information for an independent party.
 *
 * <p>See: https://spec.edmcouncil.org/fibo/ontology/FND/Parties/Parties/IndependentParty
 *
 * @author Marcus Portmann
 */
@Schema(description = "Party")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"id", "type", "name"})
@XmlRootElement(name = "Party", namespace = "http://party.inception.digital")
@XmlType(
    name = "Party",
    namespace = "http://party.inception.digital",
    propOrder = {"id", "type", "name"})
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(schema = "party", name = "parties")
public class Party {

  /** The Universally Unique Identifier (UUID) uniquely identifying the party. */
  @Schema(
      description = "The Universally Unique Identifier (UUID) uniquely identifying the party",
      required = true)
  @JsonProperty(required = true)
  @XmlElement(name = "Id", required = true)
  @NotNull
  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  /** The name of the party. */
  @Schema(description = "The name of the party", required = true)
  @JsonProperty(required = true)
  @XmlElement(name = "Name", required = true)
  @NotNull
  @Size(min = 1, max = 100)
  @Column(name = "name", nullable = false, length = 100)
  private String name;

  /** The type of party. */
  @Schema(
      description = "The type of party, i.e. 0 = Unknown, 1 = Organization, 2 = Person",
      required = true)
  @JsonProperty(required = true)
  @XmlElement(name = "Type", required = true)
  @NotNull
  @Column(name = "type", nullable = false)
  private PartyType type;

  /** Constructs a new <code>Party</code>. */
  public Party() {}

  /**
   * Constructs a new <code>Party</code>.
   *
   * @param type the type of party
   */
  public Party(PartyType type) {
    this.type = type;
  }

  /**
   * Returns the Universally Unique Identifier (UUID) uniquely identifying the party.
   *
   * @return the Universally Unique Identifier (UUID) uniquely identifying the party
   */
  public UUID getId() {
    return id;
  }

  /**
   * Returns the name of the party.
   *
   * @return the name of the party
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the type of party.
   *
   * @return the type of party
   */
  public PartyType getType() {
    return type;
  }

  /**
   * Set the Universally Unique Identifier (UUID) uniquely identifying the party.
   *
   * @param id the Universally Unique Identifier (UUID) uniquely identifying the party
   */
  public void setId(UUID id) {
    this.id = id;
  }

  /**
   * Set the name of the party.
   *
   * @param name the name of the party
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Set the type of party.
   *
   * @param type the type of party
   */
  public void setType(PartyType type) {
    this.type = type;
  }
}