# -*- coding: utf-8 -*-
import os
import matplotlib.pyplot as plt
import matplotlib.patches as patches

os.makedirs('docs/diagrams', exist_ok=True)

def draw_circle(ax, x, y, r, text, bg_color='white', border_color='black', text_color='black', font_size=9, bold=False):
    c = patches.Circle((x, y), r, facecolor=bg_color, edgecolor=border_color, lw=1.2, zorder=3)
    ax.add_patch(c)
    ax.text(x, y, text, color=text_color, fontsize=font_size, ha='center', va='center',
            fontweight='bold' if bold else 'normal', zorder=4)

def draw_arrow(ax, x1, y1, x2, y2, label="", label_pos=(0.5, 0.5), label_side='left', color='#404040'):
    ax.annotate('', xy=(x2, y2), xytext=(x1, y1),
                arrowprops=dict(arrowstyle="-|>", color=color, lw=1.2, mutation_scale=10),
                zorder=2)
    if label:
        lx = x1 + (x2 - x1) * label_pos[0]
        ly = y1 + (y2 - y1) * label_pos[1]
        offset_x = -0.3 if label_side == 'left' else 0.3
        ax.text(lx + offset_x, ly, label, fontsize=8, color=color, ha='center', va='center', fontweight='bold', zorder=5)

def draw_annotation(ax, x, y, text, color='red', side='right', font_size=8):
    offset = 0.5 if side == 'right' else -0.5
    ha = 'left' if side == 'right' else 'right'
    ax.text(x + offset, y, text, color=color, fontsize=font_size, ha=ha, va='center', fontweight='bold', zorder=5)


# ==========================================
# 1. createBeverage: CFG
# ==========================================
def draw_cfg_create_beverage():
    fig, ax = plt.subplots(figsize=(4.5, 7.5), dpi=200)
    ax.set_xlim(0, 10)
    ax.set_ylim(0, 12)
    ax.axis('off')

    # Nodes
    draw_circle(ax, 5, 11, 0.45, 'Bắt đầu', bg_color='#4CAF50', border_color='#2E7D32', text_color='white', font_size=8, bold=True)
    draw_circle(ax, 5, 9.5, 0.4, '1')
    
    # Switch cases
    draw_circle(ax, 1.8, 7.5, 0.4, '2')
    draw_circle(ax, 3.4, 7.5, 0.4, '3')
    draw_circle(ax, 5.0, 7.5, 0.4, '4')
    draw_circle(ax, 6.6, 7.5, 0.4, '5')
    draw_circle(ax, 8.4, 7.5, 0.4, '6')
    
    draw_circle(ax, 4.2, 5.2, 0.4, '7')
    draw_circle(ax, 4.2, 3.5, 0.4, '8')
    draw_circle(ax, 5.0, 1.2, 0.45, 'Kết thúc', bg_color='#4CAF50', border_color='#2E7D32', text_color='white', font_size=8, bold=True)

    # Arrows
    draw_arrow(ax, 5, 10.55, 5, 9.9)
    draw_arrow(ax, 4.7, 9.2, 2.1, 7.8, label="COFFEE", label_pos=(0.4, 0.5), label_side='left')
    draw_arrow(ax, 4.8, 9.1, 3.5, 7.9, label="TEA", label_pos=(0.4, 0.5), label_side='left')
    draw_arrow(ax, 5.0, 9.1, 5.0, 7.9, label="MATCHA", label_pos=(0.5, 0.5), label_side='right')
    draw_arrow(ax, 5.2, 9.1, 6.5, 7.9, label="SMOOTHIE", label_pos=(0.4, 0.5), label_side='right')
    draw_arrow(ax, 5.3, 9.2, 8.1, 7.8, label="default", label_pos=(0.4, 0.5), label_side='right')

    draw_arrow(ax, 2.0, 7.1, 3.9, 5.5)
    draw_arrow(ax, 3.5, 7.1, 4.1, 5.6)
    draw_arrow(ax, 4.9, 7.1, 4.3, 5.6)
    draw_arrow(ax, 6.4, 7.1, 4.5, 5.5)

    draw_arrow(ax, 4.2, 4.8, 4.2, 3.9)
    draw_arrow(ax, 4.2, 3.1, 4.8, 1.6)
    draw_arrow(ax, 8.4, 7.1, 5.4, 1.5)

    plt.tight_layout()
    plt.savefig('docs/diagrams/cfg_create_beverage.png', bbox_inches='tight')
    plt.close()

