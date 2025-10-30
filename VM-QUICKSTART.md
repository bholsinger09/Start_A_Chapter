# VM Quick Start

## One-Command VM Setup

For Ubuntu-based virtual machines, run:

```bash
curl -sSL https://raw.githubusercontent.com/bholsinger09/Start_A_Chapter/main/scripts/setup-vm.sh | bash
```

Or manually:

```bash
git clone https://github.com/bholsinger09/Start_A_Chapter.git
cd Start_A_Chapter
./scripts/setup-vm.sh
```

## Quick Commands

**Start QA Environment:**
```bash
./scripts/start-qa.sh
```

**Docker Alternative:**
```bash
docker-compose -f docker-compose.qa.yml up --build
```

**Database Management:**
```bash
./scripts/export-db.sh    # Export data
./scripts/import-db.sh    # Import data
```

## Access URLs

- **Backend API:** http://VM_IP:8080
- **H2 Console:** http://VM_IP:8080/h2-console
- **Frontend:** http://VM_IP:3000 (Docker)
- **DB Admin:** http://VM_IP:8081 (Docker)

## VM Requirements

- **OS:** Ubuntu 20.04+ or similar
- **RAM:** 2GB minimum, 4GB recommended  
- **Storage:** 10GB minimum
- **Network:** Bridge or NAT with port forwarding

See **VM-SETUP-GUIDE.md** for detailed instructions.