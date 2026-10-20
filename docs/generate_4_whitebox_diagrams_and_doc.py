# -*- coding: utf-8 -*-
import os
import matplotlib.pyplot as plt
import matplotlib.patches as patches
import docx
from docx.shared import Pt, RGBColor, Inches
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT

os.makedirs('docs/diagrams', exist_ok=True)

# ==============================================================================
# DIAGRAM DRAWING HELPERS
# ==============================================================================
def draw_circle(ax, x, y, r, text, bg_color='white', border_color='black', text_color='black', font_size=9, bold=False):
    c = patches.Circle((x, y), r, facecolor=bg_color, edgecolor=border_color, lw=1.2, zorder=3)
    ax.add_patch(c)
    ax.text(x, y, text, color=text_color, fontsize=font_size, ha='center', va='center',
            fontweight='bold' if bold else 'normal', zorder=4)

def draw_arrow(ax, x1, y1, x2, y2, label="", label_pos=(0.5, 0.5), label_side='left', color='#333333'):
    ax.annotate('', xy=(x2, y2), xytext=(x1, y1),
                arrowprops=dict(arrowstyle="-|>", color=color, lw=1.2, mutation_scale=10),
                zorder=2)
    if label:
        lx = x1 + (x2 - x1) * label_pos[0]
        ly = y1 + (y2 - y1) * label_pos[1]
        offset_x = -0.35 if label_side == 'left' else 0.35
        ax.text(lx + offset_x, ly, label, fontsize=8, color=color, ha='center', va='center', fontweight='bold', zorder=5)

def draw_curved_arrow(ax, x1, y1, x2, y2, rad=0.3, label="", color='#333333'):
    ax.annotate('', xy=(x2, y2), xytext=(x1, y1),
                arrowprops=dict(arrowstyle="-|>", color=color, lw=1.2, mutation_scale=10,
                                connectionstyle=f"arc3,rad={rad}"),
                zorder=2)
    if label:
        mx = (x1 + x2) / 2 + 0.4
        my = (y1 + y2) / 2
        ax.text(mx, my, label, fontsize=8, color=color, ha='center', va='center', fontweight='bold', zorder=5)

def draw_annotation(ax, x, y, text, color='#C00000', side='right', font_size=8):
    offset = 0.45 if side == 'right' else -0.45
    ha = 'left' if side == 'right' else 'right'
    ax.text(x + offset, y, text, color=color, fontsize=font_size, ha=ha, va='center', fontweight='bold', zorder=5)


# ------------------------------------------------------------------------------
# 1. restockForOrder: CFG & DFG
# ------------------------------------------------------------------------------
def generate_cfg_restock_for_order():
    fig, ax = plt.subplots(figsize=(4.2, 7.5), dpi=200)
    ax.set_xlim(0, 10)
    ax.set_ylim(0, 12)
    ax.axis('off')

    draw_circle(ax, 5, 11, 0.45, 'Bắt đầu', bg_color='#4CAF50', border_color='#2E7D32', text_color='white', font_size=8, bold=True)
    draw_circle(ax, 5, 9.6, 0.4, '1')
    draw_circle(ax, 2.2, 8.0, 0.4, '2')
    draw_circle(ax, 6.2, 8.0, 0.4, '3')
    draw_circle(ax, 6.2, 6.3, 0.4, '4')
    draw_circle(ax, 8.0, 4.8, 0.4, '5')
    draw_circle(ax, 8.0, 3.2, 0.4, '6')
    draw_circle(ax, 4.5, 4.8, 0.4, '7')
    draw_circle(ax, 3.5, 1.4, 0.45, 'Kết thúc', bg_color='#4CAF50', border_color='#2E7D32', text_color='white', font_size=8, bold=True)

    draw_arrow(ax, 5, 10.55, 5, 10.0)
    draw_arrow(ax, 4.7, 9.3, 2.5, 8.3, label="True", label_pos=(0.5, 0.5), label_side='left')
    draw_arrow(ax, 5.3, 9.3, 6.0, 8.4, label="False", label_pos=(0.5, 0.5), label_side='right')

    draw_arrow(ax, 2.2, 7.6, 3.3, 1.8)
    draw_arrow(ax, 6.2, 7.6, 6.2, 6.7)
    
    draw_arrow(ax, 6.5, 6.0, 7.7, 5.1, label="Còn pt", label_pos=(0.4, 0.5), label_side='right')
    draw_arrow(ax, 5.9, 6.0, 4.7, 5.1, label="Hết pt", label_pos=(0.4, 0.5), label_side='left')

    draw_arrow(ax, 8.0, 4.4, 8.0, 3.6)
    draw_curved_arrow(ax, 8.2, 3.5, 6.5, 6.4, rad=-0.4)

    draw_arrow(ax, 4.5, 4.4, 3.7, 1.8)

    plt.tight_layout()
    plt.savefig('docs/diagrams/cfg_restock_for_order.png', bbox_inches='tight')
    plt.close()

