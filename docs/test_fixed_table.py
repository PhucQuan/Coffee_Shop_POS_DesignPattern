# -*- coding: utf-8 -*-
import docx
from docx.shared import Inches, Pt, RGBColor
from docx.oxml import parse_xml
from docx.oxml.ns import nsdecls

doc = docx.Document()
table = doc.add_table(rows=2, cols=5)
col_widths_dxa = [1152, 1656, 3240, 2376, 864] # 0.8, 1.15, 2.25, 1.65, 0.6 in -> sum 9288 dxa = 6.45 in

tblPr = table._tbl.tblPr
tblW = parse_xml(f'<w:tblW {nsdecls("w")} w:w="9288" w:type="dxa"/>')
tblLayout = parse_xml(f'<w:tblLayout {nsdecls("w")} w:type="fixed"/>')
tblJc = parse_xml(f'<w:jc {nsdecls("w")} w:val="center"/>')
tblPr.append(tblW)
tblPr.append(tblLayout)
tblPr.append(tblJc)

tblGrid = parse_xml(f'<w:tblGrid {nsdecls("w")}>' + ''.join(f'<w:gridCol w:w="{w}"/>' for w in col_widths_dxa) + '</w:tblGrid>')
table._tbl.insert(table._tbl.index(tblPr) + 1, tblGrid)

doc.save('docs/test_fixed_table.docx')
print('Successfully saved test_fixed_table.docx!')
