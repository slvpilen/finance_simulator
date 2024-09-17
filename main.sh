#!/bin/bash

# NOTE: mvn clean install must be run before running this script
# This is for development purposes only

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"

# Function to kill processes on exit
cleanup() {
  echo "Shutting down Spring Boot and npm..."
  kill $SPRING_PID $NPM_PID
}

# Trap SIGINT & SIGTERM and call the cleanup function
trap cleanup SIGINT SIGTERM

# Navigate to the backend directory relative to the script's location
cd "$SCRIPT_DIR/backend/springboot" || { echo "Failed to navigate to the backend directory."; exit 1; }

echo "Starting Spring Boot application..."
mvn spring-boot:run -Dspring-boot.run.profiles=dev &
SPRING_PID=$!

sleep 8

cd "$SCRIPT_DIR/frontend" || { echo "Failed to navigate to the frontend directory."; exit 1; }

# Check if node_modules exists and npm ci if it doesn't
if [ ! -d "node_modules" ]; then
    echo "node_modules not found. Running npm ci to install dependencies..."
    npm ci
fi

echo "Starting npm development server..."
npm run dev &
NPM_PID=$!

# Wait for both processes to exit
wait $SPRING_PID $NPM_PID
