package io.solar.controller.inventory;

import io.solar.dto.inventory.InventorySocketDto;
import io.solar.mapper.SocketMapper;
import io.solar.utils.db.Query;
import io.solar.utils.db.Transaction;
import io.solar.utils.server.beans.Controller;
import io.solar.utils.server.controller.PathVariable;
import io.solar.utils.server.controller.RequestMapping;
import io.solar.utils.server.controller.RequestParam;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Controller
@RequestMapping("sockets")
public class SocketController {

    @RequestMapping
    public List<InventorySocketDto> getSocketList(
            Transaction transaction,
            @RequestParam("itemDescription") Long itemDescription
    ) {
        if (itemDescription == null) {
            return new ArrayList<>();
        }
        Query query = transaction.query("select * from object_type_socket where item_id = :itemDescription");
        query.setLong("itemDescription", itemDescription);
        return query.executeQuery(/*new SocketMapper()*/ null);
    }

    @RequestMapping("{id}")
    public InventorySocketDto getSocket(
            Transaction transaction,
            @PathVariable("id") Long id
    ) {
        Query query = transaction.query("select * from object_type_socket where id = :id");
        query.setLong("id", id);
        List<InventorySocketDto> out = query.executeQuery(/*new SocketMapper()*/ null);
        return out.size() == 1 ? out.get(0) : null;
    }
}