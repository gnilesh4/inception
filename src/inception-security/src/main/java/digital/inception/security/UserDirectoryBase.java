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

import java.util.List;
import java.util.UUID;

// ~--- JDK imports ------------------------------------------------------------

/**
 * The <code>UserDirectoryBase</code> class provides the base class from which all user directory
 * classes should be derived.
 *
 * @author Marcus Portmann
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class UserDirectoryBase implements IUserDirectory {

  /** The Group Repository. */
  private GroupRepository groupRepository;

  /** The parameters for the user directory. */
  private List<UserDirectoryParameter> parameters;

  /** The Role Repository. */
  private RoleRepository roleRepository;

  /** The Universally Unique Identifier (UUID) uniquely identifying the user directory. */
  private UUID userDirectoryId;

  /** The User Repository. */
  private UserRepository userRepository;

  /**
   * Constructs a new <code>UserDirectoryBase</code>.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   * @param parameters the parameters for the user directory
   * @param groupRepository the Group Repository
   * @param userRepository the User Repository
   * @param roleRepository the Role Repository
   */
  public UserDirectoryBase(
      UUID userDirectoryId,
      List<UserDirectoryParameter> parameters,
      GroupRepository groupRepository,
      UserRepository userRepository,
      RoleRepository roleRepository) {
    this.userDirectoryId = userDirectoryId;
    this.parameters = parameters;
    this.groupRepository = groupRepository;
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
  }

  /**
   * Returns the Group Repository.
   *
   * @return the Group Repository
   */
  public GroupRepository getGroupRepository() {
    return groupRepository;
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
   * Returns the Role Repository.
   *
   * @return the Role Repository
   */
  public RoleRepository getRoleRepository() {
    return roleRepository;
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
   * Returns the User Repository.
   *
   * @return the User Repository
   */
  public UserRepository getUserRepository() {
    return userRepository;
  }

  /**
   * Checks whether the specified value is <code>null</code> or blank.
   *
   * @param value the value to check
   * @return true if the value is <code>null</code> or blank
   */
  protected boolean isNullOrEmpty(Object value) {
    if (value == null) {
      return true;
    }

    if (value instanceof String) {
      return ((String) value).length() == 0;
    }

    return false;
  }
}
