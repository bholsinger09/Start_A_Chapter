# Database Persistence Configuration for Render Deployment

## Overview
This document explains how to prevent your database from being wiped when manually deploying to Render.

## Key Configuration Changes Made

### 1. Hibernate DDL Auto Setting
- **Development**: `create-drop` - Wipes database on each restart (for testing)
- **Production**: `validate` - Only validates schema, preserves all data

### 2. Flyway Migrations
Your app uses Flyway for database migrations, which is the proper way to handle schema changes in production:
- `V1__Create_initial_tables.sql` - Initial schema
- `V2__Add_institutions_table.sql` - Institutions feature
- `V3__Migrate_existing_chapters_to_institutions.sql` - Data migration
- `V4__Create_audit_logs_table.sql` - Audit logging

## Required Environment Variables on Render

Set these environment variables in your Render service dashboard:

### Essential Variables
```
SPRING_PROFILES_ACTIVE=production
SPRING_JPA_HIBERNATE_DDL_AUTO=validate
DATABASE_URL=(automatically provided by Render PostgreSQL)
```

### Optional but Recommended
```
SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.PostgreSQLDialect
CORS_ALLOWED_ORIGINS=https://your-frontend-domain.onrender.com
```

## Deployment Process

### For Data Preservation:
1. **First-time deployment**: Use `update` to create initial schema
   - Set: `SPRING_JPA_HIBERNATE_DDL_AUTO=update`
   - Deploy once to create tables
   
2. **Subsequent deployments**: Use `validate` to preserve data
   - Change to: `SPRING_JPA_HIBERNATE_DDL_AUTO=validate`
   - All future deployments will preserve data

### Schema Changes:
When you need to modify the database schema:
1. Create new Flyway migration file: `V5__Your_change_description.sql`
2. Keep `SPRING_JPA_HIBERNATE_DDL_AUTO=validate`
3. Flyway will handle the schema changes safely

## What Each DDL Auto Setting Does:

- `create-drop`: **DANGEROUS** - Drops and recreates tables (loses all data)
- `create`: **DANGEROUS** - Creates tables if they don't exist, may conflict
- `update`: **RISKY** - Updates schema, can cause data loss in some cases
- `validate`: **SAFE** - Only validates schema matches entities, preserves all data
- `none`: **SAFEST** - No automatic schema operations

## Verification

After deployment, check your logs for:
```
✓ DDL Auto: validate
✓ Profile: production
✓ Flyway migrations completed successfully
```

## Troubleshooting

If you accidentally wipe data:
1. Check your environment variables in Render dashboard
2. Ensure `SPRING_JPA_HIBERNATE_DDL_AUTO=validate`
3. Consider database backups for critical data

## Database Backup Recommendation

For production data, consider:
1. Render's PostgreSQL backup features
2. Regular database dumps
3. Separate staging environment for testing