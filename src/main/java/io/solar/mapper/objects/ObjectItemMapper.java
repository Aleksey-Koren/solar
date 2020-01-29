package io.solar.mapper.objects;

import io.solar.entity.objects.ObjectItem;
import io.solar.utils.ObjectUtils;
import io.solar.utils.db.DbMapper;
import io.solar.utils.db.SafeResultSet;

public class ObjectItemMapper implements DbMapper<ObjectItem> {

    @Override
    public ObjectItem map(SafeResultSet resultSet) {
        ObjectItem out = new ObjectItem();
        ObjectUtils.populate(out, resultSet);
        return out;
    }
}
