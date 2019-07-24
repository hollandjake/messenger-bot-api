import com.hollandjake.messenger_bot_api.API;
import com.hollandjake.messenger_bot_api.message.Message;
import com.hollandjake.messenger_bot_api.util.Config;

import java.sql.Connection;
import java.sql.SQLException;

class APITest extends API {

	public APITest(Config config) throws SQLException {
		super(config);
	}

	public static void main(String[] args) throws SQLException {
		String configFile = args.length > 0 ? args[0] : null;
		new APITest(new Config(configFile));
	}

	@Override
	public void newMessage(Message message) {
		message.print();
	}

	@Override
	public void databaseReload(Connection connection) {
		System.out.println("Database reloaded");
	}

	@Override
	public void loaded(Connection connection) {
		System.out.println("Loaded");
	}
}