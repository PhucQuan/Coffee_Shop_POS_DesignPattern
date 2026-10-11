name_mapping = {
    'Ca phe sua': 'Cà phê sữa',
    'Bac xiu': 'Bạc xỉu',
    'Tra dao': 'Trà đào cam sả',
    'Tra sua': 'Trà sữa truyền thống',
    'Matcha latte': 'Matcha Latte',
    'Sinh to xoai': 'Sinh tố xoài',
    'Espresso': 'Espresso',
    'Americano': 'Americano',
    'Latte': 'Latte',
    'Cappuccino': 'Cappuccino',
    'Cold brew': 'Cold Brew',
    'Tra vai': 'Trà vải lài',
    'Tra tac mat ong': 'Trà tắc mật ong',
    'Matcha da xay': 'Matcha đá xay',
    'Sinh to dau': 'Sinh tố dâu tây',
    'Cacao nong': 'Cacao nóng',
    'Vanilla latte': 'Vanilla Latte',
    'Caramel macchiato': 'Caramel Macchiato',
    'Mocha': 'Mocha',
    'Hong tra sua': 'Hồng trà sữa',
    'Oolong sua': 'Ô long sữa',
    'Tra sen vang': 'Trà sen vàng',
    'Matcha cream cheese': 'Matcha Cream Cheese',
    'Sinh to bo': 'Sinh tố bơ'
}

with open('frontend-react/src/data/mockData.js', 'r', encoding='utf-8') as f:
    content = f.read()

for no_acc, acc in name_mapping.items():
    content = content.replace(f'"name": "{no_acc}"', f'"name": "{acc}"')

with open('frontend-react/src/data/mockData.js', 'w', encoding='utf-8') as f:
    f.write(content)

print('Updated Vietnamese accents for all beverages in mockData.js!')
