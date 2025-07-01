package com.skeleton.service.impl.base

import com.accenture.acts.spock.core.DeemedDateSpecification

class ServiceSpecification extends DeemedDateSpecification {
  static final UUID_REG_EXP = /^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$/
}
