package com.accenture.acts.logback

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.LoggingEvent
import ch.qos.logback.classic.spi.ThrowableProxy
import net.logstash.logback.stacktrace.ShortenedThrowableConverter
import org.apache.commons.lang3.StringUtils
import org.springframework.test.util.ReflectionTestUtils
import spock.lang.Specification
import spock.lang.Title

@Title('MaskingShortenedThrowableConverter- Unit Test')
class MaskingShortenedThrowableConverterSpec extends  Specification{

  def 'test-convert-without sensitive values'(){
    given:
    System.setProperty(LogMaskingProcessor.SENSITIVE_ENV_HOLDER_PROPERTY, '')
    ReflectionTestUtils.setField(LogMaskingProcessor, 'cachedMaskMap', null)
    LoggingEvent event = new LoggingEvent()
    event.setMessage('Some exception')
    def exception = new RuntimeException('Some message')
    event.setArgumentArray(List.of(exception).toArray())
    event.setThrowableProxy(new ThrowableProxy(exception))

    def sensitiveConverter = new MaskingShortenedThrowableConverter()
    def normalConverter = new ShortenedThrowableConverter()
    normalConverter.start()
    sensitiveConverter.start()

    when:
    def withNewConverterString = sensitiveConverter.convert(event)
    def normalString = normalConverter.convert(event)
    normalConverter.stop()
    sensitiveConverter.stop()

    then:
    withNewConverterString == normalString
  }

  def 'test-convert-with sensitive values only in exception'(){
    given:
    def map = new HashMap<String, String>()
    ReflectionTestUtils.setField(LogMaskingProcessor, 'cachedMaskMap', map)
    def sensitiveVal = 'e3x6b806-493b-4487-b2a6-8d5a3f9f5d63'
    def mask = 'e3x*********************************'
    def sensitiveVal1 = 'e3x6b806493b4487b2a68d5a3f9f5d6'
    def mask1 = '*******************************'
    map.put(sensitiveVal, mask)
    map.put(sensitiveVal1, mask1)
    def exception = new RuntimeException('With SensitiveKey:'+sensitiveVal)
    def nestedException = new RuntimeException('With SensitiveKey2:'+sensitiveVal1, exception)
    Object[] args = List.of(nestedException).toArray()
    String message = 'Print some sensitive value:'+sensitiveVal+ ' some error'
    LoggingEvent event = new LoggingEvent()
    event.setMessage(message)
    event.setArgumentArray(args)
    event.setLevel(Level.ERROR)
    event.setThrowableProxy(new ThrowableProxy(nestedException))
    def sensitiveConverter = new MaskingShortenedThrowableConverter()
    sensitiveConverter.start()
    def normalConverter = new ShortenedThrowableConverter()
    normalConverter.start()

    when:
    def formattedString = sensitiveConverter.convert(event)
    def unformattedString = normalConverter.convert(event)
    sensitiveConverter.stop()
    normalConverter.stop()
    def maskArr = (String [])List.of(mask, mask1).toArray()
    def targetArr = (String [])List.of(sensitiveVal, sensitiveVal1).toArray()

    then:
    StringUtils.replaceEach(unformattedString, targetArr, maskArr ) == formattedString
  }

  def 'test-convert-with maskingArray not null'() {
    given:
    def map = new HashMap<String, String>()
    ReflectionTestUtils.setField(LogMaskingProcessor, 'cachedMaskMap', map)
    def sensitiveVal = 'sensitiveValue'
    def mask = '************'
    map.put(sensitiveVal, mask)
    def exception = new RuntimeException('Exception with sensitiveValue')
    LoggingEvent event = new LoggingEvent()
    event.setMessage('Message with sensitiveValue')
    event.setArgumentArray([exception] as Object[])
    event.setThrowableProxy(new ThrowableProxy(exception))

    def sensitiveConverter = new MaskingShortenedThrowableConverter()
    sensitiveConverter.start()
    sensitiveConverter.maskingArray = [[sensitiveVal], [mask]]

    when:
    def formattedString = sensitiveConverter.convert(event)
    sensitiveConverter.stop()

    then:
    formattedString.contains(mask)
    !formattedString.contains(sensitiveVal)
  }
}
