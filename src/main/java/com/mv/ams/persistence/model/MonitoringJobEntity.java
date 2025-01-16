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
@Entity(name = "monitoring_job")
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"}, callSuper = false)
public class MonitoringJobEntity extends BaseEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "short_address", nullable = false)
    private String shortAddress;

    @Column(name = "cron_expression", nullable = false)
    private String cronExpression;

    @Column(name = "monitoring_type", nullable = false)
    private String monitoringType;
}
