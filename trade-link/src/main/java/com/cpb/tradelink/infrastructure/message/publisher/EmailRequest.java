package com.cpb.tradelink.infrastructure.message.publisher;

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
    private static final long serialVersionUID = -5374737456399464606L;
    private Long id;
    private String emailTemplateId;
    private String payload;

}
