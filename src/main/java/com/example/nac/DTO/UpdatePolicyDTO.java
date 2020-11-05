package main.java.com.example.nac.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdatePolicyDTO {
    private IdentityDTO object;
    private String resource;
    private String action;
    private Boolean permission;
    private Integer threshold;
    private Long minInterval;
}