def generate_dfg_restock_for_order():
    fig, ax = plt.subplots(figsize=(5.5, 7.5), dpi=200)
    ax.set_xlim(0, 11)
    ax.set_ylim(0, 12)
    ax.axis('off')

    draw_circle(ax, 5, 11, 0.45, 'd(order)', bg_color='#4CAF50', border_color='#2E7D32', text_color='white', font_size=7.5, bold=True)
    draw_circle(ax, 5, 9.6, 0.4, '1')
    draw_annotation(ax, 5, 9.6, 'u(order)', side='right')

    draw_circle(ax, 2.0, 8.0, 0.4, '2')
    draw_circle(ax, 6.2, 8.0, 0.4, '3')
    draw_annotation(ax, 6.2, 8.0, 'u(order), d(requirements)', side='right', font_size=7)

    draw_circle(ax, 6.2, 6.3, 0.4, '4')
    draw_annotation(ax, 6.2, 6.3, 'u(requirements)', side='right', font_size=7.5)

    draw_circle(ax, 7.5, 4.8, 0.4, '5')
    draw_annotation(ax, 7.5, 4.8, 'd(name), d(quantity), u(name), d(item)', side='right', font_size=6.5)

    draw_circle(ax, 7.5, 3.2, 0.4, '6')
    draw_annotation(ax, 7.5, 3.2, 'u(item), u(quantity), u(order)', side='right', font_size=6.5)

    draw_circle(ax, 4.2, 4.8, 0.4, '7')
    draw_annotation(ax, 4.2, 4.8, 'u(order)', side='left', font_size=7.5)

    draw_circle(ax, 3.2, 1.4, 0.45, 'k(all)', bg_color='#4CAF50', border_color='#2E7D32', text_color='white', font_size=8, bold=True)

    draw_arrow(ax, 5, 10.55, 5, 10.0)
    draw_arrow(ax, 4.7, 9.3, 2.3, 8.3)
    draw_arrow(ax, 5.3, 9.3, 6.0, 8.4)
    draw_arrow(ax, 2.0, 7.6, 3.0, 1.8)
    draw_arrow(ax, 6.2, 7.6, 6.2, 6.7)
    draw_arrow(ax, 6.5, 6.0, 7.3, 5.1)
    draw_arrow(ax, 5.9, 6.0, 4.4, 5.1)
    draw_arrow(ax, 7.5, 4.4, 7.5, 3.6)
    draw_curved_arrow(ax, 7.8, 3.5, 6.5, 6.4, rad=-0.4)
    draw_arrow(ax, 4.2, 4.4, 3.4, 1.8)

    plt.tight_layout()
    plt.savefig('docs/diagrams/dfg_restock_for_order.png', bbox_inches='tight')
    plt.close()


