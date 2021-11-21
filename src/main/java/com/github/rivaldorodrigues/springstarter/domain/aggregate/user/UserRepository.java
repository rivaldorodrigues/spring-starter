package com.github.rivaldorodrigues.springstarter.domain.aggregate.user;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Long>, JpaSpecificationExecutor<User> {

	@Query("SELECT u FROM User u ORDER BY u.name")
	List<User> findAll();

	Optional<User> findByLogin(@Param("login") String login);

	Optional<User> findByEmail(@Param("email") String email);
}
