package com.mv.ams.persistence.repository;

import com.mv.ams.fixture.JobFixture;
import com.mv.ams.fixture.TestContainersTest;
import com.mv.ams.persistence.model.MonitoringJobEntity;
import com.mv.ams.persistence.model.MonitoringJobResultEntity;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class MonitoringJobResultRepositoryIT extends TestContainersTest {

    @Autowired
    private MonitoringJobResultRepository resultRepo;

    @Autowired
    private MonitoringJobRepository jobRepo;

    @PostConstruct
    private void initializeDB() {
        LocalDateTime now = LocalDateTime.now();
        MonitoringJobEntity job1 = JobFixture.everyMinJobEntity("", true);
        job1.setCreatedAt(now.minusDays(8));
        job1.addResult(resultEntity("SUCCESS", now.minusDays(7)));
        job1.addResult(resultEntity("SUCCESS", now.minusDays(5)));
        job1.addResult(resultEntity("SUCCESS", now.minusDays(5)));
        job1.addResult(resultEntity("FAILURE", now.minusDays(4)));
        job1.addResult(resultEntity("FAILURE", now.minusDays(1)));
        job1.addResult(resultEntity("FAILURE", now.minusDays(1)));
        MonitoringJobEntity job2 = JobFixture.everyMinJobEntity("", true);
        job2.setCreatedAt(now.minusDays(4));
        job2.addResult(resultEntity("FAILURE", now.minusDays(3)));
        job2.addResult(resultEntity("FAILURE", now.minusDays(2)));
        job2.addResult(resultEntity("SUCCESS", now.minusDays(1)));
        jobRepo.save(job1);
        jobRepo.save(job2);
    }

    @Test
    void findByFilters_allJob1_return4Results() {
        List<MonitoringJobResultEntity> result = resultRepo
            .findByFilters(1L, null, null, null, Pageable.unpaged())
            .toList();

        Assertions.assertEquals(6, result.size());
    }

    @Test
    void findByFilters_allJob1Last2Days_return2Results() {
        List<MonitoringJobResultEntity> result = resultRepo
                .findByFilters(1L, LocalDateTime.now().minusDays(2), null, null, Pageable.unpaged())
                .toList();

        Assertions.assertEquals(2, result.size());
    }

    @Test
    void findByFilters_allJob1BetweenDay8AndDay6_return1Results() {
        List<MonitoringJobResultEntity> result = resultRepo
                .findByFilters(1L, LocalDateTime.now().minusDays(8), LocalDateTime.now().minusDays(6), null, Pageable.unpaged())
                .toList();

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void findByFilters_allJob1BetweenDay8AndDay6AndSuccess_return1Results() {
        List<MonitoringJobResultEntity> result = resultRepo
                .findByFilters(1L, LocalDateTime.now().minusDays(8), LocalDateTime.now().minusDays(6), "SUCCESS", Pageable.unpaged())
                .toList();

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void findByFilters_allJob1BetweenDay8AndDay6AndFailure_returnNoResults() {
        List<MonitoringJobResultEntity> result = resultRepo
                .findByFilters(1L, LocalDateTime.now().minusDays(8), LocalDateTime.now().minusDays(6), "FAILURE", Pageable.unpaged())
                .toList();

        Assertions.assertEquals(0, result.size());
    }

    private MonitoringJobResultEntity resultEntity(String status, LocalDateTime executedAt) {
        MonitoringJobResultEntity entity = new MonitoringJobResultEntity();
        entity.setDurationMillis(100L);
        entity.setStatus(status);
        entity.setExecutedAt(executedAt);
        entity.setCreatedAt(executedAt);
        return entity;
    }

}