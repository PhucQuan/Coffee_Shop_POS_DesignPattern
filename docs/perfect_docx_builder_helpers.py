# -*- coding: utf-8 -*-
import docx
from docx.shared import Pt, RGBColor, Inches
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT, WD_ALIGN_VERTICAL
from docx.oxml import parse_xml, OxmlElement
from docx.oxml.ns import nsdecls, qn

def set_cell_background(cell, hex_color):
    tcPr = cell._tc.get_or_add_tcPr()
    shd = parse_xml(f'<w:shd {nsdecls("w")} w:fill="{hex_color}"/>')
    tcPr.append(shd)

def set_cell_margins(cell, top=60, bottom=60, left=80, right=80):
    tcPr = cell._tc.get_or_add_tcPr()
    tcMar = parse_xml(f'<w:tcMar {nsdecls("w")}><w:top w:w="{top}" w:type="dxa"/><w:bottom w:w="{bottom}" w:type="dxa"/><w:left w:w="{left}" w:type="dxa"/><w:right w:w="{right}" w:type="dxa"/></w:tcMar>')
    tcPr.append(tcMar)

def add_h1(doc, text, center=True):
    p = doc.add_paragraph(style='Heading 1')
    p.alignment = WD_ALIGN_PARAGRAPH.CENTER if center else WD_ALIGN_PARAGRAPH.LEFT
    p.paragraph_format.space_before = Pt(14)
    p.paragraph_format.space_after = Pt(8)
    p.paragraph_format.keep_with_next = True
    for r in p.runs:
        r.text = ""
    run = p.add_run(text)
    run.font.name = 'Times New Roman'
    run.font.size = Pt(14)
    run.font.bold = True
    run.font.color.rgb = RGBColor(0x0F, 0x47, 0x61)
    return p

def add_h2(doc, text):
    p = doc.add_paragraph(style='Heading 2')
    p.alignment = WD_ALIGN_PARAGRAPH.LEFT
    p.paragraph_format.space_before = Pt(12)
    p.paragraph_format.space_after = Pt(4)
    p.paragraph_format.keep_with_next = True
    for r in p.runs:
        r.text = ""
    run = p.add_run(text)
    run.font.name = 'Times New Roman'
    run.font.size = Pt(13)
    run.font.bold = True
    return p

def add_h3(doc, text):
    p = doc.add_paragraph(style='Heading 3')
    p.alignment = WD_ALIGN_PARAGRAPH.LEFT
    p.paragraph_format.space_before = Pt(8)
    p.paragraph_format.space_after = Pt(3)
    p.paragraph_format.keep_with_next = True
    for r in p.runs:
        r.text = ""
    run = p.add_run(text)
    run.font.name = 'Times New Roman'
    run.font.size = Pt(13)
    run.font.bold = True
    return p

def add_body(doc, text, bold_prefix="", indent=False):
    p = doc.add_paragraph()
    # Explicit LEFT alignment avoids ugly justified whitespace stretching
    p.alignment = WD_ALIGN_PARAGRAPH.LEFT
    p.paragraph_format.space_before = Pt(0)
    p.paragraph_format.space_after = Pt(4)
    p.paragraph_format.line_spacing = 1.2
    if indent:
        p.paragraph_format.left_indent = Inches(0.25)
    if bold_prefix:
        r_pre = p.add_run(bold_prefix)
        r_pre.font.name = 'Times New Roman'
        r_pre.font.size = Pt(13)
        r_pre.font.bold = True
    if text:
        r = p.add_run(text)
        r.font.name = 'Times New Roman'
        r.font.size = Pt(13)
    return p

def add_code_line_with_red_label(doc, code_part, label_part=""):
    p = doc.add_paragraph()
    p.alignment = WD_ALIGN_PARAGRAPH.LEFT
    p.paragraph_format.space_before = Pt(0)
    p.paragraph_format.space_after = Pt(1)
    p.paragraph_format.line_spacing = 1.05
    p.paragraph_format.left_indent = Inches(0.2)
    
    r_code = p.add_run(code_part)
    r_code.font.name = 'Consolas'
    r_code.font.size = Pt(10.5)
    r_code.font.color.rgb = RGBColor(0x11, 0x11, 0x11)
    
    if label_part:
        r_label = p.add_run(label_part)
        r_label.font.name = 'Consolas'
        r_label.font.size = Pt(10.5)
        r_label.font.bold = True
        r_label.font.color.rgb = RGBColor(0xFF, 0x00, 0x00)
    return p

def add_centered_diagram(doc, img_path, width_in=2.5, caption=""):
    p = doc.add_paragraph()
    p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p.paragraph_format.space_before = Pt(6)
    p.paragraph_format.space_after = Pt(2)
    r = p.add_run()
    r.add_picture(img_path, width=Inches(width_in))
    if caption:
        p_cap = doc.add_paragraph()
        p_cap.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p_cap.paragraph_format.space_before = Pt(0)
        p_cap.paragraph_format.space_after = Pt(6)
        rc = p_cap.add_run(caption)
        rc.font.name = 'Times New Roman'
        rc.font.size = Pt(12)
        rc.font.italic = True
    return p

