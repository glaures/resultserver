package de.sandkastenliga.resultserver.security;

import de.sandkastenliga.resultserver.security.repositories.RoleRepository;
import de.sandkastenliga.resultserver.security.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.UUID;

@Configuration
@org.springframework.core.annotation.Order(2)
public class CreateInitialUserAndRole {

    private final static String ROLE_ADMIN = "ROLE_ADMIN";
    private final static String SYSTEM_USERNAME = "system";

    @Value("${resultserver.user.admin.username}")
    String adminUsername;
    @Value("${resultserver.user.admin.password}")
    String adminPassword;
    final UserRepository userRepository;
    final RoleRepository roleRepository;
    final BCryptPasswordEncoder bCryptPasswordEncoder;

    public CreateInitialUserAndRole(UserRepository userRepository,
                                    RoleRepository roleRepository,
                                    BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @PostConstruct
    @Transactional
    public void generateAdminUserAndRole() {
        // ROLE_ADMIN als Rolle erzeugen, falls nicht vorhanden
        if (roleRepository.findById(ROLE_ADMIN).isEmpty()) {
            Role adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");
            roleRepository.save(adminRole);
        }
        // admin user erzeugen, falls nicht vorhanden
        if (userRepository.findUserByUsername(adminUsername).isEmpty()) {
            User adminUser = new User();
            adminUser.setUsername(adminUsername);
            adminUser.setPassword(bCryptPasswordEncoder.encode(adminPassword));
            adminUser.getRoles().add(roleRepository.getOne(ROLE_ADMIN));
            userRepository.save(adminUser);
        }
        if(userRepository.findUserByUsername(SYSTEM_USERNAME).isEmpty()) {
            User systemUser = new User();
            systemUser.setUsername(SYSTEM_USERNAME);
            systemUser.setPassword(bCryptPasswordEncoder.encode(UUID.randomUUID().toString()));
            userRepository.save(systemUser);
        }
    }


}
