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

package digital.inception.codes.test;

// ~--- non-JDK imports --------------------------------------------------------

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import digital.inception.codes.Code;
import digital.inception.codes.CodeCategory;
import digital.inception.codes.ICodesService;
import digital.inception.test.TestClassRunner;
import digital.inception.test.TestConfiguration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

// ~--- JDK imports ------------------------------------------------------------

/**
 * The <code>CodesServiceTest</code> class contains the implementation of the JUnit tests for the
 * <code>CodesService</code> class.
 *
 * @author Marcus Portmann
 */
@RunWith(TestClassRunner.class)
@ContextConfiguration(classes = {TestConfiguration.class})
@TestExecutionListeners(
    listeners = {
      DependencyInjectionTestExecutionListener.class,
      DirtiesContextTestExecutionListener.class,
      TransactionalTestExecutionListener.class
    })
public class CodesServiceTest {

  private static int codeCategoryCount;

  private static int codeCount;

  /** The Codes Service. */
  @Autowired private ICodesService codesService;

  private static synchronized CodeCategory getTestCodeCategoryDetails() {
    codeCategoryCount++;

    CodeCategory codeCategory = new CodeCategory();

    codeCategory.setId("TestCodeCategory" + codeCategoryCount);
    codeCategory.setName("Test Code Category Name " + codeCategoryCount);
    codeCategory.setData("Test Code Category Data " + codeCategoryCount);

    return codeCategory;
  }

  private static synchronized Code getTestCodeDetails(String codeCategoryId) {
    codeCount++;

    Code code = new Code();
    code.setId("Test Code Id " + codeCount);
    code.setCodeCategoryId(codeCategoryId);
    code.setName("Test Code Name " + codeCount);
    code.setValue("Test Code Value " + codeCount);

    return code;
  }

  @SuppressWarnings("unused")
  private static synchronized Code getTestCodeWithoutIdDetails(String codeCategoryId) {
    codeCount++;

    Code code = new Code();
    code.setCodeCategoryId(codeCategoryId);
    code.setName("Test Code Name " + codeCount);
    code.setValue("Test Code Value " + codeCount);

    return code;
  }

  /** Test the local custom code category functionality. */
  @Test
  public void codeCategoryTest() throws Exception {
    CodeCategory codeCategory = getTestCodeCategoryDetails();

    codesService.createCodeCategory(codeCategory);

    CodeCategory retrievedCodeCategory = codesService.getCodeCategory(codeCategory.getId());

    compareCodeCategories(codeCategory, retrievedCodeCategory);

    String retrievedCodeCategoryName = codesService.getCodeCategoryName(codeCategory.getId());

    assertEquals(
        "The correct code category name was not retrieved",
        codeCategory.getName(),
        retrievedCodeCategoryName);

    codeCategory.setName(codeCategory.getName() + " Updated");

    codesService.updateCodeCategory(codeCategory);

    CodeCategory updatedCodeCategory = codesService.getCodeCategory(codeCategory.getId());

    compareCodeCategories(codeCategory, updatedCodeCategory);

    LocalDateTime lastUpdated = codesService.getCodeCategoryUpdated(codeCategory.getId());

    if (!lastUpdated.isEqual(updatedCodeCategory.getUpdated())) {
      fail("The date and time the code category was last updated was not correct");
    }

    retrievedCodeCategory = codesService.getCodeCategory(codeCategory.getId());

    compareCodeCategories(codeCategory, retrievedCodeCategory);

    String codeCategoryData = "codeCategoryData";

    codesService.updateCodeCategoryData(codeCategory.getId(), codeCategoryData);

    String retrievedCodeCategoryData = codesService.getCodeCategoryData(codeCategory.getId());

    if (!codeCategoryData.equals(retrievedCodeCategoryData)) {
      fail(
          "Failed to retrieve the correct data for the code category ("
              + codeCategory.getId()
              + ")");
    }

    retrievedCodeCategoryData =
        codesService.getCodeCategoryDataWithParameters(codeCategory.getId(), new HashMap<>());

    if (!codeCategoryData.equals(retrievedCodeCategoryData)) {
      fail(
          "Failed to retrieve the correct data for the code category ("
              + codeCategory.getId()
              + ")");
    }

    codesService.deleteCodeCategory(codeCategory.getId());

    if (codesService.codeCategoryExists(codeCategory.getId())) {
      fail(
          "Retrieved the code category ("
              + codeCategory.getId()
              + ") that should have been deleted");
    }
  }

  /** Test the code provider functionality. */
  @Test
  public void codeProviderTest() throws Exception {
    CodeCategory retrievedCodeCategory = codesService.getCodeCategory("TestCodeCategory");

    assertNotNull("The code category is null", retrievedCodeCategory);
  }

