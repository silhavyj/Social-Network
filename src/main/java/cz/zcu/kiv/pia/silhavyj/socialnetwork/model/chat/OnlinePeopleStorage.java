package cz.zcu.kiv.pia.silhavyj.socialnetwork.model.chat;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class OnlinePeopleStorage {

    private Set<String> onlinePeople;

    public OnlinePeopleStorage() {
        onlinePeople = new HashSet<>();
    }
}
