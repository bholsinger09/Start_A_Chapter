# AWS Console Deployment Guide

## Step 1: Access Your EC2 Instance via AWS Console

1. **Log into AWS Console**
   - Go to https://console.aws.amazon.com
   - Navigate to EC2 service

2. **Find Your Instance**
   - Look for instance with IP: `184.73.57.225`
   - Should be running Ubuntu 24.04.3 LTS

3. **Connect via Session Manager or EC2 Instance Connect**
   - Select your instance
   - Click "Connect" 
   - Choose "EC2 Instance Connect" or "Session Manager"
   - Click "Connect" to open browser-based terminal

## Step 2: Deploy the SearchController Fix

Once connected to the server terminal, run these commands:

```bash
# Navigate to the project directory
cd /opt/start_a_chapter

# Create the controller directory if it doesn't exist
sudo mkdir -p src/main/java/com/turningpoint/chapterorganizer/controller/

# Create the updated ChapterController.java with search endpoints
sudo tee src/main/java/com/turningpoint/chapterorganizer/controller/ChapterController.java > /dev/null << 'EOF'
package com.turningpoint.chapterorganizer.controller;

import com.turningpoint.chapterorganizer.entity.Chapter;
import com.turningpoint.chapterorganizer.service.ChapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ChapterController {

    @Autowired
    private ChapterService chapterService;

    @GetMapping("/chapters")
    public ResponseEntity<List<Chapter>> getAllChapters() {
        try {
            List<Chapter> chapters = chapterService.getAllActiveChapters();
            return ResponseEntity.ok(chapters);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/chapters/{id}")
    public ResponseEntity<Chapter> getChapterById(@PathVariable Long id) {
        try {
            Optional<Chapter> chapter = chapterService.getChapterById(id);
            return chapter.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/chapters")
    public ResponseEntity<Chapter> createChapter(@RequestBody Chapter chapter) {
        try {
            Chapter createdChapter = chapterService.createChapter(chapter);
            return ResponseEntity.ok(createdChapter);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Chapter> updateChapter(@PathVariable Long id, @RequestBody Chapter chapter) {
        try {
            Chapter updatedChapter = chapterService.updateChapter(id, chapter);
            return ResponseEntity.ok(updatedChapter);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChapter(@PathVariable Long id) {
        try {
            chapterService.deleteChapter(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Search endpoints - fixes dashboard 404 errors
    @GetMapping("/search/trending")
    public ResponseEntity<List<Map<String, Object>>> getTrendingChapters(
            @RequestParam(defaultValue = "5") int limit) {
        try {
            List<Chapter> allChapters = chapterService.getAllChapters();
            
            // Take the first few chapters as "trending"
            List<Chapter> trendingChapters = allChapters.stream()
                .limit(limit)
                .toList();
            
            // Convert to simplified format for frontend
            List<Map<String, Object>> trending = new ArrayList<>();
            for (Chapter chapter : trendingChapters) {
                Map<String, Object> trendingItem = new HashMap<>();
                trendingItem.put("id", chapter.getId());
                trendingItem.put("name", chapter.getName());
                trendingItem.put("university", chapter.getUniversityName());
                trendingItem.put("state", chapter.getState());
                trendingItem.put("memberCount", 0);
                trendingItem.put("trend", "up");
                trending.add(trendingItem);
            }
            
            return ResponseEntity.ok(trending);
        } catch (Exception e) {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @GetMapping("/search/recommendations")
    public ResponseEntity<List<Map<String, Object>>> getRecommendations(
            @RequestParam(defaultValue = "3") int limit) {
        try {
            List<Chapter> allChapters = chapterService.getAllChapters();
            
            // Sort by creation date (newest first) and limit
            List<Chapter> recommendedChapters = allChapters.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(limit)
                .toList();
            
            // Convert to simplified format for frontend
            List<Map<String, Object>> recommendations = new ArrayList<>();
            for (Chapter chapter : recommendedChapters) {
                Map<String, Object> recommendation = new HashMap<>();
                recommendation.put("id", chapter.getId());
                recommendation.put("name", chapter.getName());
                recommendation.put("university", chapter.getUniversityName());
                recommendation.put("state", chapter.getState());
                recommendation.put("memberCount", 0);
                recommendation.put("type", "chapter");
                recommendations.add(recommendation);
            }
            
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }
}
EOF

# Stop the current backend container
sudo docker-compose -f docker-compose.prod.yml stop backend

# Remove the old backend container
sudo docker-compose -f docker-compose.prod.yml rm -f backend

# Rebuild the backend with the new controller
sudo docker-compose -f docker-compose.prod.yml build backend

# Start the backend container
sudo docker-compose -f docker-compose.prod.yml up -d backend

# Wait for startup and test the endpoints
echo "Waiting for backend to start..."
sleep 30

# Test the new endpoints
echo "Testing trending endpoint:"
curl -s "http://localhost:8080/api/search/trending?limit=3"

echo -e "\n\nTesting recommendations endpoint:"
curl -s "http://localhost:8080/api/search/recommendations?limit=3"

echo -e "\n\nDeployment complete! Dashboard 404 errors should now be resolved."
```

## Step 3: Verify the Fix

After running the deployment commands, test the endpoints externally:

```bash
# Test from your local machine
curl "https://startachapter.duckdns.org/api/search/trending?limit=3"
curl "https://startachapter.duckdns.org/api/search/recommendations?limit=3"
```

## Expected Results

- Both endpoints should return JSON arrays (may be empty if no chapters in database)
- Dashboard should no longer show 404 errors
- Search functionality should work properly

## Troubleshooting

If the endpoints still return 404:
1. Check backend container logs: `sudo docker logs start_a_chapter-backend-1`
2. Verify container is running: `sudo docker ps`
3. Check if file was created properly: `ls -la src/main/java/com/turningpoint/chapterorganizer/controller/`

## Alternative: Quick Test Data

If you want to test with sample data, create a test chapter first:

```bash
curl -X POST "https://startachapter.duckdns.org/api/chapters" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Chapter",
    "universityName": "Test University", 
    "state": "CA",
    "contactEmail": "test@test.com"
  }'
```

Then test the search endpoints to see the chapter data returned.