# ==========================================
# 2. createBeverage: DFG
# ==========================================
def draw_dfg_create_beverage():
    fig, ax = plt.subplots(figsize=(5.5, 7.5), dpi=200)
    ax.set_xlim(0, 11)
    ax.set_ylim(0, 12)
    ax.axis('off')

    draw_circle(ax, 5, 11, 0.45, 'd(item)', bg_color='#4CAF50', border_color='#2E7D32', text_color='white', font_size=8, bold=True)
    draw_circle(ax, 5, 9.5, 0.4, '1')
    draw_annotation(ax, 5, 9.5, 'u(item)', color='#C00000', side='right')

    draw_circle(ax, 1.5, 7.5, 0.4, '2')
    draw_annotation(ax, 1.5, 7.0, 'd(factory)', color='#C00000', side='right')
    draw_circle(ax, 3.2, 7.5, 0.4, '3')
    draw_annotation(ax, 3.2, 7.0, 'd(factory)', color='#C00000', side='right')
    draw_circle(ax, 5.0, 7.5, 0.4, '4')
    draw_annotation(ax, 5.0, 7.0, 'd(factory)', color='#C00000', side='right')
    draw_circle(ax, 6.8, 7.5, 0.4, '5')
    draw_annotation(ax, 6.8, 7.0, 'd(factory)', color='#C00000', side='right')
    draw_circle(ax, 8.8, 7.5, 0.4, '6')
    draw_annotation(ax, 8.8, 7.5, 'u(item)', color='#C00000', side='right')

    draw_circle(ax, 4.2, 5.2, 0.4, '7')
    draw_circle(ax, 4.2, 3.5, 0.4, '8')
    draw_annotation(ax, 4.2, 3.5, 'u(factory), u(item)', color='#C00000', side='right')

    draw_circle(ax, 5.0, 1.2, 0.45, 'k(all)', bg_color='#4CAF50', border_color='#2E7D32', text_color='white', font_size=8, bold=True)

    draw_arrow(ax, 5, 10.55, 5, 9.9)
    draw_arrow(ax, 4.7, 9.2, 1.8, 7.8)
    draw_arrow(ax, 4.8, 9.1, 3.3, 7.9)
    draw_arrow(ax, 5.0, 9.1, 5.0, 7.9)
    draw_arrow(ax, 5.2, 9.1, 6.7, 7.9)
    draw_arrow(ax, 5.3, 9.2, 8.5, 7.8)

    draw_arrow(ax, 1.7, 7.1, 3.9, 5.5)
    draw_arrow(ax, 3.3, 7.1, 4.1, 5.6)
    draw_arrow(ax, 4.9, 7.1, 4.3, 5.6)
    draw_arrow(ax, 6.6, 7.1, 4.5, 5.5)

    draw_arrow(ax, 4.2, 4.8, 4.2, 3.9)
    draw_arrow(ax, 4.2, 3.1, 4.8, 1.6)
    draw_arrow(ax, 8.8, 7.1, 5.4, 1.5)

    plt.tight_layout()
    plt.savefig('docs/diagrams/dfg_create_beverage.png', bbox_inches='tight')
    plt.close()

