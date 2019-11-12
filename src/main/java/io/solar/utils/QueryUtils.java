package io.solar.utils;

import io.solar.utils.db.Query;
import io.solar.utils.server.Pageable;

public class QueryUtils {
    public static void applyPagination(Query query, Pageable pageable) {
        query.setInt("skip", pageable.getPage() * pageable.getPageSize());
        query.setInt("pageSize", pageable.getPageSize());
    }
}
