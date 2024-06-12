import csv
import json


def parse_dishes_to_json() -> None:
    data = []

    with open('./assets/filtered_dishes.csv', encoding='utf-8') as csv_file:
        csv_reader = csv.reader(csv_file)

        for i, row in enumerate(csv_reader):
            if i == 1:
                continue
            elif i == 0:
                for j in range(1, len(row), 3):
                    data.append({'carbon': (float(row[j]) + float(row[j + 1])) / 2})
            elif i == 2:
                for j in range(1, len(row), 3):
                    data[j // 3]['name'] = row[j]
                    data[j // 3]['ingredients'] = {}
            else:
                for j in range(0, len(row), 3):
                    if not row[j]:
                        continue
                    data[j // 3]['ingredients'][row[j]] = (float(row[j + 1]) + float(row[j + 2])) / 2

    with open('./assets/dishes.json', 'w') as json_file:
        json_file.write(json.dumps(data))


if __name__ == '__main__':
    parse_dishes_to_json()