# ------------------------------------------------------------------------------
# 2. validateAvailable: CFG & DFG
# ------------------------------------------------------------------------------
def generate_cfg_validate_available():
    fig, ax = plt.subplots(figsize=(4.5, 7.5), dpi=200)
    ax.set_xlim(0, 10)
    ax.set_ylim(0, 12)
    ax.axis('off')

    draw_circle(ax, 5, 11, 0.45, 'Bắt đầu', bg_color='#4CAF50', border_color='#2E7D32', text_color='white', font_size=8, bold=True)
    draw_circle(ax, 5, 9.5, 0.4, '1')
    draw_circle(ax, 3.0, 7.8, 0.4, '2')
    draw_circle(ax, 3.0, 6.0, 0.4, '3')
    draw_circle(ax, 1.8, 4.2, 0.4, '4')
    draw_circle(ax, 7.0, 7.8, 0.4, '5')
    draw_circle(ax, 4.5, 1.6, 0.45, 'Kết thúc', bg_color='#4CAF50', border_color='#2E7D32', text_color='white', font_size=8, bold=True)

    draw_arrow(ax, 5, 10.55, 5, 9.9)
    draw_arrow(ax, 4.7, 9.2, 3.2, 8.1, label="Còn pt", label_pos=(0.4, 0.5), label_side='left')
    draw_arrow(ax, 5.3, 9.2, 6.7, 8.1, label="Hết pt", label_pos=(0.4, 0.5), label_side='right')

    draw_arrow(ax, 3.0, 7.4, 3.0, 6.4)
    draw_arrow(ax, 2.7, 5.7, 2.0, 4.6, label="True", label_pos=(0.4, 0.5), label_side='left')
    draw_curved_arrow(ax, 3.3, 6.2, 4.8, 9.3, rad=0.35, label="False")

    draw_arrow(ax, 1.8, 3.8, 4.2, 2.0)
    draw_arrow(ax, 7.0, 7.4, 4.8, 2.0)

    plt.tight_layout()
    plt.savefig('docs/diagrams/cfg_validate_available.png', bbox_inches='tight')
    plt.close()

def generate_dfg_validate_available():
    fig, ax = plt.subplots(figsize=(6.0, 7.5), dpi=200)
    ax.set_xlim(0, 12)
    ax.set_ylim(0, 12)
    ax.axis('off')

    draw_circle(ax, 5, 11, 0.45, 'd(requirements)', bg_color='#4CAF50', border_color='#2E7D32', text_color='white', font_size=7, bold=True)
    draw_circle(ax, 5, 9.5, 0.4, '1')
    draw_annotation(ax, 5, 9.5, 'u(requirements)', side='right')

    draw_circle(ax, 3.0, 7.8, 0.4, '2')
    draw_annotation(ax, 3.0, 7.8, 'd(req), u(req), d(item)', side='left', font_size=7)

    draw_circle(ax, 3.0, 6.0, 0.4, '3')
    draw_annotation(ax, 3.0, 6.0, 'u(item), u(req)', side='left', font_size=7.5)

    draw_circle(ax, 1.8, 4.2, 0.4, '4')
    draw_annotation(ax, 1.8, 4.2, 'u(item), u(req)', side='left', font_size=7.5)

    draw_circle(ax, 7.5, 7.8, 0.4, '5')
    draw_circle(ax, 4.5, 1.6, 0.45, 'k(all)', bg_color='#4CAF50', border_color='#2E7D32', text_color='white', font_size=8, bold=True)

    draw_arrow(ax, 5, 10.55, 5, 9.9)
    draw_arrow(ax, 4.7, 9.2, 3.2, 8.1)
    draw_arrow(ax, 5.3, 9.2, 7.2, 8.1)
    draw_arrow(ax, 3.0, 7.4, 3.0, 6.4)
    draw_arrow(ax, 2.7, 5.7, 2.0, 4.6)
    draw_curved_arrow(ax, 3.3, 6.2, 4.8, 9.3, rad=0.35)
    draw_arrow(ax, 1.8, 3.8, 4.2, 2.0)
    draw_arrow(ax, 7.5, 7.4, 4.8, 2.0)

    plt.tight_layout()
    plt.savefig('docs/diagrams/dfg_validate_available.png', bbox_inches='tight')
    plt.close()


