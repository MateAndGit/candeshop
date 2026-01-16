package com.mateandgit.candestore.domain.jwt.repository;

import com.mateandgit.candestore.domain.jwt.entity.RefreshEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface RefreshRepository extends JpaRepository<RefreshEntity, String> {
    Boolean existsByRefresh(String refresh);

    @Transactional
    void deleteByRefresh(String refresh);
}
