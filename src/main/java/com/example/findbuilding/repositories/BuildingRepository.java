package com.example.findbuilding.repositories;

import com.example.findbuilding.models.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {

    // JPQL-запрос: сортировка по убыванию рейтинга и количеству отзывов
    @Query("SELECT b FROM Building b LEFT JOIN b.reviews r " +
      "GROUP BY b " +
      "ORDER BY AVG(r.rating) DESC NULLS LAST, COUNT(r) DESC")
    List<Building> findTopBuildings();

    // Native Query (для PostgreSQL/MySQL)
    @Query(value = "SELECT b.* FROM building b " +
      "LEFT JOIN review r ON b.id = r.building_id " +
      "GROUP BY b.id " +
      "ORDER BY COALESCE(AVG(r.rating), 0) DESC, COUNT(r.id) DESC",
      nativeQuery = true)
    List<Building> findTopBuildingsNative();
}
