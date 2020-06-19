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

package digital.inception.sample.api;

// ~--- non-JDK imports --------------------------------------------------------

import digital.inception.sample.model.Data;
import digital.inception.sample.model.ISampleService;
import digital.inception.sample.model.SampleServiceException;
import digital.inception.validation.ValidationError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// ~--- JDK imports ------------------------------------------------------------

/**
 * The <code>SampleServiceController</code> class.
 *
 * @author Marcus Portmann
 */
@Schema(description = "Sample API")
@RestController
@RequestMapping(value = "/api/sample")
@SuppressWarnings({"unused"})
public class SampleRestController {

  private ISampleService sampleService;

  /**
   * Constructs a new <code>SampleServiceController</code>.
   *
   * @param sampleService the Sample Service
   */
  public SampleRestController(ISampleService sampleService) {
    this.sampleService = sampleService;
  }

  /**
   * Returns all the data.
   *
   * @return all the data
   */
  @RequestMapping(value = "/all-data", method = RequestMethod.GET, produces = "application/json")
  public List<Data> allData() throws SampleServiceException {
    return sampleService.getAllData();
  }

  /**
   * Retrieve the data.
   *
   * @return the data
   */
  @RequestMapping(value = "/data", method = RequestMethod.GET, produces = "application/json")
  public Data getData() throws SampleServiceException {
    long id = System.currentTimeMillis();

    Data data =
        new Data(
            id, "Test Name " + id, 777, "Test Value " + id, LocalDate.now(), LocalDateTime.now());

    sampleService.addData(data);

    data = sampleService.getData(data.getId());

    return data;
  }

  /** Test the exception handling. */
  @RequestMapping(value = "/test-exception-handling", method = RequestMethod.GET)
  public void testExceptionHandling() throws SampleServiceException {
    throw new SampleServiceException("Testing 1.. 2.. 3..");
  }

  /**
   * Test the local date time mapping.
   *
   * @param localDateTime the local date time
   */
  @Operation(
      summary = "Test the local date time serialization",
      description = "Test the local date time serialization")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
  @RequestMapping(
      value = "/test-local-date-time",
      method = RequestMethod.GET,
      produces = "application/json")
  public LocalDateTime testLocalDateTime(
      @Parameter(name = "localDateTime", description = "The local date time", required = true)
          @RequestParam("localDateTime")
          LocalDateTime localDateTime)
      throws SampleServiceException {
    if (true) {
      throw new SampleServiceException("Testing 1.. 2.. 3...");
    }

    System.out.println("localDateTime = " + localDateTime);

    return localDateTime;
  }

  /**
   * Test the zoned date time mapping.
   *
   * @param zonedDateTime the zoned date time
   */
  @Operation(
      summary = "Test the zoned date time serialization",
      description = "Test the zoned date time serialization")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
  @RequestMapping(
      value = "/test-zoned-date-time",
      method = RequestMethod.GET,
      produces = "application/json")
  public ZonedDateTime testZonedDateTime(
      @Parameter(name = "zonedDateTime", description = "The zoned date time", required = true)
          @RequestParam("zonedDateTime")
          ZonedDateTime zonedDateTime)
      throws SampleServiceException {
    if (false) {
      throw new SampleServiceException("Testing 1.. 2.. 3...");
    }

    System.out.println("zonedDateTime = " + zonedDateTime);

    return zonedDateTime;
  }

  /** Validate the data. */
  @Operation(summary = "Validate the data", description = "Validate the data")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content =
                @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ValidationError.class))))
      })
  @RequestMapping(value = "/validate", method = RequestMethod.POST, produces = "application/json")
  public List<ValidationError> validate(
      @Parameter(name = "data", description = "The data", required = true) @RequestBody Data data)
      throws SampleServiceException {
    return sampleService.validate(data);
  }
}
