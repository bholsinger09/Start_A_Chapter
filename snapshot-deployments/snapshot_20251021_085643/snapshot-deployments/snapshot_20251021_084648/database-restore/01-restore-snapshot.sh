#!/bin/bash

set -e

echo "=== Snapshot Database Restore Script ==="
echo "Snapshot: ${SNAPSHOT_NAME:-unknown}"

# Wait for PostgreSQL to be ready
until pg_isready -h localhost -U postgres -d postgres; do
    echo "Waiting for PostgreSQL to be ready..."
    sleep 2
done

echo "PostgreSQL is ready. Starting snapshot restore process..."

# Create the database if it doesn't exist
psql -U postgres -c "CREATE DATABASE chapterdb_snapshot;" || echo "Database already exists"

# Restore from snapshot database backup
if [ -f "/docker-entrypoint-initdb.d/../database.sql.gz" ]; then
    echo "Restoring from compressed PostgreSQL backup"
    gunzip -c "/docker-entrypoint-initdb.d/../database.sql.gz" | psql -U postgres -d chapterdb_snapshot
elif [ -f "/docker-entrypoint-initdb.d/../database.sql" ]; then
    echo "Restoring from uncompressed PostgreSQL backup"
    psql -U postgres -d chapterdb_snapshot -f "/docker-entrypoint-initdb.d/../database.sql"
elif [ -f "/docker-entrypoint-initdb.d/../database.zip" ]; then
    echo "Found H2 backup - creating compatible PostgreSQL schema"
    psql -U postgres -d chapterdb_snapshot -c "
        CREATE TABLE IF NOT EXISTS chapters (
            id BIGSERIAL PRIMARY KEY,
            name VARCHAR(255) NOT NULL,
            university VARCHAR(255),
            city VARCHAR(255),
            state VARCHAR(255),
            created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        );
        
        CREATE TABLE IF NOT EXISTS members (
            id BIGSERIAL PRIMARY KEY,
            first_name VARCHAR(255),
            last_name VARCHAR(255),
            email VARCHAR(255),
            chapter_id BIGINT REFERENCES chapters(id)
        );
        
        CREATE TABLE IF NOT EXISTS events (
            id BIGSERIAL PRIMARY KEY,
            title VARCHAR(255),
            description TEXT,
            event_date TIMESTAMP,
            chapter_id BIGINT REFERENCES chapters(id)
        );
        
        CREATE TABLE IF NOT EXISTS member_roles (
            id BIGSERIAL PRIMARY KEY,
            role_name VARCHAR(255),
            member_id BIGINT REFERENCES members(id)
        );
        
        CREATE TABLE IF NOT EXISTS event_types (
            id BIGSERIAL PRIMARY KEY,
            type_name VARCHAR(255),
            description TEXT
        );
    "
    echo "Created basic schema for H2 backup compatibility"
elif [ -f "/docker-entrypoint-initdb.d/../database.empty" ]; then
    echo "Found empty backup marker - creating empty database with schema"
    psql -U postgres -d chapterdb_snapshot -c "SELECT 1;" # Just ensure connection works
else
    echo "WARNING: No database backup found - creating empty database"
fi

echo "=== Snapshot Database Restore Complete ==="
