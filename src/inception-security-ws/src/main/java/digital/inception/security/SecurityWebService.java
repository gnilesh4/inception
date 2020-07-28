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

import digital.inception.validation.InvalidArgumentException;
import digital.inception.validation.ValidationError;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.xml.bind.annotation.XmlElement;
import org.springframework.util.StringUtils;

// ~--- JDK imports ------------------------------------------------------------

//  For get functions use classes with this naming convention...
//
//      Users, Groups, Organizations, etc

/**
 * The <code>SecurityWebService</code> class.
 *
 * @author Marcus Portmann
 */
@WebService(
    serviceName = "SecurityService",
    name = "ISecurityService",
    targetNamespace = "http://security.inception.digital")
@SOAPBinding
@SuppressWarnings({"unused", "ValidExternallyBoundObject"})
public class SecurityWebService {

  /** The Security Service. */
  private final ISecurityService securityService;

  /** The JSR-303 validator. */
  private final Validator validator;

  /**
   * Constructs a new <code>SecurityWebService</code>.
   *
   * @param securityService the Security Service
   * @param validator the JSR-303 validator
   */
  public SecurityWebService(ISecurityService securityService, Validator validator) {
    this.securityService = securityService;
    this.validator = validator;
  }

  /**
   * Add the group member to the group.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   * @param groupName the name identifying the group
   * @param memberType the group member type
   * @param memberName the name identifying the group member
   */
  @WebMethod(operationName = "AddMemberToGroup")
  public void addMemberToGroup(
      @WebParam(name = "UserDirectoryId") @XmlElement(required = true) UUID userDirectoryId,
      @WebParam(name = "GroupName") @XmlElement(required = true) String groupName,
      @WebParam(name = "MemberType") @XmlElement(required = true) GroupMemberType memberType,
      @WebParam(name = "MemberName") @XmlElement(required = true) String memberName)
      throws InvalidArgumentException, UserDirectoryNotFoundException, GroupNotFoundException,
          UserNotFoundException, ExistingGroupMemberException, SecurityServiceException {
    validateGroupMember(userDirectoryId, groupName, memberType, memberName);

    securityService.addMemberToGroup(userDirectoryId, groupName, memberType, memberName);
  }

  /**
   * Add the role to the group.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   * @param groupName the name identifying the group
   * @param roleCode the code uniquely identifying the role
   */
  @WebMethod(operationName = "AddRoleToGroup")
  public void addRoleToGroup(
      @WebParam(name = "UserDirectoryId") @XmlElement(required = true) UUID userDirectoryId,
      @WebParam(name = "GroupName") @XmlElement(required = true) String groupName,
      @WebParam(name = "RoleCode") @XmlElement(required = true) String roleCode)
      throws InvalidArgumentException, UserDirectoryNotFoundException, GroupNotFoundException,
          RoleNotFoundException, ExistingGroupRoleException, SecurityServiceException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    if (StringUtils.isEmpty(groupName)) {
      throw new InvalidArgumentException("groupName");
    }

    if (StringUtils.isEmpty(roleCode)) {
      throw new InvalidArgumentException("roleCode");
    }