# ------------------------------------------------------------------------------
# 3. restockItem: CFG & DFG
# ------------------------------------------------------------------------------
def generate_cfg_restock_item():
    fig, ax = plt.subplots(figsize=(4.0, 7.5), dpi=200)
    ax.set_xlim(0, 10)
    ax.set_ylim(0, 12)
    ax.axis('off')

    draw_circle(ax, 5, 11, 0.45, 'Bắt đầu', bg_color='#4CAF50', border_color='#2E7D32', text_color='white', font_size=8, bold=True)
    draw_circle(ax, 5, 9.5, 0.4, '1')
    draw_circle(ax, 2.5, 7.8, 0.4, '2')
    draw_circle(ax, 6.5, 7.8, 0.4, '3')
    draw_circle(ax, 6.5, 5.8, 0.4, '4')
    draw_circle(ax, 6.5, 3.8, 0.4, '5')
    draw_circle(ax, 4.5, 1.5, 0.45, 'Kết thúc', bg_color='#4CAF50', border_color='#2E7D32', text_color='white', font_size=8, bold=True)

    draw_arrow(ax, 5, 10.55, 5, 9.9)
    draw_arrow(ax, 4.7, 9.2, 2.7, 8.1, label="True", label_pos=(0.5, 0.5), label_side='left')
    draw_arrow(ax, 5.3, 9.2, 6.3, 8.1, label="False", label_pos=(0.5, 0.5), label_side='right')

    draw_arrow(ax, 2.5, 7.4, 4.3, 1.9)
    draw_arrow(ax, 6.5, 7.4, 6.5, 6.2)
    draw_arrow(ax, 6.5, 5.4, 6.5, 4.2)
    draw_arrow(ax, 6.5, 3.4, 4.7, 1.9)

    plt.tight_layout()
    plt.savefig('docs/diagrams/cfg_restock_item.png', bbox_inches='tight')
    plt.close()

def generate_dfg_restock_item():
    fig, ax = plt.subplots(figsize=(5.5, 7.5), dpi=200)
    ax.set_xlim(0, 11)
    ax.set_ylim(0, 12)
    ax.axis('off')

    draw_circle(ax, 5, 11, 0.45, 'd(id, amount)', bg_color='#4CAF50', border_color='#2E7D32', text_color='white', font_size=7, bold=True)
    draw_circle(ax, 5, 9.5, 0.4, '1')
    draw_annotation(ax, 5, 9.5, 'u(amount)', side='right')

    draw_circle(ax, 2.5, 7.8, 0.4, '2')

    draw_circle(ax, 6.5, 7.8, 0.4, '3')
    draw_annotation(ax, 6.5, 7.8, 'u(id), d(item)', side='right', font_size=7.5)

    draw_circle(ax, 6.5, 5.8, 0.4, '4')
    draw_annotation(ax, 6.5, 5.8, 'u(item), u(amount)', side='right', font_size=7.5)

    draw_circle(ax, 6.5, 3.8, 0.4, '5')

    draw_circle(ax, 4.5, 1.5, 0.45, 'k(all)', bg_color='#4CAF50', border_color='#2E7D32', text_color='white', font_size=8, bold=True)

    draw_arrow(ax, 5, 10.55, 5, 9.9)
    draw_arrow(ax, 4.7, 9.2, 2.7, 8.1)
    draw_arrow(ax, 5.3, 9.2, 6.3, 8.1)
    draw_arrow(ax, 2.5, 7.4, 4.3, 1.9)
    draw_arrow(ax, 6.5, 7.4, 6.5, 6.2)
    draw_arrow(ax, 6.5, 5.4, 6.5, 4.2)
    draw_arrow(ax, 6.5, 3.4, 4.7, 1.9)

    plt.tight_layout()
    plt.savefig('docs/diagrams/dfg_restock_item.png', bbox_inches='tight')
    plt.close()


