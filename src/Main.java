import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {


    public static void main(String[] args) {
        java.sql.Connection conn = connetti("127.0.0.1", "3306", "eserciziocinemascraper", "root", "");
        webScraper(conn);

        stampaTabella(conn);


    }

    private static void stampaTabella(java.sql.Connection dbConn) {
        String query = "SELECT * FROM movies";
        try {
            PreparedStatement statement = dbConn.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            String result = "";
            while (rs.next()) {
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    result += rs.getString(i) + " ";
                }
                result += "\n";
            }
            System.out.println(result);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static java.sql.Connection connetti(String address, String port, String databaseName, String
            username, String password) {

        String db = "jdbc:mysql://" + address + ": " + port + "/" + databaseName;
        java.sql.Connection conn = null;
        try {
            conn = DriverManager.getConnection(db, username, password);
            if (conn != null) System.out.println("connessione avvenuta");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }


    public static void webScraper(java.sql.Connection dbConn) {

        Connection conn = Jsoup.connect("https://www.imdb.com/chart/moviemeter/");
        Document doc;
        try {
            doc = conn.get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int counter = 0;
        for (Element ul : doc.select("ul.ipc-metadata-list.ipc-metadata-list--dividers-between.sc-9d2f6de0-0.iMNUXk.compact-list-view.ipc-metadata-list--base")) {
            for (Element title : ul.select("h3.ipc-title__text")) {
                if (!isInside(dbConn, title.text())) {
                    insertTitle(dbConn, title.text());
                }
            }
        }
    }

    private static void insertTitle(java.sql.Connection dbConn, String title) {

        String query = "INSERT INTO movies(name) VALUES(?)";
        try {
            PreparedStatement statement = dbConn.prepareStatement(query);
            statement.setString(1, title);
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static boolean isInside(java.sql.Connection dbConn, String titolo) {

        String query = "SELECT * FROM movies WHERE name = ?";
        try {
            PreparedStatement statement = dbConn.prepareStatement(query);
            statement.setString(1, titolo);
            ResultSet rs = statement.executeQuery();
            String result = null;
            while (rs.next()) {
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    result += rs.getString(i) + " ";
                }
                result += "\n";
            }
            if (result == null) return false;
            return true;

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }


    }
}