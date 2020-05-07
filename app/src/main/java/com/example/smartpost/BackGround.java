package com.example.smartpost;

import android.os.AsyncTask;
import android.util.Xml;
import android.webkit.WebView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class BackGround {
    ArrayList<Post> posts;
    private static BackGround bd = new BackGround();
    String url_FI = "http://iseteenindus.smartpost.ee/api/?request=destinations&country=FI&type=APT";
    String url_EE = "http://iseteenindus.smartpost.ee/api/?request=destinations&country=EE&type=APT";
    private boolean updated = false;

    public static BackGround getInstance(){
        return bd;
    }

    private BackGround(){

        posts = new ArrayList<Post>();

        new DownloadXmlTask().execute(url_FI,url_EE);


    }
    public boolean isUpdated(){return updated;}




    public String getInformation(int id) { //Todo siirto main tai wörkkii id kanssa?? ID!
        String s;
        int index = 0;
        int i = 0;
        System.out.println(posts.get(5).getName());
        System.out.println(posts.get(5).getWeekdays());
        System.out.println(posts.get(5).getOpens());
        System.out.println(posts.get(5).getCloses());
        for(Post post : posts){
            if(post.getPlace_id() == id){
                index = i;
            }
            i++;
        }
        Post p = posts.get(index);
        s = ("Name: "+p.getName()+"\n"+"Address: "+p.getAddress()+", "+p.getPostalcode()+"\n"+p.getCity()+", " +p.getCountry()+"\n");
        s=s.concat("Open: "+p.getAvailability()+"\n"+"Additional information:\n"+p.getDescription());

        return s;
    }

    public ArrayList<Post> getPosts() {
        updated = false;
        return posts;
    }

    public ArrayList<Post> getPosts(List<Integer> wd, double opening, double closing, int country) {
        ArrayList<Post> e = new ArrayList<Post>();

        for(Post post:posts){
            Boolean wdCheck = true;
            Boolean openingCheck = true;
            Boolean closingCheck = true;
            Boolean countryCheck ;
            //country filter
             if(country==1 && post.getCountry().equals("FI")){
                countryCheck = true;
            } else if(country==2 && post.getCountry().equals("EE")){
                countryCheck = true;
            } else if (country==0){
                    countryCheck = true;
            } else {
                 countryCheck = false;
             }
         //week day filter
            List<Integer> wdPost = post.getWeekdays();
            System.out.println(wdPost);
            System.out.println(wd);
            for(int i = 0; i<7 ;i++){
                if(wd.get(i) > wdPost.get(i)){
                    wdCheck=false;
                System.out.println("this happened"+wd.get(i)+">"+wdPost.get(i) );
                }
            }

            // Timing filters
            if(wdCheck==true && opening != 0 && closing != 0) {
                ArrayList<Double> opPost = post.getOpens();
                for (int i = 0; i < 7; i++) {
                    if((wd.get(i) <= wdPost.get(i)) ==true ){ //only selected days are compared
                        if (opening <= opPost.get(i)) {
                            openingCheck = false;
                        }
                    }
                }
                ArrayList<Double> clPost = post.getCloses();
                for (int i = 0; i < 7; i++) {
                    if((wd.get(i) <= wdPost.get(i)) ==true ){ //only selected days are compared
                        if (closing > clPost.get(i)) {
                            closingCheck = false;
                        }
                    }
                }
            }
            System.out.println(wdCheck+" "+ countryCheck+ " "+ closingCheck+ " "+ openingCheck);
            if(wdCheck == true && countryCheck== true && closingCheck == true && openingCheck == true ){
                e.add(post);
            }
        }
        updated = false;
        return e;
    }



    public class XMLParser {
        private final String ns = null;
        public ArrayList<Post> parse(InputStream in) throws XmlPullParserException, IOException {
            try {
                XmlPullParser parser = Xml.newPullParser();
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(in, null);
                parser.nextTag();
                return readFeed(parser);
            } finally {
                in.close();
            }
        }
    }

    private ArrayList<Post> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<Post> entries = new ArrayList();

        parser.require(XmlPullParser.START_TAG, null, "destinations");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("item")) {
                entries.add(readEntry(parser));
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    private Post readEntry(XmlPullParser parser) throws XmlPullParserException, IOException { // creates new post object and returns it from the item entry
        parser.require(XmlPullParser.START_TAG, null, "item");
        int place_id=0;
        String name	=null;
        String city	=null;
        String address=null;
        String country=null;
        String postalcode=null;
        String routingcode=null;
        String availability=null;
        String description=null;
        Double lat=null;
        Double lng=null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tag = parser.getName();
            if (tag.equals("place_id")) {
                place_id = readPlace_id(parser);
            } else if (tag.equals("name")) {
                name = readName(parser);
            } else if (tag.equals("city")) {
                city = readCity(parser);
            } else if (tag.equals("address")) {
                address = readAddress(parser);
            }else if (tag.equals("country")) {
                country = readCountry(parser);
            }else if (tag.equals("postalcode")) {
                postalcode = readPostalcode(parser);
            }else if (tag.equals("routingcode")) {
                routingcode = readRoutingcode(parser);
            }else if (tag.equals("availability")) {
                availability = readAvailability(parser);
            }else if (tag.equals("description")) {
                description = readDescription(parser);
            }else if (tag.equals("lat")) {
                lat = readLat(parser);
            }else if (tag.equals("lng")) {
                lng = readLng(parser);
            }
                else {
                skip(parser);
            }
        }
       // System.out.print(place_id + name + city + address+country+postalcode+routingcode+availability+description+lat+lng);
        return new Post(place_id,name,city,address,country,postalcode,routingcode,availability,description,lat,lng);
    }

    private Double readLng(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "lng");
        String s = readText(parser);
        parser.require(XmlPullParser.END_TAG,null , "lng");
        return Double.valueOf(s);
    }

    private Double readLat(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "lat");
        String s = readText(parser);
        parser.require(XmlPullParser.END_TAG,null , "lat");
        return Double.valueOf(s);
    }

    private String readDescription(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "description");
        String s = readText(parser);
        parser.require(XmlPullParser.END_TAG,null , "description");
        return s;
    }

    private String readAvailability(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "availability");
        String s = readText(parser);
        parser.require(XmlPullParser.END_TAG,null , "availability");
        return s;
    }

    private String readRoutingcode(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "routingcode");
        String s = readText(parser);
        parser.require(XmlPullParser.END_TAG,null , "routingcode");
        return s;
    }

    private String readPostalcode(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "postalcode");
        String s = readText(parser);
        parser.require(XmlPullParser.END_TAG,null , "postalcode");
        return s;
    }

    private String readCountry(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "country");
        String s = readText(parser);
        parser.require(XmlPullParser.END_TAG,null , "country");
        return s;
    }

    private String readAddress(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "address");
        String s = readText(parser);
        parser.require(XmlPullParser.END_TAG,null , "address");
        return s;

    }

    private String readCity(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "city");
        String s = readText(parser);
        parser.require(XmlPullParser.END_TAG,null , "city");
        return s;
    }

    private String readName(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "name");
        String s = readText(parser);
        parser.require(XmlPullParser.END_TAG,null , "name");
        return s;
    }

    private int readPlace_id(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "place_id");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "place_id");
        return Integer.parseInt(title);
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    // Implementation of AsyncTask used to download XML feed from stackoverflow.com.
    private class DownloadXmlTask extends AsyncTask<String, Void, ArrayList<Post>> { // use it on load?
        protected ArrayList<Post> doInBackground(String... urls){
            ArrayList<Post> entries = new ArrayList<Post>();

            for (int i = 0; i < urls.length; i++) {
                try {
                    entries.addAll(loadXmlFromNetwork(urls[i]));
                    System.out.println("Täällä ollaan");
                    //System.out.print(entries);
                } catch (IOException e) {

                } catch (XmlPullParserException e) {

                }
            }
            return (entries);
        }

        @Override
        protected void onPostExecute(ArrayList<Post> entries) {
            setPosts(entries);
            /*setContentView(R.layout.main);
            // Displays the HTML string in the UI via a WebView
            WebView myWebView = (WebView) findViewById(R.id.webview);
            myWebView.loadData(result, "text/html", null);*/
        }
    }

    public void setPosts(ArrayList<Post> e){
        posts.addAll(e);
        updated = true;
    }


    private ArrayList<Post> loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
        InputStream stream = null;
        // Instantiate the parser
        XMLParser XmlParser = new XMLParser();
        ArrayList<Post> entries = null;
        String title = null;
        String url = null;
        String summary = null;
        /*
        Calendar rightNow = Calendar.getInstance();
        DateFormat formatter = new SimpleDateFormat("MMM dd h:mmaa");

        // Checks whether the user set the preference to include summary text
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean pref = sharedPrefs.getBoolean("summaryPref", false);
        */
        /*
        StringBuilder htmlString = new StringBuilder();
        htmlString.append("<h3>" + getResources().getString(R.string.page_title) + "</h3>");
        htmlString.append("<em>" + getResources().getString(R.string.updated) + " " +
                formatter.format(rightNow.getTime()) + "</em>");*/

        try {
            stream = downloadUrl(urlString);
            entries = XmlParser.parse(stream);
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        // StackOverflowXmlParser returns a List (called "entries") of Entry objects.
        // Each Entry object represents a single post in the XML feed.
        // This section processes the entries list to combine each entry with HTML markup.
        // Each entry is displayed in the UI as a link that optionally includes
        // a text summary.

        /*
            htmlString.append("<p><a href='");
            htmlString.append(entry.link);
            htmlString.append("'>" + entry.title + "</a></p>");
            // If the user set the preference to include summary text,
            // adds it to the display.
            if (pref) {
                htmlString.append(entry.summary);
            }*/
        //this.posts.addAll(entries);
        return entries;
    }

    // Given a string representation of a URL, sets up a connection and gets
// an input stream.
    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }

}
