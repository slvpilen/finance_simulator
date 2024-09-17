#!/bin/bash

# Check and perform actions based on arguments
for arg in "$@"
do
  case "$arg" in
    ci)
      echo "Clean install..."
      cd backend
      mvn clean install
        cd ..
      ;;
    download)
      echo "Downloading daily data..."
      cd downloadData
      pipenv run python fetch_all_tick_daily.py
      cd ..
      ;;
    *)
      echo "Invalid command: $arg. Usage: sh main.sh [install|download]"
      exit 1
      ;;
  esac
done


echo "Downloading preclose date for stocks"
cd downloadData
echo "Note: take care of half day and different opening hours"
pipenv run python fetch_preclose.py
cd ../backend/strategies
echo "Assuming clean install is DONE!"
echo "Update holdings Makro Trend"
mvn exec:java -Dexec.mainClass="strategies.makroTrendStrategy.StrategyRunnerLive"
echo "Done updating holdings Makro Trend"


