package main.java.com.example.nac.DTO;


import lombok.*;

import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LookUpTableDTO {
    private String methodName;
    private IdentityDTO subject;
    private ArrayList<IdentityDTO> object;
    private String scName;
    private String abi;
}
