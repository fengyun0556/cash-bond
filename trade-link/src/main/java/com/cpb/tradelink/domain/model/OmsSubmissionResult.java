package com.cpb.tradelink.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OmsSubmissionResult {
    private Boolean success;
    private Long tps2Id;
}
