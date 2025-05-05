package com.example.airPlan.models;


public class Hebergement {
    private int acc_id;
    private String name;
    private String type;
    private String city;
    private String address;
    private String country;
    private double pricePerNight;
    private boolean disponibility;
    private String photo;
    private String album;
    private String description;
    private String options;
    private int rating;
    private int capacity;
    private String status;

    public Hebergement(String name, String type, String city, String address, String country,
                       double pricePerNight, boolean disponibility, String photo, String album,
                       String description, String options, int rating, int capacity,String status) {
        this.name = name;
        this.type = type;
        this.city = city;
        this.address = address;
        this.country = country;
        this.pricePerNight = pricePerNight;
        this.disponibility = disponibility;
        this.photo = photo;
        this.album = album;
        this.description = description;
        this.options = options;
        this.rating = rating;
        this.capacity = capacity;
        this.status=status;
    }

    public Hebergement(int acc_id, String name, String type, String city, String address, String country,
                       double pricePerNight, boolean disponibility, String photo, String album,
                       String description, String options, int rating, int capacity,String status) {
        this(name, type, city, address, country, pricePerNight, disponibility, photo, album, description, options, rating, capacity,status);
        this.acc_id = acc_id;
    }




    public int getId() { return acc_id; }
    public void setId(int acc_id) { this.acc_id = acc_id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public double getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(double pricePerNight) { this.pricePerNight = pricePerNight; }

    public boolean isDisponibility() {
        return capacity != 0;
    }
    public void setDisponibility(boolean disponibility) { this.disponibility = disponibility; }

    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }

    public String getAlbum() { return album; }
    public void setAlbum(String album) { this.album = album; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getOptions() { return options; }
    public void setOptions(String options) { this.options = options; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }


    @Override
    public String toString() {
        return "Hebergement{" +
                "acc_id=" + acc_id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", city='" + city + '\'' +
                ", address='" + address + '\'' +
                ", country='" + country + '\'' +
                ", pricePerNight=" + pricePerNight +
                ", disponibility=" + disponibility +
                ", photo='" + photo + '\'' +
                ", album='" + album + '\'' +
                ", description='" + description + '\'' +
                ", options='" + options + '\'' +
                ", rating=" + rating +
                ", capacity=" + capacity +
                ", Status" + status +

                '}';
    }

}
