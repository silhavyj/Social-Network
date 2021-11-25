package cz.zcu.kiv.pia.silhavyj.socialnetwork.model.chat;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/***
 * Data structure used for keeping track of
 * who is currently online. When a user is now online, their
 * e-mail address is stored into this data structure, and when
 * they go offline, their e-mail address is removed from the data structure.
 *
 * @author Jakub Silhavy (A21N0072P)
 */
@Getter
@Setter
public class OnlinePeopleStorage {

    /*** set that holds users that are currently online (their e-mail addresses) */
    private Set<String> onlinePeople;

    /***
     * Creates an instance of OnlinePeopleStorage
     */
    public OnlinePeopleStorage() {
        onlinePeople = new HashSet<>();
    }
}
