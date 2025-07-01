package com.accenture.acts.repository;

import java.util.List;

import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.accenture.acts.entity.DeemedDateEntity;

/**
 * みなし日を扱うためのRepository。
 */
@Repository
public interface DeemedDateRepository extends DeemedDateRepositoryBase {

    @Select("""
        SELECT
            function_id, deemed_date
        FROM
            m_deemed_date
        WHERE
            function_id = '10' OR function_id = '11'
        """)
    @ResultType(DeemedDateEntity.class)
    @Override
    List<DeemedDateEntity> select();
}
