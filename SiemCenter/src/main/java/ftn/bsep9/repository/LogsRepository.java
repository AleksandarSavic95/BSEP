package ftn.bsep9.repository;

import ftn.bsep9.model.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface LogsRepository extends MongoRepository<Log, String>, QuerydslPredicateExecutor<Log> {

//    @Override
//    Optional<Log> findById(String search);

//    List<Log> findByDate(LocalDateTime date);

//    List<Log> findAllByTextContainsIgnoreCase(String text);

//    @Query("{ 'text':{$regex:?0,$options:'i'} }")
//    List<Log> findByTextRegexIgnoreCase(String text);

    Page<Log> findAll(Pageable pageable);

}
