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

import {Inject, Injectable} from '@angular/core';
import {Observable, throwError} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {HttpClient, HttpErrorResponse, HttpResponse} from '@angular/common/http';
import {Code} from './code';
import {CodeCategory} from './code-category';
import {
  CodeCategoryNotFoundError,
  CodeNotFoundError,
  CodesServiceError,
  DuplicateCodeCategoryError,
  DuplicateCodeError
} from './codes.service.errors';
import {CommunicationError} from '../../core/errors/communication-error';
import {ApiError} from '../../core/errors/api-error';
import {SystemUnavailableError} from '../../core/errors/system-unavailable-error';
import {CodeCategorySummary} from './code-category-summary';
import {AccessDeniedError} from '../../core/errors/access-denied-error';
import {INCEPTION_CONFIG, InceptionConfig} from '../../inception-config';

/**
 * The Codes Service implementation.
 *
 * @author Marcus Portmann
 */
@Injectable({
  providedIn: 'root'
})
export class CodesService {

  /**
   * Constructs a new CodesService.
   *
   * @param config     The Inception configuration.
   * @param httpClient The HTTP client.
   */
  constructor(@Inject(INCEPTION_CONFIG) private config: InceptionConfig, private httpClient: HttpClient) {
    console.log('Initializing the Codes Service');
  }

  /**
   * Create a code.
   *
   * @param code The code to create.
   *
   * @return True if the code was created successfully or false otherwise.
   */
  createCode(code: Code): Observable<boolean> {
    return this.httpClient.post<boolean>(
      this.config.codesApiUrlPrefix + '/code-categories/' + encodeURIComponent(code.codeCategoryId) + '/codes', code,
      {observe: 'response'})
    .pipe(map((httpResponse: HttpResponse<boolean>) => {
      return httpResponse.status === 204;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ApiError.isApiError(httpErrorResponse)) {
        const apiError: ApiError = new ApiError(httpErrorResponse);

        if (apiError.code === 'CodeCategoryNotFoundError') {
          return throwError(new CodeCategoryNotFoundError(apiError));
        } else if (apiError.code === 'DuplicateCodeError') {
          return throwError(new DuplicateCodeError(apiError));
        } else {
          return throwError(new CodesServiceError('Failed to create the code.', apiError));
        }
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else {
        return throwError(new SystemUnavailableError(httpErrorResponse));
      }
    }));
  }

  /**
   * Create a code category.
   *
   * @param codeCategory The code category to create.
   *
   * @return True if the code category was created successfully or false otherwise.
   */
  createCodeCategory(codeCategory: CodeCategory): Observable<boolean> {
    return this.httpClient.post<boolean>(this.config.codesApiUrlPrefix + '/code-categories', codeCategory,
      {observe: 'response'})
    .pipe(map((httpResponse: HttpResponse<boolean>) => {
      return httpResponse.status === 204;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ApiError.isApiError(httpErrorResponse)) {
        const apiError: ApiError = new ApiError(httpErrorResponse);

        if (apiError.code === 'DuplicateCodeCategoryError') {
          return throwError(new DuplicateCodeCategoryError(apiError));
        } else {
          return throwError(new CodesServiceError('Failed to create the code category.', apiError));
        }
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else {
        return throwError(new SystemUnavailableError(httpErrorResponse));
      }
    }));
  }

  /**
   * Delete the code.
   *
   * @param codeCategoryId The ID uniquely identifying the code category the code is
   *                       associated with.
   * @param codeId         The ID uniquely identifying the code.
   *
   * @return True if the code was deleted or false otherwise.
   */
  deleteCode(codeCategoryId: string, codeId: string): Observable<boolean> {
    return this.httpClient.delete<boolean>(
      this.config.codesApiUrlPrefix + '/code-categories/' + encodeURIComponent(codeCategoryId) + '/codes/' +
      encodeURIComponent(codeId), {observe: 'response'})
    .pipe(map((httpResponse: HttpResponse<boolean>) => {
      return httpResponse.status === 204;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ApiError.isApiError(httpErrorResponse)) {
        const apiError: ApiError = new ApiError(httpErrorResponse);

        if (apiError.code === 'CodeNotFoundError') {
          return throwError(new CodeNotFoundError(apiError));
        } else {
          return throwError(new CodesServiceError('Failed to delete the code.', apiError));
        }
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else {
        return throwError(new SystemUnavailableError(httpErrorResponse));
      }
    }));
  }

