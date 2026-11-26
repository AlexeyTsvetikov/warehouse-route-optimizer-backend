package ru.tsvetikov.warehouse.router.aspect;

import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SqlQueryCounter implements StatementInspector {

    private static final ThreadLocal<Map<String, Integer>> QUERY_COUNTS =
            ThreadLocal.withInitial(HashMap::new);

    public static void start() {
        QUERY_COUNTS.get().clear();
    }

    public static int getTotalCount() {
        return QUERY_COUNTS.get().values().stream().mapToInt(Integer::intValue).sum();
    }

    public static int getMaxDuplicateCount() {
        return QUERY_COUNTS.get().values().stream().mapToInt(Integer::intValue).max().orElse(0);
    }

    public static void clear() {
        QUERY_COUNTS.remove();
    }

    @Override
    public String inspect(String sql) {
        QUERY_COUNTS.get().merge(sql, 1, Integer::sum);
        return sql;
    }
}
