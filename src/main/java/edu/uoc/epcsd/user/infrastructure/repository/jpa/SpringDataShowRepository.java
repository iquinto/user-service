package edu.uoc.epcsd.user.infrastructure.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataShowRepository extends JpaRepository<ShowEntity, Long> {
}
