package org.luismore.hlvsapi.domain.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.luismore.hlvsapi.domain.entities.Token;

@Data
@NoArgsConstructor
public class TokenDTO {
    @NotBlank
    private String token;
    @NotBlank
    public TokenDTO(Token token){
        this.token = token.getContent();
    }
}
