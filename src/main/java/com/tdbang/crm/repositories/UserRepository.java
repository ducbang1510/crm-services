package com.tdbang.crm.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tdbang.crm.dtos.UserDTO;
import com.tdbang.crm.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.pk = :pk")
    User findUserByPk(Long pk);

    Optional<User> findByUsername(String username);

    @Query("SELECT u.pk FROM User u WHERE u.username = :username")
    Long getUserPkByUsername(String username);

    @Query("SELECT new com.tdbang.crm.dtos.UserDTO(u.pk, u.name, u.email, u.phone, u.isAdmin, u.isActive, u.createdOn) FROM User u")
    Page<UserDTO> getUsersPageable(Pageable pageable);

    @Query("SELECT new com.tdbang.crm.dtos.UserDTO(u.pk, u.name, u.email, u.phone, u.isAdmin, u.isActive, u.createdOn) FROM User u")
    List<UserDTO> getAllUsers();
}
