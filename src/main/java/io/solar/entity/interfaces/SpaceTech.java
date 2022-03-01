package io.solar.entity.interfaces;

import io.solar.entity.Goods;
import io.solar.entity.User;
import io.solar.entity.inventory.socket.SpaceTechSocket;
import io.solar.entity.objects.BasicObject;

import java.util.List;

public interface SpaceTech {

    Long getId();

    List<Goods> getGoods();

    User getUser();

    List<BasicObject> getAttachedObjects();

    List<SpaceTechSocket> getSockets();

    void setSockets(List<SpaceTechSocket> sockets);
}
