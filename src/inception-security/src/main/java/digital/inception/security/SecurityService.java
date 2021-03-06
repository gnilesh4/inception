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

import digital.inception.core.sorting.SortDirection;
import digital.inception.core.util.PasswordUtil;
import digital.inception.core.util.RandomStringGenerator;
import digital.inception.core.util.ResourceUtil;
import digital.inception.mail.IMailService;
import digital.inception.mail.MailTemplate;
import digital.inception.mail.MailTemplateContentType;
import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriUtils;

// ~--- JDK imports ------------------------------------------------------------

/**
 * The <code>SecurityService</code> class provides the Security Service implementation.
 *
 * @author Marcus Portmann
 */
@Service
@SuppressWarnings({"unused", "WeakerAccess"})
public class SecurityService implements ISecurityService, InitializingBean {

  /** The Universally Unique Identifier (UUID) uniquely identifying the Administration tenant. */
  public static final UUID ADMINISTRATION_TENANT_ID =
      UUID.fromString("00000000-0000-0000-0000-000000000000");

  /**
   * The Universally Unique Identifier (UUID) uniquely identifying the Administration user
   * directory.
   */
  public static final UUID ADMINISTRATION_USER_DIRECTORY_ID =
      UUID.fromString("00000000-0000-0000-0000-000000000000");

  /** The Universally Unique Identifier (UUID) uniquely identifying the Administrators group. */
  public static final UUID ADMINISTRATORS_GROUP_ID =
      UUID.fromString("00000000-0000-0000-0000-000000000000");

  /** The name of the Administrators group. */
  public static final String ADMINISTRATORS_GROUP_NAME = "Administrators";

  /** The code uniquely identifying the Administrator role. */
  public static final String ADMINISTRATOR_ROLE_CODE = "Administrator";

  /** The username for the Administrator user. */
  public static final String ADMINISTRATOR_USERNAME = "Administrator";

  /** The code uniquely identifying the internal user directory type. */
  public static final String INTERNAL_USER_DIRECTORY_TYPE = "InternalUserDirectory";

  /** The code uniquely identifying the LDAP user directory type. */
  public static final String LDAP_USER_DIRECTORY_TYPE = "LDAPUserDirectory";

  /** The code uniquely identifying the Password Resetter role. */
  public static final String PASSWORD_RESETTER_ROLE_CODE = "PasswordResetter";

  /** The code uniquely identifying the Tenant Administrator role. */
  public static final String TENANT_ADMINISTRATOR_ROLE_CODE = "TenantAdministrator";

  /** The maximum number of filtered tenants. */
  private static final int MAX_FILTERED_ORGANISATIONS = 100;

  /** The maximum number of filtered user directories. */
  private static final int MAX_FILTERED_USER_DIRECTORIES = 100;

  /**
   * The Universally Unique Identifier (UUID) uniquely identifying the password reset mail template.
   */
  private static final String PASSWORD_RESET_MAIL_TEMPLATE_ID =
      "Inception.Security.PasswordResetMail";

  /* Logger */
  private static final Logger logger = LoggerFactory.getLogger(SecurityService.class);

  /** The Spring application context. */
  private final ApplicationContext applicationContext;

  /** The Function Repository. */
  private final FunctionRepository functionRepository;

  /** The Group repository. */
  private final GroupRepository groupRepository;

  /** The Mail Service. */
  private final IMailService mailService;

  /** The Password Reset Repository. */
  private final PasswordResetRepository passwordResetRepository;

  /** The Role Repository. */
  private final RoleRepository roleRepository;

  /**
   * The random alphanumeric string generator that will be used to generate security codes for
   * password resets.
   */
  private final RandomStringGenerator securityCodeGenerator =
      new RandomStringGenerator(
          20, new SecureRandom(), "1234567890ACEFGHJKLMNPQRUVWXYabcdefhijkprstuvwx");

  /** The Tenant Repository. */
  private final TenantRepository tenantRepository;

  /** The User Directory Repository. */
  private final UserDirectoryRepository userDirectoryRepository;

  /** The User Directory Summary Repository. */
  private final UserDirectorySummaryRepository userDirectorySummaryRepository;

  /** The User Directory Type Repository. */
  private final UserDirectoryTypeRepository userDirectoryTypeRepository;

  /** The User Repository. */
  private final UserRepository userRepository;

  /** The user directories. */
  private Map<UUID, IUserDirectory> userDirectories = new ConcurrentHashMap<>();

  /**
   * Constructs a new <code>SecurityService</code>.
   *
   * @param applicationContext the Spring application context
   * @param mailService the Mail Service
   * @param functionRepository the Function Repository
   * @param groupRepository the Group Repository
   * @param tenantRepository the Tenant Repository
   * @param passwordResetRepository the Password Reset Repository
   * @param roleRepository the Role Repository
   * @param userDirectoryRepository the User Directory Repository
   * @param userDirectorySummaryRepository the User Directory Summary Repository
   * @param userDirectoryTypeRepository the User Directory Type Repository
   * @param userRepository the User Repository
   */
  public SecurityService(
      ApplicationContext applicationContext,
      IMailService mailService,
      FunctionRepository functionRepository,
      GroupRepository groupRepository,
      TenantRepository tenantRepository,
      PasswordResetRepository passwordResetRepository,
      RoleRepository roleRepository,
      UserDirectoryRepository userDirectoryRepository,
      UserDirectorySummaryRepository userDirectorySummaryRepository,
      UserDirectoryTypeRepository userDirectoryTypeRepository,
      UserRepository userRepository) {
    this.applicationContext = applicationContext;
    this.mailService = mailService;
    this.functionRepository = functionRepository;
    this.groupRepository = groupRepository;
    this.tenantRepository = tenantRepository;
    this.passwordResetRepository = passwordResetRepository;
    this.roleRepository = roleRepository;
    this.userDirectoryRepository = userDirectoryRepository;
    this.userDirectorySummaryRepository = userDirectorySummaryRepository;
    this.userDirectoryTypeRepository = userDirectoryTypeRepository;
    this.userRepository = userRepository;
  }

  /**
   * Add the group member to the group.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   * @param groupName the name identifying the group
   * @param memberType the group member type
   * @param memberName the group member name
   */
  @Override
  @Transactional
  public void addMemberToGroup(
      UUID userDirectoryId, String groupName, GroupMemberType memberType, String memberName)
      throws UserDirectoryNotFoundException, GroupNotFoundException, UserNotFoundException,
          ExistingGroupMemberException, SecurityServiceException {
    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    userDirectory.addMemberToGroup(groupName, memberType, memberName);
  }

  /**
   * Add the role to the group.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   * @param groupName the name identifying the group
   * @param roleCode the code uniquely identifying the role
   */
  @Override
  @Transactional
  public void addRoleToGroup(UUID userDirectoryId, String groupName, String roleCode)
      throws UserDirectoryNotFoundException, GroupNotFoundException, RoleNotFoundException,
          ExistingGroupRoleException, SecurityServiceException {
    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    userDirectory.addRoleToGroup(groupName, roleCode);
  }

