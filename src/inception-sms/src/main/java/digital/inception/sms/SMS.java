/*
 * Copyright 2017 Marcus Portmann
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

package digital.inception.sms;

//~--- JDK imports ------------------------------------------------------------

import java.io.Serializable;

import java.util.Date;

/**
 * The <code>SMS</code> class holds the information for a SMS.
 *
 * @author Marcus Portmann
 */
@SuppressWarnings("unused")
public class SMS
  implements Serializable
{
  private static final long serialVersionUID = 1000000;

  /**
   * The ID used to uniquely identify the SMS.
   */
  private long id;

  /**
   * The date and time the last attempt was made to send the SMS.
   */
  private Date lastProcessed;

  /**
   * The name of the entity that has locked the SMS for sending.
   */
  private String lockName;

  /**
   * The message to send.
   */
  private String message;

  /**
   * The mobile number to send the SMS to.
   */
  private String mobileNumber;

  /**
   * The number of times that the sending of the SMS was attempted.
   */
  private int sendAttempts;

  /**
   * The status of the SMS.
   */
  private SMSStatus status;

  /**
   * Constructs a new <code>SMS</code>.
   */
  public SMS() {}

  /**
   * Constructs a new <code>SMS</code>.
   *
   * @param mobileNumber the mobile number to send the SMS to
   * @param message      the message to send
   */
  public SMS(String mobileNumber, String message)
  {
    this.mobileNumber = mobileNumber;
    this.message = message;
  }

  /**
   * Constructs a new <code>SMS</code>.
   *
   * @param mobileNumber the mobile number to send the SMS to
   * @param message      the message to send
   * @param status       the status of the SMS
   */
  SMS(String mobileNumber, String message, SMSStatus status)
  {
    this.mobileNumber = mobileNumber;
    this.message = message;
    this.status = status;
  }

  /**
   * Constructs a new <code>SMS</code>.
   *
   * @param id            the ID used to uniquely identify the SMS
   * @param mobileNumber  the mobile number to send the SMS to
   * @param message       the message to send
   * @param status        the status of the SMS
   * @param sendAttempts  the number of times that the sending of the SMS was attempted
   * @param lockName      the name of the entity that has locked the SMS for sending
   * @param lastProcessed the date and time the last attempt was made to send the SMS
   */
  SMS(long id, String mobileNumber, String message, SMSStatus status, int sendAttempts,
      String lockName, Date lastProcessed)
  {
    this.id = id;
    this.mobileNumber = mobileNumber;
    this.message = message;
    this.status = status;
    this.sendAttempts = sendAttempts;
    this.lockName = lockName;
    this.lastProcessed = lastProcessed;
  }

  /**
   * Returns the ID used to uniquely identify the SMS.
   *
   * @return the ID used to uniquely identify the SMS
   */
  public long getId()
  {
    return id;
  }

  /**
   * Returns the date and time the last attempt was made to send the SMS.
   *
   * @return the date and time the last attempt was made to send the SMS
   */
  public Date getLastProcessed()
  {
    return lastProcessed;
  }

  /**
   * Returns the name of the entity that has locked the SMS for sending.
   *
   * @return the name of the entity that has locked the SMS for sending
   */
  public String getLockName()
  {
    return lockName;
  }

  /**
   * Returns the message to send.
   *
   * @return the message to send
   */
  public String getMessage()
  {
    return message;
  }

  /**
   * Returns the mobile number to send the SMS to.
   *
   * @return the mobile number to send the SMS to
   */
  public String getMobileNumber()
  {
    return mobileNumber;
  }

  /**
   * Returns the number of times that the sending of the SMS was attempted.
   *
   * @return the number of times that the sending of the SMS was attempted
   */
  public int getSendAttempts()
  {
    return sendAttempts;
  }

  /**
   * Returns the status of the SMS.
   *
   * @return the status of the SMS
   */
  public SMSStatus getStatus()
  {
    return status;
  }

  /**
   * Set the ID used to uniquely identify the SMS.
   *
   * @param id the ID used to uniquely identify the SMS
   */
  public void setId(long id)
  {
    this.id = id;
  }

  /**
   * Set the date and time the last attempt was made to send the SMS.
   *
   * @param lastProcessed the date and time the last attempt was made to send the SMS
   */
  public void setLastProcessed(Date lastProcessed)
  {
    this.lastProcessed = lastProcessed;
  }

  /**
   * Set the name of the entity that has locked the SMS for sending.
   *
   * @param lockName the name of the entity that has locked the SMS for sending
   */
  public void setLockName(String lockName)
  {
    this.lockName = lockName;
  }

  /**
   * Set the message to send.
   *
   * @param message the message to send
   */
  public void setMessage(String message)
  {
    this.message = message;
  }

  /**
   * Set the mobile number to send the SMS to.
   *
   * @param mobileNumber the mobile number to send the SMS to
   */
  public void setMobileNumber(String mobileNumber)
  {
    this.mobileNumber = mobileNumber;
  }

  /**
   * Set the number of times that the sending of the SMS was attempted.
   *
   * @param sendAttempts the number of times that the sending of the SMS was attempted
   */
  public void setSendAttempts(int sendAttempts)
  {
    this.sendAttempts = sendAttempts;
  }

  /**
   * Set the status of the SMS.
   *
   * @param status the status of the SMS
   */
  public void setStatus(SMSStatus status)
  {
    this.status = status;
  }
}
