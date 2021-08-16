import java.sql.*;

public class DatabaseOperations {

    private final String database;
    private final String createTable = "CREATE TABLE IF NOT EXISTS card (\n"
            + "	id integer PRIMARY KEY,\n"
            + "	number text NOT NULL,\n"
            + "	pin text NOT NULL,\n"
            + " balance INTEGER DEFAULT 0"
            + ");";

    private final String insertNewCard = "INSERT INTO card(number,pin) VALUES(?,?)";

    public DatabaseOperations(String databaseName) {
        this.database = databaseName;
        createDatabase();
    }

    public DatabaseOperations() {
        this.database = "db.s3db";
        createDatabase();
    }

    private void createDatabase() {
        try (Connection conn = connect()) {
            Statement stmt = conn.createStatement();
            stmt.execute(createTable);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void insertNewCard(Card card) {
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(insertNewCard)) {
            pstmt.setString(1, card.getNumber());
            pstmt.setString(2, card.getPin());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public Card findCard(String number) {
        String sql = String.format("SELECT * FROM card WHERE " +
                "(number = '%s')", number);

        Card foundCard = null;

        try (Connection conn = connect();
             PreparedStatement pstmt  = conn.prepareStatement(sql)){
            ResultSet rs  = pstmt.executeQuery();

            if (rs.next()) {
                String pin = rs.getString("pin");
                int balance = rs.getInt("balance");
                foundCard = new Card(number, pin, balance);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return foundCard;
    }

    public void updateBalance(Card card, int newBalance) {
        String updateQuery = String.format("UPDATE card SET balance = '%d' WHERE number = '%s'", newBalance, card.getNumber());

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean removeCard(Card card) {
        String removeQuery = String.format("DELETE FROM card WHERE number = '%s'", card.getNumber());

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(removeQuery)) {
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return false;
    }

    private Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:" + this.database;
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

}