  /**
   * Delete the code category.
   *
   * @param codeCategoryId The ID uniquely identifying the code category.
   *
   * @return True if the code category was deleted or false otherwise.
   */
  deleteCodeCategory(codeCategoryId: string): Observable<boolean> {
    return this.httpClient.delete<boolean>(
      this.config.codesApiUrlPrefix + '/code-categories/' + encodeURIComponent(codeCategoryId), {observe: 'response'})
    .pipe(map((httpResponse: HttpResponse<boolean>) => {
      return httpResponse.status === 204;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ApiError.isApiError(httpErrorResponse)) {
        const apiError: ApiError = new ApiError(httpErrorResponse);

        if (apiError.code === 'CodeCategoryNotFoundError') {
          return throwError(new CodeCategoryNotFoundError(apiError));
        } else {
          return throwError(new CodesServiceError('Failed to delete the code category.', apiError));
        }
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else {
        return throwError(new SystemUnavailableError(httpErrorResponse));
      }
    }));
  }

  /**
   * Retrieve the code.
   *
   * @param codeCategoryId The ID uniquely identifying the code category the code is
   *                       associated with.
   * @param codeId         The ID uniquely identifying the code.
   *
   * @return The code.
   */
  getCode(codeCategoryId: string, codeId: string): Observable<Code> {
    return this.httpClient.get<Code>(
      this.config.codesApiUrlPrefix + '/code-categories/' + encodeURIComponent(codeCategoryId) + '/codes/' +
      encodeURIComponent(codeId), {reportProgress: true}).pipe(map((code: Code) => {
      return code;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ApiError.isApiError(httpErrorResponse)) {
        const apiError: ApiError = new ApiError(httpErrorResponse);

        if (apiError.code === 'CodeNotFoundError') {
          return throwError(new CodeNotFoundError(apiError));
        } else {
          return throwError(new CodesServiceError('Failed to retrieve the code.', apiError));
        }
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else {
        return throwError(new SystemUnavailableError(httpErrorResponse));
      }
    }));
  }

  /**
   * Retrieve all the code categories.
   *
   * @return The code categories.
   */
  getCodeCategories(): Observable<CodeCategory[]> {
    return this.httpClient.get<CodeCategory[]>(this.config.codesApiUrlPrefix + '/code-categories',
      {reportProgress: true})
    .pipe(map((codeCategories: CodeCategory[]) => {
      return codeCategories;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ApiError.isApiError(httpErrorResponse)) {
        const apiError: ApiError = new ApiError(httpErrorResponse);

        return throwError(new CodesServiceError('Failed to retrieve the code categories.', apiError));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else {
        return throwError(new SystemUnavailableError(httpErrorResponse));
      }
    }));
  }

  /**
   * Retrieve the code category.
   *
   * @param codeCategoryId The ID uniquely identifying the code category.
   *
   * @return The code category.
   */
  getCodeCategory(codeCategoryId: string): Observable<CodeCategory> {
    return this.httpClient.get<CodeCategory>(
      this.config.codesApiUrlPrefix + '/code-categories/' + encodeURIComponent(codeCategoryId), {reportProgress: true})
    .pipe(map((codeCategory: CodeCategory) => {
      return codeCategory;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ApiError.isApiError(httpErrorResponse)) {
        const apiError: ApiError = new ApiError(httpErrorResponse);

        if (apiError.code === 'CodeCategoryNotFoundError') {
          return throwError(new CodeCategoryNotFoundError(apiError));
        } else {
          return throwError(new CodesServiceError('Failed to retrieve the code category.', apiError));
        }
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else {
        return throwError(new SystemUnavailableError(httpErrorResponse));
      }
    }));
  }

  /**
   * Retrieve the name of the code category.
   *
   * @param codeCategoryId The ID uniquely identifying the code category.
   *
   * @return The name of the code category.
   */
  getCodeCategoryName(codeCategoryId: string): Observable<string> {
    return this.httpClient.get<string>(
      this.config.codesApiUrlPrefix + '/code-categories/' + encodeURIComponent(codeCategoryId) + '/name', {
        reportProgress: true,
      }).pipe(map((codeCategoryName: string) => {
      return codeCategoryName;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ApiError.isApiError(httpErrorResponse)) {
        const apiError: ApiError = new ApiError(httpErrorResponse);

        if (apiError.code === 'CodeCategoryNotFoundError') {
          return throwError(new CodeCategoryNotFoundError(apiError));
        } else {
          return throwError(new CodesServiceError('Failed to retrieve the code category name.', apiError));
        }
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else {
        return throwError(new SystemUnavailableError(httpErrorResponse));
      }
    }));
  }

