package com.turningpoint.chapterorganizer.repository;

import com.turningpoint.chapterorganizer.entity.OperationalMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OperationalMetricRepository extends JpaRepository<OperationalMetric, Long> {

    // Find metrics by name
    List<OperationalMetric> findByMetricNameOrderByMetricDateDesc(String metricName);

    // Find metrics by date range
    @Query("SELECT m FROM OperationalMetric m WHERE m.metricDate BETWEEN :startDate AND :endDate ORDER BY m.metricDate DESC")
    List<OperationalMetric> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Find metrics by category
    List<OperationalMetric> findByCategoryOrderByMetricDateDesc(String category);

    // Find chapter-specific metrics
    List<OperationalMetric> findByChapterIdOrderByMetricDateDesc(Long chapterId);

    // Find latest metric by name
    Optional<OperationalMetric> findFirstByMetricNameOrderByMetricDateDesc(String metricName);

    // Find metrics for specific name and date range
    @Query("SELECT m FROM OperationalMetric m WHERE m.metricName = :metricName AND m.metricDate BETWEEN :startDate AND :endDate ORDER BY m.metricDate DESC")
    List<OperationalMetric> findByMetricNameAndDateRange(@Param("metricName") String metricName, 
                                                        @Param("startDate") LocalDate startDate, 
                                                        @Param("endDate") LocalDate endDate);

    // Calculate average for a metric over time period
    @Query("SELECT AVG(m.metricValue) FROM OperationalMetric m WHERE m.metricName = :metricName AND m.metricDate BETWEEN :startDate AND :endDate")
    Double calculateAverageByMetricNameAndDateRange(@Param("metricName") String metricName, 
                                                   @Param("startDate") LocalDate startDate, 
                                                   @Param("endDate") LocalDate endDate);

    // Get daily metrics summary
    @Query("SELECT m.metricName, COUNT(m), AVG(m.metricValue), MAX(m.metricValue), MIN(m.metricValue) " +
           "FROM OperationalMetric m WHERE m.metricDate = :date " +
           "GROUP BY m.metricName ORDER BY m.metricName")
    List<Object[]> getDailyMetricsSummary(@Param("date") LocalDate date);

    // Get chapter performance comparison
    @Query("SELECT m.chapterId, m.metricName, AVG(m.metricValue) " +
           "FROM OperationalMetric m WHERE m.metricName = :metricName AND m.metricDate BETWEEN :startDate AND :endDate AND m.chapterId IS NOT NULL " +
           "GROUP BY m.chapterId, m.metricName ORDER BY AVG(m.metricValue) DESC")
    List<Object[]> getChapterPerformanceComparison(@Param("metricName") String metricName,
                                                   @Param("startDate") LocalDate startDate, 
                                                   @Param("endDate") LocalDate endDate);

    // Check if metric exists for specific date
    boolean existsByMetricNameAndMetricDate(String metricName, LocalDate metricDate);

    // Get trending metrics (growth rate)
    @Query("SELECT m1.metricName, " +
           "(AVG(CASE WHEN m1.metricDate >= :recentStart THEN m1.metricValue END) - " +
           " AVG(CASE WHEN m1.metricDate < :recentStart THEN m1.metricValue END)) as growth " +
           "FROM OperationalMetric m1 WHERE m1.metricDate BETWEEN :overallStart AND :overallEnd " +
           "GROUP BY m1.metricName ORDER BY growth DESC")
    List<Object[]> getTrendingMetrics(@Param("overallStart") LocalDate overallStart,
                                     @Param("recentStart") LocalDate recentStart,
                                     @Param("overallEnd") LocalDate overallEnd);
}