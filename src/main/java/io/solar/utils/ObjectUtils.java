package io.solar.utils;

import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.ObjectItem;
import io.solar.entity.objects.ObjectStatus;
import io.solar.mapper.objects.ObjectItemMapper;
import io.solar.utils.db.Query;
import io.solar.utils.db.SafeResultSet;
import io.solar.utils.db.Transaction;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

public class ObjectUtils {

    public static void populate(BasicObject out, SafeResultSet resultSet) {

        ResultSetMetaData metadata = resultSet.getMetaData();
        try {
            int count = metadata.getColumnCount() + 1;
            String title = null;
            for (int idx = 1; idx < count; idx++) {
                String table = metadata.getTableName(idx);
                if (!"objects".equals(table)) {
                    if("object_type_description".equals(table)) {
                        if("title".equals(metadata.getColumnName(idx))) {
                            title = resultSet.getString(idx);
                        }
                    }
                    continue;
                }
                String column = metadata.getColumnName(idx);
                switch (column) {
                    case "id":
                        out.setId(resultSet.fetchLong(idx));
                        break;
//                    case "planet":
//                        out.setPlanet(resultSet.fetchLong(idx));
//                        break;
                    case "population":
                        out.setPopulation(resultSet.fetchLong(idx));
                        break;
                    case "fraction":
                        out.setFraction(resultSet.getString(idx));
                        break;
                    case "title":
                        out.setTitle(resultSet.getString(idx));
                        break;
                    case "x":
                        out.setX(resultSet.fetchFloat(idx));
                        break;
                    case "y":
                        out.setY(resultSet.fetchFloat(idx));
                        break;
                    case "aphelion":
                        out.setAphelion(resultSet.fetchFloat(idx));
                        break;
                    case "orbital_period":
                        out.setOrbitalPeriod(resultSet.fetchFloat(idx));
                        break;
                    case "angle":
                        out.setAngle(resultSet.fetchFloat(idx));
                        break;
//                    case "hull_id":
//                        out.setHullId(resultSet.fetchLong(idx));
//                        break;
                    case "user_id":
                        out.setUserId(resultSet.fetchLong(idx));
                        break;
                    case "active":
                        out.setActive(Boolean.TRUE.equals(resultSet.getBoolean(idx)));
                        break;
                    case "durability":
                        out.setDurability(resultSet.fetchLong(idx));
                        break;
                    case "attached_to_ship":
                        out.setAttachedToShip(resultSet.fetchLong(idx));
                        break;
                    case "attached_to_socket":
                        out.setAttachedToSocket(resultSet.fetchLong(idx));
                        break;
                    case "status":
                        out.setStatus(ObjectStatus.fromString(resultSet.getString(idx)));
                        break;
                }
            }
            if(out.getTitle() == null || "".equals(out.getTitle())) {
                out.setTitle(title);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void appendSockets(BasicObject object, Transaction transaction) {
        Query query = transaction.query("select" +
                " object_type_socket.id, object_type_socket.item_id, " +
                " object_type_socket.item_type_id, object_type_socket.sort_order, " +
                " object_type_socket.alias" +
                " from object_type_socket " +
                " where item_id = :itemId order by sort_order");
//        query.setLong("itemId", object.getHullId());
//        object.setSocketList(query.executeQuery(new SocketMapper()));
    }

    public static void appendObjects(BasicObject object, Transaction transaction) {
        Query query = transaction.query("select * from objects where attached_to_ship = :attachedToShip");
        query.setLong("attachedToShip", object.getId());
        List<ObjectItem> objects = query.executeQuery(new ObjectItemMapper());
//        object.setAttachedObjects(objects);
    }
}
