package com.example.airPlan.Test;/*package com.example.travelwise.Test;

import com.example.travelwise.Services.Services;
import com.example.travelwise.models.FlightModel;

import com.example.travelwise.Services.FlightServices;

import java.util.List;

public class FlightServiceTest  {
    public static void main(String[] args) {
        FlightServices service = new FlightServices();

        try {
            // 🔵 Test Add
            FlightModel flight = new FlightModel(
                    1,
                    "Tunisair",
                    "Tunis",
                    "Paris",
                    "Economy",
                    "Scheduled",
                    "TW101",
                    java.sql.Date.valueOf("2025-06-15"),
                    java.sql.Date.valueOf("2025-06-22"),
                    480.00
            );

            service.addFlight(flight);
            System.out.println("✅ Flight added!");

            // 🟢 Test Read
            for (FlightModel f : service.getAllFlights()) {
                System.out.println(f.getFlightNumber() + " → " + f.getDestination() + " (€" + f.getPrice() + ")");
            }

            // 🟡 Test Update
            List<FlightModel> all = service.getAllFlights();
            if (!all.isEmpty()) {
                FlightModel f = all.get(0);
                f.setStatus("Delayed");
                f.setPrice(499.99);
                service.updateFlight(f);
                System.out.println("✏️ Flight updated: " + f.getFlight_id());
            }

            // 🔴 Test Delete
            /*if (!all.isEmpty()) {
                FlightModel toDelete = all.get(all.size() - 1);
                service.deleteFlight(toDelete.getFlight_id());
                System.out.println("🗑️ Flight deleted: ID " + toDelete.getFlight_id());
            }*/

       /* } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
*/
