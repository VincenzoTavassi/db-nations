// milestone 1
// SELECT countries.country_id as country_id, countries.name as country, regions.name as region_name, continents.name as continent_name FROM `countries`
//JOIN regions ON regions.region_id = countries.country_id
//JOIN continents ON continents.continent_id = regions.continent_id
//ORDER BY country

import java.rmi.server.RemoteStub;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        String host = "jdbc:mysql://localhost:3306/dbnations";
        String username = "root";
        String password = "root";

        List<Integer> coutriesIDList = new ArrayList<>();
        System.out.println("Benvenuto, inserisci una parola da cercare");
        Scanner scanner = new Scanner(System.in);
        String userCountry = scanner.nextLine();

        try (Connection con = DriverManager.getConnection(host, username, password)) {
            String query = """
SELECT countries.country_id as country_id, countries.name as country, regions.name as region_name, continents.name as continent_name FROM `countries`
JOIN regions ON regions.region_id = countries.region_id
JOIN continents ON continents.continent_id = regions.continent_id
WHERE countries.name LIKE ?
ORDER BY country;
          """;

            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setString(1, "%" + userCountry + "%");
                try (ResultSet result = ps.executeQuery()) {
                    System.out.println("ID - COUNTRY - REGION - CONTINENT" );
                    while(result.next()) {
                        System.out.println(result.getInt("country_id") + " " + result.getString("country") + " " +
                                result.getString("region_name") + " " + result.getString("continent_name")
                                );
                        // Aggiungo ID della nazione alla lista degli ID in modo da poi evitare una selezione invalida
                        coutriesIDList.add(result.getInt("country_id"));
                    }
                    boolean valid = false;
                    Integer userCountryID = 0;
                    while (!valid) {
                        System.out.println("Inserisci l'id di una nazione per ulteriori dettagli");
                        userCountryID = Integer.parseInt(scanner.nextLine());
                        if (coutriesIDList.contains(userCountryID)) valid = true;
                        else System.out.println("L'ID non è tra quelli della lista.");
                    }
                        scanner.close();

                    try (Connection con2 = DriverManager.getConnection(host, username, password)) {
                        query = """
                    SELECT GROUP_CONCAT(languages.language) as languages, countries.*, country_stats.* FROM `countries`
                    JOIN country_stats ON country_stats.country_id = countries.country_id
                    JOIN country_languages ON country_languages.country_id = countries.country_id
                    JOIN languages ON country_languages.language_id = languages.language_id
                    WHERE countries.country_id = ? AND country_stats.year = (SELECT MAX(country_stats.year) FROM country_stats)
                    GROUP BY country_stats.year;""";
                        try (PreparedStatement secondPs = con2.prepareStatement(query)) {
                        secondPs.setInt(1, userCountryID);
                        try (ResultSet secondResult = secondPs.executeQuery()) {
                            if (secondResult.next()) {
                                System.out.println("Details for country: " + secondResult.getString("name"));
                                System.out.println("Languages: " + secondResult.getString("languages"));
                                System.out.println("Most recent stats");
                                System.out.println("Year: " + secondResult.getInt("year"));
                                System.out.println("Population: " + secondResult.getInt("population"));
                                System.out.println("GDP: " + secondResult.getBigDecimal("gdp"));
                            }

                        }

                    }
                }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();

        }


    }
}