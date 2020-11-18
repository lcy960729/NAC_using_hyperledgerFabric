package main.java.com.example.nac.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DeletePolicyDTO {
    private String methodName;
    private IdentityDTO object;
    private String resource;
}
