#!/bin/bash

# Deploy the member creation fix to the server
echo "Deploying member creation fix..."

# Copy the JAR to the server
scp -i temp_key target/campus-chapter-organizer-1.0.0-SNAPSHOT.jar ubuntu@startachapter.duckdns.org:~/app.jar

# Restart the backend container
ssh -i temp_key ubuntu@startachapter.duckdns.org "docker restart chapter-backend"

echo "Deployment complete!"