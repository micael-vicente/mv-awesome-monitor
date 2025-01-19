package com.mv.ams.services.monitoring;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.http.ResponseEntity;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class HttpExecutionResult {
    private ResponseEntity<String> response;
    private long requestDurationMillis;
    private boolean failedDueToError;
    private String errorMessage;
}
