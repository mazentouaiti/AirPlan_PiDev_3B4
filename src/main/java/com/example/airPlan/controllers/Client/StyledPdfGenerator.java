package com.example.airPlan.controllers.Client;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import javax.imageio.ImageIO;
import java.io.FileOutputStream;

public class StyledPdfGenerator {

    public void generatePdf(String filePath,
                            String countryCity,
                            String hebergementName,
                            String type,
                            String options,
                            String price,
                            String rating,
                            String dateArrival,
                            String dateDeparture,
                            int nbAdults,
                            int nbChildren,
                            int nbRooms,
                            String request,
                            String imagePath) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // Couleurs et polices
            BaseColor headerColor = new BaseColor(63, 81, 181); // Indigo
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 22, Font.BOLD, headerColor);
            Font sectionFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.BLACK);
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 12);
            Font labelFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);

            // Titre
            Paragraph title = new Paragraph("🛎️ Reservation Confirmation", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(15);
            document.add(title);

            // Tableau avec les détails
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);
            table.setWidths(new float[]{1f, 2f});

            // Méthode d'ajout de ligne stylée
            addStyledRow(table, "Accommodation", hebergementName, labelFont, normalFont);
            addStyledRow(table, "Location", countryCity, labelFont, normalFont);
            addStyledRow(table, "Type", type, labelFont, normalFont);
            addStyledRow(table, "Options", options, labelFont, normalFont);
            addStyledRow(table, "Price", price, labelFont, normalFont);
            addStyledRow(table, "Rating", rating, labelFont, normalFont);
            addStyledRow(table, "Arrival", dateArrival, labelFont, normalFont);
            addStyledRow(table, "Departure", dateDeparture, labelFont, normalFont);
            addStyledRow(table, "Adults", String.valueOf(nbAdults), labelFont, normalFont);
            addStyledRow(table, "Children", String.valueOf(nbChildren), labelFont, normalFont);
            addStyledRow(table, "Rooms", String.valueOf(nbRooms), labelFont, normalFont);
            addStyledRow(table, "Special Request", request, labelFont, normalFont);

            document.add(table);

            // Image
            if (imagePath != null) {
                Image img = Image.getInstance(imagePath);
                img.scaleToFit(400, 300);
                img.setAlignment(Element.ALIGN_CENTER);
                document.add(img);
            }

            document.close();
            System.out.println("PDF stylisé créé avec succès !");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addStyledRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBackgroundColor(new BaseColor(224, 224, 224));
        labelCell.setPadding(8);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setPadding(8);
        table.addCell(valueCell);
    }
}