# ==========================================
# 3. createBeverage: Lifecycle item
# ==========================================
def draw_lifecycle_item():
    fig, ax = plt.subplots(figsize=(4.5, 7.5), dpi=200)
    ax.set_xlim(0, 10)
    ax.set_ylim(0, 12)
    ax.axis('off')

    draw_circle(ax, 5, 11, 0.45, 'd(item)', bg_color='#4CAF50', border_color='#2E7D32', text_color='white', font_size=8, bold=True)
    draw_circle(ax, 5, 9.5, 0.4, '1')
    draw_annotation(ax, 5, 9.5, 'u(item)', color='#C00000', side='right')

    draw_circle(ax, 1.8, 7.5, 0.4, '2')
    draw_circle(ax, 3.4, 7.5, 0.4, '3')
    draw_circle(ax, 5.0, 7.5, 0.4, '4')
    draw_circle(ax, 6.6, 7.5, 0.4, '5')
    draw_circle(ax, 8.4, 7.5, 0.4, '6')
    draw_annotation(ax, 8.4, 7.5, 'u(item)', color='#C00000', side='right')

    draw_circle(ax, 4.2, 5.2, 0.4, '7')
    draw_circle(ax, 4.2, 3.5, 0.4, '8')
    draw_annotation(ax, 4.2, 3.5, 'u(item)', color='#C00000', side='right')
    draw_circle(ax, 5.0, 1.2, 0.45, 'k(all)', bg_color='#4CAF50', border_color='#2E7D32', text_color='white', font_size=8, bold=True)

    draw_arrow(ax, 5, 10.55, 5, 9.9)
    draw_arrow(ax, 4.7, 9.2, 2.1, 7.8)
    draw_arrow(ax, 4.8, 9.1, 3.5, 7.9)
    draw_arrow(ax, 5.0, 9.1, 5.0, 7.9)
    draw_arrow(ax, 5.2, 9.1, 6.5, 7.9)
    draw_arrow(ax, 5.3, 9.2, 8.1, 7.8)

    draw_arrow(ax, 2.0, 7.1, 3.9, 5.5)
    draw_arrow(ax, 3.5, 7.1, 4.1, 5.6)
    draw_arrow(ax, 4.9, 7.1, 4.3, 5.6)
    draw_arrow(ax, 6.4, 7.1, 4.5, 5.5)

    draw_arrow(ax, 4.2, 4.8, 4.2, 3.9)
    draw_arrow(ax, 4.2, 3.1, 4.8, 1.6)
    draw_arrow(ax, 8.4, 7.1, 5.4, 1.5)

    plt.tight_layout()
    plt.savefig('docs/diagrams/var_item_create_beverage.png', bbox_inches='tight')
    plt.close()

# ==========================================
# 4. createBeverage: Lifecycle factory
# ==========================================
def draw_lifecycle_factory():
    fig, ax = plt.subplots(figsize=(4.5, 7.5), dpi=200)
    ax.set_xlim(0, 10)
    ax.set_ylim(0, 12)
    ax.axis('off')

    draw_circle(ax, 5, 11, 0.45, 'Bắt đầu', bg_color='#4CAF50', border_color='#2E7D32', text_color='white', font_size=8, bold=True)
    draw_circle(ax, 5, 9.5, 0.4, '1')

    draw_circle(ax, 1.8, 7.5, 0.4, '2')
    draw_annotation(ax, 1.8, 7.0, 'd(factory)', color='#C00000', side='right')
    draw_circle(ax, 3.4, 7.5, 0.4, '3')
    draw_annotation(ax, 3.4, 7.0, 'd(factory)', color='#C00000', side='right')
    draw_circle(ax, 5.0, 7.5, 0.4, '4')
    draw_annotation(ax, 5.0, 7.0, 'd(factory)', color='#C00000', side='right')
    draw_circle(ax, 6.6, 7.5, 0.4, '5')
    draw_annotation(ax, 6.6, 7.0, 'd(factory)', color='#C00000', side='right')
    draw_circle(ax, 8.4, 7.5, 0.4, '6')

    draw_circle(ax, 4.2, 5.2, 0.4, '7')
    draw_circle(ax, 4.2, 3.5, 0.4, '8')
    draw_annotation(ax, 4.2, 3.5, 'u(factory)', color='#C00000', side='right')
    draw_circle(ax, 5.0, 1.2, 0.45, 'k(all)', bg_color='#4CAF50', border_color='#2E7D32', text_color='white', font_size=8, bold=True)

    draw_arrow(ax, 5, 10.55, 5, 9.9)
    draw_arrow(ax, 4.7, 9.2, 2.1, 7.8)
    draw_arrow(ax, 4.8, 9.1, 3.5, 7.9)
    draw_arrow(ax, 5.0, 9.1, 5.0, 7.9)
    draw_arrow(ax, 5.2, 9.1, 6.5, 7.9)
    draw_arrow(ax, 5.3, 9.2, 8.1, 7.8)

    draw_arrow(ax, 2.0, 7.1, 3.9, 5.5)
    draw_arrow(ax, 3.5, 7.1, 4.1, 5.6)
    draw_arrow(ax, 4.9, 7.1, 4.3, 5.6)
    draw_arrow(ax, 6.4, 7.1, 4.5, 5.5)

    draw_arrow(ax, 4.2, 4.8, 4.2, 3.9)
    draw_arrow(ax, 4.2, 3.1, 4.8, 1.6)
    draw_arrow(ax, 8.4, 7.1, 5.4, 1.5)

    plt.tight_layout()
    plt.savefig('docs/diagrams/var_factory_create_beverage.png', bbox_inches='tight')
    plt.close()


