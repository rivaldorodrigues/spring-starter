package com.github.rivaldorodrigues.springstarter.domain.aggregate.profile;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfileRepository extends CrudRepository<Profile, Long> {

	@Query("SELECT p FROM Profile p ORDER BY p.name")
	List<Profile> findAll();

}
