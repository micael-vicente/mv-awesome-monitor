package com.mv.ams.persistence.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
@Entity(name = "monitoring_job")
@NoArgsConstructor
public class MonitoringJobEntity extends BaseEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @Column(name = "cron_expression", nullable = false)
    private String cronExpression;

    @Column(name = "monitoring_type", nullable = false)
    private String monitoringType;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "job")
    @ToString.Exclude
    private final Set<MonitoringJobResultEntity> results = new HashSet<>();

    public void addResult(MonitoringJobResultEntity result) {
        results.add(result);
        result.setJob(this);
    }
}