# ------------------------------------------------------------------------------
# 4. addRequirements: CFG & DFG
# ------------------------------------------------------------------------------
def generate_cfg_add_requirements():
    fig, ax = plt.subplots(figsize=(4.5, 14.0), dpi=200)
    ax.set_xlim(0, 10)
    ax.set_ylim(0, 22)
    ax.axis('off')

    draw_circle(ax, 5, 21.2, 0.35, 'Bắt đầu', bg_color='#4CAF50', border_color='#2E7D32', text_color='white', font_size=7, bold=True)
    draw_circle(ax, 5, 20.2, 0.3, '1')
    draw_circle(ax, 5, 19.3, 0.3, '2')
    draw_circle(ax, 5, 18.4, 0.3, '3')
    
    # Branch empty vs recipe loop
    draw_circle(ax, 3.0, 17.4, 0.3, '4')
    draw_circle(ax, 6.8, 17.4, 0.3, '5')
    draw_circle(ax, 8.2, 16.5, 0.3, '6')
    draw_circle(ax, 8.2, 15.6, 0.3, '7')

    draw_circle(ax, 5, 15.6, 0.3, '8')

    # Sequential condition pairs (if / add)
    pairs = [
        (9, 10, 14.5), (11, 12, 13.3), (13, 14, 12.1), (15, 16, 10.9),
        (17, 18, 9.7), (19, 20, 8.5), (21, 22, 7.3), (23, 24, 6.1),
        (25, 26, 4.9), (27, 28, 3.7)
    ]
    for if_node, add_node, y in pairs:
        draw_circle(ax, 4.2, y, 0.28, str(if_node))
        draw_circle(ax, 6.5, y - 0.4, 0.28, str(add_node))

    draw_circle(ax, 4.2, 2.4, 0.28, '29')
    draw_circle(ax, 4.2, 1.2, 0.35, 'Kết thúc', bg_color='#4CAF50', border_color='#2E7D32', text_color='white', font_size=7, bold=True)

    # Connections
    draw_arrow(ax, 5, 20.85, 5, 20.5)
    draw_arrow(ax, 5, 19.9, 5, 19.6)
    draw_arrow(ax, 5, 19.0, 5, 18.7)

    draw_arrow(ax, 4.8, 18.2, 3.2, 17.6, label="True", label_pos=(0.5, 0.5), label_side='left')
    draw_arrow(ax, 5.2, 18.2, 6.6, 17.6, label="False", label_pos=(0.5, 0.5), label_side='right')

    draw_arrow(ax, 3.0, 17.1, 4.8, 15.8)
    draw_arrow(ax, 6.9, 17.1, 7.9, 16.6)
    draw_arrow(ax, 8.2, 16.2, 8.2, 15.9)
    draw_curved_arrow(ax, 8.0, 15.4, 7.0, 17.2, rad=0.3)
    draw_arrow(ax, 6.6, 17.1, 5.2, 15.8)

    # Chain through pairs
    prev_y = 15.6
    draw_arrow(ax, 5.0, prev_y - 0.3, 4.2, 14.78)

    for i in range(len(pairs)):
        if_node, add_node, y = pairs[i]
        next_y = pairs[i+1][2] if i + 1 < len(pairs) else 2.4
        next_x = 4.2

        # True to add_node
        draw_arrow(ax, 4.5, y - 0.1, 6.2, y - 0.35)
        # False down to next
        draw_arrow(ax, 4.2, y - 0.28, next_x, next_y + 0.28)
        # From add_node down to next
        draw_arrow(ax, 6.3, y - 0.65, next_x + 0.2, next_y + 0.28)

    draw_arrow(ax, 4.2, 2.12, 4.2, 1.55)

    plt.tight_layout()
    plt.savefig('docs/diagrams/cfg_add_requirements.png', bbox_inches='tight')
    plt.close()