def format_custom_table(table, col_widths, headers, data_rows, hdr_bg='2E75B6', hdr_text_white=True, font_size_pt=9.5):
    # Set explicit fixed layout and total width in XML
    total_dxa = int(sum(col_widths) * 1440)
    tblPr = table._tbl.tblPr
    tblPr.append(parse_xml(f'<w:tblW {nsdecls("w")} w:w="{total_dxa}" w:type="dxa"/>'))
    tblPr.append(parse_xml(f'<w:tblLayout {nsdecls("w")} w:type="fixed"/>'))
    tblPr.append(parse_xml(f'<w:jc {nsdecls("w")} w:val="center"/>'))
    
    # Add tblGrid
    grid_cols = ''.join(f'<w:gridCol {nsdecls("w")} w:w="{int(w * 1440)}"/>' for w in col_widths)
    tblGrid = parse_xml(f'<w:tblGrid {nsdecls("w")}>{grid_cols}</w:tblGrid>')
    table._tbl.insert(table._tbl.index(tblPr) + 1, tblGrid)
    
    # Repeat header row & prevent split
    trPr = table.rows[0]._tr.get_or_add_trPr()
    trPr.append(parse_xml(f'<w:tblHeader {nsdecls("w")}/>'))
    trPr.append(parse_xml(f'<w:cantSplit {nsdecls("w")}/>'))
    
    # Header cells
    for col_idx, h_text in enumerate(headers):
        cell = table.cell(0, col_idx)
        cell.width = Inches(col_widths[col_idx])
        set_cell_background(cell, hdr_bg)
        set_cell_margins(cell, top=80, bottom=80, left=70, right=70)
        p = cell.paragraphs[0]
        p.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p.paragraph_format.space_before = Pt(0)
        p.paragraph_format.space_after = Pt(0)
        p.paragraph_format.line_spacing = 1.05
        r = p.add_run(h_text)
        r.font.name = 'Times New Roman'
        r.font.size = Pt(font_size_pt)
        r.font.bold = True
        if hdr_text_white:
            r.font.color.rgb = RGBColor(0xFF, 0xFF, 0xFF)
        else:
            r.font.color.rgb = RGBColor(0x11, 0x11, 0x11)
            
    # Data rows
    for r_idx, row_vals in enumerate(data_rows):
        row_obj = table.rows[r_idx + 1]
        trPr_r = row_obj._tr.get_or_add_trPr()
        trPr_r.append(parse_xml(f'<w:cantSplit {nsdecls("w")}/>'))
        
        for c_idx, val in enumerate(row_vals):
            cell = row_obj.cells[c_idx]
            cell.width = Inches(col_widths[c_idx])
            set_cell_margins(cell, top=60, bottom=60, left=70, right=70)
            
            # Allow rich multiline content
            lines = str(val).split('\n')
            for line_idx, line_text in enumerate(lines):
                if line_idx == 0:
                    p = cell.paragraphs[0]
                else:
                    p = cell.add_paragraph()
                p.paragraph_format.space_before = Pt(0)
                p.paragraph_format.space_after = Pt(1 if line_idx < len(lines)-1 else 0)
                p.paragraph_format.line_spacing = 1.05
                
                # Alignment
                if c_idx == 0 or (len(row_vals) in [4, 5] and c_idx == len(row_vals) - 1):
                    p.alignment = WD_ALIGN_PARAGRAPH.CENTER
                else:
                    p.alignment = WD_ALIGN_PARAGRAPH.LEFT
                
                # Highlight bold prefixes (like "Mục đích:", "Dữ liệu:", "Kỹ thuật:")
                if any(line_text.startswith(prefix) for prefix in ["Mục đích:", "Dữ liệu:", "Kỹ thuật:"]) and ":" in line_text:
                    parts = line_text.split(":", 1)
                    r_lbl = p.add_run(parts[0] + ":")
                    r_lbl.font.name = 'Times New Roman'
                    r_lbl.font.size = Pt(font_size_pt)
                    r_lbl.font.bold = True
                    if len(parts) > 1:
                        r_txt = p.add_run(parts[1])
                        r_txt.font.name = 'Times New Roman'
                        r_txt.font.size = Pt(font_size_pt)
                else:
                    r = p.add_run(line_text)
                    r.font.name = 'Times New Roman'
                    r.font.size = Pt(font_size_pt)
                    if 'Pass' in line_text or 'Đạt' in line_text:
                        r.font.bold = True
                        r.font.color.rgb = RGBColor(0x1B, 0x5E, 0x20)
                    elif 'Closed' in line_text:
                        r.font.bold = True
                        r.font.color.rgb = RGBColor(0x00, 0x4D, 0x40)

def add_code_box(doc, code_str):
    tbl = doc.add_table(rows=1, cols=1)
    tbl.alignment = WD_TABLE_ALIGNMENT.CENTER
    tbl.autofit = False
    
    cell = tbl.cell(0, 0)
    cell.width = Inches(6.45)
    set_cell_background(cell, 'F8F9FA')
    set_cell_margins(cell, top=100, bottom=100, left=140, right=140)
    
    tcPr = cell._tc.get_or_add_tcPr()
    borders = parse_xml(
        f'<w:tcBorders {nsdecls("w")}>'
        f'<w:top w:val="single" w:sz="4" w:space="0" w:color="D0D0D0"/>'
        f'<w:left w:val="single" w:sz="18" w:space="0" w:color="2E75B6"/>'
        f'<w:bottom w:val="single" w:sz="4" w:space="0" w:color="D0D0D0"/>'
        f'<w:right w:val="single" w:sz="4" w:space="0" w:color="D0D0D0"/>'
        f'</w:tcBorders>'
    )
    tcPr.append(borders)
    
    p = cell.paragraphs[0]
    p.alignment = WD_ALIGN_PARAGRAPH.LEFT
    p.paragraph_format.space_before = Pt(0)
    p.paragraph_format.space_after = Pt(0)
    p.paragraph_format.line_spacing = 1.05
    
    r = p.add_run(code_str)
    r.font.name = 'Consolas'
    r.font.size = Pt(9.0)
    r.font.color.rgb = RGBColor(0x24, 0x29, 0x2E)
    
    doc.add_paragraph().paragraph_format.space_after = Pt(4)

print("Updated perfect_docx_builder_helpers.py with fixed table layouts and left paragraph alignments.")
