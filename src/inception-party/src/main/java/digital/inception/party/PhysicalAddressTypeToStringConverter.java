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

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

/**
 * The <code>PhysicalAddressTypeToStringConverter</code> class implements the Spring converter that
 * converts a <code>PhysicalAddressType</code> type into a <code>String</code> type.
 *
 * @author Marcus Portmann
 */
@Component
@WritingConverter
public class PhysicalAddressTypeToStringConverter
    implements Converter<PhysicalAddressType, String> {

  /** Constructs a new <code>PhysicalAddressTypeToStringConverter</code>. */
  public PhysicalAddressTypeToStringConverter() {}

  @Override
  public String convert(PhysicalAddressType source) {
    return source.code();
  }
}
