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

package digital.inception.core.util;

//~--- JDK imports ------------------------------------------------------------

import java.security.SecureRandom;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * The <code>PasswordUtil</code> class is a utility class which provides password-related utility
 * methods.
 *
 * @author Marcus Portmann
 */
public class PasswordUtil
{
  /**
   *  Special characters allowed in password.
   */
  private static final String ALLOWED_SPL_CHARACTERS = "!@#$%^&*()_+";
  private static Random random = new SecureRandom();

  /**
   * Generate a random password.
   *
   * @return the random password
   */
  public static String generateRandomPassword()
  {
    Stream<Character> pwdStream = Stream.concat(getRandomNumbers(2), Stream.concat(
        getRandomSpecialChars(2), Stream.concat(getRandomAlphabets(2, true), getRandomAlphabets(4,
        false))));
    List<Character> charList = pwdStream.collect(Collectors.toList());
    Collections.shuffle(charList);

    String password = charList.stream().collect(StringBuilder::new, StringBuilder::append,
        StringBuilder::append).toString();

    return password;
  }

  private static Stream<Character> getRandomAlphabets(int count, boolean upperCase)
  {
    IntStream characters = null;
    if (upperCase)
    {
      characters = random.ints(count, 65, 90);
    }
    else
    {
      characters = random.ints(count, 97, 122);
    }

    return characters.mapToObj(data -> (char) data);
  }

  private static Stream<Character> getRandomNumbers(int count)
  {
    IntStream numbers = random.ints(count, 48, 57);

    return numbers.mapToObj(data -> (char) data);
  }

  private static Stream<Character> getRandomSpecialChars(int count)
  {
    IntStream specialChars = random.ints(count, 33, 45);

    return specialChars.mapToObj(data -> (char) data);
  }
}