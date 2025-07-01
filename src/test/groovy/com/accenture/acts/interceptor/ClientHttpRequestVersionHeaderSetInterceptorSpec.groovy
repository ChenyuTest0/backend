package com.accenture.acts.interceptor

// cSpell:disable-next-line
import nl.altindag.log.LogCaptor
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import spock.lang.Specification
import spock.lang.Title

import com.accenture.acts.utils.LogUtil

@Title('request header version interceptor test')
class ClientHttpRequestVersionHeaderSetInterceptorSpec extends Specification {

  def "指定したVersionHeaderが設定されること"() {
    setup:
    def interceptor = new ClientHttpRequestVersionHeaderSetInterceptor('version')
    def executionMock = Mock(ClientHttpRequestExecution)
    def httpHeaders = new HttpHeaders()
    def requestMock = Mock(HttpRequest)
    def body = [] as byte[]

    and: '内部利用するLogUtilの初期化'
    def logUtil = new LogUtil()
    logUtil.limitedLogLength = 512
    logUtil.init()

    and: 'ログキャプチャ'
    def logCaptor = LogCaptor.forClass(ClientHttpRequestVersionHeaderSetInterceptor)

    when:
    2 * requestMock.headers >> httpHeaders
    1 * executionMock.execute(requestMock, body)
    interceptor.intercept(requestMock, body, executionMock)

    then:
    httpHeaders.get('x-request-version').get(0) == 'version'

    and:
    logCaptor.getDebugLogs() == [
      'Http request Header : [x-request-version:"version"]'
    ]
  }

  def "ログレベルがINFO以上の場合ログが出力されないこと"() {
    setup:
    def interceptor = new ClientHttpRequestVersionHeaderSetInterceptor('version')
    def executionMock = Mock(ClientHttpRequestExecution)
    def httpHeaders = new HttpHeaders()
    def requestMock = Mock(HttpRequest)
    def body = [] as byte[]

    and: '内部利用するLogUtilの初期化'
    def logUtil = new LogUtil()
    logUtil.limitedLogLength = 512
    logUtil.init()

    and: 'ログレベルを変更'
    def logCaptor = LogCaptor.forClass(ClientHttpRequestVersionHeaderSetInterceptor)
    logCaptor.setLogLevelToInfo()

    when:
    1 * requestMock.headers >> httpHeaders
    1 * executionMock.execute(requestMock, body)
    interceptor.intercept(requestMock, body, executionMock)

    then:
    httpHeaders.get('x-request-version').get(0) == 'version'

    and:
    logCaptor.getDebugLogs() == []
  }
}
