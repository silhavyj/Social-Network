package cz.zcu.kiv.pia.silhavyj.socialnetwork.service.friendship;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.friendship.FriendRequestStatus;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.repository.IFriendRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static cz.zcu.kiv.pia.silhavyj.socialnetwork.model.friendship.FriendRequestStatus.ACCEPTED;
import static cz.zcu.kiv.pia.silhavyj.socialnetwork.model.friendship.FriendRequestStatus.PENDING;

@Service
@RequiredArgsConstructor
public class FriendshipService implements IFriendshipService {

    private final IFriendRequestRepository friendRequestRepository;

    @Override
    public List<User> getAllUsersFriends(String email) {
        return getAllUsersFriends(email, ACCEPTED);
    }

    @Override
    public List<User> getAllUsersPendingFriends(String email) {
        return getAllUsersFriends(email, PENDING);
    }

    private List<User> getAllUsersFriends(String email, FriendRequestStatus status) {
        return friendRequestRepository.getAllUsersFriendRequests(email)
                .stream()
                .filter(request -> request.getStatus().equals(status))
                .map(request -> request.getRequestReceiver().getEmail().equals(email) ? request.getRequestSender() : request.getRequestReceiver())
                .collect(Collectors.toList());
    }
}
