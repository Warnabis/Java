package com.example.findbuilding.services;

import com.example.findbuilding.models.Building;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class BuildingCacheService {

    private static final int MAX_BUILDING_CACHE_SIZE = 100;
    private static final int MAX_QUERY_CACHE_SIZE = 50;

    private final Map<Long, Building> buildingCache = new LinkedHashMap<>(16, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Long, Building> eldest) {
            return size() > MAX_BUILDING_CACHE_SIZE;
        }
    };

    private final Map<String, List<Building>> queryCache = new LinkedHashMap<>(16, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, List<Building>> eldest) {
            return size() > MAX_QUERY_CACHE_SIZE;
        }
    };

    public Building getCachedBuilding(Long id) {
        return buildingCache.get(id);
    }

    public void cacheBuilding(Long id, Building building) {
        buildingCache.put(id, building);
        log.info("Размер кэша зданий после добавления: {}", buildingCache.size());
    }

    public List<Building> getCachedQueryResult(String query) {
        return queryCache.get(query);
    }

    public void cacheQueryResult(String query, List<Building> buildings) {
        queryCache.put(query, buildings);
        log.info("Размер кэша запросов после добавления: {}", queryCache.size());
    }

    public void removeBuildingFromCache(Long id) {
        buildingCache.remove(id);
        log.info("Здание с ID {} удалено из кэша", id);
    }

    public void clearCache() {
        buildingCache.clear();
        queryCache.clear();
        log.info("Кэш зданий и кэш запросов очищены.");
    }

    public void updateBuildingInCache(Long id, Building building) {
        buildingCache.put(id, building);
        log.info("Здание ID: {} обновлено в кэше", id);
    }
}
