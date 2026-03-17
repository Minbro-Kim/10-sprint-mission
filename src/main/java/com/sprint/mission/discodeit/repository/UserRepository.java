package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, UUID> {

  boolean existsByEmail(String email);

  boolean existsByUsername(String username);

  @Query("select u from User u join fetch u.userStatus left join fetch u.profile where u.username = :username and u.password =:password")
    // 유저상태와 프로필 정보를 유저 디티오에 같이 보내기 때문에 쿼리 3번 발생.패치조인으로 한번에 가져오기, 프로필 사진 없는 경우도 있으니까 레프트 조인
  Optional<User> findByUsernameAndPassword(String username, String password);

  @Query("select u from User u join fetch u.userStatus left join fetch u.profile")
  List<User> findAllFetchUserInfo();

  @Query("select u from User u join fetch u.userStatus left join fetch u.profile where u.id = :id")
  Optional<User> findByIdFetchUserInfo(UUID id);

}
