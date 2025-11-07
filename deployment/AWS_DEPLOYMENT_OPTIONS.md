# Alternative Deployment Methods for AWS EC2

## Method 1: SSH Key Setup (Recommended)
On your server, run:
```bash
mkdir -p ~/.ssh
echo "ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAILXj+n5uLJecHOvh407aleeKAy+9i6bNou8vcYm0AdAj bholsinger@gmail.com" >> ~/.ssh/authorized_keys
chmod 700 ~/.ssh
chmod 600 ~/.ssh/authorized_keys
```

Then from your local machine:
```bash
cd /Users/benh/Documents/StartAChapter/deployment
./deploy-to-production.sh
```

## Method 2: Manual File Transfer via AWS Console
1. Use AWS EC2 Instance Connect in the AWS Console
2. Or use AWS Systems Manager Session Manager
3. Upload the JAR file through the web interface

## Method 3: Manual SCP with Password Authentication
If your server allows password authentication:
```bash
scp -o PreferredAuthentications=password app.jar ubuntu@13.222.125.134:/home/ubuntu/app/
```

## Method 4: Direct Upload on Server
Since you're logged in to the server, you can download directly:
```bash
# On the server
cd /home/ubuntu/app
sudo systemctl stop startachapter
curl -L https://github.com/bholsinger09/Start_A_Chapter/releases/latest/download/app.jar -o app.jar
sudo systemctl start startachapter
```

## What's in the New Version?
✨ **University Dropdown Feature**
- Replaces text input with dropdown of 50+ universities
- Auto-populates state and city when university is selected
- Enhanced form validation and user experience
- Complete Vue.js 3 frontend rebuild with Bootstrap 5

🔧 **Backend Improvements** 
- Enhanced `/api/chapters/with-institution` endpoint
- Better error handling and logging
- Maintained backward compatibility

## Verification After Deployment
1. Visit: https://startachapter.duckdns.org/#/chapters/create
2. Confirm university field is now a dropdown
3. Test state/city auto-population by selecting a university
4. Verify form submission works correctly