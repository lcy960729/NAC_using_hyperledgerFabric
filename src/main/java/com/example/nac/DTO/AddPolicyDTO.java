package main.java.com.example.nac.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class AddPolicyDTO {
    private IdentityDTO object;
    private String resource;
    private String action;
    private Boolean permission;
    private Integer threshold;
    private Long minInterval;
}
