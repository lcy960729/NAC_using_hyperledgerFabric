package main.java.com.example.nac.DTO;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChainCodeArgsDTO {
    private String userName;
    private String secretKey;
    private String orgAffiliation;
    private String orgMspId;
    private String chaincodeName;
    private String func;
    private ArrayList<String> arguments;
}
