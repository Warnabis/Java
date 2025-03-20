package com.example.findbuilding.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;
    private String workingHours;
    private Double rating;
    private Double coordinateX;
    private Double coordinateZ;

    @OneToMany(mappedBy = "building", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Review> reviews;

    @ManyToMany(mappedBy = "favoriteBuildings", fetch = FetchType.LAZY)
    private List<User> favoritedByUsers;

    public void updateRating() {
        if (reviews == null || reviews.isEmpty()) {
            this.rating = 0d;
        } else {
            this.rating = reviews.stream()
              .mapToDouble(Review::getRating)
              .average()
              .orElse(0);
        }
    }

}


