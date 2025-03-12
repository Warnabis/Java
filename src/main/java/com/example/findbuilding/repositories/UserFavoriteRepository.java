package com.example.findbuilding.repositories;

import com.example.findbuilding.models.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserFavoriteRepository extends JpaRepository<Building, Long> {

}