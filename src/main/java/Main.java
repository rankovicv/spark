import com.fasterxml.jackson.databind.ObjectMapper;
import connection.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import user.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static spark.Spark.get;
/**
 * Created by veljko on 9.11.18.
 */
public class Main {
    public static void main(String[] args) {

        get("/hello", (reg, res) -> {
            Connection connection = null;
            PreparedStatement statement = null;
            ResultSet resultSet = null;

            try {
                BasicDataSource bds = DataSource.getInstance().getBds();
                connection = bds.getConnection();
                statement = connection
                        .prepareStatement("SELECT * FROM user");
                resultSet = statement.executeQuery();
                System.out.println("\n=====Releasing Connection Object To Pool=====\n");
                List<User> users = new ArrayList<>();
                while (resultSet.next()) {
                    User user = new User();
                    user.setId(resultSet.getInt("id"));
                    user.setFirstName(resultSet.getString("first_name"));
                    user.setLastName(resultSet.getString("last_name"));
                    users.add(user);
                }
                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(res.raw().getOutputStream(), users);
                return null;
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (resultSet != null)
                        resultSet.close();
                    if (statement != null)
                        statement.close();
                    if (connection != null)
                        connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return null;
        });
    }
}