# ==========================================
# 5. saveRecipeItem: CFG
# ==========================================
def draw_cfg_save_recipe():
    fig, ax = plt.subplots(figsize=(4.0, 8.0), dpi=200)
    ax.set_xlim(0, 10)
    ax.set_ylim(0, 12)
    ax.axis('off')

    draw_circle(ax, 5, 11.2, 0.45, 'Bắt đầu', bg_color='#4CAF50', border_color='#2E7D32', text_color='white', font_size=8, bold=True)
    draw_circle(ax, 5, 9.8, 0.4, '1')
    draw_circle(ax, 2.2, 8.5, 0.4, '2')

    draw_circle(ax, 5, 8.2, 0.4, '3')
    draw_circle(ax, 2.2, 6.8, 0.4, '4')

    draw_circle(ax, 5, 6.5, 0.4, '5')
    draw_circle(ax, 2.2, 5.0, 0.4, '6')

    draw_circle(ax, 5, 4.8, 0.4, '7')
    draw_circle(ax, 5, 3.2, 0.4, '8')
    draw_circle(ax, 5, 1.2, 0.45, 'Kết thúc', bg_color='#4CAF50', border_color='#2E7D32', text_color='white', font_size=8, bold=True)

    draw_arrow(ax, 5, 10.75, 5, 10.2)
    draw_arrow(ax, 4.7, 9.5, 2.5, 8.7, label="True", label_pos=(0.5, 0.5), label_side='left')
    draw_arrow(ax, 5.0, 9.4, 5.0, 8.6, label="False", label_pos=(0.5, 0.5), label_side='right')

    draw_arrow(ax, 4.7, 7.9, 2.5, 7.0, label="True", label_pos=(0.5, 0.5), label_side='left')
    draw_arrow(ax, 5.0, 7.8, 5.0, 6.9, label="False", label_pos=(0.5, 0.5), label_side='right')

    draw_arrow(ax, 4.7, 6.2, 2.5, 5.2, label="True", label_pos=(0.5, 0.5), label_side='left')
    draw_arrow(ax, 5.0, 6.1, 5.0, 5.2, label="False", label_pos=(0.5, 0.5), label_side='right')

    draw_arrow(ax, 5, 4.4, 5, 3.6)
    draw_arrow(ax, 5, 2.8, 5, 1.65)

    draw_arrow(ax, 2.2, 8.1, 4.6, 1.4)
    draw_arrow(ax, 2.2, 6.4, 4.7, 1.4)
    draw_arrow(ax, 2.2, 4.6, 4.8, 1.4)

    plt.tight_layout()
    plt.savefig('docs/diagrams/cfg_save_recipe.png', bbox_inches='tight')
    plt.close()

