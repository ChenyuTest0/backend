// cSpell:ignore traceid spanid
package com.accenture.acts.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

import org.mockito.Mockito
import org.slf4j.MDC
import spock.lang.Specification
import spock.lang.Unroll

import com.accenture.acts.utils.HttpRequestUtils

class LoggingFilterSpec extends Specification {
  private httpServletRequestMock
  private httpServletResponse
  private filterChainMock
  private httpRequestUtilsMock

  def setup() {
    httpServletRequestMock = Mock(HttpServletRequest)
    httpServletResponse = Mock(HttpServletResponse)
    filterChainMock = Mock(FilterChain)
    httpRequestUtilsMock = Mockito.mockStatic(HttpRequestUtils)
  }

  def cleanup() {
    httpRequestUtilsMock.close()
  }

  @Unroll
  def "doFilterInternal - #key"() {
    setup:
    def loggingFilter = new LoggingFilter()

    and:
    httpRequestUtilsMock.when(HttpRequestUtils.getHeaderValue(header)).thenReturn(value)

    when:
    loggingFilter.doFilterInternal(httpServletRequestMock, httpServletResponse, filterChainMock)

    then:
    reportInfo "$key として $header の値が設定されていること"
    MDC.get(key) == value

    and:
    1 * filterChainMock.doFilter(_, _)

    where:
    header         | key         || value
    'x-device-id'  | 'deviceId'  || '12345'
    'x-b3-traceid' | 'traceId'   || 'example1'
    'x-b3-spanid'  | 'spanId'    || 'example2'
    'x-request-id' | 'requestId' || 'example3'
  }
}
