# Manual Deployment Guide - ChapterController Routing Fix

## Problem
The frontend chapter creation form fails with 400 error when loading institutions because of a Spring Boot routing conflict:
- `/api/chapters/{id}` was intercepting `/api/chapters/institutions` requests
- Spring Boot was trying to parse "institutions" as a Long ID parameter

## Solution Applied
✅ Added `/chapters/institutions` endpoint BEFORE `/chapters/{id}` in ChapterController.java
✅ Built corrected application jar: `app.jar` (ready to deploy)

## Deployment Steps

### Method 1: AWS Console Browser SSH
1. Go to AWS EC2 Console → Instances
2. Select your instance → Connect → EC2 Instance Connect
3. Open browser terminal

### Method 2: If you have the correct SSH key
```bash
ssh -i /path/to/your-key.pem ubuntu@184.73.57.225
```

### Once Connected to Server:

1. **Navigate to deployment directory:**
```bash
cd ~/Start_A_Chapter/deployment
```

2. **Upload the corrected jar** (you'll need to transfer the file somehow):
   - Option A: Use AWS console file transfer
   - Option B: If you get SSH working: `scp app.jar ubuntu@184.73.57.225:~/Start_A_Chapter/deployment/`
   - Option C: Rebuild on server (see alternative method below)

3. **Stop current containers:**
```bash
sudo docker-compose -f docker-compose.prod.yml down
```

4. **Rebuild backend container:**
```bash
sudo docker-compose -f docker-compose.prod.yml build --no-cache chapter-backend
```

5. **Start updated containers:**
```bash
sudo docker-compose -f docker-compose.prod.yml up -d
```

6. **Wait for startup and check logs:**
```bash
sleep 30
sudo docker-compose -f docker-compose.prod.yml logs chapter-backend --tail=20
```

## Alternative Method: Rebuild on Server

If you can't transfer the jar file, you can apply the fix directly on the server:

1. **Connect to server and navigate to source:**
```bash
cd ~/Start_A_Chapter
```

2. **Edit ChapterController.java on server:**
```bash
# Add the institutions endpoint before the {id} endpoint
# The fix is to add this method after getAllChapters() and before getChapterById():

# @GetMapping("/chapters/institutions")
# public ResponseEntity<List<Institution>> getChapterInstitutions() {
#     try {
#         List<Institution> institutions = institutionService.getAllInstitutions();
#         return ResponseEntity.ok(institutions);
#     } catch (Exception e) {
#         return ResponseEntity.ok(new ArrayList<>());
#     }
# }
```

3. **Rebuild application:**
```bash
mvn clean compile package -Dmaven.test.skip=true
cp target/campus-chapter-organizer-1.0.0-SNAPSHOT.jar deployment/app.jar
```

4. **Follow deployment steps 3-6 above**

## Testing the Fix

After deployment, test the institutions endpoint:
```bash
curl https://startachapter.duckdns.org/api/chapters/institutions
```

Should return a JSON array of institutions instead of a 400 error.

## Verification

1. **Check container status:**
```bash
sudo docker-compose -f docker-compose.prod.yml ps
```

2. **Test the endpoint:**
```bash
curl -v https://startachapter.duckdns.org/api/chapters/institutions
```

3. **Check backend logs if issues:**
```bash
sudo docker-compose -f docker-compose.prod.yml logs chapter-backend --tail=50
```

## Expected Result

✅ `/api/chapters/institutions` returns list of institutions  
✅ Chapter creation form loads institution dropdown  
✅ No more 400 "Failed to convert 'institutions' to Long" errors  

The fix ensures Spring Boot routes `/chapters/institutions` to the specific endpoint before trying to match it against `/chapters/{id}`.