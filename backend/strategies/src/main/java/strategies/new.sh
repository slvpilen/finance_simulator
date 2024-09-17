#!/bin/bash

# Script to create a new strategy in the strategies module 
# Further information: README.md

# Directory where templates are stored
TEMPLATE_DIR=".templates"

PLACEHOLDER_STRATEGY_NAME="<STRATEGY_NAME>"
PLACEHOLDER_STRATEGY_FILENAME="<STRATEGY_FILENAME>"
PLACEHOLDER_PACKAGE="<PACKAGE>"
PLACEHOLDER_NEW_DIR="<NEW_DIR>"

# Function to check if a string is all lowercase
is_lowercase() {
    [[ "$1" == "$(echo "$1" | tr '[:upper:]' '[:lower:]')" ]]
}

# Function to convert kebab-case to camelCase with the first letter optionally capitalized
to_camel_case() {
    local input=$1
    local capitalize_first=$2
    IFS='-' read -ra ADDR <<< "$input"
    if [ "$capitalize_first" == "true" ]; then
        RESULT="${ADDR[0]^}"
    else
        RESULT="${ADDR[0]}"
    fi
    for i in "${ADDR[@]:1}"; do
        RESULT+=$(tr '[:lower:]' '[:upper:]' <<< "${i:0:1}")${i:1}
    done
    echo "$RESULT"
}

# Check if a strategy name was provided as a command-line argument
if [ -z "$1" ]; then
    echo "Usage: $0 strategy-name (all lowercase, kebab-case)"
    echo "Example: ./new.sh new-strategy-name"
    exit 1
fi

STRATEGY_NAME="$1"

# Check if the strategy name is all lowercase
if ! is_lowercase "$STRATEGY_NAME"; then
    echo "Strategy name must be all lowercase and in kebab-case."
    exit 1
fi

# Convert strategy name to camelCase for the directory and file name
CAMEL_CASE_DIR_NAME=$(to_camel_case "$STRATEGY_NAME" false)
CAMEL_CASE_FILE_NAME=$(to_camel_case "$STRATEGY_NAME" true)

# Define the template file and new Java file name
TEMPLATE_STRATEGY_FILE="$TEMPLATE_DIR/Strategy.java.template"
TEMPLATE_STRATEGYRUNNER_FILE="$TEMPLATE_DIR/StrategyRunner.java.template"
TEMPLATE_SETTINGS_FILE="$TEMPLATE_DIR/Settings.java.template"
TEMPLATE_RUN_DEV_FILE="$TEMPLATE_DIR/run_dev.sh.template"
TEMPLATE_SQL_FILE="$TEMPLATE_DIR/delete_strategy_result.sql"
TEMPLATE_OPTIMIZATIONFACTORY_FILE="$TEMPLATE_DIR/OptimizationFactory.java.template"

NEW_DIR="$CAMEL_CASE_DIR_NAME"
NEW_FILE_STRATEGY="$NEW_DIR/${CAMEL_CASE_FILE_NAME}Strategy.java"
NEW_FILE_STRATEGYRUNNER="$NEW_DIR/StrategyRunner.java"
NEW_FILE_SETTINGS="$NEW_DIR/Settings.java"
NEW_FILE_RUN_DEV="$NEW_DIR/run_dev.sh"
NEW_FILE_SQL="$NEW_DIR/delete_strategy_result.sql"
NEW_FILE_OPTIMIZATIONFACTORY="$NEW_DIR/OptimizationFactory.java"


# Check if the directory already exists
if [ -d "$NEW_DIR" ]; then
    echo "Strategy already exists: $NEW_DIR"
    exit 1
fi

# Check if template file exists
if [ ! -f "$TEMPLATE_STRATEGY_FILE" ]; then
    echo "Template file not found: $TEMPLATE_STRATEGY_FILE"
    exit 1
fi
if [ ! -f "$TEMPLATE_OPTIMIZATIONFACTORY_FILE" ]; then
    echo "Template file not found: $TEMPLATE_OPTIMIZATIONFACTORY_FILE"
    exit 1
fi


# Create a new directory for the strategy
mkdir -p "$NEW_DIR"

# Replace placeholder in template and save to new file
sed -e "s/$PLACEHOLDER_PACKAGE/$NEW_DIR/g" -e "s/$PLACEHOLDER_STRATEGY_NAME/$STRATEGY_NAME/g" -e "s/$PLACEHOLDER_STRATEGY_FILENAME/${CAMEL_CASE_FILE_NAME}Strategy/g" "$TEMPLATE_STRATEGY_FILE" > "$NEW_FILE_STRATEGY"
sed -e "s/$PLACEHOLDER_PACKAGE/$NEW_DIR/g" -e "s/$PLACEHOLDER_STRATEGY_NAME/$STRATEGY_NAME/g" -e "s/$PLACEHOLDER_STRATEGY_FILENAME/${CAMEL_CASE_FILE_NAME}Strategy/g" "$TEMPLATE_STRATEGYRUNNER_FILE" > "$NEW_FILE_STRATEGYRUNNER"
sed -e "s/$PLACEHOLDER_PACKAGE/$NEW_DIR/g" "$TEMPLATE_SETTINGS_FILE" > "$NEW_FILE_SETTINGS"
sed -e "s/$PLACEHOLDER_NEW_DIR/$NEW_DIR/g" -e "s/$PLACEHOLDER_STRATEGY_NAME/$STRATEGY_NAME/g" "$TEMPLATE_RUN_DEV_FILE" > "$NEW_FILE_RUN_DEV"
sed -e "s/$PLACEHOLDER_PACKAGE/$NEW_DIR/g" -e "s/$PLACEHOLDER_STRATEGY_FILENAME/${CAMEL_CASE_FILE_NAME}Strategy/g" "$TEMPLATE_OPTIMIZATIONFACTORY_FILE" > "$NEW_FILE_OPTIMIZATIONFACTORY" 

echo "1) Java file created: $NEW_FILE_STRATEGY"
echo "2) Java file created: $NEW_FILE_STRATEGYRUNNER"
echo "3) Java file created: $NEW_FILE_SETTINGS"
echo "4) Shell script created: $NEW_FILE_RUN_DEV"
echo "5) Java file created: $NEW_FILE_OPTIMIZATIONFACTORY"

