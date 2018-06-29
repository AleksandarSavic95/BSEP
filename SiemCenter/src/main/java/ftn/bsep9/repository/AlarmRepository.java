package ftn.bsep9.repository;

import ftn.bsep9.model.Alarm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface AlarmRepository extends MongoRepository<Alarm, String>, QuerydslPredicateExecutor<Alarm> {
    Page<Alarm> findAll(Pageable pageable);
}
