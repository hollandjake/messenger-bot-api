import com.hollandjake.messengerBotAPI.API;
import com.hollandjake.messengerBotAPI.message.Message;
import com.hollandjake.messengerBotAPI.util.Config;

import java.sql.Connection;

class APITest extends API {

	public APITest(Config config) {
		super(config);
	}

	public static void main(String[] args) {
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
	public void loaded() {
		sendMessage(getVersion());
		System.out.println("Loaded");
	}
}