  /** Test the code functionality. */
  @Test
  public void codeTest() throws Exception {
    CodeCategory codeCategory = getTestCodeCategoryDetails();

    codesService.createCodeCategory(codeCategory);

    assertTrue(codesService.codeCategoryExists(codeCategory.getId()));

    CodeCategory retrievedCodeCategory = codesService.getCodeCategory(codeCategory.getId());

    compareCodeCategories(codeCategory, retrievedCodeCategory);

    Code code = getTestCodeDetails(codeCategory.getId());

    codesService.createCode(code);

    Code retrievedCode = codesService.getCode(codeCategory.getId(), code.getId());

    compareCodes(code, retrievedCode);

    String retrievedCodeName = codesService.getCodeName(codeCategory.getId(), code.getId());

    assertEquals("The correct code name was not retrieved", code.getName(), retrievedCodeName);

    code.setName("Updated " + code.getName());
    code.setValue("Updated " + code.getValue());

    codesService.updateCode(code);

    retrievedCode = codesService.getCode(codeCategory.getId(), code.getId());

    compareCodes(code, retrievedCode);

    codesService.deleteCode(codeCategory.getId(), code.getId());

    if (codesService.codeExists(codeCategory.getId(), code.getId())) {
      fail(
          "Retrieved the code ("
              + code.getId()
              + ") for the code category ("
              + codeCategory.getId()
              + ") that should have been deleted");
    }
  }

  /** Test the codes functionality. */
  @Test
  public void codesTest() throws Exception {
    CodeCategory codeCategory = getTestCodeCategoryDetails();

    codesService.createCodeCategory(codeCategory);

    CodeCategory retrievedCodeCategory = codesService.getCodeCategory(codeCategory.getId());

    compareCodeCategories(codeCategory, retrievedCodeCategory);

    List<Code> codes = new ArrayList<>();

    for (int i = 0; i < 10; i++) {
      codes.add(getTestCodeDetails(codeCategory.getId()));
    }

    for (Code code : codes) {
      codesService.createCode(code);
    }

    List<Code> retrievedCodes = codesService.getCodesForCodeCategory(codeCategory.getId());

    compareCodes(codes, retrievedCodes);

    retrievedCodes =
        codesService.getCodesForCodeCategoryWithParameters(codeCategory.getId(), new HashMap<>());

    compareCodes(codes, retrievedCodes);
  }

  /** Test the code category and code constructors. */
  @Test
  public void constructorTest() {
    CodeCategory codeCategory = getTestCodeCategoryDetails();

    CodeCategory codeCategory1 =
        new CodeCategory(codeCategory.getId(), codeCategory.getName(), codeCategory.getData());

    compareCodeCategories(codeCategory, codeCategory1);

    Code code = getTestCodeDetails(codeCategory.getId());

    Code code1 = new Code(code.getCodeCategoryId());

    assertEquals(code.getCodeCategoryId(), code1.getCodeCategoryId());

    Code code2 = new Code(code.getId(), code.getCodeCategoryId(), code.getName(), code.getValue());

    compareCodes(code, code2);
  }

  /** Test the retrieve code categories functionality. */
  @Test
  public void getCodeCategoriesTest() throws Exception {
    CodeCategory codeCategory = getTestCodeCategoryDetails();

    codesService.createCodeCategory(codeCategory);

    List<CodeCategory> retrievedCodeCategories = codesService.getCodeCategories();

    assertEquals(
        "The correct number of code categories was not retrieved",
        1,
        retrievedCodeCategories.size());

    compareCodeCategories(codeCategory, retrievedCodeCategories.get(0));

    retrievedCodeCategories = codesService.getCodeCategories();

    assertEquals(
        "The correct number of code categories was not retrieved",
        1,
        retrievedCodeCategories.size());

    compareCodeCategories(codeCategory, retrievedCodeCategories.get(0));
  }

  private void compareCodeCategories(CodeCategory codeCategory1, CodeCategory codeCategory2) {
    assertEquals(
        "The ID values for the two code categories do not match",
        codeCategory1.getId(),
        codeCategory2.getId());
    assertEquals(
        "The name values for the two code categories do not match",
        codeCategory1.getName(),
        codeCategory2.getName());
    assertEquals(
        "The data values for the two code categories do not match",
        codeCategory1.getData(),
        codeCategory2.getData());
  }

  private void compareCodes(Code code1, Code code2) {
    assertEquals("The ID values for the two codes do not match", code1.getId(), code2.getId());
    assertEquals(
        "The category ID values for the two codes do not match",
        code1.getCodeCategoryId(),
        code2.getCodeCategoryId());
    assertEquals(
        "The name values for the two codes do not match", code1.getName(), code2.getName());
    assertEquals(
        "The value values for the two codes do not match", code1.getValue(), code2.getValue());
  }

  private void compareCodes(List<Code> codes1, List<Code> codes2) {
    for (Code code1 : codes1) {
      boolean foundCode = false;

      for (Code code2 : codes2) {
        if (code1.getId().equals(code2.getId())) {
          compareCodes(code1, code2);

          foundCode = true;

          break;
        }
      }

      if (!foundCode) {
        fail("Failed to find the code (" + code1.getId() + ") in the list of codes");
      }
    }
  }
}
