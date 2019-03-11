///*
// * Copyright 2019 Marcus Portmann
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *   http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package digital.inception.rs.oauth;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.AccessDeniedException;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
//import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
//import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
//import org.springframework.web.HttpRequestMethodNotSupportedException;
//
///**
// * The <code>ExceptionTranslator</code> class provides the exception translator implementation.
// *
// * @author Marcus Portmann
// */
//public class ExceptionTranslator implements WebResponseExceptionTranslator<OAuth2Exception>
//{
//
//  @Override
//  public ResponseEntity<OAuth2Exception> translate(Exception e)
//    throws Exception
//  {
//
//
//    /*
//    Throwable[] causeChain = this.throwableAnalyzer.determineCauseChain(e);
//    Exception ase = (OAuth2Exception)this.throwableAnalyzer.getFirstThrowableOfType(OAuth2Exception.class, causeChain);
//    if (ase != null) {
//      return this.handleOAuth2Exception((OAuth2Exception)ase);
//    } else {
//      Exception ase = (AuthenticationException)this.throwableAnalyzer.getFirstThrowableOfType(AuthenticationException.class, causeChain);
//      if (ase != null) {
//        return this.handleOAuth2Exception(new DefaultWebResponseExceptionTranslator.UnauthorizedException(e.getMessage(), e));
//      } else {
//        Exception ase = (AccessDeniedException)this.throwableAnalyzer.getFirstThrowableOfType(AccessDeniedException.class, causeChain);
//        if (ase instanceof AccessDeniedException) {
//          return this.handleOAuth2Exception(new DefaultWebResponseExceptionTranslator.ForbiddenException(ase.getMessage(), ase));
//        } else {
//          Exception ase = (HttpRequestMethodNotSupportedException)this.throwableAnalyzer.getFirstThrowableOfType(HttpRequestMethodNotSupportedException.class, causeChain);
//          return ase instanceof HttpRequestMethodNotSupportedException ? this.handleOAuth2Exception(new DefaultWebResponseExceptionTranslator.MethodNotAllowed(ase.getMessage(), ase)) : this.handleOAuth2Exception(new DefaultWebResponseExceptionTranslator.ServerErrorException(
//            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), e));
//        }
//      }
//    }
//    */
//  }
//}