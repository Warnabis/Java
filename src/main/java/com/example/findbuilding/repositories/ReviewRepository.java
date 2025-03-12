package com.example.findbuilding.repositories;

import com.example.findbuilding.models.Review;
import com.example.findbuilding.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    void deleteAllByUser(User user);
}
