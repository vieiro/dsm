/*
 * Copyright 2022 Antonio Vieiro <antonio@vieiro.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.vieiro.dsm.graph.dsm;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.util.Units;

import net.vieiro.dsm.graph.DirectedGraph;

public final class DSMExcelGenerator<ID> implements Runnable {

    private static final Logger LOG = Logger.getLogger(DSMExcelGenerator.class.getName());

    private static enum Columns {
        COL_NAME, COL_ID, COL_REST,
    };

    private final DirectedGraph<ID> graph;
    private final List<ID> fas;
    private final List<String> names;
    private final OutputStream outputStream;
    private final HSSFWorkbook workbook;
    private final Sheet sheet;
    private final Font fontBold;
    private final Font fontPlain;
    private final CellStyle cellStyle_HEADER_NAME;
    private final CellStyle cellStyle_HEADER_OTHER;
    private final CellStyle cellStyles[];
    private final CellStyle cellStyles_NAMES[];
    private final CellStyle cellStyles_NAMES_LEFT[];
    private final CellStyle cellStyles_IDS[];
    private final CellStyle cellStyle_DIAGONAL;
    private final CellStyle cellStyles_FOOTER[];
    private final int ncols;

    public DSMExcelGenerator(DirectedGraph<ID> graph, List<ID> fas, OutputStream outputStream) throws IOException {
        this.graph = graph;
        this.fas = fas;
        this.names = this.fas.stream().map(Objects::toString).collect(Collectors.toList());
        this.ncols = fas.size();

        this.outputStream = outputStream;

        this.workbook = new HSSFWorkbook();

        this.sheet = workbook.createSheet();
        for (int i = 0; i < ncols + 1; i++) {
            sheet.setColumnWidth(Columns.COL_ID.ordinal() + i, 4 * 256);
        }

        // Compute row height for approximate squares
        final double defaultCharacterWidthInPixels = 4 * Units.DEFAULT_CHARACTER_WIDTH - 4;
        final double defaultCharacterWidthInPoints = Units.pixelToPoints(defaultCharacterWidthInPixels);
        final double defaultCharacterHeightInPoints = defaultCharacterWidthInPoints; // * 7 / 12;
        this.sheet.setDefaultRowHeight((short) Math.floor(20 * defaultCharacterHeightInPoints));

        // Freeze the two first rows and the first column
        this.sheet.createFreezePane(2, 1);

        // Fonts
        this.fontBold = workbook.createFont();
        fontBold.setFontName("Arial");
        fontBold.setFontHeightInPoints((short) 14);
        fontBold.setBold(true);

        this.fontPlain = workbook.createFont();
        fontPlain.setFontName("Arial");
        fontPlain.setFontHeightInPoints((short) 11);
        fontPlain.setBold(false);

        // Colors
        HSSFColor lightGray = getColor(IndexedColors.GREY_25_PERCENT.index, 0xFAFAFA);

        // Header name style (aligned to right)
        cellStyle_HEADER_NAME = workbook.createCellStyle();
        cellStyle_HEADER_NAME.setAlignment(HorizontalAlignment.RIGHT);
        cellStyle_HEADER_NAME.setFillForegroundColor(lightGray.getIndex());
        cellStyle_HEADER_NAME.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle_HEADER_NAME.setFont(fontPlain);

        // Header style (aligned center)
        cellStyle_HEADER_OTHER = workbook.createCellStyle();
        cellStyle_HEADER_OTHER.setAlignment(HorizontalAlignment.CENTER);
        cellStyle_HEADER_OTHER.setFillForegroundColor(lightGray.getIndex());
        cellStyle_HEADER_OTHER.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle_HEADER_OTHER.setFont(fontPlain);

        // Diagonal style
        cellStyle_DIAGONAL = workbook.createCellStyle();
        cellStyle_DIAGONAL.setAlignment(HorizontalAlignment.CENTER);
        cellStyle_DIAGONAL.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.index);
        cellStyle_DIAGONAL.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle_DIAGONAL.setFont(fontPlain);


        // Some light background colors
        short[] colors = {getColor(IndexedColors.AQUA.index, 0xE8F5E9).getIndex(),
            getColor(IndexedColors.BLUE.index, 0xC8E6C9).getIndex(),
            getColor(IndexedColors.BLUE_GREY.index, 0xA5D6A7).getIndex(),
            getColor(IndexedColors.BRIGHT_GREEN.index, 0x81C784).getIndex(),};

        // Different (rotating) cell styles with different background colors
        cellStyles = new CellStyle[colors.length];
        cellStyles_NAMES = new CellStyle[colors.length];
        cellStyles_NAMES_LEFT = new CellStyle[colors.length];
        cellStyles_IDS = new CellStyle[colors.length];
        // Footer style
        cellStyles_FOOTER = new CellStyle[colors.length];
        for (int i = 0; i < cellStyles.length; i++) {
            cellStyles[i] = workbook.createCellStyle();
            cellStyles[i].setAlignment(HorizontalAlignment.CENTER);
            cellStyles[i].setFont(fontBold);
            cellStyles[i].setFillForegroundColor(colors[i]);
            cellStyles[i].setFillPattern(FillPatternType.SOLID_FOREGROUND);

            cellStyles_NAMES[i] = workbook.createCellStyle();
            cellStyles_NAMES[i].setAlignment(HorizontalAlignment.RIGHT);
            cellStyles_NAMES[i].setFont(fontBold);
            cellStyles_NAMES[i].setFillForegroundColor(colors[i]);
            cellStyles_NAMES[i].setFillPattern(FillPatternType.SOLID_FOREGROUND);

            cellStyles_NAMES_LEFT[i] = workbook.createCellStyle();
            cellStyles_NAMES_LEFT[i].setAlignment(HorizontalAlignment.LEFT);
            cellStyles_NAMES_LEFT[i].setFont(fontPlain);
            cellStyles_NAMES_LEFT[i].setFillForegroundColor(colors[i]);
            cellStyles_NAMES_LEFT[i].setFillPattern(FillPatternType.SOLID_FOREGROUND);

            cellStyles_IDS[i] = workbook.createCellStyle();
            cellStyles_IDS[i].setAlignment(HorizontalAlignment.CENTER);
            cellStyles_IDS[i].setFont(fontBold);
            cellStyles_IDS[i].setFillForegroundColor(colors[i]);
            cellStyles_IDS[i].setFillPattern(FillPatternType.SOLID_FOREGROUND);

            cellStyles_FOOTER[i] = workbook.createCellStyle();
            cellStyles_FOOTER[i].setAlignment(HorizontalAlignment.CENTER);
            cellStyles_FOOTER[i].setFillForegroundColor(colors[i]);
            cellStyles_FOOTER[i].setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cellStyles_FOOTER[i].setFont(fontPlain);

        }

    }

    private HSSFColor getColor(short index, int rgb) {
        byte r = (byte) ((rgb >> 16) & 0xFF);
        byte g = (byte) ((rgb >> 8) & 0xFF);
        byte b = (byte) (rgb & 0xFF);
        HSSFPalette palette = workbook.getCustomPalette();
        HSSFColor hssfColor = null;
        try {
            hssfColor = palette.findColor(r, g, b);
            if (hssfColor == null) {
                palette.setColorAtIndex(index, r, g, b);
                hssfColor = palette.getColor(index);
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getMessage() + e.getClass().getName(), e);
        }

        return hssfColor;
    }

    @Override
    public void run() {
        try {
            generateExcel();
            sheet.autoSizeColumn(Columns.COL_NAME.ordinal());
            sheet.autoSizeColumn(Columns.COL_REST.ordinal() + fas.size());
            workbook.write(outputStream);
        } catch (Exception e) {
            LOG.log(Level.SEVERE,
                    String.format("Error generating workbook: %s (%s)", e.getMessage(), e.getClass().getName()), e);
        } finally {
            // HSSFWorkbooks only:
            // workbook.dispose();
        }
    }

    public void generateExcel() throws Exception {
        int rowIndex = 0;
        createHeaderRow(rowIndex++);
        for (int i = 0; i < fas.size(); i++) {
            Row nodeRow = sheet.createRow(rowIndex++);
            createNodeRow(i, nodeRow);
        }
        createFooterRow(rowIndex++);
    }

    private void createFooterRow(int rowIndex) {
        Row footerRow = sheet.createRow(rowIndex);
        Cell usedByName = footerRow.createCell(0);
        usedByName.setCellStyle(cellStyle_HEADER_NAME);
        usedByName.setCellValue("Predecessors");
        for (int j = 0; j < fas.size(); j++) {
            Cell connectsCell = footerRow.createCell(Columns.COL_REST.ordinal() + j);
            CellStyle style = cellStyles_FOOTER[j % cellStyles.length];
            connectsCell.setCellStyle(style);
            int usedByCount = 0;
            for (int index = 0; index < fas.size(); index++) {
                boolean connects = graph.connects(fas.get(index), fas.get(j));
                usedByCount += connects ? 1 : 0;
            }
            connectsCell.setCellValue("" + usedByCount);
        }
    }

    private void createNodeRow(int index, Row row) {
        CellStyle style_name = cellStyles_NAMES[index % cellStyles_NAMES.length];
        CellStyle style_id = cellStyles_IDS[index % cellStyles_IDS.length];

        Cell nameCell = row.createCell(Columns.COL_NAME.ordinal());
        nameCell.setCellStyle(style_name);
        nameCell.setCellValue(names.get(index));

        Cell idCell = row.createCell(Columns.COL_ID.ordinal());
        idCell.setCellStyle(style_id);
        idCell.setCellValue("" + (index + 1));

        final String arrowUp = "X"; // "\u21d1";
        final String diagonal = ""; // "\u21d4";

        int dependsOnCount = 0;
        for (int j = 0; j < fas.size(); j++) {
            Cell connectsCell = row.createCell(Columns.COL_REST.ordinal() + j);
            if (j == index) {
                connectsCell.setCellValue(diagonal);
                connectsCell.setCellStyle(cellStyle_DIAGONAL);
            } else {
                CellStyle style = index < j ? cellStyles[j % cellStyles.length] : cellStyles[index % cellStyles.length];
                connectsCell.setCellStyle(style);
                boolean connects = graph.connects(fas.get(index), fas.get(j));
                if (connects) {
                    connectsCell.setCellValue(arrowUp);
                    dependsOnCount++;
                }
            }
        }
        Cell dependsOnCell = row.createCell(Columns.COL_REST.ordinal() + fas.size());
        CellStyle style_name_left = cellStyles_NAMES_LEFT[index % cellStyles_NAMES.length];
        dependsOnCell.setCellStyle(style_name_left);
        dependsOnCell.setCellValue("" + dependsOnCount);

    }

    private void createHeaderRow(int row) {
        // Create header row
        Row headerRow = sheet.createRow(row);
        Cell name = headerRow.createCell(Columns.COL_NAME.ordinal());
        name.setCellStyle(cellStyle_HEADER_NAME);
        name.setCellValue("Node");

        // Create the rest of rows
        Cell index = headerRow.createCell(Columns.COL_ID.ordinal());
        index.setCellStyle(cellStyle_HEADER_OTHER);
        index.setCellValue("#");

        for (int col = 0; col < ncols; col++) {
            Cell cell = headerRow.createCell(Columns.COL_REST.ordinal() + col);
            cell.setCellStyle(cellStyles_IDS[col % cellStyles_IDS.length]);
            cell.setCellValue("" + (col + 1));
        }

        Cell dependsOn = headerRow.createCell(Columns.COL_REST.ordinal() + fas.size());
        dependsOn.setCellStyle(cellStyle_HEADER_OTHER);
        dependsOn.setCellValue("Successors");
    }

}