  /**
   * Add the user directory to the tenant.
   *
   * @param tenantId the Universally Unique Identifier (UUID) uniquely identifying the tenant
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   */
  @Override
  @Transactional
  public void addUserDirectoryToTenant(UUID tenantId, UUID userDirectoryId)
      throws TenantNotFoundException, UserDirectoryNotFoundException,
          ExistingTenantUserDirectoryException, SecurityServiceException {
    try {
      if (!tenantRepository.existsById(tenantId)) {
        throw new TenantNotFoundException(tenantId);
      }

      if (!userDirectoryRepository.existsById(userDirectoryId)) {
        throw new UserDirectoryNotFoundException(userDirectoryId);
      }

      if (tenantRepository.countTenantUserDirectory(tenantId, userDirectoryId) > 0) {
        throw new ExistingTenantUserDirectoryException(tenantId, userDirectoryId);
      }

      tenantRepository.addUserDirectoryToTenant(tenantId, userDirectoryId);
    } catch (TenantNotFoundException
        | UserDirectoryNotFoundException
        | ExistingTenantUserDirectoryException e) {
      throw e;
    } catch (Throwable e) {
      throw new SecurityServiceException(
          "Failed to add the user directory ("
              + userDirectoryId
              + ") to the tenant ("
              + tenantId
              + ")",
          e);
    }
  }

  /**
   * Add the user to the group.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   * @param groupName the name identifying the group
   * @param username the username identifying the user
   */
  @Override
  @Transactional
  public void addUserToGroup(UUID userDirectoryId, String groupName, String username)
      throws UserDirectoryNotFoundException, GroupNotFoundException, UserNotFoundException,
          SecurityServiceException {
    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    userDirectory.addUserToGroup(groupName, username);
  }

  /**
   * Administratively change the password for the user.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   * @param username the username identifying the user
   * @param newPassword the new password
   * @param expirePassword expire the user's password
   * @param lockUser lock the user
   * @param resetPasswordHistory reset the user's password history
   * @param reason the reason for changing the password
   */
  @Override
  @Transactional
  public void adminChangePassword(
      UUID userDirectoryId,
      String username,
      String newPassword,
      boolean expirePassword,
      boolean lockUser,
      boolean resetPasswordHistory,
      PasswordChangeReason reason)
      throws UserDirectoryNotFoundException, UserNotFoundException, SecurityServiceException {
    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    userDirectory.adminChangePassword(
        username, newPassword, expirePassword, lockUser, resetPasswordHistory, reason);
  }

  /** Initialize the Security Service. */
  @Override
  public void afterPropertiesSet() {
    try {
      // Load the default password reset mail template
      if (!mailService.mailTemplateExists(PASSWORD_RESET_MAIL_TEMPLATE_ID)) {
        byte[] passwordResetMailTemplate =
            ResourceUtil.getClasspathResource("digital/inception/security/PasswordReset.ftl");

        MailTemplate mailTemplate =
            new MailTemplate(
                PASSWORD_RESET_MAIL_TEMPLATE_ID,
                "Password Reset",
                MailTemplateContentType.HTML,
                passwordResetMailTemplate);

        mailService.createMailTemplate(mailTemplate);
      }

      // Load the user directories
      reloadUserDirectories();
    } catch (Throwable e) {
      throw new RuntimeException("Failed to initialize the Security Service", e);
    }
  }

  /**
   * Authenticate the user.
   *
   * @param username the username identifying the user
   * @param password the password being used to authenticate
   * @return the Universally Unique Identifier (UUID) uniquely identifying the user directory
   */
  @Override
  @Transactional
  public UUID authenticate(String username, String password)
      throws AuthenticationFailedException, UserLockedException, ExpiredPasswordException,
          UserNotFoundException, SecurityServiceException {
    try {
      // First check if this is an internal user and if so determine the user directory ID
      UUID internalUserDirectoryId = getInternalUserDirectoryIdForUser(username);

      if (internalUserDirectoryId != null) {
        IUserDirectory internalUserDirectory = userDirectories.get(internalUserDirectoryId);

        if (internalUserDirectory == null) {
          throw new SecurityServiceException(
              "The user directory ID ("
                  + internalUserDirectoryId
                  + ") for the internal user ("
                  + username
                  + ") is invalid");
        } else {
          internalUserDirectory.authenticate(username, password);

          return internalUserDirectoryId;
        }
      } else {
        /*
         * Check all of the "external" user directories to see if one of them can authenticate this
         * user.
         */
        for (UUID userDirectoryId : userDirectories.keySet()) {
          IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

          if (userDirectory != null) {
            if (!(userDirectory instanceof InternalUserDirectory)) {
              if (userDirectory.isExistingUser(username)) {
                userDirectory.authenticate(username, password);

                return userDirectoryId;
              }
            }
          }
        }

        throw new UserNotFoundException(username);
      }
    } catch (AuthenticationFailedException
        | UserNotFoundException
        | UserLockedException
        | ExpiredPasswordException e) {
      throw e;
    } catch (Throwable e) {
      throw new SecurityServiceException("Failed to authenticate the user (" + username + ")", e);
    }
  }

  /**
   * Change the password for the user.
   *
   * @param username the username identifying the user
   * @param password the password for the user that is used to authorise the operation
   * @param newPassword the new password
   * @return the Universally Unique Identifier (UUID) uniquely identifying the user directory
   */
  @Override
  @Transactional
  public UUID changePassword(String username, String password, String newPassword)
      throws AuthenticationFailedException, UserLockedException, UserNotFoundException,
          ExistingPasswordException, SecurityServiceException {
    try {
      // First check if this is an internal user and if so determine the user directory ID
      UUID internalUserDirectoryId = getInternalUserDirectoryIdForUser(username);

      if (internalUserDirectoryId != null) {
        IUserDirectory internalUserDirectory = userDirectories.get(internalUserDirectoryId);

        if (internalUserDirectory == null) {
          throw new SecurityServiceException(
              "The user directory ID ("
                  + internalUserDirectoryId
                  + ") for the internal user ("
                  + username
                  + ") is invalid");
        } else {
          internalUserDirectory.changePassword(username, password, newPassword);

          return internalUserDirectoryId;
        }
      } else {
        /*
         * Check all of the "external" user directories to see if one of them can change the
         * password for this user.
         */
        for (UUID userDirectoryId : userDirectories.keySet()) {
          IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

          if (userDirectory != null) {
            if (!(userDirectory instanceof InternalUserDirectory)) {
              if (userDirectory.isExistingUser(username)) {
                userDirectory.changePassword(username, password, newPassword);

                return userDirectoryId;
              }
            }
          }
        }

        throw new UserNotFoundException(username);
      }
    } catch (AuthenticationFailedException
        | UserNotFoundException
        | UserLockedException
        | ExistingPasswordException e) {
      throw e;
    } catch (Throwable e) {
      throw new SecurityServiceException(
          "Failed to change the password for the user (" + username + ")", e);
    }
  }

  /**
   * Create the new authorised function.
   *
   * @param function the function
   */
  @Override
  @Transactional
  public void createFunction(Function function)
      throws DuplicateFunctionException, SecurityServiceException {
    try {
      if (functionRepository.existsById(function.getCode())) {
        throw new DuplicateFunctionException(function.getCode());
      }

      functionRepository.saveAndFlush(function);
    } catch (DuplicateFunctionException e) {
      throw e;
    } catch (Throwable e) {
      throw new SecurityServiceException(
          "Failed to create the function (" + function.getCode() + ")", e);
    }
  }

  /**
   * Create the new group.
   *
   * @param group the group
   */
  @Override
  @Transactional
  public void createGroup(Group group)
      throws UserDirectoryNotFoundException, DuplicateGroupException, SecurityServiceException {
    IUserDirectory userDirectory = userDirectories.get(group.getUserDirectoryId());

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(group.getUserDirectoryId());
    }

