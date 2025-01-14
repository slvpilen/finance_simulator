# Simulator

## Get Started

Run `sh main.sh` in a unix terminal.

## Overview 🔎

This program is a trading simulator, akin to other platforms like MetaTrader and AutoTrader. However, it stands out due to its ability to manage an unlimited amount of data and a diverse range of financial instruments including stocks, energy futures, and commodities, all within a single strategy framework.

## Key Features 🗝️

- **High Data Capacity**: Unlike other simulators, this tool can handle an unprecedented volume of data seamlessly. Not only price, but COT-data (commitment of trader), Makro-data (CPI, labor...), etc
- **Diverse Instrument Support**: It supports a vast array of financial instruments, allowing for a comprehensive trading simulation experience.
- **Simulation within Simulation**: A unique feature that enables automated "walk forward testing". This tool can also be used to verify the effectiveness of a strategy on a single instrument or a group of instruments.
- **Multi-Asset Categorization**: Capable of simulating across 1000s of stocks, it allows for organizing these into various categories, such as different industries.
- **Flexible Strategy Testing**: Test strategies on individual categories, and easily toggle them on or off based on their performance.

## How to open postgres FinancialDB from terminal:

psql -U postgres -d FinanceDB -h localhost -p 5432

# Apply changes

## How to pack backend:

cd backend
mvn clean package

## How to send to server:

`cd backend/api/target`

`scp springboot-1.0-SNAPSHOT.jar oskar@192.168.10.106:/home/oskar/simulator_exec/backend`

`java -jar springboot-1.0-SNAPSHOT.jar --spring.profiles.active=prod --server.port=3000`

## Build frontend

`npm run build `

## Send frontend

`scp -r dist oskar@192.168.10.106:/home/oskar/simulator_exec/frontend`

## How to excecute on server:

`pm2 start java --name springboot-app -- -jar ./springboot-1.0-SNAPSHOT.jar --spring.profiles.active=prod --server.port=8080`

`pm2 save`
`pm2 startup`

## How to fix cant find java folder:

`sudo apt-get update`
`sudo apt-get install default-jdk`

## How to update a running springboot server:

scp -r dist oskar@192.168.10.106:/home/oskar/simulator_exec/frontend`Delete old jar file and add the new. THen run:`pm2 restart springboot-app --update-env -- -jar ./springboot-1.0-SNAPSHOT.jar --spring.profiles.active=prod --server.port=8080`

## Start frontend::

#!/bin/bash

### Variables

PORT=3000 # Change to your desired port

### Step 1: Update and install necessary packages

echo "Updating package list and installing necessary packages..."
sudo apt update
sudo apt install -y nodejs npm

### Step 2: Install 'serve' and 'pm2' globally

echo "Installing 'serve' and 'pm2' globally..."
sudo npm install -g serve pm2

### Step 3: Serve the application using 'serve' and 'pm2' in the current directory

echo "Serving the application..."
pm2 serve dist/ 3000 --name "frontend" --spa

### How to update frontend with zero down time

pm2 reload frontend

### Step 4: Save the pm2 process list and startup script

pm2 save
pm2 startup

echo "Application is now being served on port $PORT."

## How to update database

`psql -h your_host -U your_username -d your_database -f path/to/your/file.sql`
