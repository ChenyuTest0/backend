package com.accenture.acts.logback

import ch.qos.logback.classic.spi.LoggingEvent
import org.springframework.test.util.ReflectionTestUtils
import spock.lang.Specification
import spock.lang.Title


@Title('SensitiveValueMessageMaskConverter- Unit Test')
class SensitiveValueMessageMaskConverterSpec extends  Specification{

  def 'test-convert-without sensitive values'(){
    given:
    System.setProperty(LogMaskingProcessor.SENSITIVE_ENV_HOLDER_PROPERTY, '')
    ReflectionTestUtils.setField(LogMaskingProcessor, 'cachedMaskMap', null)
    def someValue = ' some value'
    LoggingEvent event = new LoggingEvent()
    event.setMessage('Print some value:{}')
    event.setArgumentArray(List.of(someValue).toArray())
    def sensitiveConverter = new SensitiveValueMessageMaskConverter()
    def normalConverter = new MessageMaskConverter()

    when:
    def withNewConverterString = sensitiveConverter.convert(event)
    def normalString = normalConverter.convert(event)

    then:
    withNewConverterString == normalString
  }

  def 'test-convert-with sensitive values only in arguments'(){
    given:
    def map = new HashMap<String, String>()
    ReflectionTestUtils.setField(LogMaskingProcessor, 'cachedMaskMap', map)
    def sensitiveVal = 'e3x6b806-493b-4487-b2a6-8d5a3f9f5d63'
    def mask =  'e3x*********************************'
    map.put(sensitiveVal, mask)
    LoggingEvent event = new LoggingEvent()
    event.setMessage('Print some sensitive value:{}')
    event.setArgumentArray(List.of(sensitiveVal).toArray())
    def sensitiveConverter = new SensitiveValueMessageMaskConverter()

    expect:
    sensitiveConverter.convert(event) == 'Print some sensitive value:'+mask
  }

  def 'test-convert-with sensitive values only in message'(){
    given:
    def map = new HashMap<String, String>()
    ReflectionTestUtils.setField(LogMaskingProcessor, 'cachedMaskMap', map)
    def sensitiveVal = 'e3x6b806-493b-4487-b2a6-8d5a3f9f5d63'
    def mask =  'e3x*********************************'
    map.put(sensitiveVal, mask)
    LoggingEvent event = new LoggingEvent()
    event.setMessage('Print some sensitive value:'+sensitiveVal)
    def sensitiveConverter = new SensitiveValueMessageMaskConverter()

    expect:
    sensitiveConverter.convert(event) == 'Print some sensitive value:'+mask
  }

  def 'test-convert-with sensitive values both in arguments and message'(){
    given:
    def map = new HashMap<String, String>()
    ReflectionTestUtils.setField(LogMaskingProcessor, 'cachedMaskMap', map)
    def sensitiveVal = 'e3x6b806-493b-4487-b2a6-8d5a3f9f5d63'
    def mask =  'e3x*********************************'
    map.put(sensitiveVal, mask)
    LoggingEvent event = new LoggingEvent()
    event.setMessage('Print some sensitive with subs:{} with concatenate:'+sensitiveVal)
    event.setArgumentArray(List.of(sensitiveVal).toArray())
    def sensitiveConverter = new SensitiveValueMessageMaskConverter()

    expect:
    sensitiveConverter.convert(event) == 'Print some sensitive with subs:'+mask+' with concatenate:'+mask
  }

  def 'test-convert-with maskingArray not null'() {
    given:
    def map = new HashMap<String, String>()
    ReflectionTestUtils.setField(LogMaskingProcessor, 'cachedMaskMap', map)
    def sensitiveVal = 'sensitiveValue'
    def mask = '************'
    map.put(sensitiveVal, mask)
    LoggingEvent event = new LoggingEvent()
    event.setMessage('Print some sensitive value:'+sensitiveVal)

    def sensitiveConverter = new SensitiveValueMessageMaskConverter()
    sensitiveConverter.start()
    sensitiveConverter.maskingArray = [[sensitiveVal], [mask]]

    expect:
    sensitiveConverter.convert(event) == 'Print some sensitive value:'+mask
  }
}