    userDirectory.createGroup(group);
  }

  /**
   * Create the new tenant.
   *
   * @param tenant the tenant
   * @param createUserDirectory should a new internal user directory be created for the tenant
   * @return the new internal user directory that was created for the tenant or <code>null
   * </code> if no user directory was created
   */
  @Override
  @Transactional
  public UserDirectory createTenant(Tenant tenant, boolean createUserDirectory)
      throws DuplicateTenantException, SecurityServiceException {
    UserDirectory userDirectory = null;

    try {
      if ((tenant.getId() != null) && tenantRepository.existsById(tenant.getId())) {
        throw new DuplicateTenantException(tenant.getId());
      }

      if (tenantRepository.existsByNameIgnoreCase(tenant.getName())) {
        throw new DuplicateTenantException(tenant.getName());
      }

      if (createUserDirectory) {
        userDirectory = newInternalUserDirectoryForTenant(tenant);

        tenant.linkUserDirectory(userDirectory);
      }

      tenantRepository.saveAndFlush(tenant);

      try {
        reloadUserDirectories();
      } catch (Throwable e) {
        logger.error("Failed to reload the user directories", e);
      }

      return userDirectory;
    } catch (DuplicateTenantException e) {
      throw e;
    } catch (Throwable e) {
      throw new SecurityServiceException("Failed to create the tenant (" + tenant.getId() + ")", e);
    }
  }

  /**
   * Create the new user.
   *
   * @param user the user
   * @param expiredPassword create the user with its password expired
   * @param userLocked create the user locked
   */
  @Override
  @Transactional
  public void createUser(User user, boolean expiredPassword, boolean userLocked)
      throws UserDirectoryNotFoundException, DuplicateUserException, SecurityServiceException {
    IUserDirectory userDirectory = userDirectories.get(user.getUserDirectoryId());

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(user.getUserDirectoryId());
    }

    if (getUserDirectoryIdForUser(user.getUsername()) != null) {
      throw new DuplicateUserException(user.getUsername());
    }

    userDirectory.createUser(user, expiredPassword, userLocked);
  }

  /**
   * Create the new user directory.
   *
   * @param userDirectory the user directory
   */
  @Override
  @Transactional
  public void createUserDirectory(UserDirectory userDirectory)
      throws DuplicateUserDirectoryException, SecurityServiceException {
    try {
      if ((userDirectory.getId() != null)
          && userDirectoryRepository.existsById(userDirectory.getId())) {
        throw new DuplicateUserDirectoryException(userDirectory.getId());
      }

      if (userDirectoryRepository.existsByNameIgnoreCase(userDirectory.getName())) {
        throw new DuplicateUserDirectoryException(userDirectory.getName());
      }

      userDirectoryRepository.saveAndFlush(userDirectory);

      try {
        reloadUserDirectories();
      } catch (Throwable e) {
        logger.error("Failed to reload the user directories", e);
      }
    } catch (DuplicateUserDirectoryException e) {
      throw e;
    } catch (Throwable e) {
      throw new SecurityServiceException(
          "Failed to create the user directory (" + userDirectory.getName() + ")", e);
    }
  }

  /**
   * Delete the authorised function.
   *
   * @param functionCode the code uniquely identifying the function
   */
  @Override
  @Transactional
  public void deleteFunction(String functionCode)
      throws FunctionNotFoundException, SecurityServiceException {
    try {
      if (!functionRepository.existsById(functionCode)) {
        throw new FunctionNotFoundException(functionCode);
      }

      functionRepository.deleteById(functionCode);
    } catch (FunctionNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new SecurityServiceException("Failed to delete the function (" + functionCode + ")", e);
    }
  }

  /**
   * Delete the group.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   * @param groupName the name identifying the group
   */
  @Override
  @Transactional
  public void deleteGroup(UUID userDirectoryId, String groupName)
      throws UserDirectoryNotFoundException, GroupNotFoundException, ExistingGroupMembersException,
          SecurityServiceException {
    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    userDirectory.deleteGroup(groupName);
  }

  /**
   * Delete the tenant.
   *
   * @param tenantId the Universally Unique Identifier (UUID) uniquely identifying the tenant
   */
  @Override
  @Transactional
  public void deleteTenant(UUID tenantId) throws TenantNotFoundException, SecurityServiceException {
    try {
      if (!tenantRepository.existsById(tenantId)) {
        throw new TenantNotFoundException(tenantId);
      }

      tenantRepository.deleteById(tenantId);
    } catch (TenantNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new SecurityServiceException("Failed to delete the tenant (" + tenantId + ")", e);
    }
  }

  /**
   * Delete the user.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   * @param username the username identifying the user
   */
  @Override
  @Transactional
  public void deleteUser(UUID userDirectoryId, String username)
      throws UserDirectoryNotFoundException, UserNotFoundException, SecurityServiceException {
    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    userDirectory.deleteUser(username);
  }

  /**
   * Delete the user directory.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   */
  @Override
  @Transactional
  public void deleteUserDirectory(UUID userDirectoryId)
      throws UserDirectoryNotFoundException, SecurityServiceException {
    try {
      if (!userDirectoryRepository.existsById(userDirectoryId)) {
        throw new UserDirectoryNotFoundException(userDirectoryId);
      }

      userDirectoryRepository.deleteById(userDirectoryId);

      try {
        reloadUserDirectories();
      } catch (Throwable e) {
        logger.error("Failed to reload the user directories", e);
      }
    } catch (UserDirectoryNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new SecurityServiceException(
          "Failed to delete the user directory (" + userDirectoryId + ")", e);
    }
  }

  /**
   * Retrieve the users matching the attribute criteria.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   * @param attributes the attribute criteria used to select the users
   * @return the users whose attributes match the attribute criteria
   */
  @Override
  public List<User> findUsers(UUID userDirectoryId, List<Attribute> attributes)
      throws UserDirectoryNotFoundException, InvalidAttributeException, SecurityServiceException {
    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    return userDirectory.findUsers(attributes);
  }

  /**
   * Retrieve the authorised function.
   *
   * @param functionCode the code uniquely identifying the function
   * @return the authorised function
   */
  @Override
  public Function getFunction(String functionCode)
      throws FunctionNotFoundException, SecurityServiceException {
    try {
      Optional<Function> functionOptional = functionRepository.findById(functionCode);

      if (functionOptional.isPresent()) {
        return functionOptional.get();
      } else {
        throw new FunctionNotFoundException(functionCode);
      }
    } catch (FunctionNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new SecurityServiceException(
          "Failed to retrieve the function (" + functionCode + ")", e);
    }
  }

  /**
   * Retrieve the authorised function codes for the user.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   * @param username the username identifying the user
   * @return the authorised function codes for the user
   */
  @Override
  public List<String> getFunctionCodesForUser(UUID userDirectoryId, String username)
      throws UserDirectoryNotFoundException, UserNotFoundException, SecurityServiceException {
    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    return userDirectory.getFunctionCodesForUser(username);
  }

  /**
   * Retrieve all the authorised functions.
   *
   * @return the authorised functions
   */
  @Override
  public List<Function> getFunctions() throws SecurityServiceException {
    try {
      return functionRepository.findAll();
    } catch (Throwable e) {
      throw new SecurityServiceException("Failed to retrieve the functions", e);
    }
  }

  /**
   * Retrieve the group.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   * @param groupName the name identifying the group
   * @return the group
   */
  @Override
  public Group getGroup(UUID userDirectoryId, String groupName)
      throws UserDirectoryNotFoundException, GroupNotFoundException, SecurityServiceException {
    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    return userDirectory.getGroup(groupName);
  }

  /**
   * Retrieve all the group names.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   * @return the group names
   */
  @Override
  public List<String> getGroupNames(UUID userDirectoryId)
      throws UserDirectoryNotFoundException, SecurityServiceException {
    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    return userDirectory.getGroupNames();
  }

  /**
   * Retrieve the names identifying the groups the user is a member of.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   * @param username the username identifying the user
   * @return the names identifying the groups the user is a member of
   */
  @Override
  public List<String> getGroupNamesForUser(UUID userDirectoryId, String username)
      throws UserDirectoryNotFoundException, UserNotFoundException, SecurityServiceException {
    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    return userDirectory.getGroupNamesForUser(username);
  }

  /**
   * Retrieve all the groups.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   * @return the groups
   */
  @Override
  public List<Group> getGroups(UUID userDirectoryId)
      throws UserDirectoryNotFoundException, SecurityServiceException {
    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    return userDirectory.getGroups();
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
  @Override
  public Groups getGroups(
      UUID userDirectoryId,
      String filter,
      SortDirection sortDirection,
      Integer pageIndex,
      Integer pageSize)
      throws UserDirectoryNotFoundException, SecurityServiceException {
    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    return userDirectory.getGroups(filter, sortDirection, pageIndex, pageSize);
  }

  /**
   * Retrieve the groups the user is a member of.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   * @param username the username identifying the user
   * @return the groups the user is a member of
   */
  @Override
  public List<Group> getGroupsForUser(UUID userDirectoryId, String username)
      throws UserDirectoryNotFoundException, UserNotFoundException, SecurityServiceException {
    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    return userDirectory.getGroupsForUser(username);
  }

  /**
   * Retrieve the group members for the group.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   * @param groupName the name identifying the group
   * @return the group members for the group
   */
  @Override
  public List<GroupMember> getMembersForGroup(UUID userDirectoryId, String groupName)
      throws UserDirectoryNotFoundException, GroupNotFoundException, SecurityServiceException {
    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    return userDirectory.getMembersForGroup(groupName);
  }

  /**
   * Retrieve the group members for the group.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   * @param groupName the name identifying the group
   * @param filter the optional filter to apply to the group members
   * @param sortDirection the optional sort direction to apply to the group members
   * @param pageIndex the optional page index
   * @param pageSize the optional page size
   * @return the group members for the group
   */
  @Override
  @Transactional
  public GroupMembers getMembersForGroup(
      UUID userDirectoryId,
      String groupName,
      String filter,
      SortDirection sortDirection,
      Integer pageIndex,
      Integer pageSize)
      throws UserDirectoryNotFoundException, GroupNotFoundException, SecurityServiceException {
    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    return userDirectory.getMembersForGroup(groupName, filter, sortDirection, pageIndex, pageSize);
  }

  /**
   * Retrieve the codes for the roles that have been assigned to the group.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   * @param groupName the name identifying the group
   * @return the codes for the roles that have been assigned to the group
   */
  @Override
  public List<String> getRoleCodesForGroup(UUID userDirectoryId, String groupName)
      throws UserDirectoryNotFoundException, GroupNotFoundException, SecurityServiceException {
    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    return userDirectory.getRoleCodesForGroup(groupName);
  }

  /**
   * Retrieve the codes for the roles that the user has been assigned.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   * @param username the username identifying the user
   * @return the codes for the roles that the user has been assigned
   */
  @Override
  public List<String> getRoleCodesForUser(UUID userDirectoryId, String username)
      throws UserDirectoryNotFoundException, UserNotFoundException, SecurityServiceException {
    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    return userDirectory.getRoleCodesForUser(username);
  }

  /**
   * Retrieve all the roles.
   *
   * @return the roles
   */
  @Override
  public List<Role> getRoles() throws SecurityServiceException {
    try {
      return roleRepository.findAll();
    } catch (Throwable e) {
      throw new SecurityServiceException("Failed to retrieve the roles", e);
    }
  }

  /**
   * Retrieve the roles that have been assigned to the group.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   * @param groupName the name identifying the group
   * @return the roles that have been assigned to the group
   */
  @Override
  public List<GroupRole> getRolesForGroup(UUID userDirectoryId, String groupName)
      throws UserDirectoryNotFoundException, GroupNotFoundException, SecurityServiceException {
    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    return userDirectory.getRolesForGroup(groupName);
  }

  /**
   * Retrieve the tenant.
   *
   * @param tenantId the Universally Unique Identifier (UUID) uniquely identifying the tenant
   * @return the tenant
   */
  @Override
  public Tenant getTenant(UUID tenantId) throws TenantNotFoundException, SecurityServiceException {
    try {
      Optional<Tenant> tenantOptional = tenantRepository.findById(tenantId);

      if (tenantOptional.isPresent()) {
        return tenantOptional.get();
      } else {
        throw new TenantNotFoundException(tenantId);
      }
    } catch (TenantNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new SecurityServiceException("Failed to retrieve the tenant (" + tenantId + ")", e);
    }
  }

  /**
   * Retrieve the Universally Unique Identifiers (UUIDs) uniquely identifying the tenants the user
   * directory is associated with.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   * @return the Universally Unique Identifiers (UUIDs) uniquely identifying the tenants the user
   *     directory is associated with
   */
  @Override
  public List<UUID> getTenantIdsForUserDirectory(UUID userDirectoryId)
      throws UserDirectoryNotFoundException, SecurityServiceException {
    try {
      if (!userDirectoryRepository.existsById(userDirectoryId)) {
        throw new UserDirectoryNotFoundException(userDirectoryId);
      }

      return userDirectoryRepository.getTenantIdsById(userDirectoryId);
    } catch (UserDirectoryNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new SecurityServiceException(
          "Failed to retrieve the IDs for the tenants for the user directory ("
              + userDirectoryId
              + ")",
          e);
    }
  }

  /**
   * Retrieve the name of the tenant.
   *
   * @param tenantId the Universally Unique Identifier (UUID) uniquely identifying the tenant
   * @return the name of the tenant
   */
  @Override
  public String getTenantName(UUID tenantId)
      throws TenantNotFoundException, SecurityServiceException {
    try {
      Optional<String> nameOptional = tenantRepository.getNameById(tenantId);

      if (nameOptional.isPresent()) {
        return nameOptional.get();
      } else {
        throw new TenantNotFoundException(tenantId);
      }
    } catch (TenantNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new SecurityServiceException(
          "Failed to retrieve the name of the tenant (" + tenantId + ")", e);
    }
  }

  /**
   * Retrieve the tenants.
   *
   * @return the tenants
   */
  @Override
  public List<Tenant> getTenants() throws SecurityServiceException {
    try {
      return tenantRepository.findAll();
    } catch (Throwable e) {
      throw new SecurityServiceException("Failed to retrieve the tenants", e);
    }
  }

  /**
   * Retrieve the tenants.
   *
   * @param filter the optional filter to apply to the tenants
   * @param sortDirection the optional sort direction to apply to the tenants
   * @param pageIndex the optional page index
   * @param pageSize the optional page size
   * @return the tenants
   */
  @Override
  public Tenants getTenants(
      String filter, SortDirection sortDirection, Integer pageIndex, Integer pageSize)
      throws SecurityServiceException {
    PageRequest pageRequest;

    if ((pageIndex != null) && (pageSize != null)) {
      pageRequest =
          PageRequest.of(
              pageIndex,
              (pageSize > MAX_FILTERED_ORGANISATIONS) ? MAX_FILTERED_ORGANISATIONS : pageSize);
    } else {
      pageRequest = PageRequest.of(0, MAX_FILTERED_ORGANISATIONS);
    }

    try {
      Page<Tenant> tenantPage;

      if (StringUtils.isEmpty(filter)) {
        if (sortDirection == SortDirection.ASCENDING) {
          tenantPage = tenantRepository.findAllByOrderByNameAsc(pageRequest);
        } else {
          tenantPage = tenantRepository.findAllByOrderByNameDesc(pageRequest);
        }
      } else {
        if (sortDirection == SortDirection.ASCENDING) {
          tenantPage =
              tenantRepository.findByNameContainingIgnoreCaseOrderByNameAsc(filter, pageRequest);
        } else {
          tenantPage =
              tenantRepository.findByNameContainingIgnoreCaseOrderByNameDesc(filter, pageRequest);
        }
      }

      return new Tenants(
          tenantPage.toList(),
          tenantPage.getTotalElements(),
          filter,
          sortDirection,
          pageIndex,
          pageSize);
    } catch (Throwable e) {
      String message = "Failed to retrieve the tenants";

      if (!StringUtils.isEmpty(filter)) {
        message += String.format(" matching the filter \"%s\"", filter);
      }

      if ((pageIndex != null) && (pageSize != null)) {
        message += " for the page " + pageIndex + " using the page size " + pageSize;
      }

      message += ": ";

      message += e.getMessage();

      throw new SecurityServiceException(message, e);
    }
  }

  /**
   * Retrieve the tenants the user directory is associated with.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   * @return the tenants the user directory is associated with
   */
  @Override
  public List<Tenant> getTenantsForUserDirectory(UUID userDirectoryId)
      throws UserDirectoryNotFoundException, SecurityServiceException {
    try {
      if (!userDirectoryRepository.existsById(userDirectoryId)) {
        throw new UserDirectoryNotFoundException(userDirectoryId);
      }

      return tenantRepository.findAllByUserDirectoryId(userDirectoryId);
    } catch (UserDirectoryNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new SecurityServiceException(
          "Failed to retrieve the tenants associated with the user directory ("
              + userDirectoryId
              + ")",
          e);
    }
  }

  /**
   * Retrieve the user.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   * @param username the username identifying the user
   * @return the user
   */
  @Override
  public User getUser(UUID userDirectoryId, String username)
      throws UserDirectoryNotFoundException, UserNotFoundException, SecurityServiceException {
    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    return userDirectory.getUser(username);
  }

  /**
   * Retrieve the user directories.
   *
   * @return the user directories
   */
  @Override
  public List<UserDirectory> getUserDirectories() throws SecurityServiceException {
    try {
      return userDirectoryRepository.findAll();
    } catch (Throwable e) {
      throw new SecurityServiceException("Failed to retrieve the user directories", e);
    }
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
  @Override
  public UserDirectories getUserDirectories(
      String filter, SortDirection sortDirection, Integer pageIndex, Integer pageSize)
      throws SecurityServiceException {
    PageRequest pageRequest;

    if ((pageIndex != null) && (pageSize != null)) {
      pageRequest =
          PageRequest.of(
              pageIndex,
              (pageSize > MAX_FILTERED_USER_DIRECTORIES)
                  ? MAX_FILTERED_USER_DIRECTORIES
                  : pageSize);
    } else {
      pageRequest = PageRequest.of(0, MAX_FILTERED_USER_DIRECTORIES);
    }

    try {
      Page<UserDirectory> userDirectoryPage;

      if (StringUtils.isEmpty(filter)) {
        if (sortDirection == SortDirection.ASCENDING) {
          userDirectoryPage = userDirectoryRepository.findAllByOrderByNameAsc(pageRequest);
        } else {
          userDirectoryPage = userDirectoryRepository.findAllByOrderByNameDesc(pageRequest);
        }
      } else {
        if (sortDirection == SortDirection.ASCENDING) {
          userDirectoryPage =
              userDirectoryRepository.findByNameContainingIgnoreCaseOrderByNameAsc(
                  filter, pageRequest);
        } else {
          userDirectoryPage =
              userDirectoryRepository.findByNameContainingIgnoreCaseOrderByNameDesc(
                  filter, pageRequest);
        }
      }

      return new UserDirectories(
          userDirectoryPage.toList(),
          userDirectoryPage.getTotalElements(),
          filter,
          sortDirection,
          pageIndex,
          pageSize);
    } catch (Throwable e) {
      throw new SecurityServiceException("Failed to retrieve the filtered user directories", e);
    }
  }

  /**
   * Retrieve the user directories the tenant is associated with.
   *
   * @param tenantId the Universally Unique Identifier (UUID) uniquely identifying the tenant
   * @return the user directories the tenant is associated with
   */
  @Override
  public List<UserDirectory> getUserDirectoriesForTenant(UUID tenantId)
      throws TenantNotFoundException, SecurityServiceException {
    try {
      if (!tenantRepository.existsById(tenantId)) {
        throw new TenantNotFoundException(tenantId);
      }

      return userDirectoryRepository.findAllByTenantId(tenantId);
    } catch (TenantNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new SecurityServiceException(
          "Failed to retrieve the user directories associated with the tenant (" + tenantId + ")",
          e);
    }
  }

  /**
   * Retrieve the user directory.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   * @return the user directory
   */
  @Override
  public UserDirectory getUserDirectory(UUID userDirectoryId)
      throws UserDirectoryNotFoundException, SecurityServiceException {
    try {
      Optional<UserDirectory> userDirectoryOptional =
          userDirectoryRepository.findById(userDirectoryId);

      if (userDirectoryOptional.isPresent()) {
        return userDirectoryOptional.get();
      } else {
        throw new UserDirectoryNotFoundException(userDirectoryId);
      }
    } catch (UserDirectoryNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new SecurityServiceException(
          "Failed to retrieve the user directory (" + userDirectoryId + ")", e);
    }
  }

  /**
   * Retrieve the capabilities the user directory supports.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   * @return the capabilities the user directory supports
   */
  @Override
  public UserDirectoryCapabilities getUserDirectoryCapabilities(UUID userDirectoryId)
      throws UserDirectoryNotFoundException, SecurityServiceException {
    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    return userDirectory.getCapabilities();
  }

  /**
   * Retrieve the Universally Unique Identifier (UUID) uniquely identifying the user directory that
   * the user with the specified username is associated with.
   *
   * @param username the username identifying the user
   * @return the Universally Unique Identifier (UUID) uniquely identifying the user directory that
   *     the user with the specified username is associated with or <code>null</code> if the user
   *     cannot be found
   */
  @Override
  public UUID getUserDirectoryIdForUser(String username) throws SecurityServiceException {
    try {
      // First check if this is an internal user and if so determine the user directory ID
      UUID internalUserDirectoryId = getInternalUserDirectoryIdForUser(username);

      if (internalUserDirectoryId != null) {
        return internalUserDirectoryId;
      } else {
        /*
         * Check all of the "external" user directories to see if the user is associated with one
         * of them.
         */
        for (UUID userDirectoryId : userDirectories.keySet()) {
          IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

          if (userDirectory != null) {
            if (!(userDirectory instanceof InternalUserDirectory)) {
              if (userDirectory.isExistingUser(username)) {
                return userDirectoryId;
              }
            }
          }
        }

        return null;
      }
    } catch (Throwable e) {
      throw new SecurityServiceException(
          "Failed to retrieve the user directory ID for the user (" + username + ")", e);
    }
  }

  /**
   * Retrieve the Universally Unique Identifiers (UUIDs) uniquely identifying the user directories
   * the tenant is associated with.
   *
   * @param tenantId the Universally Unique Identifier (UUID) uniquely identifying the tenant
   * @return the Universally Unique Identifiers (UUIDs) uniquely identifying the user directories
   *     the tenant is associated with
   */
  @Override
  public List<UUID> getUserDirectoryIdsForTenant(UUID tenantId)
      throws TenantNotFoundException, SecurityServiceException {
    try {
      if (!tenantRepository.existsById(tenantId)) {
        throw new TenantNotFoundException(tenantId);
      }

      return tenantRepository.getUserDirectoryIdsById(tenantId);
    } catch (TenantNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new SecurityServiceException(
          "Failed to retrieve the IDs for the user directories associated with the tenant ("
              + tenantId
              + ")",
          e);
    }
  }

  /**
   * Retrieve the Universally Unique Identifiers (UUIDs) uniquely identifying the user directories
   * the user is associated with. Every user is associated with a user directory, which is in turn
   * associated with one or more tenants, which are in turn associated with one or more user
   * directories. The user is therefore associated indirectly with all these user directories.
   *
   * @param username the username identifying the user
   * @return the Universally Unique Identifiers (UUIDs) uniquely identifying the user directories
   *     the user is associated with
   */
  @Override
  public List<UUID> getUserDirectoryIdsForUser(String username)
      throws UserNotFoundException, SecurityServiceException {
    try {
      List<UUID> userDirectoryIdsForUser = new ArrayList<>();

      UUID userDirectoryId = getUserDirectoryIdForUser(username);

      if (userDirectoryId == null) {
        throw new UserNotFoundException(username);
      }

      /*
       * Retrieve the list of IDs for the tenants the user is associated with as a result
       * of their user directory being associated with these tenants.
       */
      List<UUID> tenantIds = getTenantIdsForUserDirectory(userDirectoryId);

      /*
       * Retrieve the list of IDs for the user directories the user is associated with as a result
       * of being associated with one or more tenants.
       */
      for (var tenantId : tenantIds) {
        // Retrieve the list of user directories associated with the tenant
        var userDirectoryIdsForTenant = getUserDirectoryIdsForTenant(tenantId);

        userDirectoryIdsForUser.addAll(userDirectoryIdsForTenant);
      }

      return userDirectoryIdsForUser;
    } catch (UserNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new SecurityServiceException(
          "Failed to retrieve the IDs for the user directories the user ("
              + username
              + ") is associated with",
          e);
    }
  }

  /**
   * Retrieve the name of the user directory.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   * @return the name of the user directory
   */
  @Override
  public String getUserDirectoryName(UUID userDirectoryId)
      throws UserDirectoryNotFoundException, SecurityServiceException {
    try {
      Optional<String> nameOptional = userDirectoryRepository.getNameById(userDirectoryId);

      if (nameOptional.isPresent()) {
        return nameOptional.get();
      } else {
        throw new UserDirectoryNotFoundException(userDirectoryId);
      }
    } catch (UserDirectoryNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new SecurityServiceException(
          "Failed to retrieve the name of the user directory (" + userDirectoryId + ")", e);
    }
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
  @Override
  public UserDirectorySummaries getUserDirectorySummaries(
      String filter, SortDirection sortDirection, Integer pageIndex, Integer pageSize)
      throws SecurityServiceException {
    PageRequest pageRequest;

    if ((pageIndex != null) && (pageSize != null)) {
      pageRequest =
          PageRequest.of(
              pageIndex,
              (pageSize > MAX_FILTERED_USER_DIRECTORIES)
                  ? MAX_FILTERED_USER_DIRECTORIES
                  : pageSize);
    } else {
      pageRequest = PageRequest.of(0, MAX_FILTERED_USER_DIRECTORIES);
    }

    try {
      Page<UserDirectorySummary> userDirectorySummaryPage;

      if (StringUtils.isEmpty(filter)) {
        if (sortDirection == SortDirection.ASCENDING) {
          userDirectorySummaryPage =
              userDirectorySummaryRepository.findAllByOrderByNameAsc(pageRequest);
        } else {
          userDirectorySummaryPage =
              userDirectorySummaryRepository.findAllByOrderByNameDesc(pageRequest);
        }
      } else {
        if (sortDirection == SortDirection.ASCENDING) {
          userDirectorySummaryPage =
              userDirectorySummaryRepository.findByNameContainingIgnoreCaseOrderByNameAsc(
                  filter, pageRequest);
        } else {
          userDirectorySummaryPage =
              userDirectorySummaryRepository.findByNameContainingIgnoreCaseOrderByNameDesc(
                  filter, pageRequest);
        }
      }

      return new UserDirectorySummaries(
          userDirectorySummaryPage.toList(),
          userDirectorySummaryPage.getTotalElements(),
          filter,
          sortDirection,
          pageIndex,
          pageSize);
    } catch (Throwable e) {
      throw new SecurityServiceException(
          "Failed to retrieve the filtered summaries for the user directories", e);
    }
  }

  /**
   * Retrieve the summaries for the user directories the tenant is associated with.
   *
   * @param tenantId the Universally Unique Identifier (UUID) uniquely identifying the tenant
   * @return the summaries for the user directories the tenant is associated with
   */
  @Override
  public List<UserDirectorySummary> getUserDirectorySummariesForTenant(UUID tenantId)
      throws TenantNotFoundException, SecurityServiceException {
    try {
      if (!tenantRepository.existsById(tenantId)) {
        throw new TenantNotFoundException(tenantId);
      }

      return userDirectorySummaryRepository.findAllByTenantId(tenantId);
    } catch (TenantNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new SecurityServiceException(
          "Failed to retrieve the summaries for the user "
              + "directories associated with the tenant ("
              + tenantId
              + ")",
          e);
    }
  }

  /**
   * Retrieve the user directory type for the user directory.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   * @return the user directory type for the user directory
   */
  @Override
  public UserDirectoryType getUserDirectoryTypeForUserDirectory(UUID userDirectoryId)
      throws UserDirectoryNotFoundException, UserDirectoryTypeNotFoundException,
          SecurityServiceException {
    try {
      Optional<String> typeOptional =
          userDirectoryRepository.getTypeForUserDirectoryById(userDirectoryId);

      if (typeOptional.isEmpty()) {
        throw new UserDirectoryNotFoundException(userDirectoryId);
      }

      Optional<UserDirectoryType> userDirectoryTypeOptional =
          userDirectoryTypeRepository.findById(typeOptional.get());

      if (userDirectoryTypeOptional.isPresent()) {
        return userDirectoryTypeOptional.get();
      } else {
        throw new UserDirectoryTypeNotFoundException(typeOptional.get());
      }
    } catch (UserDirectoryNotFoundException | UserDirectoryTypeNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new SecurityServiceException(
          "Failed to retrieve the user directory type for the user directory ("
              + userDirectoryId
              + ")",
          e);
    }
  }

  /**
   * Retrieve the user directory types.
   *
   * @return the user directory types
   */
  @Override
  public List<UserDirectoryType> getUserDirectoryTypes() throws SecurityServiceException {
    try {
      return userDirectoryTypeRepository.findAll();
    } catch (Throwable e) {
      throw new SecurityServiceException("Failed to retrieve the user directory types", e);
    }
  }

  /**
   * Retrieve the name of the user.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   * @param username the username identifying the user
   * @return the name of the user
   */
  @Override
  public String getUserName(UUID userDirectoryId, String username)
      throws UserDirectoryNotFoundException, UserNotFoundException, SecurityServiceException {
    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    return userDirectory.getUserName(username);
  }

  /**
   * Retrieve all the users.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   * @return the users
   */
  @Override
  public List<User> getUsers(UUID userDirectoryId)
      throws UserDirectoryNotFoundException, SecurityServiceException {
    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    return userDirectory.getUsers();
  }

  /**
   * Retrieve the users.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   * @param filter the optional filter to apply to the users
   * @param sortBy the optional method used to sort the users e.g. by name
   * @param sortDirection the optional sort direction to apply to the users
   * @param pageIndex the optional page index
   * @param pageSize the optional page size
   * @return the users
   */
  @Override
  public Users getUsers(
      UUID userDirectoryId,
      String filter,
      UserSortBy sortBy,
      SortDirection sortDirection,
      Integer pageIndex,
      Integer pageSize)
      throws UserDirectoryNotFoundException, SecurityServiceException {
    if (sortBy == null) {
      sortBy = UserSortBy.NAME;
    }

    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    return userDirectory.getUsers(filter, sortBy, sortDirection, pageIndex, pageSize);
  }

  /**
   * Initiate the password reset process for the user.
   *
   * @param username the username identifying the user
   * @param resetPasswordUrl the reset password URL
   * @param sendEmail should the password reset e-mail be sent to the user
   */
  @Override
  @Transactional
  public void initiatePasswordReset(String username, String resetPasswordUrl, boolean sendEmail)
      throws UserNotFoundException, SecurityServiceException {
    initiatePasswordReset(username, resetPasswordUrl, sendEmail, null);
  }

  /**
   * Initiate the password reset process for the user.
   *
   * @param username the username identifying the user
   * @param resetPasswordUrl the reset password URL
   * @param sendEmail should the password reset e-mail be sent to the user
   * @param securityCode the pre-generated security code to use
   */
  @Override
  @Transactional
  public void initiatePasswordReset(
      String username, String resetPasswordUrl, boolean sendEmail, String securityCode)
      throws UserNotFoundException, SecurityServiceException {
    try {
      UUID userDirectoryId = getUserDirectoryIdForUser(username);

      if (userDirectoryId == null) {
        throw new UserNotFoundException(username);
      }

      IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

      User user = userDirectory.getUser(username);

      if (!StringUtils.isEmpty(user.getEmail())) {
        if (StringUtils.isEmpty(securityCode)) {
          securityCode = securityCodeGenerator.nextString();
        }

        String securityCodeHash = PasswordUtil.createPasswordHash(securityCode);

        PasswordReset passwordReset = new PasswordReset(username, securityCodeHash);

        if (sendEmail) {
          sendPasswordResetEmail(user, resetPasswordUrl, securityCode);
        }

        passwordResetRepository.saveAndFlush(passwordReset);
      } else {
        logger.warn(
            "Failed to send the password reset communication to the user ("
                + username
                + ") who does not have a valid e-mail address");
      }
    } catch (UserNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new SecurityServiceException(
          "Failed to initiate the password reset process for the user (" + username + ")", e);
    }
  }

  /**
   * Does the user with the specified username exist?
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   * @param username the username identifying the user
   * @return <code>true</code> if a user with specified username exists or <code>false</code>
   *     otherwise
   */
  @Override
  public boolean isExistingUser(UUID userDirectoryId, String username)
      throws UserDirectoryNotFoundException, SecurityServiceException {
    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    return userDirectory.isExistingUser(username);
  }

  /**
   * Is the user in the group?
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   * @param groupName the name identifying the group
   * @param username the username identifying the user
   * @return <code>true</code> if the user is a member of the group or <code>false</code> otherwise
   */
  @Override
  public boolean isUserInGroup(UUID userDirectoryId, String groupName, String username)
      throws UserDirectoryNotFoundException, UserNotFoundException, GroupNotFoundException,
          SecurityServiceException {
    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    return userDirectory.isUserInGroup(groupName, username);
  }

  /** Reload the user directories. */
  @Override
  public void reloadUserDirectories() throws SecurityServiceException {
    try {
      Map<UUID, IUserDirectory> reloadedUserDirectories = new ConcurrentHashMap<>();

      List<UserDirectoryType> userDirectoryTypes = getUserDirectoryTypes();

      for (UserDirectory userDirectory : getUserDirectories()) {
        UserDirectoryType userDirectoryType;

        userDirectoryType =
            userDirectoryTypes.stream()
                .filter(
                    possibleUserDirectoryType ->
                        possibleUserDirectoryType.getCode().equals(userDirectory.getType()))
                .findFirst()
                .orElse(null);

        if (userDirectoryType == null) {
          logger.error(
              "Failed to load the user directory ("
                  + userDirectory.getId()
                  + "): The user directory type ("
                  + userDirectory.getType()
                  + ") was not loaded");

          continue;
        }

        try {
          Class<?> clazz = userDirectoryType.getUserDirectoryClass();

          if (!IUserDirectory.class.isAssignableFrom(clazz)) {
            throw new SecurityServiceException(
                "The user directory class ("
                    + userDirectoryType.getUserDirectoryClassName()
                    + ") does not implement the IUserDirectory interface");
          }

          Class<? extends IUserDirectory> userDirectoryClass =
              clazz.asSubclass(IUserDirectory.class);

          Constructor<? extends IUserDirectory> userDirectoryClassConstructor;

          try {
            userDirectoryClassConstructor =
                userDirectoryClass.getConstructor(
                    UUID.class,
                    List.class,
                    GroupRepository.class,
                    UserRepository.class,
                    RoleRepository.class);
          } catch (NoSuchMethodException e) {
            throw new SecurityServiceException(
                "The user directory class ("
                    + userDirectoryType.getUserDirectoryClassName()
                    + ") does not provide a valid constructor (long, Map<String,String>)");
          }

          IUserDirectory userDirectoryInstance =
              userDirectoryClassConstructor.newInstance(
                  userDirectory.getId(),
                  userDirectory.getParameters(),
                  groupRepository,
                  userRepository,
                  roleRepository);

          applicationContext.getAutowireCapableBeanFactory().autowireBean(userDirectoryInstance);

          reloadedUserDirectories.put(userDirectory.getId(), userDirectoryInstance);
        } catch (Throwable e) {
          throw new SecurityServiceException(
              "Failed to initialize the user directory ("
                  + userDirectory.getId()
                  + ")("
                  + userDirectory.getName()
                  + ")",
              e);
        }
      }

      this.userDirectories = reloadedUserDirectories;
    } catch (Throwable e) {
      throw new SecurityServiceException("Failed to reload the user directories", e);
    }
  }

  /**
   * Remove the group member from the group.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   * @param groupName the name identifying the group
   * @param memberType the group member type
   * @param memberName the group member name
   */
  @Override
  @Transactional
  public void removeMemberFromGroup(
      UUID userDirectoryId, String groupName, GroupMemberType memberType, String memberName)
      throws UserDirectoryNotFoundException, GroupNotFoundException, GroupMemberNotFoundException,
          SecurityServiceException {
    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    userDirectory.removeMemberFromGroup(groupName, memberType, memberName);
  }

  /**
   * Remove the role from the group.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   * @param groupName the name identifying the group
   * @param roleCode the code uniquely identifying the role
   */
  @Override
  @Transactional
  public void removeRoleFromGroup(UUID userDirectoryId, String groupName, String roleCode)
      throws UserDirectoryNotFoundException, GroupNotFoundException, GroupRoleNotFoundException,
          SecurityServiceException {
    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    userDirectory.removeRoleFromGroup(groupName, roleCode);
  }

  /**
   * Remove the user directory from the tenant.
   *
   * @param tenantId the Universally Unique Identifier (UUID) uniquely identifying the tenant
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   */
  @Override
  @Transactional
  public void removeUserDirectoryFromTenant(UUID tenantId, UUID userDirectoryId)
      throws TenantNotFoundException, TenantUserDirectoryNotFoundException,
          SecurityServiceException {
    try {
      if (!tenantRepository.existsById(tenantId)) {
        throw new TenantNotFoundException(tenantId);
      }

      if (tenantRepository.countTenantUserDirectory(tenantId, userDirectoryId) == 0) {
        throw new TenantUserDirectoryNotFoundException(tenantId, userDirectoryId);
      }

      tenantRepository.removeUserDirectoryFromTenant(tenantId, userDirectoryId);
    } catch (TenantNotFoundException | TenantUserDirectoryNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new SecurityServiceException(
          "Failed to add the user directory ("
              + userDirectoryId
              + ") to the tenant ("
              + tenantId
              + ")",
          e);
    }
  }

  /**
   * Remove the user from the group.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) uniquely identifying the user
   *     directory
   * @param groupName the name identifying the group
   * @param username the username identifying the user
   */
  @Override
  @Transactional
  public void removeUserFromGroup(UUID userDirectoryId, String groupName, String username)
      throws UserDirectoryNotFoundException, GroupNotFoundException, UserNotFoundException,
          SecurityServiceException {
    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    userDirectory.removeUserFromGroup(groupName, username);
  }

  /**
   * Reset the password for the user.
   *
   * @param username the username identifying the user
   * @param newPassword the new password
   * @param securityCode the security code
   */
  @Override
  @Transactional
  public void resetPassword(String username, String newPassword, String securityCode)
      throws UserNotFoundException, UserLockedException, InvalidSecurityCodeException,
          ExistingPasswordException, SecurityServiceException {
    try {
      UUID userDirectoryId = getUserDirectoryIdForUser(username);

      if (userDirectoryId == null) {
        throw new UserNotFoundException(username);
      }

      List<PasswordReset> passwordResets =
          passwordResetRepository.findAllByUsernameAndStatus(
              username, PasswordResetStatus.REQUESTED);

      String securityCodeHash = PasswordUtil.createPasswordHash(securityCode);

      for (PasswordReset passwordReset : passwordResets) {
        if (passwordReset.getSecurityCodeHash().equals(securityCodeHash)) {
          IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

          userDirectory.resetPassword(username, newPassword);

          return;
        }
      }

      throw new InvalidSecurityCodeException();
    } catch (UserNotFoundException
        | UserLockedException
        | InvalidSecurityCodeException
        | ExistingPasswordException e) {
      throw e;
    } catch (Throwable e) {
      throw new SecurityServiceException(
          "Failed to reset the password for the user (" + username + ")", e);
    }
  }

  /**
   * Update the authorised function.
   *
   * @param function the function
   */
  @Override
  @Transactional
  public void updateFunction(Function function)
      throws FunctionNotFoundException, SecurityServiceException {
    try {
      if (!functionRepository.existsById(function.getCode())) {
        throw new FunctionNotFoundException(function.getCode());
      }

      functionRepository.saveAndFlush(function);
    } catch (FunctionNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new SecurityServiceException(
          "Failed to update the function (" + function.getCode() + ")", e);
    }
  }

  /**
   * Update the group.
   *
   * @param group the group
   */
  @Override
  @Transactional
  public void updateGroup(Group group)
      throws UserDirectoryNotFoundException, GroupNotFoundException, SecurityServiceException {
    IUserDirectory userDirectory = userDirectories.get(group.getUserDirectoryId());

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(group.getUserDirectoryId());
    }

    userDirectory.updateGroup(group);
  }

  /**
   * Update the tenant.
   *
   * @param tenant the tenant
   */
  @Override
  @Transactional
  public void updateTenant(Tenant tenant) throws TenantNotFoundException, SecurityServiceException {
    try {
      Optional<Tenant> tenantOptional = tenantRepository.findById(tenant.getId());

      if (tenantOptional.isPresent()) {
        Tenant existingTenant = tenantOptional.get();

        existingTenant.setName(tenant.getName());
        existingTenant.setStatus(tenant.getStatus());

        tenantRepository.saveAndFlush(existingTenant);
      } else {
        throw new TenantNotFoundException(tenant.getId());
      }
    } catch (TenantNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new SecurityServiceException("Failed to update the tenant (" + tenant.getId() + ")", e);
    }
  }

  /**
   * Update the user.
   *
   * @param user the user
   * @param expirePassword expire the user's password as part of the update
   * @param lockUser lock the user as part of the update
   */
  @Override
  @Transactional
  public void updateUser(User user, boolean expirePassword, boolean lockUser)
      throws UserDirectoryNotFoundException, UserNotFoundException, SecurityServiceException {
    IUserDirectory userDirectory = userDirectories.get(user.getUserDirectoryId());

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(user.getUserDirectoryId());
    }

    userDirectory.updateUser(user, expirePassword, lockUser);
  }

  /**
   * Update the user directory.
   *
   * @param userDirectory the user directory
   */
  @Override
  @Transactional
  public void updateUserDirectory(UserDirectory userDirectory)
      throws UserDirectoryNotFoundException, SecurityServiceException {
    try {
      if (!userDirectoryRepository.existsById(userDirectory.getId())) {
        throw new UserDirectoryNotFoundException(userDirectory.getId());
      }

      userDirectoryRepository.saveAndFlush(userDirectory);

      reloadUserDirectories();
    } catch (UserDirectoryNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new SecurityServiceException(
          "Failed to update the user directory (" + userDirectory.getName() + ")", e);
    }
  }

  /**
   * Returns the Universally Unique Identifier (UUID) uniquely identifying the internal user
   * directory the internal user with the specified username is associated with.
   *
   * @param username the username uniquely identifying the internal user
   * @return the Universally Unique Identifier (UUID) uniquely identifying the internal user
   *     directory the internal user with the specified username is associated with or <code>null
   * </code> if an internal user with the specified username could not be found
   */
  private UUID getInternalUserDirectoryIdForUser(String username) throws SecurityServiceException {
    try {
      Optional<UUID> userDirectoryIdOptional =
          userRepository.getUserDirectoryIdByUsernameIgnoreCase(username);

      return userDirectoryIdOptional.orElse(null);
    } catch (Throwable e) {
      throw new SecurityServiceException(
          "Failed to retrieve the ID for the internal user directory for the internal user ("
              + username
              + ")",
          e);
    }
  }

  /**
   * Checks whether the specified value is <code>null</code> or blank.
   *
   * @param value the value to check
   * @return true if the value is <code>null</code> or blank
   */
  private boolean isNullOrEmpty(Object value) {
    if (value == null) {
      return true;
    }

    if (value instanceof String) {
      return ((String) value).length() == 0;
    }

    return false;
  }

  private UserDirectory newInternalUserDirectoryForTenant(Tenant tenant)
      throws SecurityServiceException {
    UserDirectory userDirectory = new UserDirectory();

    if (tenant.getId() != null) {
      userDirectory.setId(tenant.getId());
    }

    userDirectory.setType("InternalUserDirectory");
    userDirectory.setName(tenant.getName() + " Internal User Directory");

    String buffer =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE userDirectory "
            + "SYSTEM \"UserDirectoryConfiguration.dtd\"><userDirectory>"
            + "<parameter><name>MaxPasswordAttempts</name><value>5</value></parameter>"
            + "<parameter><name>PasswordExpiryMonths</name><value>12</value></parameter>"
            + "<parameter><name>PasswordHistoryMonths</name><value>24</value></parameter>"
            + "<parameter><name>MaxFilteredUsers</name><value>100</value></parameter>"
            + "</userDirectory>";

    userDirectory.setConfiguration(buffer);

    return userDirectory;
  }

  private void sendPasswordResetEmail(User user, String resetPasswordUrl, String securityCode)
      throws SecurityServiceException {
    try {
      if (!StringUtils.isEmpty(user.getEmail())) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("preferredName", user.getPreferredName().toUpperCase());
        parameters.put("securityCode", securityCode);
        parameters.put(
            "resetPasswordUrl",
            resetPasswordUrl
                + "?username="
                + UriUtils.encodeQueryParam(user.getUsername(), StandardCharsets.UTF_8)
                + "&securityCode="
                + UriUtils.encodeQueryParam(securityCode, StandardCharsets.UTF_8));

        mailService.sendMail(
            Collections.singletonList(user.getEmail()),
            "Password Reset",
            "no-reply@inception.digital",
            "Inception",
            PASSWORD_RESET_MAIL_TEMPLATE_ID,
            parameters);
      }
    } catch (Throwable e) {
      throw new SecurityServiceException("Failed to send the password reset e-mail", e);
    }
  }
}
