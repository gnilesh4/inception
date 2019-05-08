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

package digital.inception.configuration;

//~--- non-JDK imports --------------------------------------------------------

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

//~--- JDK imports ------------------------------------------------------------

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import javax.xml.bind.annotation.*;

/**
 * The <code>Configuration</code> class stores the summary for a configuration.
 *
 * @author Marcus Portmann
 */
@ApiModel(value = "ConfigurationSummary")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "key", "description" })
@XmlRootElement(name = "ConfigurationSummary", namespace = "http://configuration.inception.digital")
@XmlType(name = "ConfigurationSummary", namespace = "http://configuration.inception.digital",
    propOrder = { "key", "description" })
@XmlAccessorType(XmlAccessType.FIELD)
public class ConfigurationSummary
  implements Serializable
{
  private static final long serialVersionUID = 1000000;

  /**
   * The description for the configuration.
   */
  @ApiModelProperty(value = "The description for the configuration", required = true)
  @JsonProperty(required = true)
  @XmlElement(name = "Description", required = true)
  @NotNull
  @Size(max = 4000)
  private String description;

  /**
   * The key used to uniquely identify the configuration.
   */
  @ApiModelProperty(value = "The key used to uniquely identify the configuration", required = true)
  @JsonProperty(required = true)
  @XmlElement(name = "Key", required = true)
  @NotNull
  @Size(min = 1, max = 4000)
  private String key;

  /**
   * Constructs a new <code>ConfigurationSummary</code>.
   */
  public ConfigurationSummary() {}

  /**
   * Constructs a new <code>ConfigurationSummary</code>.
   *
   * @param key         the key used to uniquely identify the configuration
   * @param description the description for the configuration
   */
  ConfigurationSummary(String key, String description)
  {
    this.key = key;
    this.description = description;
  }

  /**
   * Returns the description for the configuration.
   *
   * @return the description for the configuration
   */
  public String getDescription()
  {
    return description;
  }

  /**
   * Returns the key used to uniquely identify the configuration.
   *
   * @return the key used to uniquely identify the configuration
   */
  public String getKey()
  {
    return key;
  }

  /**
   * Set the description for the configuration.
   *
   * @param description the description for the configuration
   */
  public void setDescription(String description)
  {
    this.description = description;
  }

  /**
   * Set the key used to uniquely identify the configuration.
   *
   * @param key the key used to uniquely identify the configuration
   */
  public void setKey(String key)
  {
    this.key = key;
  }
}