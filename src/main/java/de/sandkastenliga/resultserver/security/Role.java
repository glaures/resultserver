package de.sandkastenliga.resultserver.security;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
public class Role implements GrantedAuthority {

    @Id
    String name;

    @Override
    public String getAuthority() {
        return name;
    }
}
