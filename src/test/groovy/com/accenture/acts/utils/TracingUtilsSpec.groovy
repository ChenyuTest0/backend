package com.accenture.acts.utils

import spock.lang.Specification
import spock.lang.Unroll

import com.accenture.acts.concurrent.AsyncThreadRequestHeadersHolder

// cSpell:ignore traceid
class TracingUtilsSpec extends Specification {
  @Unroll
  def "should get traceId: #caseName" () {
    setup:
    AsyncThreadRequestHeadersHolder.requestHeaders = requestHeaders

    when:
    def actual = TracingUtils.traceId

    then:
    actual == expect

    where:
    caseName         | requestHeaders                  | expect
    'no header'      | [:]                             | null
    'null value'     | ['x-b3-traceid':null]           | null
    'empty value'    | ['x-b3-traceid':'']             | ''
    'concrete value' | ['x-b3-traceid':'xxx-trace-id'] | 'xxx-trace-id'
  }
}
