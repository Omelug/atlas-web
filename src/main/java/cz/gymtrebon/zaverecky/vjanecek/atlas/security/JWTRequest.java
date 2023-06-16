package cz.gymtrebon.zaverecky.vjanecek.atlas.security;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JWTRequest {
    @SerializedName("name")
    private String name;
    @SerializedName("password")
    private String password;
}
