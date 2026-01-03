#!/bin/bash

# Rileva se è installato Redis o Memurai
REDIS_CLI=""
REDIS_SERVER=""
REDIS_NAME=""

# Cerca Redis
if [ -f "/c/Program Files/Redis/redis-cli.exe" ]; then
    REDIS_CLI="/c/Program Files/Redis/redis-cli.exe"
    REDIS_SERVER="/c/Program Files/Redis/redis-server.exe"
    REDIS_NAME="Redis"
# Cerca Memurai
elif [ -f "/c/Program Files/Memurai/memurai-cli.exe" ]; then
    REDIS_CLI="/c/Program Files/Memurai/memurai-cli.exe"
    REDIS_SERVER="/c/Program Files/Memurai/memurai.exe"
    REDIS_NAME="Memurai"
# Prova i comandi nel PATH
elif command -v redis-cli &>/dev/null; then
    REDIS_CLI="redis-cli"
    REDIS_SERVER="redis-server"
    REDIS_NAME="Redis"
elif command -v memurai-cli &>/dev/null; then
    REDIS_CLI="memurai-cli"
    REDIS_SERVER="memurai"
    REDIS_NAME="Memurai"
else
    echo "neither Redis nor Memurai found. error."
    exit 1
fi

# Controlla se è già in esecuzione
if ! "$REDIS_CLI" ping &>/dev/null; then
    echo "starting $REDIS_NAME..."
    start "" "$REDIS_SERVER" &>/dev/null
    sleep 2
    if "$REDIS_CLI" ping &>/dev/null; then
        echo "$REDIS_NAME started successfully"
    else
        echo "$REDIS_NAME may not have started correctly"
        exit 1
    fi
else
    echo "$REDIS_NAME already running"
fi

