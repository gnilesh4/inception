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

package digital.inception.json.databind;

// ~--- non-JDK imports --------------------------------------------------------

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import digital.inception.core.util.ISO8601Util;
import java.io.IOException;
import java.time.LocalTime;

// ~--- JDK imports ------------------------------------------------------------

/**
 * The <code>LocalTimeDeserializer</code> class implements the Jackson deserializer for the <code>
 * LocalTime</code> type.
 *
 * @author Marcus Portmann
 */
public class LocalTimeDeserializer extends JsonDeserializer<LocalTime> {

  @Override
  public LocalTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
      throws IOException {
    try {
      return ISO8601Util.toLocalTime(jsonParser.getValueAsString());
    } catch (Throwable e) {
      throw new IOException(
          "Failed to deserialize the ISO 8601 value (" + jsonParser.getValueAsString() + ")");
    }
  }
}
