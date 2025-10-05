package com.springboottest.user_management_api.repository;

import com.springboottest.user_management_api.entity.UserSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSettingRepository extends JpaRepository<UserSetting, Long> {

    /*
    * Find all settings for specific user
    * */
    @Query("SELECT us FROM UserSetting us WHERE us.user.id = :userId")
    List<UserSetting> findByUserId(@Param("userId") Long userId);

    /*
     * Find specific setting by user id and key
     * */
    @Query("SELECT us FROM UserSetting us WHERE us.user.id = :userId AND us.key = :key")
    Optional<UserSetting> findByUserIdAndKey(@Param("userId") Long userId,
                                             @Param("key") String key);
}
