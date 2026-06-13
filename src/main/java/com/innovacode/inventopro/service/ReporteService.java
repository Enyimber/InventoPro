package com.innovacode.inventopro.service;

import com.innovacode.inventopro.model.Articulo;
import com.innovacode.inventopro.model.Movimiento;
import com.innovacode.inventopro.repository.MovimientoRepository;

// ── OpenPDF: imports EXPLÍCITOS, sin comodín ──────────────────────────────
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
// NOTA: com.lowagie.text.Font NO se importa; se usa el tipo devuelto por FontFactory

// ── Apache POI: imports EXPLÍCITOS, sin comodín ───────────────────────────
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
// NOTA: org.apache.poi.ss.usermodel.Font se referencia con nombre completo
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Sprint 4 · HU-12 — Exportación PDF / Excel
 * Genera reportes de artículos, bajo stock y movimientos en PDF y XLSX.
 */
@Service
@RequiredArgsConstructor
public class ReporteService {

    private final ArticuloService articuloService;
    private final MovimientoRepository movRepo;
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // =====================  PDF  =====================

    public byte[] articulosPdf() {
        List<Articulo> data = articuloService.listar();
        return pdfTabla("Listado de Artículos",
                new String[]{"Código","Nombre","Categoría","Stock","Mínimo","Precio","Estado"},
                data.stream().map(a -> new String[]{
                        s(a.getCodigo()), s(a.getNombre()), s(a.getCategoria()),
                        String.valueOf(a.getStockActual()),
                        String.valueOf(a.getStockMinimo()),
                        String.format("$%,.2f", a.getPrecioUnitario()),
                        a.isActivo() ? "Activo" : "Inactivo"
                }).toList());
    }

    public byte[] bajoStockPdf() {
        List<Articulo> data = articuloService.bajoStock();
        return pdfTabla("Alertas de Bajo Stock",
                new String[]{"Código","Nombre","Stock actual","Mínimo","Diferencia"},
                data.stream().map(a -> new String[]{
                        s(a.getCodigo()), s(a.getNombre()),
                        String.valueOf(a.getStockActual()),
                        String.valueOf(a.getStockMinimo()),
                        String.valueOf(a.getStockActual() - a.getStockMinimo())
                }).toList());
    }

    public byte[] movimientosPdf() {
        Map<String,String> nombres = articuloService.listar().stream()
                .collect(Collectors.toMap(Articulo::getId, Articulo::getNombre, (a,b)->a));
        List<Movimiento> data = movRepo.findAll();
        return pdfTabla("Reporte de Movimientos",
                new String[]{"Fecha","Tipo","Artículo","Cant.","Usuario","Estado"},
                data.stream().map(m -> new String[]{
                        m.getFecha()==null ? "-" : m.getFecha().format(FMT),
                        s(m.getTipo()),
                        nombres.getOrDefault(m.getArticuloId(), s(m.getArticuloId())),
                        String.valueOf(m.getCantidad()),
                        s(m.getUsuario()),
                        m.getEstado()==null ? "ACEPTADO" : m.getEstado()
                }).toList());
    }

