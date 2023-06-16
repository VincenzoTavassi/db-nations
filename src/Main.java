// milestone 1
// SELECT countries.country_id as country_id, countries.name as country, regions.name as region_name, continents.name as continent_name FROM `countries`
//JOIN regions ON regions.region_id = countries.country_id
//JOIN continents ON continents.continent_id = regions.continent_id
//ORDER BY country

import java.sql.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        String host = "jdbc:mysql://localhost:3306/dbnations";
        String username = "root";
        String password = "root";

        System.out.println("Benvenuto, inserisci una parola da cercare");
        Scanner scanner = new Scanner(System.in);
        String userCountry = scanner.nextLine();
        scanner.close();

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
                    while(result.next()) {
                        System.out.println(result.getInt("country_id") + " " + result.getString("country") + " " +
                                result.getString("region_name") + " " + result.getString("continent_name")
                                );
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
}