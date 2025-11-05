package com.cpb.tradelink.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -8646654910002048886L;
    private Long id;
    private String emailTemplateId;
    private String payload;

}
    