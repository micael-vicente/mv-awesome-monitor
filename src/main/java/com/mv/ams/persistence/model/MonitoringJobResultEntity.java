package com.mv.ams.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity(name = "monitoring_job_result")
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"}, callSuper = false)
public class MonitoringJobResultEntity extends BaseEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "duration_millis", nullable = false)
    private Long durationMillis;

    @Column(name = "result", nullable = false)
    private String result;

    @Column(name = "result_detail", nullable = false)
    private String resultDetail;

}
