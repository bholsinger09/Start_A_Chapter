# University Filter Fix Deployment Commands

Since you can connect through AWS Console, please run these commands on the EC2 instance:

## 1. Navigate to project directory and pull latest changes
```bash
cd ~/StartAChapter
git pull origin main
```

## 2. Check current status
```bash
sudo docker-compose -f docker-compose.prod.yml ps
```

## 3. Rebuild and restart the backend with the university filtering fix
```bash
sudo docker-compose -f docker-compose.prod.yml build chapter-backend
sudo docker-compose -f docker-compose.prod.yml up -d chapter-backend
```

## 4. Check backend logs to ensure it started successfully
```bash
sudo docker-compose -f docker-compose.prod.yml logs chapter-backend --tail=20
```

## 5. Test the new filtering functionality
```bash
# Test default behavior (should exclude Idaho universities)
curl -s "https://startachapter.duckdns.org/api/chapters/institutions" | jq -r '.[].state' | sort | uniq

# Test specific state filtering
curl -s "https://startachapter.duckdns.org/api/chapters/institutions?state=CA" | jq -r '.[].name'

# Test custom exclusion (exclude Idaho and Alaska)
curl -s "https://startachapter.duckdns.org/api/chapters/institutions?exclude=ID,AK" | jq -r '.[].state' | sort | uniq
```

## What the fix does:

1. **Default behavior**: Now excludes Idaho (ID), Alaska (AK), Hawaii (HI), Wyoming (WY), North Dakota (ND), South Dakota (SD), Vermont (VT), Delaware (DE), Rhode Island (RI), and DC from the university dropdown

2. **State filtering**: Add `?state=CA` to get only California universities

3. **Custom exclusion**: Add `?exclude=ID,TX,FL` to exclude specific states

4. **Type filtering**: Add `?type=University` to get only universities (not colleges)

This should resolve the issue of Idaho universities showing up in the chapter creation form!