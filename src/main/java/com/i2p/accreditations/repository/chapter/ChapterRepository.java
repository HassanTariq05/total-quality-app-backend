package com.i2p.accreditations.repository.chapter;

import com.i2p.accreditations.model.chapter.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, UUID> {
    Optional<Chapter> findTopByOrderByNumberDesc();
}
