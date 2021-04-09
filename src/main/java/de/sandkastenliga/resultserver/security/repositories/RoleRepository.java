package de.sandkastenliga.resultserver.security.repositories;

import de.sandkastenliga.resultserver.security.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
}