  /**
   * Retrieve the name of the code.
   *
   * @param codeCategoryId The ID uniquely identifying the code category the code is
   *                       associated with.
   * @param codeId         The ID uniquely identifying the code.
   *
   * @return The name of the code.
   */
  getCodeName(codeCategoryId: string, codeId: string): Observable<string> {
    return this.httpClient.get<string>(
      this.config.codesApiUrlPrefix + '/code-categories/' + encodeURIComponent(codeCategoryId) + '/codes/' +
      encodeURIComponent(codeId) + '/name', {
        reportProgress: true
      }).pipe(map((codeName: string) => {
      return codeName;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ApiError.isApiError(httpErrorResponse)) {
        const apiError: ApiError = new ApiError(httpErrorResponse);

        if (apiError.code === 'CodeNotFoundError') {
          return throwError(new CodeNotFoundError(apiError));
        } else {
          return throwError(new CodesServiceError('Failed to retrieve the code name.', apiError));
        }
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else {
        return throwError(new SystemUnavailableError(httpErrorResponse));
      }
    }));
  }

  /**
   * Retrieve the codes for the code category.
   *
   * @param codeCategoryId The ID uniquely identifying the code category.
   *
   * @return the codes for the code category
   */
  getCodesForCodeCategory(codeCategoryId: string): Observable<Code[]> {
    return this.httpClient.get<Code[]>(
      this.config.codesApiUrlPrefix + '/code-categories/' + encodeURIComponent(codeCategoryId) + '/codes',
      {reportProgress: true}).pipe(map((codes: Code[]) => {
      return codes;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ApiError.isApiError(httpErrorResponse)) {
        const apiError: ApiError = new ApiError(httpErrorResponse);

        if (apiError.code === 'CodeCategoryNotFoundError') {
          return throwError(new CodeCategoryNotFoundError(apiError));
        } else {
          return throwError(new CodesServiceError('Failed to retrieve the codes.', apiError));
        }
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else {
        return throwError(new SystemUnavailableError(httpErrorResponse));
      }
    }));
  }

  /**
   * Retrieve the summaries for all code categories.
   *
   * @return The code category summaries.
   */
  getCodeCategorySummaries(): Observable<CodeCategorySummary[]> {
    return this.httpClient.get<CodeCategory[]>(this.config.codesApiUrlPrefix + '/code-category-summaries',
      {reportProgress: true})
    .pipe(map((codeCategorySummaries: CodeCategorySummary[]) => {
      return codeCategorySummaries;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (ApiError.isApiError(httpErrorResponse)) {
        const apiError: ApiError = new ApiError(httpErrorResponse);

        return throwError(
          new CodesServiceError('Failed to retrieve the summaries for the code categories.', apiError));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else {
        return throwError(new SystemUnavailableError(httpErrorResponse));
      }
    }));
  }

  /**
   * Update the code.
   *
   * @param code The code to update.
   *
   * @return True if the code was updated successfully or false otherwise.
   */
  updateCode(code: Code): Observable<boolean> {
    return this.httpClient.put<boolean>(
      this.config.codesApiUrlPrefix + '/code-categories/' + encodeURIComponent(code.codeCategoryId) + '/codes/' +
      encodeURIComponent(code.id), code, {observe: 'response'}).pipe(map((httpResponse: HttpResponse<boolean>) => {
      return httpResponse.status === 204;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ApiError.isApiError(httpErrorResponse)) {
        const apiError: ApiError = new ApiError(httpErrorResponse);

        if (apiError.code === 'CodeNotFoundError') {
          return throwError(new CodeNotFoundError(apiError));
        } else {
          return throwError(new CodesServiceError('Failed to update the code.', apiError));
        }
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else {
        return throwError(new SystemUnavailableError(httpErrorResponse));
      }
    }));
  }

  /**
   * Update the code category.
   *
   * @param codeCategory The code category to update.
   *
   * @return True if the code category was updated successfully or false otherwise.
   */
  updateCodeCategory(codeCategory: CodeCategory): Observable<boolean> {
    return this.httpClient.put<boolean>(
      this.config.codesApiUrlPrefix + '/code-categories/' + encodeURIComponent(codeCategory.id), codeCategory,
      {observe: 'response'}).pipe(map((httpResponse: HttpResponse<boolean>) => {
      return httpResponse.status === 204;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ApiError.isApiError(httpErrorResponse)) {
        const apiError: ApiError = new ApiError(httpErrorResponse);

        if (apiError.code === 'CodeCategoryNotFoundError') {
          return throwError(new CodeCategoryNotFoundError(apiError));
        } else {
          return throwError(new CodesServiceError('Failed to update the code category.', apiError));
        }
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else {
        return throwError(new SystemUnavailableError(httpErrorResponse));
      }
    }));
  }
}
