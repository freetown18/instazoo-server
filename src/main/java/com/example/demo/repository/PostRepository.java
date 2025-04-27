package com.example.demo.repository;

import com.example.demo.entity.Post;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByUserOrderByCreatedDateDesc(User user);

    List<Post> findAllByOrderByCreatedDateDesc();

    Optional<Post> findPostByIdAndUser(Long id, User user);

//    @Query("SELECT p FROM Post p JOIN FETCH p.user WHERE p.id = :postId AND p.user.username = :username")
//    Optional<Post> findByIdAndUsername(@Param("postId") Long postId,
//                                       @Param("username") String username);

    @Modifying
    @Query("UPDATE Post p SET p.likes = p.likes + :increment WHERE p.id = :postId")
    void updateLikeCount(@Param("postId") Long postId,
                         @Param("increment") int increment);

    @Modifying
    @Query(value = "UPDATE posts SET liked_users = array_append(liked_users, :username) WHERE id = :postId",
            nativeQuery = true)
    void addLikedUser(@Param("postId") Long postId,
                      @Param("username") String username);

    @Modifying
    @Query(value = "UPDATE posts SET liked_users = array_remove(liked_users, :username) WHERE id = :postId",
            nativeQuery = true)
    void removeLikedUser(@Param("postId") Long postId,
                         @Param("username") String username);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likedUsers WHERE p.id = :postId")
    Optional<Post> findByIdWithLikes(@Param("postId") Long postId);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM posts, unnest(liked_users) AS user_name WHERE id = :postId AND user_name = :username)",
            nativeQuery = true)
    boolean isUserLikedPost(@Param("postId") Long postId,
                            @Param("username") String username);

}