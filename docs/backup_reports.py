# -*- coding: utf-8 -*-
import shutil
import os
import docx
from docx.shared import Pt, RGBColor, Inches
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT
from docx.oxml import parse_xml
from docx.oxml.ns import nsdecls

# 1. Backup original files
shutil.copyfile('docs/BaoCao_CoffeeShopPOS_DesignPattern.docx', 'docs/BaoCao_CoffeeShopPOS_DesignPattern_backup.docx')
shutil.copyfile('docs/Chuong_IV_KiemThuHopTrang_CoffeeShopPOS.docx', 'docs/Chuong_IV_KiemThuHopTrang_CoffeeShopPOS_backup.docx')

print("Backup created successfully.")
