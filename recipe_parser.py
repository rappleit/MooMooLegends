from collections import defaultdict
import csv
import itertools
import json
from typing import Mapping, Tuple
from urllib.request import urlopen


DISHES = [
    'Aglio Olio',
    'Aloo Gobi',
    'Assam Pedas',
    'Ayam Penyet',
    'Bahn Mi',
    'Bak Kut Teh',
    'Ban Mian',
    'Beef Kway Teow',
    'Beef Noodle Soup',
    'Braised Duck Noodle',
    'Butter Chicken',
    'Carbonara',
    'Century Egg Porridge',
    'Cereal Prawn',
    'Char Kway Teow',
    'Char Siu Rice',
    'Chee Cheong Fun',
    'Chicken Kebab',
    'Chicken Pot Pie',
    'Chicken Rice',
    'Chicken Tikka Masala',
    'Chicken Wings',
    'Chili Con Carne',
    'Chili Crab',
    'Claypot Rice',
    'Crab Bee Hoon',
    'Curry Udon',
    'Donburi',
    'Drunken Prawn',
    'Duck Rice',
    'Empanadas',
    'Falafel',
    'Fish and Chips',
    'Fish Head Curry',
    'Fish Soup',
    'Fishball Noodle',
    'Fried Radish Cake',
    'Frog Porridge',
    'Hainanese Curry Rice',
    'Hokkien Mee',
    'Kung Pao Chicken',
    'Kway Chap',
    'Laksa',
    'Lamb Kofta',
    'Lontong',
    'Lotus Root Soup',
    'Masala Dosa',
    'Mee Goreng',
    'Mee Rebus',
    'Mee Siam',
    'Mee Soto',
    'Minced Meat Noodle',
    'Murtabak',
    'Nasi Briyani',
    'Nasi Goreng',
    'Nasi Lemak',
    'Nasi Padang',
    'Oxtail Soup',
    'Oyster Omelette',
    'Pad Thai',
    'Pao Fan',
    'Pig Organ Soup',
    'Pineapple Fried Rice',
    'Prawn Noodles',
    'Prawn Paste Chicken',
    'Roti John',
    'Roti Prata',
    'Sambal Stingray',
    'Satay',
    'Satay Bee Hoon',
    'Sayur Lodeh',
    "Shepherd's Pie",
    'Shredded Chicken Noodle',
    'Shrimp Paste Chicken',
    'Sliced Fish Soup',
    'Soto Ayam',
    'Soup Kambing',
    'Spanish Paella',
    'Takoyaki',
    'Tandoori Chicken',
    'Teochew Braised Duck',
    'Teochew Porridge',
    'Thai Basil Chicken',
    'Thunder Tea Rice',
    'Tom Yum Goong',
    'Tom Yum Soup',
    'Tonkatsu ',
    'Tonkatsu Ramen',
    'Vegetarian Bee Hoon',
    'Vietnamese Pho',
    'Wanton Mee'
]


def ingredient_carbon_footprint() -> None:
    print('Processing ingredient carbon footprint...')
    data = None
    with urlopen('https://assets.plateupfortheplanet.org/carbon-calculator/JSON/ingredients-updated.json') as url:
        data = list(json.loads(url.read().decode()))

    ingredients = {}
    for ingredient in data:
        ingredients[ingredient['FOOD']] = ingredient['Asia']  # get carbon footprint of ingredient sourced in Asia

    with open('./assets/ingredients.csv', 'w', encoding='utf-8') as csv_file:
        csv_writer = csv.writer(csv_file)
        csv_writer.writerow(('Ingredient Name', 'Carbon Footprint (kg CO2e / kg)'))
        csv_writer.writerows(sorted(ingredients.items()))
    print('Done processing ingredient carbon footprint.\n')


def dish_ingredient_weights(dish: str) -> Mapping[str, Tuple[float, float]]:
    print(f'Processing {dish} ingredients...')
    recipes = None
    with urlopen(f"https://www.edamam.com/api/recipes/v2?type=public&q={'%20'.join(dish.split())}&field=ingredients&field=yield") as url:
        recipes = dict(json.loads(url.read().decode()))

    ingredients = defaultdict(lambda: [float('inf'), -float('inf')])
    for recipe in recipes['hits']:
        servings = int(recipe['recipe']['yield'])
        for ingredient in recipe['recipe']['ingredients']:
            # Average out the portions of ingredients according to the serving size
            ingredients[ingredient['food']][0] = min(ingredients[ingredient['food']][0], float(ingredient['weight']) / servings)
            ingredients[ingredient['food']][1] = max(ingredients[ingredient['food']][1], float(ingredient['weight']) / servings)

    print(f'Done processing {dish} ingredients.\n')
    return ingredients


def process_dishes() -> None:
    dishes = [list(sorted(dish_ingredient_weights(dish).items(), key=lambda t: t[1][0], reverse=True)) for dish in DISHES]

    with open('./assets/dishes.csv', 'w', encoding='utf-8') as csv_file:
        csv_writer = csv.writer(csv_file)
        csv_writer.writerow(('Ingredient Name', 'Minimum Weight (g, if present)', 'Maximum Weight (g, if present)') * len(DISHES))
        csv_writer.writerow(itertools.chain.from_iterable(('', dish, '') for dish in DISHES))
        for row in itertools.zip_longest(*dishes, fillvalue=('', ('', ''))):
            csv_writer.writerow(itertools.chain.from_iterable((name, min_weight, max_weight) for name, (min_weight, max_weight) in row))

    print('Done processing all dishes.\n')


if __name__ == '__main__':
    ingredient_carbon_footprint()
    process_dishes()
