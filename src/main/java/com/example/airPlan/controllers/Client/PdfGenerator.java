package com.example.airPlan.controllers.Client;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileOutputStream;

public class PdfGenerator {

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

            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Font sectionFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 12);

            document.add(new Paragraph("Reservation Confirmation", titleFont));
            document.add(new Paragraph(" "));

            // Hébergement
            document.add(new Paragraph("Accommodation Details", sectionFont));
            document.add(new Paragraph("Name: " + hebergementName, normalFont));
            document.add(new Paragraph("Location: " + countryCity, normalFont));
            document.add(new Paragraph("Type: " + type, normalFont));
            document.add(new Paragraph("Options: " + options, normalFont));
            document.add(new Paragraph("Price: " + price, normalFont));
            document.add(new Paragraph("Rating: " + rating, normalFont));
            document.add(new Paragraph(" "));

            // Dates et demande
            document.add(new Paragraph("Reservation Dates", sectionFont));
            document.add(new Paragraph("Arrival: " + dateArrival, normalFont));
            document.add(new Paragraph("Departure: " + dateDeparture, normalFont));
            document.add(new Paragraph("Adults: " + nbAdults, normalFont));
            document.add(new Paragraph("Children: " + nbChildren, normalFont));
            document.add(new Paragraph("Rooms: " + nbRooms, normalFont));
            document.add(new Paragraph("Special Request: " + request, normalFont));
            document.add(new Paragraph(" "));

            // Ajouter l'image
            if (imagePath != null) {
                Image img = Image.getInstance(imagePath);
                img.scaleToFit(300, 200);
                img.setAlignment(Element.ALIGN_CENTER);
                document.add(img);
            }

            document.close();
            System.out.println("PDF created!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