    securityService.addRoleToGroup(userDirectoryId, groupName, roleCode);
  }

  /**
   * Add the user directory to the organization.
   *
   * @param organizationId the Universally Unique Identifier (UUID) uniquely identifying the
   *     organization
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   */
  @WebMethod(operationName = "AddUserDirectoryToOrganization")
  public void addUserDirectoryToOrganization(
      @WebParam(name = "OrganizationId") @XmlElement(required = true) UUID organizationId,
      @WebParam(name = "UserDirectoryId") @XmlElement(required = true) UUID userDirectoryId)
      throws InvalidArgumentException, OrganizationNotFoundException,
          UserDirectoryNotFoundException, ExistingOrganizationUserDirectoryException,
          SecurityServiceException {
    if (organizationId == null) {
      throw new InvalidArgumentException("organizationId");
    }

    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    securityService.addUserDirectoryToOrganization(organizationId, userDirectoryId);
  }

  /**
   * Administratively change the password for the user.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   * @param username the username identifying the user
   * @param passwordChange the password change
   */
  @WebMethod(operationName = "AdminChangePassword")
  public void adminChangePassword(
      @WebParam(name = "UserDirectoryId") @XmlElement(required = true) UUID userDirectoryId,
      @WebParam(name = "Username") @XmlElement(required = true) String username,
      @WebParam(name = "PasswordChange") @XmlElement(required = true) PasswordChange passwordChange)
      throws InvalidArgumentException, UserDirectoryNotFoundException, UserNotFoundException,
          SecurityServiceException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    if (StringUtils.isEmpty(username)) {
      throw new InvalidArgumentException("username");
    }

    if (passwordChange == null) {
      throw new InvalidArgumentException("passwordChange");
    }

    if (StringUtils.isEmpty(passwordChange.getNewPassword())) {
      throw new InvalidArgumentException("passwordChange");
    }

    Set<ConstraintViolation<PasswordChange>> constraintViolations =
        validator.validate(passwordChange);

    if (!constraintViolations.isEmpty()) {
      throw new InvalidArgumentException(
          "passwordChange", ValidationError.toValidationErrors(constraintViolations));
    }

    securityService.adminChangePassword(
        userDirectoryId,
        username,
        passwordChange.getNewPassword(),
        (passwordChange.getExpirePassword() == null) ? false : passwordChange.getExpirePassword(),
        (passwordChange.getLockUser() == null) ? false : passwordChange.getLockUser(),
        (passwordChange.getResetPasswordHistory() == null)
            ? false
            : passwordChange.getResetPasswordHistory(),
        passwordChange.getReason());
  }

  /**
   * Change the password for the user.
   *
   * @param username the username identifying the user
   * @param passwordChange the password change
   */
  @WebMethod(operationName = "ChangePassword")
  public void changePassword(
      @WebParam(name = "Username") @XmlElement(required = true) String username,
      @WebParam(name = "PasswordChange") @XmlElement(required = true) PasswordChange passwordChange)
      throws InvalidArgumentException, UserDirectoryNotFoundException, UserNotFoundException,
          AuthenticationFailedException, InvalidSecurityCodeException, ExistingPasswordException,
          UserLockedException, SecurityServiceException {
    if (StringUtils.isEmpty(username)) {
      throw new InvalidArgumentException("username");
    }

    if (passwordChange == null) {
      throw new InvalidArgumentException("passwordChange");
    }

    UUID userDirectoryId = securityService.getUserDirectoryIdForUser(username);

    if (userDirectoryId == null) {
      throw new UserNotFoundException(username);
    }

    Set<ConstraintViolation<PasswordChange>> constraintViolations =
        validator.validate(passwordChange);

    if (!constraintViolations.isEmpty()) {
      throw new InvalidArgumentException(
          "passwordChange", ValidationError.toValidationErrors(constraintViolations));
    }

    if (passwordChange.getReason() == PasswordChangeReason.ADMINISTRATIVE) {
      securityService.adminChangePassword(
          userDirectoryId,
          username,
          passwordChange.getNewPassword(),
          (passwordChange.getExpirePassword() == null) ? false : passwordChange.getExpirePassword(),
          (passwordChange.getLockUser() == null) ? false : passwordChange.getLockUser(),
          (passwordChange.getResetPasswordHistory() == null)
              ? false
              : passwordChange.getResetPasswordHistory(),
          passwordChange.getReason());
    } else if (passwordChange.getReason() == PasswordChangeReason.USER) {
      if (StringUtils.isEmpty(passwordChange.getPassword())) {
        throw new InvalidArgumentException("passwordChange");
      }

      securityService.changePassword(
          username, passwordChange.getPassword(), passwordChange.getNewPassword());
    } else if (passwordChange.getReason() == PasswordChangeReason.RESET) {
      if (StringUtils.isEmpty(passwordChange.getSecurityCode())) {
        throw new InvalidArgumentException("passwordChange");
      }

      securityService.resetPassword(
          username, passwordChange.getNewPassword(), passwordChange.getSecurityCode());
    }
  }

  /**
   * Create the new group.
   *
   * @param group the group
   */
  @WebMethod(operationName = "CreateGroup")
  public void createGroup(@WebParam(name = "Group") @XmlElement(required = true) Group group)
      throws InvalidArgumentException, UserDirectoryNotFoundException, DuplicateGroupException,
          SecurityServiceException {
    validateGroup(group);

    securityService.createGroup(group);
  }

  /**
   * Create the new organization.
   *
   * @param organization the organization to create
   * @param createUserDirectory should a new internal user directory be created for the organization
   */
  @WebMethod(operationName = "CreateOrganization")
  public void createOrganization(
      @WebParam(name = "Organization") @XmlElement(required = true) Organization organization,
      @WebParam(name = "CreateUserDirectory") @XmlElement(required = true)
          Boolean createUserDirectory)
      throws InvalidArgumentException, DuplicateOrganizationException, SecurityServiceException {
    validateOrganization(organization);

    securityService.createOrganization(
        organization, (createUserDirectory != null) && createUserDirectory);
  }

  /**
   * Create the new user.
   *
   * @param user the user
   * @param expiredPassword create the user with its password expired
   * @param userLocked create the user locked
   */
  @WebMethod(operationName = "CreateUser")
  public void createUser(
      @WebParam(name = "User") @XmlElement(required = true) User user,
      @WebParam(name = "ExpiredPassword") @XmlElement(required = true) Boolean expiredPassword,
      @WebParam(name = "UserLocked") @XmlElement(required = true) Boolean userLocked)
      throws InvalidArgumentException, UserDirectoryNotFoundException, DuplicateUserException,
          SecurityServiceException {
    validateUser(user);

    securityService.createUser(
        user,
        (expiredPassword != null) && expiredPassword,
        (userLocked != null) && userLocked);
  }

  /**
   * Create the new user directory.
   *
   * @param userDirectory the user directory
   */
  @WebMethod(operationName = "CreateUserDirectory")
  public void createUserDirectory(
      @WebParam(name = "UserDirectory") @XmlElement(required = true) UserDirectory userDirectory)
      throws InvalidArgumentException, DuplicateUserDirectoryException, SecurityServiceException {
    validateUserDirectory(userDirectory);

    securityService.createUserDirectory(userDirectory);
  }

  /**
   * Delete the group.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   * @param groupName the name identifying the group
   */
  @WebMethod(operationName = "DeleteGroup")
  public void deleteGroup(
      @WebParam(name = "UserDirectoryId") @XmlElement(required = true) UUID userDirectoryId,
      @WebParam(name = "GroupName") @XmlElement(required = true) String groupName)
      throws InvalidArgumentException, UserDirectoryNotFoundException, GroupNotFoundException,
          ExistingGroupMembersException, SecurityServiceException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    if (StringUtils.isEmpty(groupName)) {
      throw new InvalidArgumentException("groupName");
    }

    securityService.deleteGroup(userDirectoryId, groupName);
  }

  /**
   * Delete the organization.
   *
   * @param organizationId the Universally Unique Identifier (UUID) uniquely identifying the
   *     organization
   */
  @WebMethod(operationName = "DeleteOrganization")
  public void deleteOrganization(
      @WebParam(name = "OrganizationId") @XmlElement(required = true) UUID organizationId)
      throws InvalidArgumentException, OrganizationNotFoundException, SecurityServiceException {
    if (organizationId == null) {
      throw new InvalidArgumentException("organizationId");
    }

    securityService.deleteOrganization(organizationId);
  }

  /**
   * Delete the user.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   * @param username the username identifying the user
   */
  @WebMethod(operationName = "DeleteUser")
  public void deleteUser(
      @WebParam(name = "UserDirectoryId") @XmlElement(required = true) UUID userDirectoryId,
      @WebParam(name = "Username") @XmlElement(required = true) String username)
      throws InvalidArgumentException, UserDirectoryNotFoundException, UserNotFoundException,
          SecurityServiceException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    if (StringUtils.isEmpty(username)) {
      throw new InvalidArgumentException("username");
    }

    securityService.deleteUser(userDirectoryId, username);
  }

  /**
   * Delete the user directory.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   */
  @WebMethod(operationName = "DeleteUserDirectory")
  public void deleteUserDirectory(
      @WebParam(name = "UserDirectoryId") @XmlElement(required = true) UUID userDirectoryId)
      throws InvalidArgumentException, UserDirectoryNotFoundException, SecurityServiceException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    securityService.deleteUserDirectory(userDirectoryId);
  }

  /**
   * Retrieve the group.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   * @param groupName the name identifying the group
   * @return the group
   */
  @WebMethod(operationName = "GetGroup")
  @WebResult(name = "Group")
  public Group getGroup(
      @WebParam(name = "UserDirectoryId") @XmlElement(required = true) UUID userDirectoryId,
      @WebParam(name = "GroupName") @XmlElement(required = true) String groupName)
      throws InvalidArgumentException, UserDirectoryNotFoundException, GroupNotFoundException,
          SecurityServiceException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    if (StringUtils.isEmpty(groupName)) {
      throw new InvalidArgumentException("groupName");
    }

    return securityService.getGroup(userDirectoryId, groupName);
  }

  /**
   * Retrieve all the group names.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   * @return the group names
   */
  @WebMethod(operationName = "GetGroupNames")
  @WebResult(name = "GroupName")
  public List<String> getGroupNames(
      @WebParam(name = "UserDirectoryId") @XmlElement(required = true) UUID userDirectoryId)
      throws InvalidArgumentException, UserDirectoryNotFoundException, SecurityServiceException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    return securityService.getGroupNames(userDirectoryId);
  }

  /**
   * Retrieve the names identifying the groups the user is a member of.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   * @param username the username identifying the user
   * @return the names identifying the groups the user is a member of
   */
  @WebMethod(operationName = "GetGroupNamesForUser")
  @WebResult(name = "GroupName")
  public List<String> getGroupNamesForUser(
      @WebParam(name = "UserDirectoryId") @XmlElement(required = true) UUID userDirectoryId,
      @WebParam(name = "Username") @XmlElement(required = true) String username)
      throws InvalidArgumentException, UserDirectoryNotFoundException, UserNotFoundException,
          SecurityServiceException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    if (StringUtils.isEmpty(username)) {
      throw new InvalidArgumentException("username");
    }

    return securityService.getGroupNamesForUser(userDirectoryId, username);
  }

  /**
   * Retrieve the groups.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   * @param filter the optional filter to apply to the groups
   * @param sortDirection the optional sort direction to apply to the groups
   * @param pageIndex the optional page index
   * @param pageSize the optional page size
   * @return the groups
   */
  @WebMethod(operationName = "GetGroups")
  @WebResult(name = "Groups")
  public Groups getGroups(
      @WebParam(name = "UserDirectoryId") @XmlElement(required = true) UUID userDirectoryId,
      @WebParam(name = "Filter") @XmlElement(required = false) String filter,
      @WebParam(name = "SortDirection") @XmlElement(required = false) SortDirection sortDirection,
      @WebParam(name = "PageIndex") @XmlElement(required = false) Integer pageIndex,
      @WebParam(name = "PageSize") @XmlElement(required = false) Integer pageSize)
      throws InvalidArgumentException, UserDirectoryNotFoundException, SecurityServiceException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    return securityService.getGroups(userDirectoryId, filter, sortDirection, pageIndex, pageSize);
  }

  /**
   * Retrieve the group members.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   * @param groupName the name identifying the group
   * @param filter the optional filter to apply to the group members
   * @param sortDirection the optional sort direction to apply to the group members
   * @param pageIndex the optional page index
   * @param pageSize the optional page size
   */
  @WebMethod(operationName = "GetMembersForGroup")
  @WebResult(name = "GroupMembers")
  public GroupMembers getMembersForGroup(
      @WebParam(name = "UserDirectoryId") @XmlElement(required = true) UUID userDirectoryId,
      @WebParam(name = "GroupName") @XmlElement(required = true) String groupName,
      @WebParam(name = "Filter") @XmlElement(required = false) String filter,
      @WebParam(name = "SortDirection") @XmlElement(required = false) SortDirection sortDirection,
      @WebParam(name = "PageIndex") @XmlElement(required = false) Integer pageIndex,
      @WebParam(name = "PageSize") @XmlElement(required = false) Integer pageSize)
      throws InvalidArgumentException, UserDirectoryNotFoundException, GroupNotFoundException,
          SecurityServiceException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    if (StringUtils.isEmpty(groupName)) {
      throw new InvalidArgumentException("groupName");
    }

    return securityService.getMembersForGroup(
        userDirectoryId, groupName, filter, sortDirection, pageIndex, pageSize);
  }

  /**
   * Retrieve the organization.
   *
   * @param organizationId the Universally Unique Identifier (UUID) uniquely identifying the
   *     organization
   * @return the organization
   */
  @WebMethod(operationName = "GetOrganization")
  @WebResult(name = "Organization")
  public Organization getOrganization(
      @WebParam(name = "OrganizationId") @XmlElement(required = true) UUID organizationId)
      throws InvalidArgumentException, OrganizationNotFoundException, SecurityServiceException {
    if (organizationId == null) {
      throw new InvalidArgumentException("organizationId");
    }

    return securityService.getOrganization(organizationId);
  }

  /**
   * Retrieve the name of the organization.
   *
   * @param organizationId the Universally Unique Identifier (UUID) uniquely identifying the
   *     organization
   * @return the name of the organization
   */
  @WebMethod(operationName = "GetOrganizationName")
  @WebResult(name = "OrganizationName")
  public String getOrganizationName(
      @WebParam(name = "OrganizationId") @XmlElement(required = true) UUID organizationId)
      throws InvalidArgumentException, OrganizationNotFoundException, SecurityServiceException {
    if (organizationId == null) {
      throw new InvalidArgumentException("organizationId");
    }

    return securityService.getOrganizationName(organizationId);
  }

  /**
   * Retrieve the filtered organizations using pagination.
   *
   * @param filter the optional filter to apply to the organizations
   * @param sortDirection the optional sort direction to apply to the organizations
   * @param pageIndex the optional page index
   * @param pageSize the optional page size
   * @return the organizations
   */
  @WebMethod(operationName = "GetOrganizations")
  @WebResult(name = "Organization")
  public Organizations getOrganizations(
      @WebParam(name = "Filter") @XmlElement(required = false) String filter,
      @WebParam(name = "SortDirection") @XmlElement(required = false) SortDirection sortDirection,
      @WebParam(name = "PageIndex") @XmlElement(required = false) Integer pageIndex,
      @WebParam(name = "PageSize") @XmlElement(required = false) Integer pageSize)
      throws SecurityServiceException {
    return securityService.getOrganizations(filter, sortDirection, pageIndex, pageSize);
  }

  /**
   * Retrieve the organizations the user directory is associated with.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   * @return the organizations the user directory is associated with
   */
  @WebMethod(operationName = "GetOrganizationsForUserDirectory")
  @WebResult(name = "Organization")
  public List<Organization> getOrganizationsForUserDirectory(
      @WebParam(name = "UserDirectoryId") @XmlElement(required = true) UUID userDirectoryId)
      throws InvalidArgumentException, UserDirectoryNotFoundException, SecurityServiceException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    return securityService.getOrganizationsForUserDirectory(userDirectoryId);
  }

  /**
   * Retrieve the codes for the roles that have been assigned to the group.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   * @param groupName the name identifying the group
   * @return the codes for the roles that have been assigned to the group
   */
  @WebMethod(operationName = "GetRoleCodesForGroup")
  @WebResult(name = "RoleCode")
  public List<String> getRoleCodesForGroup(
      @WebParam(name = "UserDirectoryId") @XmlElement(required = true) UUID userDirectoryId,
      @WebParam(name = "GroupName") @XmlElement(required = true) String groupName)
      throws InvalidArgumentException, UserDirectoryNotFoundException, GroupNotFoundException,
          SecurityServiceException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    if (StringUtils.isEmpty(groupName)) {
      throw new InvalidArgumentException("groupName");
    }

    return securityService.getRoleCodesForGroup(userDirectoryId, groupName);
  }

  /**
   * Retrieve all the roles.
   *
   * @return the roles
   */
  @WebMethod(operationName = "GetRoles")
  @WebResult(name = "Role")
  public List<Role> getRoles() throws SecurityServiceException {
    return securityService.getRoles();
  }

  /**
   * Retrieve the roles that have been assigned to the group.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   * @param groupName the name identifying the group
   * @return the roles that have been assigned to the group
   */
  @WebMethod(operationName = "GetRolesForGroup")
  @WebResult(name = "GroupRole")
  public List<GroupRole> getRolesForGroup(
      @WebParam(name = "UserDirectoryId") @XmlElement(required = true) UUID userDirectoryId,
      @WebParam(name = "GroupName") @XmlElement(required = true) String groupName)
      throws InvalidArgumentException, UserDirectoryNotFoundException, GroupNotFoundException,
          SecurityServiceException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    if (StringUtils.isEmpty(groupName)) {
      throw new InvalidArgumentException("groupName");
    }

    return securityService.getRolesForGroup(userDirectoryId, groupName);
  }

  /**
   * Retrieve the user.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   * @param username the username identifying the user
   * @return the user
   */
  @WebMethod(operationName = "GetUser")
  @WebResult(name = "User")
  public User getUser(
      @WebParam(name = "UserDirectoryId") @XmlElement(required = true) UUID userDirectoryId,
      @WebParam(name = "Username") @XmlElement(required = true) String username)
      throws InvalidArgumentException, UserDirectoryNotFoundException, UserNotFoundException,
          SecurityServiceException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    if (StringUtils.isEmpty(username)) {
      throw new InvalidArgumentException("username");
    }

    User user = securityService.getUser(userDirectoryId, username);

    // Remove the password information
    user.setPassword(null);
    user.setPasswordAttempts(null);
    user.setPasswordExpiry(null);

    return user;
  }

  /**
   * Retrieve the user directories.
   *
   * @param filter the optional filter to apply to the user directories
   * @param sortDirection the optional sort direction to apply to the user directories
   * @param pageIndex the optional page index
   * @param pageSize the optional page size
   * @return the user directories
   */
  @WebMethod(operationName = "GetUserDirectories")
  @WebResult(name = "UserDirectories")
  public UserDirectories getUserDirectories(
      @WebParam(name = "Filter") @XmlElement(required = true) String filter,
      @WebParam(name = "SortDirection") @XmlElement(required = true) SortDirection sortDirection,
      @WebParam(name = "PageIndex") @XmlElement(required = true) Integer pageIndex,
      @WebParam(name = "PageSize") @XmlElement(required = true) Integer pageSize)
      throws SecurityServiceException {
    return securityService.getUserDirectories(filter, sortDirection, pageIndex, pageSize);
  }

  /**
   * Retrieve the user directories the organization is associated with.
   *
   * @param organizationId the Universally Unique Identifier (UUID) uniquely identifying the
   *     organization
   * @return the user directories the organization is associated with
   */
  @WebMethod(operationName = "GetUserDirectoriesForOrganization")
  @WebResult(name = "UserDirectory")
  public List<UserDirectory> getUserDirectoriesForOrganization(
      @WebParam(name = "OrganizationId") @XmlElement(required = true) UUID organizationId)
      throws OrganizationNotFoundException, SecurityServiceException {
    return securityService.getUserDirectoriesForOrganization(organizationId);
  }

  /**
   * Retrieve the user directory.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   * @return the user directory
   */
  @WebMethod(operationName = "GetUserDirectory")
  @WebResult(name = "UserDirectory")
  public UserDirectory getUserDirectory(
      @WebParam(name = "UserDirectoryId") @XmlElement(required = true) UUID userDirectoryId)
      throws InvalidArgumentException, UserDirectoryNotFoundException, SecurityServiceException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    return securityService.getUserDirectory(userDirectoryId);
  }

  /**
   * Retrieve the capabilities the user directory supports.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   * @return the capabilities the user directory supports
   */
  @WebMethod(operationName = "GetUserDirectoryCapabilities")
  @WebResult(name = "UserDirectoryCapabilities")
  public UserDirectoryCapabilities getUserDirectoryCapabilities(
      @WebParam(name = "UserDirectoryId") @XmlElement(required = true) UUID userDirectoryId)
      throws InvalidArgumentException, UserDirectoryNotFoundException, SecurityServiceException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    return securityService.getUserDirectoryCapabilities(userDirectoryId);
  }

  /**
   * Retrieve the name of the user directory.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   * @return the name of user directory
   */
  @WebMethod(operationName = "GetUserDirectoryName")
  @WebResult(name = "UserDirectoryName")
  public String getUserDirectoryName(
      @WebParam(name = "UserDirectoryId") @XmlElement(required = true) UUID userDirectoryId)
      throws InvalidArgumentException, UserDirectoryNotFoundException, SecurityServiceException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    return securityService.getUserDirectoryName(userDirectoryId);
  }

  /**
   * Retrieve the summaries for the user directories.
   *
   * @param filter the optional filter to apply to the user directories
   * @param sortDirection the optional sort direction to apply to the user directories
   * @param pageIndex the optional page index
   * @param pageSize the optional page size
   * @return the summaries for the user directories
   */
  @WebMethod(operationName = "GetUserDirectorySummaries")
  @WebResult(name = "UserDirectorySummaries")
  public UserDirectorySummaries getUserDirectorySummaries(
      @WebParam(name = "Filter") @XmlElement(required = true) String filter,
      @WebParam(name = "SortDirection") @XmlElement(required = true) SortDirection sortDirection,
      @WebParam(name = "PageIndex") @XmlElement(required = true) Integer pageIndex,
      @WebParam(name = "PageSize") @XmlElement(required = true) Integer pageSize)
      throws SecurityServiceException {
    return securityService.getUserDirectorySummaries(filter, sortDirection, pageIndex, pageSize);
  }

  /**
   * Retrieve the summaries for the user directories the organization is associated with.
   *
   * @param organizationId the Universally Unique Identifier (UUID) uniquely identifying the
   *     organization
   * @return the summaries for the user directories the organization is associated with
   */
  @WebMethod(operationName = "GetUserDirectorySummariesForOrganization")
  @WebResult(name = "UserDirectorySummary")
  public List<UserDirectorySummary> getUserDirectorySummariesForOrganization(
      @WebParam(name = "OrganizationId") @XmlElement(required = true) UUID organizationId)
      throws InvalidArgumentException, OrganizationNotFoundException, SecurityServiceException {
    if (organizationId == null) {
      throw new InvalidArgumentException("organizationId");
    }

    return securityService.getUserDirectorySummariesForOrganization(organizationId);
  }

  /**
   * Retrieve the user directory type for the user directory.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   * @return the user directory type for the user directory
   */
  @WebMethod(operationName = "GetUserDirectoryTypeForUserDirectory")
  @WebResult(name = "UserDirectoryType")
  public UserDirectoryType getUserDirectoryTypeForUserDirectory(
      @WebParam(name = "UserDirectoryId") @XmlElement(required = true) UUID userDirectoryId)
      throws InvalidArgumentException, UserDirectoryNotFoundException,
          UserDirectoryTypeNotFoundException, SecurityServiceException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    return securityService.getUserDirectoryTypeForUserDirectory(userDirectoryId);
  }

  /**
   * Retrieve the user directory types.
   *
   * @return the user directory types
   */
  @WebMethod(operationName = "GetUserDirectoryTypes")
  @WebResult(name = "UserDirectoryType")
  public List<UserDirectoryType> getUserDirectoryTypes() throws SecurityServiceException {
    return securityService.getUserDirectoryTypes();
  }

  /**
   * Retrieve the full name for the user.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   * @param username the username identifying the user
   * @return the full name for the user
   */
  @WebMethod(operationName = "GetUserFullName")
  @WebResult(name = "UserFullName")
  public String getUserFullName(
      @WebParam(name = "UserDirectoryId") @XmlElement(required = true) UUID userDirectoryId,
      @WebParam(name = "Username") @XmlElement(required = true) String username)
      throws InvalidArgumentException, UserDirectoryNotFoundException, UserNotFoundException,
          SecurityServiceException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    if (StringUtils.isEmpty(username)) {
      throw new InvalidArgumentException("username");
    }

    return securityService.getUserFullName(userDirectoryId, username);
  }

  /**
   * Retrieve the users.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   * @param filter the optional filter to apply to the users
   * @param sortBy The optional method used to sort the users e.g. by last name.
   * @param sortDirection the optional sort direction to apply to the users
   * @param pageIndex the optional page index
   * @param pageSize the optional page size
   * @return the users
   */
  @WebMethod(operationName = "GetUsers")
  @WebResult(name = "Users")
  public Users getUsers(
      @WebParam(name = "UserDirectoryId") @XmlElement(required = true) UUID userDirectoryId,
      @WebParam(name = "Filter") @XmlElement(required = false) String filter,
      @WebParam(name = "SortBy") @XmlElement(required = false) UserSortBy sortBy,
      @WebParam(name = "SortDirection") @XmlElement(required = false) SortDirection sortDirection,
      @WebParam(name = "PageIndex") @XmlElement(required = false) Integer pageIndex,
      @WebParam(name = "PageSize") @XmlElement(required = false) Integer pageSize)
      throws InvalidArgumentException, UserDirectoryNotFoundException, SecurityServiceException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    return securityService.getUsers(
        userDirectoryId, filter, sortBy, sortDirection, pageIndex, pageSize);
  }

  /**
   * Remove the group member from the group.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   * @param groupName the name identifying the group
   * @param memberType the group member type
   * @param memberName the name identifying the group member
   */
  @WebMethod(operationName = "RemoveMemberFromGroup")
  public void removeMemberFromGroup(
      @WebParam(name = "UserDirectoryId") @XmlElement(required = true) UUID userDirectoryId,
      @WebParam(name = "GroupName") @XmlElement(required = true) String groupName,
      @WebParam(name = "MemberType") @XmlElement(required = true) GroupMemberType memberType,
      @WebParam(name = "MemberName") @XmlElement(required = true) String memberName)
      throws InvalidArgumentException, UserDirectoryNotFoundException, GroupNotFoundException,
          GroupMemberNotFoundException, SecurityServiceException {
    validateGroupMember(userDirectoryId, groupName, memberType, memberName);

    securityService.removeMemberFromGroup(userDirectoryId, groupName, memberType, memberName);
  }

  /**
   * Remove the role from the group.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   * @param groupName the name identifying the group
   * @param roleCode the code uniquely identifying the role
   */
  @WebMethod(operationName = "RemoveRoleFromGroup")
  public void removeRoleFromGroup(
      @WebParam(name = "UserDirectoryId") @XmlElement(required = true) UUID userDirectoryId,
      @WebParam(name = "GroupName") @XmlElement(required = true) String groupName,
      @WebParam(name = "RoleCode") @XmlElement(required = true) String roleCode)
      throws InvalidArgumentException, UserDirectoryNotFoundException, GroupNotFoundException,
          GroupRoleNotFoundException, SecurityServiceException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    if (StringUtils.isEmpty(groupName)) {
      throw new InvalidArgumentException("groupName");
    }

    if (StringUtils.isEmpty(roleCode)) {
      throw new InvalidArgumentException("roleCode");
    }

    securityService.removeRoleFromGroup(userDirectoryId, groupName, roleCode);
  }

  /**
   * Remove the user directory from the organization.
   *
   * @param organizationId the Universally Unique Identifier (UUID) uniquely identifying the
   *     organization
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   */
  @WebMethod(operationName = "RemoveUserDirectoryFromOrganization")
  public void removeUserDirectoryFromOrganization(
      @WebParam(name = "OrganizationId") @XmlElement(required = true) UUID organizationId,
      @WebParam(name = "UserDirectoryId") @XmlElement(required = true) UUID userDirectoryId)
      throws InvalidArgumentException, OrganizationNotFoundException,
          OrganizationUserDirectoryNotFoundException, SecurityServiceException {
    if (organizationId == null) {
      throw new InvalidArgumentException("organizationId");
    }

    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    securityService.removeUserDirectoryFromOrganization(organizationId, userDirectoryId);
  }

  /**
   * Initiate the password reset process for the user.
   *
   * @param username the username identifying the user
   * @param resetPasswordUrl the reset password URL
   */
  @WebMethod(operationName = "ResetPassword")
  public void resetPassword(
      @WebParam(name = "Username") @XmlElement(required = true) String username,
      @WebParam(name = "ResetPasswordUrl") @XmlElement(required = true) String resetPasswordUrl)
      throws InvalidArgumentException, UserNotFoundException, SecurityServiceException {
    if (StringUtils.isEmpty(username)) {
      throw new InvalidArgumentException("username");
    }

    if (StringUtils.isEmpty(resetPasswordUrl)) {
      throw new InvalidArgumentException("resetPasswordUrl");
    }

    securityService.initiatePasswordReset(username, resetPasswordUrl, true);
  }

  /**
   * Update the group.
   *
   * @param group the group
   */
  @WebMethod(operationName = "UpdateGroup")
  public void updateGroup(@WebParam(name = "Group") @XmlElement(required = true) Group group)
      throws InvalidArgumentException, UserDirectoryNotFoundException, GroupNotFoundException,
          SecurityServiceException {
    validateGroup(group);

    securityService.updateGroup(group);
  }

  /**
   * Update the organization.
   *
   * @param organization the organization
   */
  @WebMethod(operationName = "UpdateOrganization")
  public void updateOrganization(
      @WebParam(name = "Organization") @XmlElement(required = true) Organization organization)
      throws InvalidArgumentException, OrganizationNotFoundException, SecurityServiceException {
    validateOrganization(organization);

    securityService.updateOrganization(organization);
  }

  /**
   * Update the user.
   *
   * @param user the user
   * @param expirePassword expire the user's password
   * @param lockUser lock the user
   */
  @WebMethod(operationName = "UpdateUser")
  public void updateUser(
      @WebParam(name = "User") @XmlElement(required = true) User user,
      @WebParam(name = "ExpirePassword") @XmlElement(required = true) boolean expirePassword,
      @WebParam(name = "LockUser") @XmlElement(required = true) boolean lockUser)
      throws InvalidArgumentException, UserDirectoryNotFoundException, UserNotFoundException,
          SecurityServiceException {
    validateUser(user);

    securityService.updateUser(user, expirePassword, lockUser);
  }

  /**
   * Update the user directory.
   *
   * @param userDirectory the user directory
   */
  @WebMethod(operationName = "UpdateUserDirectory")
  public void updateUserDirectory(
      @WebParam(name = "UserDirectory") @XmlElement(required = true) UserDirectory userDirectory)
      throws InvalidArgumentException, UserDirectoryNotFoundException, SecurityServiceException {
    validateUserDirectory(userDirectory);

    securityService.updateUserDirectory(userDirectory);
  }

  private void validateGroup(Group group) throws InvalidArgumentException {
    if (group == null) {
      throw new InvalidArgumentException("group");
    }

    Set<ConstraintViolation<Group>> constraintViolations = validator.validate(group);

    if (!constraintViolations.isEmpty()) {
      throw new InvalidArgumentException(
          "group", ValidationError.toValidationErrors(constraintViolations));
    }
  }

  private void validateGroupMember(
      UUID userDirectoryId, String groupName, GroupMemberType memberType, String memberName)
      throws InvalidArgumentException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    if (StringUtils.isEmpty(groupName)) {
      throw new InvalidArgumentException("groupName");
    }

    if (memberType == null) {
      throw new InvalidArgumentException("memberType");
    }

    if (StringUtils.isEmpty(memberName)) {
      throw new InvalidArgumentException("memberName");
    }
  }

  private void validateOrganization(Organization organization) throws InvalidArgumentException {
    if (organization == null) {
      throw new InvalidArgumentException("organization");
    }

    Set<ConstraintViolation<Organization>> constraintViolations = validator.validate(organization);

    if (!constraintViolations.isEmpty()) {
      throw new InvalidArgumentException(
          "organization", ValidationError.toValidationErrors(constraintViolations));
    }
  }

  private void validateUser(User user) throws InvalidArgumentException {
    if (user == null) {
      throw new InvalidArgumentException("user");
    }

    Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);

    if (!constraintViolations.isEmpty()) {
      throw new InvalidArgumentException(
          "user", ValidationError.toValidationErrors(constraintViolations));
    }
  }

  private void validateUserDirectory(UserDirectory userDirectory) throws InvalidArgumentException {
    if (userDirectory == null) {
      throw new InvalidArgumentException("userDirectory");
    }

    Set<ConstraintViolation<UserDirectory>> constraintViolations =
        validator.validate(userDirectory);

    if (!constraintViolations.isEmpty()) {
      throw new InvalidArgumentException(
          "userDirectory", ValidationError.toValidationErrors(constraintViolations));
    }
  }
}
