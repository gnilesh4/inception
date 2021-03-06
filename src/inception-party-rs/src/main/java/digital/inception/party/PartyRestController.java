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

// ~--- non-JDK imports --------------------------------------------------------

import digital.inception.core.sorting.SortDirection;
import digital.inception.rs.RestControllerError;
import digital.inception.rs.SecureRestController;
import digital.inception.validation.InvalidArgumentException;
import digital.inception.validation.ValidationError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Set;
import java.util.UUID;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// ~--- JDK imports ------------------------------------------------------------

/**
 * The <code>PartyRestController</code> class.
 *
 * @author Marcus Portmann
 */
@Tag(name = "Party API")
@RestController
@RequestMapping(value = "/api/party")
@CrossOrigin
@SuppressWarnings({"unused", "WeakerAccess"})
public class PartyRestController extends SecureRestController {

  /** The Party Service. */
  private final IPartyService partyService;

  /** The JSR-303 validator. */
  private final Validator validator;

  /**
   * Constructs a new <code>PartyRestController</code>.
   *
   * @param partyService the Party Service
   * @param validator the JSR-303 validator
   */
  public PartyRestController(IPartyService partyService, Validator validator) {
    this.partyService = partyService;
    this.validator = validator;
  }

  /**
   * Create the new organization.
   *
   * @param organization the organization
   */
  @Operation(summary = "Create the organization", description = "Create the organization")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "204",
            description = "The organization was created successfully"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid argument",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RestControllerError.class))),
        @ApiResponse(
            responseCode = "409",
            description = "An organization with the specified ID already exists",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RestControllerError.class))),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RestControllerError.class)))
      })
  @RequestMapping(
      value = "/organizations",
      method = RequestMethod.POST,
      produces = "application/json")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  //  @PreAuthorize(
  //      "hasRole('Administrator') or hasAuthority('FUNCTION_Party.PartyAdministration') or
  // hasAuthority('FUNCTION_Party.OrganizationAdministration')")
  public void createOrganization(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "The organization to create",
              required = true)
          @RequestBody
          Organization organization)
      throws InvalidArgumentException, DuplicateOrganizationException, PartyServiceException {
    if (organization == null) {
      throw new InvalidArgumentException("organization");
    }

    Set<ConstraintViolation<Organization>> constraintViolations = validator.validate(organization);

    if (!constraintViolations.isEmpty()) {
      throw new InvalidArgumentException(
          "organization", ValidationError.toValidationErrors(constraintViolations));
    }

    partyService.createOrganization(organization);
  }

  /**
   * Create the new person.
   *
   * @param person the person
   */
  @Operation(summary = "Create the person", description = "Create the person")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "The person was created successfully"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid argument",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RestControllerError.class))),
        @ApiResponse(
            responseCode = "409",
            description = "A person with the specified ID already exists",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RestControllerError.class))),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RestControllerError.class)))
      })
  @RequestMapping(value = "/persons", method = RequestMethod.POST, produces = "application/json")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  //  @PreAuthorize(
  //      "hasRole('Administrator') or hasAuthority('FUNCTION_Party.PartyAdministration') or
  // hasAuthority('FUNCTION_Party.PersonAdministration')")
  public void createPerson(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "The person to create",
              required = true)
          @RequestBody
          Person person)
      throws InvalidArgumentException, DuplicatePersonException, PartyServiceException {
    if (person == null) {
      throw new InvalidArgumentException("person");
    }

    Set<ConstraintViolation<Person>> constraintViolations = validator.validate(person);

    if (!constraintViolations.isEmpty()) {
      throw new InvalidArgumentException(
          "person", ValidationError.toValidationErrors(constraintViolations));
    }

    partyService.createPerson(person);
  }

  /**
   * Retrieve the organization.
   *
   * @param organizationId the Universally Unique Identifier (UUID) uniquely identifying the
   *     organization
   * @return the organization
   */
  @Operation(summary = "Retrieve the organization", description = "Retrieve the organization")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid argument",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RestControllerError.class))),
        @ApiResponse(
            responseCode = "404",
            description = "The organization could not be found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RestControllerError.class))),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RestControllerError.class)))
      })
  @RequestMapping(
      value = "/organizations/{organizationId}",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  //  @PreAuthorize(
  //      "hasRole('Administrator') or hasAuthority('FUNCTION_Party.PartyAdministration') or
  // hasAuthority('FUNCTION_Party.OrganizationAdministration')")
  public Organization getOrganization(
      @Parameter(
              name = "organizationId",
              description =
                  "The Universally Unique Identifier (UUID) uniquely identifying the organization",
              required = true)
          @PathVariable
          UUID organizationId)
      throws InvalidArgumentException, OrganizationNotFoundException, PartyServiceException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (organizationId == null) {
      throw new InvalidArgumentException("organizationId");
    }

    return null;
  }

  /**
   * Retrieve the organizations.
   *
   * @param filter the optional filter to apply to the organizations
   * @param sortDirection the optional sort direction to apply to the organizations
   * @param pageIndex the optional page index
   * @param pageSize the optional page size
   * @return the organizations
   */
  @Operation(summary = "Retrieve the organizations", description = "Retrieve the organizations")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RestControllerError.class)))
      })
  @RequestMapping(
      value = "/organizations",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  //  @PreAuthorize(
  //      "hasRole('Administrator') or hasAuthority('FUNCTION_Party.PartyAdministration') or
  // hasAuthority('FUNCTION_Party.OrganizationAdministration')")
  public Organizations getOrganizations(
      @Parameter(name = "filter", description = "The optional filter to apply to the organizations")
          @RequestParam(value = "filter", required = false)
          String filter,
      @Parameter(
              name = "sortDirection",
              description = "The optional sort direction to apply to the organizations")
          @RequestParam(value = "sortDirection", required = false)
          SortDirection sortDirection,
      @Parameter(name = "pageIndex", description = "The optional page index", example = "0")
          @RequestParam(value = "pageIndex", required = false)
          Integer pageIndex,
      @Parameter(name = "pageSize", description = "The optional page size", example = "0")
          @RequestParam(value = "pageSize", required = false)
          Integer pageSize)
      throws PartyServiceException {
    return partyService.getOrganizations(filter, sortDirection, pageIndex, pageSize);
  }

  /**
   * Retrieve the parties.
   *
   * @param filter the optional filter to apply to the parties
   * @param sortDirection the optional sort direction to apply to the parties
   * @param pageIndex the optional page index
   * @param pageSize the optional page size
   * @return the parties
   */
  @Operation(summary = "Retrieve the parties", description = "Retrieve the parties")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RestControllerError.class)))
      })
  @RequestMapping(value = "/parties", method = RequestMethod.GET, produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  //  @PreAuthorize(
  //      "hasRole('Administrator') or hasAuthority('FUNCTION_Party.PartyAdministration')")
  public Parties getParties(
      @Parameter(name = "filter", description = "The optional filter to apply to the parties")
          @RequestParam(value = "filter", required = false)
          String filter,
      @Parameter(
              name = "sortDirection",
              description = "The optional sort direction to apply to the parties")
          @RequestParam(value = "sortDirection", required = false)
          SortDirection sortDirection,
      @Parameter(name = "pageIndex", description = "The optional page index", example = "0")
          @RequestParam(value = "pageIndex", required = false)
          Integer pageIndex,
      @Parameter(name = "pageSize", description = "The optional page size", example = "0")
          @RequestParam(value = "pageSize", required = false)
          Integer pageSize)
      throws PartyServiceException {
    return partyService.getParties(filter, sortDirection, pageIndex, pageSize);
  }

  /**
   * Retrieve the person.
   *
   * @param personId the Universally Unique Identifier (UUID) uniquely identifying the person
   * @return the person
   */
  @Operation(summary = "Retrieve the person", description = "Retrieve the person")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid argument",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RestControllerError.class))),
        @ApiResponse(
            responseCode = "404",
            description = "The person could not be found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RestControllerError.class))),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RestControllerError.class)))
      })
  @RequestMapping(
      value = "/persons/{personId}",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  //  @PreAuthorize(
  //      "hasRole('Administrator') or hasAuthority('FUNCTION_Party.PartyAdministration') or
  // hasAuthority('FUNCTION_Party.PersonAdministration')")
  public Person getPerson(
      @Parameter(
              name = "personId",
              description =
                  "The Universally Unique Identifier (UUID) uniquely identifying the person",
              required = true)
          @PathVariable
          UUID personId)
      throws InvalidArgumentException, PersonNotFoundException, PartyServiceException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (personId == null) {
      throw new InvalidArgumentException("personId");
    }

    return null;
  }

  /**
   * Retrieve the persons.
   *
   * @param filter the optional filter to apply to the persons
   * @param sortBy the optional method used to sort the persons e.g. by name
   * @param sortDirection the optional sort direction to apply to the persons
   * @param pageIndex the optional page index
   * @param pageSize the optional page size
   * @return the persons
   */
  @Operation(summary = "Retrieve the persons", description = "Retrieve the persons")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RestControllerError.class)))
      })
  @RequestMapping(value = "/persons", method = RequestMethod.GET, produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  //  @PreAuthorize(
  //      "hasRole('Administrator') or hasAuthority('FUNCTION_Party.PartyAdministration') or
  // hasAuthority('FUNCTION_Party.PersonAdministration')")
  public Persons getPersons(
      @Parameter(name = "filter", description = "The optional filter to apply to the persons")
          @RequestParam(value = "filter", required = false)
          String filter,
      @Parameter(
              name = "sortBy",
              description = "The optional method used to sort the persons e.g. by name")
          @RequestParam(value = "sortBy", required = false)
          PersonSortBy sortBy,
      @Parameter(
              name = "sortDirection",
              description = "The optional sort direction to apply to the persons")
          @RequestParam(value = "sortDirection", required = false)
          SortDirection sortDirection,
      @Parameter(name = "pageIndex", description = "The optional page index", example = "0")
          @RequestParam(value = "pageIndex", required = false)
          Integer pageIndex,
      @Parameter(name = "pageSize", description = "The optional page size", example = "0")
          @RequestParam(value = "pageSize", required = false)
          Integer pageSize)
      throws PartyServiceException {
    return partyService.getPersons(filter, sortBy, sortDirection, pageIndex, pageSize);
  }
}
