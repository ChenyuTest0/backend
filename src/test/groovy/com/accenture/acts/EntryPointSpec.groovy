package com.accenture.acts

import org.mockito.MockedStatic
import org.mockito.Mockito
import org.springframework.boot.SpringApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import spock.lang.Specification

class EntryPointSpec extends Specification {

  def "configure"() {
    setup:
    def entryPoint = new EntryPoint()
    def springApplicationBuilderMock = Mock(SpringApplicationBuilder)

    when:
    entryPoint.configure(springApplicationBuilderMock)

    then:
    1 * springApplicationBuilderMock.sources(EntryPoint)
  }

  def "main"() {
    setup:
    String[] args = ['']

    and:
    MockedStatic<SpringApplication> mock = Mockito.mockStatic(SpringApplication)
    mock.when(SpringApplication.run(EntryPoint, args)).thenReturn(null)

    expect:
    EntryPoint.main(args)

    cleanup:
    mock.close()
  }
}
