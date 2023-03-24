package cz.gymtrebon.zaverecky.vjanecek.atlas.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JWTResponse {
    private String jwtToken;
}
