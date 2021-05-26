package kr.ad.hansung.cse.ecommercespringrestjpa.repository;


import kr.ad.hansung.cse.ecommercespringrestjpa.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByRolename(String rolename);
}