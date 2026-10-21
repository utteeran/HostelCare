
package com.example.hosteltracker.repository;

import com.example.hosteltracker.model.ComplaintEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComplaintRepository
        extends JpaRepository<ComplaintEntity, Long> {
}
