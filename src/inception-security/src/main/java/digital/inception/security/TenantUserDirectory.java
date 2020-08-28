package digital.inception.security;

// ~--- non-JDK imports --------------------------------------------------------

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

// ~--- JDK imports ------------------------------------------------------------

/**
 * The <code>TenantUserDirectory</code> class holds the information for a tenant user directory.
 *
 * @author Marcus Portmann
 */
@Schema(description = "TenantUserDirectory")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"tenantId", "userDirectoryId"})
@XmlRootElement(name = "TenantUserDirectory", namespace = "http://security.inception.digital")
@XmlType(
    name = "TenantUserDirectory",
    namespace = "http://security.inception.digital",
    propOrder = {"tenantId", "userDirectoryId"})
@XmlAccessorType(XmlAccessType.FIELD)
@SuppressWarnings({"unused"})
public class TenantUserDirectory implements Serializable {

  /** The Universally Unique Identifier (UUID) uniquely identifying the tenant. */
  @Schema(
      description = "The Universally Unique Identifier (UUID) uniquely identifying the tenant",
      required = true)
  @JsonProperty(required = true)
  @XmlElement(name = "TenantId", required = true)
  @NotNull
  @Id
  @Column(name = "tenantId", nullable = false)
  private UUID tenantId;

  /** The Universally Unique Identifier (UUID) uniquely identifying the user directory. */
  @Schema(
      description =
          "The Universally Unique Identifier (UUID) uniquely identifying the user directory",
      required = true)
  @JsonProperty(required = true)
  @XmlElement(name = "UserDirectoryId", required = true)
  @NotNull
  @Id
  @Column(name = "userDirectoryId", nullable = false)
  private UUID userDirectoryId;

  /** Constructs a new <code>TenantUserDirectory</code>. */
  public TenantUserDirectory() {}

  /**
   * Constructs a new <code>TenantUserDirectory</code>.
   *
   * @param tenantId the Universally Unique Identifier (UUID) uniquely identifying the tenant
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   */
  public TenantUserDirectory(UUID tenantId, UUID userDirectoryId) {
    this.tenantId = tenantId;
    this.userDirectoryId = userDirectoryId;
  }

  /**
   * Returns the Universally Unique Identifier (UUID) uniquely identifying the tenant.
   *
   * @return the Universally Unique Identifier (UUID) uniquely identifying the tenant
   */
  public UUID getTenantId() {
    return tenantId;
  }

  /**
   * Returns the Universally Unique Identifier (UUID) uniquely identifying the user directory.
   *
   * @return the Universally Unique Identifier (UUID) uniquely identifying the user directory
   */
  public UUID getUserDirectoryId() {
    return userDirectoryId;
  }

  /**
   * Set the Universally Unique Identifier (UUID) uniquely identifying the tenant.
   *
   * @param tenantId the Universally Unique Identifier (UUID) uniquely identifying the tenant
   */
  public void setTenantId(UUID tenantId) {
    this.tenantId = tenantId;
  }

  /**
   * Set the Universally Unique Identifier (UUID) uniquely identifying the user directory.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   */
  public void setUserDirectoryId(UUID userDirectoryId) {
    this.userDirectoryId = userDirectoryId;
  }
}