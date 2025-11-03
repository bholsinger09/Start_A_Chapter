#!/bin/bash

# Simple deployment script for SearchController
echo "Deploying SearchController..."

# Try to copy the SearchController to the server
scp -o StrictHostKeyChecking=no -i ~/.ssh/id_rsa src/main/java/com/turningpoint/chapterorganizer/controller/SearchController.java ubuntu@184.73.57.225:/tmp/

# SSH to the server and deploy
ssh -o StrictHostKeyChecking=no -i ~/.ssh/id_rsa ubuntu@184.73.57.225 << 'ENDSSH'
    echo "Copying SearchController to project..."
    sudo mkdir -p /opt/start_a_chapter/src/main/java/com/turningpoint/chapterorganizer/controller/
    sudo cp /tmp/SearchController.java /opt/start_a_chapter/src/main/java/com/turningpoint/chapterorganizer/controller/
    
    echo "Rebuilding backend container..."
    cd /opt/start_a_chapter
    sudo docker-compose -f docker-compose.prod.yml stop backend
    sudo docker-compose -f docker-compose.prod.yml rm -f backend
    sudo docker-compose -f docker-compose.prod.yml build backend
    sudo docker-compose -f docker-compose.prod.yml up -d backend
    
    echo "SearchController deployed successfully!"
ENDSSH

echo "Deployment complete!"