def generate_dfg_add_requirements():
    fig, ax = plt.subplots(figsize=(6.0, 14.0), dpi=200)
    ax.set_xlim(0, 13)
    ax.set_ylim(0, 22)
    ax.axis('off')

    draw_circle(ax, 5, 21.2, 0.35, 'd(req, item)', bg_color='#4CAF50', border_color='#2E7D32', text_color='white', font_size=6.5, bold=True)
    draw_circle(ax, 5, 20.2, 0.3, '1')
    draw_annotation(ax, 5, 20.2, 'u(item), d(qty)', side='right', font_size=7)

    draw_circle(ax, 5, 19.3, 0.3, '2')
    draw_annotation(ax, 5, 19.3, 'u(item), d(recipeItems)', side='right', font_size=7)

    draw_circle(ax, 5, 18.4, 0.3, '3')
    draw_annotation(ax, 5, 18.4, 'u(recipeItems)', side='right', font_size=7)

    draw_circle(ax, 2.5, 17.4, 0.3, '4')
    draw_annotation(ax, 2.5, 17.4, 'u(req, qty)', side='left', font_size=6.5)

    draw_circle(ax, 7.5, 17.4, 0.3, '5')
    draw_circle(ax, 9.0, 16.5, 0.3, '6')
    draw_annotation(ax, 9.0, 16.5, 'd(rItem), d(inv)', side='right', font_size=6.5)

    draw_circle(ax, 9.0, 15.6, 0.3, '7')
    draw_annotation(ax, 9.0, 15.6, 'u(req, inv, qty)', side='right', font_size=6.5)

    draw_circle(ax, 5, 15.6, 0.3, '8')
    draw_annotation(ax, 5, 15.6, 'u(item), d(desc)', side='right', font_size=7)

    pairs = [
        (9, 10, 14.5), (11, 12, 13.3), (13, 14, 12.1), (15, 16, 10.9),
        (17, 18, 9.7), (19, 20, 8.5), (21, 22, 7.3), (23, 24, 6.1),
        (25, 26, 4.9), (27, 28, 3.7)
    ]
    for if_node, add_node, y in pairs:
        draw_circle(ax, 4.2, y, 0.28, str(if_node))
        draw_annotation(ax, 4.2, y, 'u(desc)', side='left', font_size=6.5)
        draw_circle(ax, 7.5, y - 0.4, 0.28, str(add_node))
        draw_annotation(ax, 7.5, y - 0.4, 'u(req, qty)', side='right', font_size=6.5)

    draw_circle(ax, 4.2, 2.4, 0.28, '29')
    draw_circle(ax, 4.2, 1.2, 0.35, 'k(all)', bg_color='#4CAF50', border_color='#2E7D32', text_color='white', font_size=7.5, bold=True)

    draw_arrow(ax, 5, 20.85, 5, 20.5)
    draw_arrow(ax, 5, 19.9, 5, 19.6)
    draw_arrow(ax, 5, 19.0, 5, 18.7)
    draw_arrow(ax, 4.8, 18.2, 2.8, 17.6)
    draw_arrow(ax, 5.2, 18.2, 7.2, 17.6)
    draw_arrow(ax, 2.5, 17.1, 4.8, 15.8)
    draw_arrow(ax, 7.7, 17.1, 8.7, 16.6)
    draw_arrow(ax, 9.0, 16.2, 9.0, 15.9)
    draw_curved_arrow(ax, 8.8, 15.4, 7.8, 17.2, rad=0.3)
    draw_arrow(ax, 7.3, 17.1, 5.3, 15.8)

    draw_arrow(ax, 5.0, 15.3, 4.2, 14.78)
    for i in range(len(pairs)):
        if_node, add_node, y = pairs[i]
        next_y = pairs[i+1][2] if i + 1 < len(pairs) else 2.4
        next_x = 4.2
        draw_arrow(ax, 4.5, y - 0.1, 7.2, y - 0.35)
        draw_arrow(ax, 4.2, y - 0.28, next_x, next_y + 0.28)
        draw_arrow(ax, 7.3, y - 0.65, next_x + 0.2, next_y + 0.28)

    draw_arrow(ax, 4.2, 2.12, 4.2, 1.55)

    plt.tight_layout()
    plt.savefig('docs/diagrams/dfg_add_requirements.png', bbox_inches='tight')
    plt.close()

# Generate all 8 diagrams
print("Generating diagrams...")
generate_cfg_restock_for_order()
generate_dfg_restock_for_order()
generate_cfg_validate_available()
generate_dfg_validate_available()
generate_cfg_restock_item()
generate_dfg_restock_item()
generate_cfg_add_requirements()
generate_dfg_add_requirements()
print("All 8 diagrams generated successfully!")
