package cz.zcu.kiv.pia.silhavyj.socialnetwork.repository;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.post.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/***
 * JPA repository that handles SQL queries regarding likes.
 *
 * @author Jakub Silhavy (A21N0072P)
 */
@Repository
public interface ILikeRepository extends JpaRepository<Like, Long> {
}
