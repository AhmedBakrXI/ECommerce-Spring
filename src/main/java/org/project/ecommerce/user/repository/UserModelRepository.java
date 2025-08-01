package org.project.ecommerce.user.repository;

import org.project.ecommerce.user.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserModelRepository extends JpaRepository<UserModel, Long> {
    /**
     * Finds a user by their username.
     *
     * @param username the username of the user
     * @return an Optional containing the UserModel if found, or empty if not found
     */
    Optional<UserModel> findByUsername(String username);

    /**
     * Checks if a user exists by their username.
     *
     * @param username the username to check
     * @return true if a user with the given username exists, false otherwise
     */
    Boolean existsByUsername(String username);

    Optional<UserModel> findByEmail(String email);
}
