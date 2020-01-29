package io.solar.mapper.objects;

import io.solar.entity.objects.ObjectView;
import io.solar.service.ObjectService;
import io.solar.utils.ObjectUtils;
import io.solar.utils.db.DbMapper;
import io.solar.utils.db.Query;
import io.solar.utils.db.SafeResultSet;
import io.solar.utils.db.Transaction;

public class ObjectViewMapper implements DbMapper<ObjectView> {

    private final Transaction transaction;
    private final boolean appendObjects;
    private final boolean appendSockets;

    public ObjectViewMapper(Transaction transaction) {
        this(transaction, true, true);
    }
    public ObjectViewMapper(Transaction transaction, boolean appendObjects, boolean appendSockets) {
        this.transaction = transaction;
        this.appendObjects = appendObjects;
        this.appendSockets = appendSockets;
    }

    @Override
    public ObjectView map(SafeResultSet resultSet) {
        ObjectView out = new ObjectView();
        ObjectUtils.populate(out, resultSet);
        if(appendSockets) {
            ObjectUtils.appendSockets(out, transaction);
        }
        if(appendObjects) {
            ObjectUtils.appendObjects(out, transaction);
        }
        return out;
    }
}