# ==========================================
# 6. saveRecipeItem: DFG
# ==========================================
def draw_dfg_save_recipe():
    fig, ax = plt.subplots(figsize=(6.0, 8.0), dpi=200)
    ax.set_xlim(0, 12)
    ax.set_ylim(0, 12)
    ax.axis('off')

    draw_circle(ax, 5, 11.2, 0.45, 'd(params)', bg_color='#4CAF50', border_color='#2E7D32', text_color='white', font_size=7, bold=True)
    draw_circle(ax, 5, 9.8, 0.4, '1')
    draw_annotation(ax, 5, 9.8, 'u(beverage)', color='#C00000', side='right')

    draw_circle(ax, 2.0, 8.5, 0.4, '2')

    draw_circle(ax, 5, 8.2, 0.4, '3')
    draw_annotation(ax, 5, 8.2, 'u(inventoryItem)', color='#C00000', side='right')

    draw_circle(ax, 2.0, 6.8, 0.4, '4')

    draw_circle(ax, 5, 6.5, 0.4, '5')
    draw_annotation(ax, 5, 6.5, 'u(quantityRequired)', color='#C00000', side='right')

    draw_circle(ax, 2.0, 5.0, 0.4, '6')

    draw_circle(ax, 5, 4.8, 0.4, '7')
    draw_annotation(ax, 5, 4.8, 'u(beverage), u(inventoryItem), u(quantity), d(recipeItem)', color='#C00000', side='right', font_size=7)

    draw_circle(ax, 5, 3.2, 0.4, '8')
    draw_annotation(ax, 5, 3.2, 'u(recipeItem), u(repository)', color='#C00000', side='right', font_size=7)

    draw_circle(ax, 5, 1.2, 0.45, 'k(all)', bg_color='#4CAF50', border_color='#2E7D32', text_color='white', font_size=8, bold=True)

    draw_arrow(ax, 5, 10.75, 5, 10.2)
    draw_arrow(ax, 4.7, 9.5, 2.4, 8.7)
    draw_arrow(ax, 5.0, 9.4, 5.0, 8.6)
    draw_arrow(ax, 4.7, 7.9, 2.4, 7.0)
    draw_arrow(ax, 5.0, 7.8, 5.0, 6.9)
    draw_arrow(ax, 4.7, 6.2, 2.4, 5.2)
    draw_arrow(ax, 5.0, 6.1, 5.0, 5.2)

    draw_arrow(ax, 5, 4.4, 5, 3.6)
    draw_arrow(ax, 5, 2.8, 5, 1.65)

    draw_arrow(ax, 2.0, 8.1, 4.6, 1.4)
    draw_arrow(ax, 2.0, 6.4, 4.7, 1.4)
    draw_arrow(ax, 2.0, 4.6, 4.8, 1.4)

    plt.tight_layout()
    plt.savefig('docs/diagrams/dfg_save_recipe.png', bbox_inches='tight')
    plt.close()

# ==========================================
# 7. saveRecipeItem: Lifecycle beverage
# ==========================================
def draw_lifecycle_beverage():
    fig, ax = plt.subplots(figsize=(4.0, 8.0), dpi=200)
    ax.set_xlim(0, 10)
    ax.set_ylim(0, 12)
    ax.axis('off')

    draw_circle(ax, 5, 11.2, 0.45, 'd(beverage)', bg_color='#4CAF50', border_color='#2E7D32', text_color='white', font_size=7, bold=True)
    draw_circle(ax, 5, 9.8, 0.4, '1')
    draw_annotation(ax, 5, 9.8, 'u(beverage)', color='#C00000', side='right')
    draw_circle(ax, 2.2, 8.5, 0.4, '2')
    draw_circle(ax, 5, 8.2, 0.4, '3')
    draw_circle(ax, 2.2, 6.8, 0.4, '4')
    draw_circle(ax, 5, 6.5, 0.4, '5')
    draw_circle(ax, 2.2, 5.0, 0.4, '6')
    draw_circle(ax, 5, 4.8, 0.4, '7')
    draw_annotation(ax, 5, 4.8, 'u(beverage)', color='#C00000', side='right')
    draw_circle(ax, 5, 3.2, 0.4, '8')
    draw_circle(ax, 5, 1.2, 0.45, 'k(beverage)', bg_color='#4CAF50', border_color='#2E7D32', text_color='white', font_size=7, bold=True)

    draw_arrow(ax, 5, 10.75, 5, 10.2)
    draw_arrow(ax, 4.7, 9.5, 2.5, 8.7)
    draw_arrow(ax, 5.0, 9.4, 5.0, 8.6)
    draw_arrow(ax, 4.7, 7.9, 2.5, 7.0)
    draw_arrow(ax, 5.0, 7.8, 5.0, 6.9)
    draw_arrow(ax, 4.7, 6.2, 2.5, 5.2)
    draw_arrow(ax, 5.0, 6.1, 5.0, 5.2)
    draw_arrow(ax, 5, 4.4, 5, 3.6)
    draw_arrow(ax, 5, 2.8, 5, 1.65)
    draw_arrow(ax, 2.2, 8.1, 4.6, 1.4)
    draw_arrow(ax, 2.2, 6.4, 4.7, 1.4)
    draw_arrow(ax, 2.2, 4.6, 4.8, 1.4)

    plt.tight_layout()
    plt.savefig('docs/diagrams/var_beverage_save_recipe.png', bbox_inches='tight')
    plt.close()

