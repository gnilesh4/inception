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

package digital.inception.application.test;

//~--- JDK imports ------------------------------------------------------------

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The <code>TestData</code> class.
 *
 * @author Marcus Portmann
 */
@Entity
@Table(schema = "TEST", name = "TEST_DATA")
public class TestData
{
  /**
   * The ID.
   */
  @Id
  @Column(name = "id", nullable = false)
  private String id;

  /**
   * The name.
   */
  @Column(name = "name", nullable = false)
  private String name;

  /**
   * The value.
   */
  @Column(name = "value", nullable = false)
  private String value;

  /**
   * Constructs a new <code>TestData</code>.
   */
  @SuppressWarnings("unused")
  TestData() {}

  /**
   * Constructs a new <code>TestData</code>.
   *
   * @param id    the ID
   * @param name  the name
   * @param value the value
   */
  TestData(String id, String name, String value)
  {
    this.id = id;
    this.name = name;
    this.value = value;
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   *
   * @param object the reference object with which to compare
   *
   * @return <code>true</code> if this object is the same as the object argument otherwise
   *         <code>false</code>
   */
  @Override
  public boolean equals(Object object)
  {
    if (this == object)
    {
      return true;
    }

    if (object == null)
    {
      return false;
    }

    if (getClass() != object.getClass())
    {
      return false;
    }

    TestData other = (TestData) object;

    return (id != null) && id.equals(other.id);
  }

  /**
   * Returns a hash code value for the object.
   *
   * @return a hash code value for the object
   */
  @Override
  public int hashCode()
  {
    return (id == null)
        ? 0
        : id.hashCode();
  }

  /**
   * Returns the ID.
   *
   * @return the ID
   */
  String getId()
  {
    return id;
  }

  /**
   * Returns the name.
   *
   * @return the name
   */
  String getName()
  {
    return name;
  }

  /**
   * Returns the value.
   *
   * @return the value
   */
  String getValue()
  {
    return value;
  }
}