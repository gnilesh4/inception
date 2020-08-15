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

package digital.inception.security;

// ~--- non-JDK imports --------------------------------------------------------

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import digital.inception.core.xml.DtdJarResolver;
import digital.inception.core.xml.XmlParserErrorHandler;
import digital.inception.core.xml.XmlUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

// ~--- JDK imports ------------------------------------------------------------

/**
 * The <code>UserDirectory</code> class holds the information for a user directory.
 *
 * @author Marcus Portmann
 */
@Schema(description = "UserDirectory")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"id", "type", "name", "parameters"})
@XmlRootElement(name = "UserDirectory", namespace = "http://security.inception.digital")
@XmlType(
    name = "UserDirectory",
    namespace = "http://security.inception.digital",
    propOrder = {"id", "type", "name", "parameters"})
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(schema = "security", name = "user_directories")
@SuppressWarnings({"unused"})
public class UserDirectory implements Serializable {

  private static final long serialVersionUID = 1000000;

  /** The Universally Unique Identifier (UUID) uniquely identifying the user directory. */
  @Schema(
      description =
          "The Universally Unique Identifier (UUID) uniquely identifying the user directory",
      required = true)
  @JsonProperty(required = true)
  @XmlElement(name = "Id", required = true)
  @NotNull
  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  /** The name of the user directory. */
  @Schema(description = "The name of the user directory", required = true)
  @JsonProperty(required = true)
  @XmlElement(name = "Name", required = true)
  @NotNull
  @Size(min = 1, max = 100)
  @Column(name = "name", nullable = false, length = 100)
  private String name;

  /** The organizations the user directory is associated with. */
  @JsonIgnore
  @XmlTransient
  @ManyToMany(mappedBy = "userDirectories")
  private Set<Organization> organizations = new HashSet<>();

  /** The parameters for the user directory. */
  @Schema(description = "The parameters for the user directory", required = true)
  @JsonProperty(required = true)
  @XmlElementWrapper(name = "Parameters", required = true)
  @XmlElement(name = "Parameter", required = true)
  @Valid
  @Transient
  private List<UserDirectoryParameter> parameters = new ArrayList<>();

  /** The code uniquely identifying the user directory type. */
  @Schema(description = "The code uniquely identifying the user directory type", required = true)
  @JsonProperty(required = true)
  @XmlElement(name = "Type", required = true)
  @NotNull
  @Size(min = 1, max = 100)
  @Column(name = "type", nullable = false, length = 100)
  private String type;

  /** Constructs a new <code>UserDirectory</code>. */
  public UserDirectory() {}

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

    UserDirectory other = (UserDirectory) object;

    return (id != null) && id.equals(other.id);
  }

  /**
   * Returns the XML configuration data for the user directory.
   *
   * @return the XML configuration data for the user directory
   */
  @JsonIgnore
  @XmlTransient
  @Access(AccessType.PROPERTY)
  @Column(name = "configuration", nullable = false, length = 4000)
  public String getConfiguration() {
    StringBuilder buffer = new StringBuilder();

    buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    buffer.append(
        "<!DOCTYPE userDirectory SYSTEM \"UserDirectoryConfiguration.dtd\"><userDirectory>");

    for (UserDirectoryParameter parameter : parameters) {
      buffer.append("<parameter>");
      buffer.append("<name>").append(parameter.getName()).append("</name>");
      buffer
          .append("<value>")
          .append(StringUtils.isEmpty(parameter.getValue()) ? "" : parameter.getValue())
          .append("</value>");
      buffer.append("</parameter>");
    }

    buffer.append("</userDirectory>");

    return buffer.toString();
  }

  /**
   * Returns the Universally Unique Identifier (UUID) uniquely identifying the user directory.
   *
   * @return the Universally Unique Identifier (UUID) uniquely identifying the user directory
   */
  public UUID getId() {
    return id;
  }

  /**
   * Returns the name of the user directory.
   *
   * @return the name of the user directory
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the organizations the user directory is associated with.
   *
   * @return the organizations the user directory is associated with
   */
  public Set<Organization> getOrganizations() {
    return organizations;
  }

  /**
   * Returns the parameters for the user directory.
   *
   * @return the parameters for the user directory
   */
  public List<UserDirectoryParameter> getParameters() {
    return parameters;
  }

  /**
   * Returns the code uniquely identifying the user directory type.
   *
   * @return the code uniquely identifying the user directory type
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
    return (id == null) ? 0 : id.hashCode();
  }

  /**
   * Set the XML configuration data for the user directory.
   *
   * @param configuration the XML configuration data for the user directory
   */
  public void setConfiguration(String configuration) throws SecurityServiceException {
    try {
      // Parse the XML configuration data
      DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();

      builderFactory.setValidating(true);

      DocumentBuilder builder = builderFactory.newDocumentBuilder();

      builder.setEntityResolver(
          new DtdJarResolver(
              "UserDirectoryConfiguration.dtd", "META-INF/UserDirectoryConfiguration.dtd"));
      builder.setErrorHandler(new XmlParserErrorHandler());

      InputSource inputSource = new InputSource(new ByteArrayInputStream(configuration.getBytes()));
      Document document = builder.parse(inputSource);

      Element rootElement = document.getDocumentElement();

      // Read the user directory parameters configuration
      parameters = new ArrayList<>();

      NodeList parameterElements = rootElement.getElementsByTagName("parameter");

      for (int i = 0; i < parameterElements.getLength(); i++) {
        Element parameterElement = (Element) parameterElements.item(i);

        parameters.add(
            new UserDirectoryParameter(
                XmlUtil.getChildElementText(parameterElement, "name"),
                XmlUtil.getChildElementText(parameterElement, "value")));
      }
    } catch (Throwable e) {
      throw new SecurityServiceException(
          "Failed to parse the XML configuration data for the user directory", e);
    }
  }

  /**
   * Set the Universally Unique Identifier (UUID) uniquely identifying the user directory.
   *
   * @param id the Universally Unique Identifier (UUID) uniquely identifying the user directory
   */
  public void setId(UUID id) {
    this.id = id;
  }

  /**
   * Set the name of the user directory.
   *
   * @param name the name of the user directory
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Set the organizations the user directory is associated with.
   *
   * @param organizations the organizations the user directory is associated with
   */
  public void setOrganizations(Set<Organization> organizations) {
    this.organizations = organizations;
  }

  /**
   * Set the parameters for the user directory.
   *
   * @param parameters the parameters for the user directory
   */
  public void setParameters(List<UserDirectoryParameter> parameters) {
    this.parameters = parameters;
  }

  /**
   * Set the code uniquely identifying the user directory type.
   *
   * @param type the code uniquely identifying the user directory type
   */
  public void setType(String type) {
    this.type = type;
  }
}
