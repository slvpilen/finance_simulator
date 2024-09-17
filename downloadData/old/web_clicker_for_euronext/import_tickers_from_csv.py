import csv
import os

"""
Dette programmet henter ut ticker-navn fra en csv-fil.

"""


FILE_NAME = "Euronext_Equities_2023-11-03.csv"


def _find_path_to_file(file_name):
    current_path = os.getcwd()

    current_path_parent = parent_path = os.path.dirname(current_path)
    file_path = current_path_parent + "\\" + "appdata" + "\\" + file_name
    return file_path


def get_stock_names_from_csv():
    file_path = _find_path_to_file(FILE_NAME)

    stock_names = []

    with open(file_path, newline="", encoding="utf-8") as csvfile:
        csvreader = csv.reader(csvfile, delimiter=";")

        # Skip the header info that's not part of the actual data
        for _ in range(4):
            next(csvreader)

        # Extract stock names from each row
        for row in csvreader:
            if row:
                stock_names.append(row[0].strip('"'))

    return stock_names
