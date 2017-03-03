/*
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package com.dangdang.ddframe.rdb.sharding.parser;

import com.alibaba.druid.sql.context.SQLContext;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlExprParser;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleExprParser;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGExprParser;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerExprParser;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.dangdang.ddframe.rdb.sharding.api.rule.ShardingRule;
import com.dangdang.ddframe.rdb.sharding.constants.DatabaseType;
import com.dangdang.ddframe.rdb.sharding.exception.SQLParserException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * SQL解析器工厂.
 * 
 * @author gaohongtao
 * @author zhangliang
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public final class SQLParserFactory {
    
    /**
     * 创建解析器引擎对象.
     * 
     * @param databaseType 数据库类型
     * @param sql SQL语句
     * @param parameters SQL中参数的值
     * @param shardingRule 分片规则
     * @return 解析器引擎对象
     * @throws SQLParserException SQL解析异常
     */
    public static SQLParseEngine create(final DatabaseType databaseType, final String sql, final List<Object> parameters, final ShardingRule shardingRule) throws SQLParserException {
        log.debug("Logic SQL: {}, {}", sql, parameters);
        SQLContext sqlContext = getSQLStatementParser(databaseType, sql, shardingRule, parameters).parseStatement();
        log.trace("Get {} SQL Statement", sqlContext.getClass().getName());
        return new SQLParseEngine(sqlContext);
    }
    
    private static SQLStatementParser getSQLStatementParser(final DatabaseType dbType, final String sql, final ShardingRule shardingRule, final List<Object> parameters) {
        SQLExprParser sqlExprParser;
        switch (dbType) {
            case H2:
            case MySQL:
                sqlExprParser = new MySqlExprParser(shardingRule, parameters, sql);
                break;
            case Oracle:
                sqlExprParser = new OracleExprParser(shardingRule, parameters, sql);
                break;
            case SQLServer:
                sqlExprParser = new SQLServerExprParser(shardingRule, parameters, sql);
                break;
            case PostgreSQL:
                sqlExprParser = new PGExprParser(shardingRule, parameters, sql);
                break;
            default:
                throw new UnsupportedOperationException(dbType.name());
        }
        return new SQLStatementParser(dbType, shardingRule, parameters, sqlExprParser);
    }
}
