package com.accenture.acts.logback

import org.mockito.Mockito
import org.springframework.test.util.ReflectionTestUtils
import spock.lang.Specification
import spock.lang.Title
import spock.lang.Unroll

import com.accenture.acts.utils.EnvironmentUtils

@Title('LogMaskingProcessor-Unit Tests')
class LogMaskingProcessorSpec extends Specification {

  def setup() {
    // Reset cache
    ReflectionTestUtils.setField(LogMaskingProcessor, 'cachedMaskMap', null)
  }

  @Unroll
  def 'test-fetchEnvironmentKeySet- expectation: #expected, input: #inputString' (){
    when:
    def actualResult = LogMaskingProcessor.fetchEnvironmentKeySet(inputString)

    then:
    actualResult.containsAll(expected)

    where:
    inputString | expected
    null        | []
    ''          | []
    'KEY1,KEY2' | ['KEY1', 'KEY2']
    'KEY3,'     | ['KEY3']
    ''          | []
  }

  @Unroll
  def 'test-getMask expectation: #expected, input: #inputString'(){
    when:
    def actualResult = LogMaskingProcessor.getMask(inputString)

    then:
    actualResult == expected

    where:
    inputString                            | expected
    'e3x6b806-493b-4487-b2a6-8d5a3f9f5d63' | 'e3x*********************************'
    'e3x6b806493b4487b2a68d5a3f9f5d6'      | '*******************************'
    'e3x6b806493b4487b2a68d5a3f9f5d6a'     | 'e3x*****************************'
    'abcdef'                               | '******'
    null                                   | ''
    '     '                                | ''
  }

  def 'test-preProcessForMaskMap-environment variables not specified'(){
    given:
    System.setProperty(LogMaskingProcessor.SENSITIVE_ENV_HOLDER_PROPERTY, '')
    ReflectionTestUtils.setField(LogMaskingProcessor, 'cachedMaskMap', null)

    when:
    def map = LogMaskingProcessor.preProcessForMaskMap(this)

    then:
    map == Map.of()
  }

  def 'test-preProcessForMaskMap-environment values are absent'() {
    given:
    def sysPropertyHolder = LogMaskingProcessor.SENSITIVE_ENV_HOLDER_PROPERTY
    System.setProperty(sysPropertyHolder, 'RANDOM_PROP1, RANDOM_PROP2, PROP3, PROP4')
    def map = LogMaskingProcessor.preProcessForMaskMap(LogMaskingProcessor)

    expect:
    map.isEmpty()
  }

  def 'test-preProcessForMaskMap-caching-basic'() {
    given:
    def sysPropertyHolder = LogMaskingProcessor.SENSITIVE_ENV_HOLDER_PROPERTY
    System.setProperty(sysPropertyHolder, 'RANDOM_PROP1, RANDOM_PROP2, PROP3, PROP4')
    def map1 = LogMaskingProcessor.preProcessForMaskMap(LogMaskingProcessor)
    def map2 = LogMaskingProcessor.preProcessForMaskMap(LogMaskingProcessor)

    expect:
    map1 == map2
  }

  def 'test-preProcessForMaskMap-caching-after first execution'() {
    given:
    def map = new HashMap<String, String>()
    map.put('abcde','*****')
    map.put('abcdef','******')
    ReflectionTestUtils.setField(LogMaskingProcessor, 'cachedMaskMap', map)
    def map1 = LogMaskingProcessor.preProcessForMaskMap(LogMaskingProcessor)

    expect:
    map1 == map
  }

  def 'test-preProcessForMaskMap with mocked getEnvironmentVariable'() {
    given:
    def environmentUtilsMock = Mockito.mockStatic(EnvironmentUtils)
    environmentUtilsMock.when(EnvironmentUtils.getEnvironmentVariable('TEST_ENV1')).thenReturn('value1')
    environmentUtilsMock.when(EnvironmentUtils.getEnvironmentVariable('TEST_ENV2')).thenReturn('value2_long')

    def sysPropertyHolder = LogMaskingProcessor.SENSITIVE_ENV_HOLDER_PROPERTY
    System.setProperty(sysPropertyHolder, 'TEST_ENV1, TEST_ENV2')

    when:
    def result = LogMaskingProcessor.preProcessForMaskMap(LogMaskingProcessor)

    then:
    result.size() == 2
    result.get('value1') == '******'
    result.get('value2_long') == '***********'

    cleanup:
    environmentUtilsMock.close()
  }
}