    private byte[] pdfTabla(String titulo, String[] headers, List<String[]> rows) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4.rotate(), 24, 24, 28, 24);
            PdfWriter.getInstance(doc, out);
            doc.open();

            // FontFactory devuelve com.lowagie.text.Font → sin ambigüedad
            com.lowagie.text.Font fTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, new Color(30,41,59));
            com.lowagie.text.Font fSub   = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.GRAY);

            Paragraph p = new Paragraph("InventoPro · " + titulo, fTitle);
            p.setSpacingAfter(4f);
            doc.add(p);
            doc.add(new Paragraph("Generado: " +
                    java.time.LocalDateTime.now().format(FMT), fSub));
            doc.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(headers.length);
            table.setWidthPercentage(100);
            com.lowagie.text.Font fH = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
            for (String h : headers) {
                PdfPCell c = new PdfPCell(new Phrase(h, fH));
                c.setBackgroundColor(new Color(37,99,235));
                c.setPadding(6f);
                table.addCell(c);
            }
            com.lowagie.text.Font fB = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.DARK_GRAY);
            boolean alt = false;
            for (String[] row : rows) {
                for (String v : row) {
                    PdfPCell c = new PdfPCell(new Phrase(v, fB));
                    if (alt) c.setBackgroundColor(new Color(243,244,246));
                    c.setPadding(5f);
                    table.addCell(c);
                }
                alt = !alt;
            }
            doc.add(table);
            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF", e);
        }
    }

    // =====================  EXCEL  =====================

    public byte[] articulosXlsx() {
        List<Articulo> data = articuloService.listar();
        return xlsxTabla("Articulos",
                new String[]{"Código","Nombre","Categoría","Marca","Stock","Mínimo","Precio","Activo"},
                data.stream().map(a -> new Object[]{
                        a.getCodigo(), a.getNombre(), a.getCategoria(), a.getMarca(),
                        a.getStockActual(), a.getStockMinimo(),
                        a.getPrecioUnitario(), a.isActivo() ? "Sí" : "No"
                }).toList());
    }

    public byte[] bajoStockXlsx() {
        List<Articulo> data = articuloService.bajoStock();
        return xlsxTabla("BajoStock",
                new String[]{"Código","Nombre","Stock","Mínimo","Diferencia"},
                data.stream().map(a -> new Object[]{
                        a.getCodigo(), a.getNombre(),
                        a.getStockActual(), a.getStockMinimo(),
                        a.getStockActual() - a.getStockMinimo()
                }).toList());
    }

    public byte[] movimientosXlsx() {
        Map<String,String> nombres = articuloService.listar().stream()
                .collect(Collectors.toMap(Articulo::getId, Articulo::getNombre, (a,b)->a));
        List<Movimiento> data = movRepo.findAll();
        return xlsxTabla("Movimientos",
                new String[]{"Fecha","Tipo","Artículo","Cantidad","Precio","Usuario","Estado"},
                data.stream().map(m -> new Object[]{
                        m.getFecha()==null ? "" : m.getFecha().format(FMT),
                        s(m.getTipo()),
                        nombres.getOrDefault(m.getArticuloId(), s(m.getArticuloId())),
                        m.getCantidad(),
                        m.getPrecioUnitario(),
                        s(m.getUsuario()),
                        m.getEstado()==null ? "ACEPTADO" : m.getEstado()
                }).toList());
    }

    private byte[] xlsxTabla(String sheetName, String[] headers, List<Object[]> rows) {
        try (Workbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sh = wb.createSheet(sheetName);

            CellStyle hStyle = wb.createCellStyle();

            // Nombre completo para evitar ambigüedad con com.lowagie.text.Font
            org.apache.poi.ss.usermodel.Font hFont = wb.createFont();
            hFont.setBold(true);
            hFont.setColor(IndexedColors.WHITE.getIndex());
            hStyle.setFont(hFont);
            hStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
            hStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            hStyle.setAlignment(HorizontalAlignment.CENTER);

            Row header = sh.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell c = header.createCell(i);
                c.setCellValue(headers[i]);
                c.setCellStyle(hStyle);
            }

            int r = 1;
            for (Object[] row : rows) {
                Row rr = sh.createRow(r++);
                for (int i = 0; i < row.length; i++) {
                    Cell c = rr.createCell(i);
                    Object v = row[i];
                    if (v instanceof Number n) c.setCellValue(n.doubleValue());
                    else                       c.setCellValue(v == null ? "" : v.toString());
                }
            }
            for (int i = 0; i < headers.length; i++) sh.autoSizeColumn(i);

            wb.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando Excel", e);
        }
    }

    private static String s(String v) { return v == null ? "" : v; }
}