# ==========================================
# 8. saveRecipeItem: Lifecycle inventoryItem
# ==========================================
def draw_lifecycle_inventory():
    fig, ax = plt.subplots(figsize=(4.0, 8.0), dpi=200)
    ax.set_xlim(0, 10)
    ax.set_ylim(0, 12)
    ax.axis('off')

    draw_circle(ax, 5, 11.2, 0.45, 'd(item)', bg_color='#4CAF50', border_color='#2E7D32', text_color='white', font_size=8, bold=True)
    draw_circle(ax, 5, 9.8, 0.4, '1')
    draw_circle(ax, 2.2, 8.5, 0.4, '2')
    draw_circle(ax, 5, 8.2, 0.4, '3')
    draw_annotation(ax, 5, 8.2, 'u(item)', color='#C00000', side='right')
    draw_circle(ax, 2.2, 6.8, 0.4, '4')
    draw_circle(ax, 5, 6.5, 0.4, '5')
    draw_circle(ax, 2.2, 5.0, 0.4, '6')
    draw_circle(ax, 5, 4.8, 0.4, '7')
    draw_annotation(ax, 5, 4.8, 'u(item)', color='#C00000', side='right')
    draw_circle(ax, 5, 3.2, 0.4, '8')
    draw_circle(ax, 5, 1.2, 0.45, 'k(item)', bg_color='#4CAF50', border_color='#2E7D32', text_color='white', font_size=8, bold=True)

    draw_arrow(ax, 5, 10.75, 5, 10.2)
    draw_arrow(ax, 4.7, 9.5, 2.5, 8.7)
    draw_arrow(ax, 5.0, 9.4, 5.0, 8.6)
    draw_arrow(ax, 4.7, 7.9, 2.5, 7.0)
    draw_arrow(ax, 5.0, 7.8, 5.0, 6.9)
    draw_arrow(ax, 4.7, 6.2, 2.5, 5.2)
    draw_arrow(ax, 5.0, 6.1, 5.0, 5.2)
    draw_arrow(ax, 5, 4.4, 5, 3.6)
    draw_arrow(ax, 5, 2.8, 5, 1.65)
    draw_arrow(ax, 2.2, 8.1, 4.6, 1.4)
    draw_arrow(ax, 2.2, 6.4, 4.7, 1.4)
    draw_arrow(ax, 2.2, 4.6, 4.8, 1.4)

    plt.tight_layout()
    plt.savefig('docs/diagrams/var_inventory_save_recipe.png', bbox_inches='tight')
    plt.close()


# Run all
draw_cfg_create_beverage()
draw_dfg_create_beverage()
draw_lifecycle_item()
draw_lifecycle_factory()

draw_cfg_save_recipe()
draw_dfg_save_recipe()
draw_lifecycle_beverage()
draw_lifecycle_inventory()

print("All diagrams drawn and saved in docs/diagrams/")
