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

package digital.inception.security;

//~--- non-JDK imports --------------------------------------------------------

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.QueryByExampleExecutor;

//~--- JDK imports ------------------------------------------------------------

import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * The <code>UserRepository</code> interface declares the repository for the
 * <code>User</code> domain type.
 *
 * @author Marcus Portmann
 */
public interface UserRepository extends JpaRepository<User, UUID>, QueryByExampleExecutor<User>
{
  @Modifying
  @Query(
      "update User u set u.password = :password, u.passwordAttempts = :passwordAttempts, u.passwordExpiry = :passwordExpiry where u.id = :userId")
  void changePassword(@Param("userId") UUID userId, @Param("password") String password, @Param(
      "passwordAttempts") int passwordAttempts, @Param(
      "passwordExpiry") Optional<LocalDateTime> passwordExpiry);

  long countByUserDirectoryId(UUID userDirectoryId);

  @Query(
      "select count(u.id) from User u where ((upper(u.username) like upper(:filter)) or (upper(u.firstName) like upper(:filter)) or (upper(u.lastName) like upper(:filter))) and u.userDirectoryId = :userDirectoryId")
  long countFiltered(@Param("userDirectoryId") UUID userDirectoryId, @Param(
      "filter") String filter);

  @Query(
      value = "select count(uph.user_id) from security.users_password_history uph where uph.user_id = :userId and uph.changed > :after and uph.password = :password",
      nativeQuery = true)
  long countPasswordHistory(@Param("userId") UUID userId, @Param("after") LocalDateTime after,
      @Param("password") String password);

  @Modifying
  @Query("delete from User u where u.id = :userId")
  void deleteById(@Param("userId") UUID userId);

  boolean existsByUserDirectoryIdAndUsernameIgnoreCase(UUID userDirectoryId, String username);

  List<User> findByUserDirectoryId(UUID userDirectoryId);

  List<User> findByUserDirectoryId(UUID userDirectoryId, Pageable pageable);

  Optional<User> findByUserDirectoryIdAndUsernameIgnoreCase(UUID userDirectoryId, String username);

  @Query(
      "select u from User u where ((upper(u.username) like upper(:filter)) or (upper(u.firstName) like upper(:filter)) or (upper(u.lastName) like upper(:filter))) and u.userDirectoryId = :userDirectoryId")
  List<User> findFiltered(@Param("userDirectoryId") UUID userDirectoryId, @Param(
      "filter") String filter, Pageable pageable);

  @Query(
      "select f.code from User u  join u.groups as g join g.roles as r join r.functions as f where u.id = :userId")
  List<String> getFunctionCodesByUserId(@Param("userId") UUID userId);

  @Query("select g.name from User u join u.groups as g where u.id = :userId")
  List<String> getGroupNamesByUserId(@Param("userId") UUID userId);

  @Query("select g from User u join u.groups as g where u.id = :userId")
  List<Group> getGroupsByUserId(@Param("userId") UUID userId);

  @Query(
      "select u.id from User u where u.userDirectoryId = :userDirectoryId and upper(u.username) like upper(:username)")
  Optional<UUID> getIdByUserDirectoryIdAndUsernameIgnoreCase(@Param(
      "userDirectoryId") UUID userDirectoryId, @Param("username") String username);

  @Query("select r.code from User u join u.groups as g join g.roles as r where u.id = :userId")
  List<String> getRoleCodesByUserId(@Param("userId") UUID userId);

  @Query("select u.userDirectoryId from User u where upper(u.username) = upper(:username)")
  Optional<UUID> getUserDirectoryIdByUsernameIgnoreCase(@Param("username") String username);

  @Modifying
  @Query("update User u set u.passwordAttempts = u.passwordAttempts + 1 where u.id = :userId")
  void incrementPasswordAttempts(@Param("userId") UUID userId);

  @Query(
      "select case when (count(u.id) > 0) then true else false end from User u join u.groups as g where u.id = :userId and g.id = :groupId")
  boolean isUserInGroup(@Param("userId") UUID userId, @Param("groupId") UUID groupId);

  @Modifying
  @Query(
      value = "insert into security.users_password_history(user_id, changed, password) values (:userId, current_timestamp, :password)",
      nativeQuery = true)
  void savePasswordInPasswordHistory(@Param("userId") UUID userId, @Param(
      "password") String password);
}
