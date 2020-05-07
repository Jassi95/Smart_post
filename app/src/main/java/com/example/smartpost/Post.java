package com.example.smartpost;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Post {
    private int place_id;
    private String name	;
    private String city	;
    private String address;
    private String country;
    private String postalcode;
    private String routingcode;
    private String availability;
    private String description;
    private Double lat;
    private Double lng;
    private List<Integer> weekdays = new ArrayList<>(Arrays.asList(0,0,0,0,0,0,0));
    private ArrayList<Double> opens = new ArrayList<Double>();
    private ArrayList<Double> closes = new ArrayList<Double>();

    public Post(int place_id, String name, String city, String address, String country, String postalcode, String routingcode, String availability, String description, Double lat, Double lng) {
        this.place_id = place_id;
        this.name = name;
        this.city = city;
        this.address = address;
        this.country = country.trim();
        this.postalcode = postalcode;
        this.routingcode = routingcode;
        this.availability = availability;
        this.description = description;
        this.lat = lat;
        this.lng = lng;

        for(int i=0; i<7; i++){
            opens.add( 0.0);
            closes.add(0.0);
        }

        parseOpening(availability,country);

    }



    public Post(int place_id, String name, String city, String address, String country, String postalcode, String routingcode, String availability,  Double lat, Double lng) {
        this.place_id = place_id;
        this.name = name;
        this.city = city;
        this.address = address;
        this.country = country.trim();
        this.postalcode = postalcode;
        this.routingcode = routingcode;
        this.availability = availability;
        this.description = "Not available";
        this.lat = lat;
        this.lng = lng;
        for(int i=0; i<7; i++){
            opens.add( 0.0);
            closes.add(0.0);
        }
        parseOpening(availability,country);
    }

    private void parseOpening(String s,String c){
        try {
        if(c.equals("FI")) {
            //ma-la 7.00 - 22.00, su 9.00 - 22.00
            String[] result = s.split(",");
            String[] week = {"ma", "ti", "ke", "to", "pe", "la", "su"};



                if (s.trim().equals("24h")) {
                    this.weekdays = new ArrayList<>(Arrays.asList(1, 1, 1, 1, 1, 1, 1));
                } else {
                    for (int i = 0; i < result.length; i++) {
                        int first = 0;
                        int end= 0;
                        double open;
                        double closing;
                        String token = result[i].trim();
                        String[] dt = token.split(" ", 2);// separates days.
                        String[] days = dt[0].split("-");
                        String[] times = dt[1].split("-");// needs trim.

                        open = Float.parseFloat(times[0].trim());
                        closing = Float.parseFloat(times[1].trim());
                        if (days.length > 1) {
                            for (int j = 0; j < week.length; j++) {
                                if (week[j].trim().equals(days[0])) {
                                    first = j;
                                    //System.out.println(first);
                                }
                            }
                            for (int j = 0; j < week.length; j++) {
                                if (week[j].trim().equals(days[1])) {
                                    end = j;
                                    //System.out.println(week[end]);
                                }
                            }
                            for(int set = first; set <= end; set++){
                                this.weekdays.set(set,1);
                                this.opens.set(set,open);
                                this.closes.set(set,closing);
                            }
                        } else {//
                            for (int j = 0; j < week.length; j++) {
                                if (week[j].equals(days[0])) {
                                    this.weekdays.set(j, 1);
                                    this.opens.set(j,open);
                                    this.closes.set(j,closing);
                                    //System.out.println(first);
                                }
                            }

                        }
                    }//end of normal
                }//end of else
            /*
            for(int g = 0;g<7;g++){ // for checking everything works.
                System.out.println("day "+g+" open: " + weekdays.get(g));
                System.out.print("opens " + opens.get(g));
                System.out.println(" closing " + closes.get(g));
            }*/

            }//End of FI


         else { // start of Eesti
            String[] result = s.split(",");
            String[] week = {"E", "T", "K", "N", "R", "L", "P"};



            if (s.equals("E-P 24h")) {
                this.weekdays = new ArrayList<>(Arrays.asList(1, 1, 1, 1, 1, 1, 1));
            } else {
                for (int i = 0; i < result.length; i++) {
                    int first = 0;
                    int end= 0;
                    double open;
                    double closing;
                    String token = result[i].trim();
                    String[] dt = token.split(" ", 2);// separates days.
                    String[] days = dt[0].split("-");
                    String[] times = dt[1].split("-");// needs trim.

                    open = Float.parseFloat(times[0].trim());
                    closing = Float.parseFloat(times[1].trim());
                    if (days.length > 1) {
                        for (int j = 0; j < week.length; j++) {
                            if (week[j].trim().equals(days[0])) {
                                first = j;
                                //System.out.println(first);
                            }
                        }
                        for (int j = 0; j < week.length; j++) {
                            if (week[j].trim().equals(days[1])) {
                                end = j;
                                //System.out.println(week[end]);
                            }
                        }
                        for(int set = first; set <= end; set++){
                            this.weekdays.set(set,1);
                            this.opens.set(set,open);
                            this.closes.set(set,closing);
                        }
                    } else {//
                        for (int j = 0; j < week.length; j++) {
                            if (week[j].equals(days[0])) {
                                this.weekdays.set(j, 1);
                                this.opens.set(j,open);
                                this.closes.set(j,closing);
                                //System.out.println(first);
                            }
                        }

                    }
                }//end of normal
            }//end of else
            /*
            for(int g = 0;g<7;g++){ // for checking everything works.
                System.out.println("day "+g+" open: " + weekdays.get(g));
                System.out.print("opens " + opens.get(g));
                System.out.println(" closing " + closes.get(g));
            }*/
         } //end of Eesti
            System.out.println(this.availability);
            System.out.println(this.weekdays);
        }
        catch (Exception e){System.out.println("Cannot parse data");}
    }



    public int getPlace_id() {
        return place_id;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getAddress() {
        return address;
    }

    public String getCountry() {
        return country;
    }

    public String getPostalcode() {
        return postalcode;
    }

    public String getRoutingcode() {
        return routingcode;
    }

    public String getAvailability() {
        return availability;
    }

    public String getDescription() {
        return description;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }

    public List<Integer> getWeekdays() {
        return weekdays;
    }

    public ArrayList<Double> getOpens() {
        return opens;
    }

    public ArrayList<Double> getCloses() {
        return closes;
    }


}
