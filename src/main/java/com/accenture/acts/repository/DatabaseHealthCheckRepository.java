package com.accenture.acts.repository;

import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * データベースヘルスチェックするためのRepository。
 */
@Repository
public interface DatabaseHealthCheckRepository extends DatabaseHealthCheckRepositoryBase {
    @Select("SELECT 1")
    @Override
    Object checkConnectorHealth();
}
