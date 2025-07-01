package com.accenture.acts.serialize

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Title

import com.accenture.acts.rest.ProcessResult

// cSpell:ignore fasterxml databind
@Title('Response Body Serializer Test')
class ResponseBodySerializerSpec extends Specification {

  // テスト用Bean定義
  class TargetBean {
    String name
    Integer score
  }

  @Subject
  private ResponseBodySerializer  serializer = new ResponseBodySerializer()

  def "正常なレスポンスがSerializeできること"() {

    expect: 'Json Serialize結果が期待値と合致するかチェックする。'
    def module = new SimpleModule()
    module.addSerializer(ProcessResult, serializer)
    def mapper = new ObjectMapper()
    mapper.registerModule(module)
    String result = mapper.writeValueAsString(responseBodyBeans)
    expectedResult == result


    where: 'テストデーター及び期待結果'
    responseBodyBeans                                                                                       || expectedResult
    new ProcessResult(new TargetBean(name:'Yamada', score:300))                ||  '{"name":"Yamada","score":300}'
    new ProcessResult([
      new TargetBean(name:'peter', score:100),
      new TargetBean(name:'sam', score:200)
    ])                                                                                                           || '[{"name":"peter","score":100},{"name":"sam","score":200}]'
    new ProcessResult([
      new TargetBean(name:'peter', score:100),
      new TargetBean(name:'sam', score:200)
    ] as TargetBean[])                                                                                             || '[{"name":"peter","score":100},{"name":"sam","score":200}]'
    new ProcessResult([
      Key1: 'value1',
      key2: 'value2'
    ]
    )                                                                                                                      ||  '{"Key1":"value1","key2":"value2"}'
    new ProcessResult(null)                                                                                   || '{}'
  }
}
