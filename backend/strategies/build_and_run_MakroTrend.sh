#!/bin/bash
echo "Clean install"
cd ..
mvn clean install
cd strategies
echo "Run strategy"
mvn exec:java -Dexec.mainClass="strategies.makroTrendStrategy.StrategyRunner"
